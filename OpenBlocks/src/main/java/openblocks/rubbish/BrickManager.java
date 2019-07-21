package openblocks.rubbish;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.OpenBlocks;
import openblocks.advancements.Criterions;
import openblocks.events.PlayerActionEvent;
import openblocks.events.PlayerActionEvent.Type;
import openmods.utils.ItemUtils;

public class BrickManager {

	public static final String BOWELS_PROPERTY = "Bowels";

	public static class BowelContents {
		public int brickCount;
	}

	private static final ResourceLocation CAPABILITY_KEY = OpenBlocks.location("bowels");

	@CapabilityInject(BowelContents.class)
	private static Capability<BowelContents> CAPABILITY;

	@Nullable
	public static BowelContents getProperty(Entity entity) {
		return CAPABILITY != null? entity.getCapability(CAPABILITY, Direction.UP) : null;
	}

	public static void registerCapability() {
		CapabilityManager.INSTANCE.register(BowelContents.class, new Capability.IStorage<BowelContents>() {
			@Override
			public NBTBase writeNBT(Capability<BowelContents> capability, BowelContents instance, Direction side) {
				return new IntNBT(instance.brickCount);
			}

			@Override
			public void readNBT(Capability<BowelContents> capability, BowelContents instance, Direction side, NBTBase nbt) {
				instance.brickCount = ((IntNBT)nbt).getInt();
			}

		}, BowelContents::new);

		MinecraftForge.EVENT_BUS.register(new CapabilityInjector());
	}

	private static class CapabilityInjector {

		@SubscribeEvent
		public void attachCapability(AttachCapabilitiesEvent<Entity> evt) {
			if (evt.getObject() instanceof ServerPlayerEntity) {
				evt.addCapability(CAPABILITY_KEY, new ICapabilitySerializable<IntNBT>() {

					private final BowelContents state = new BowelContents();

					@Override
					public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
						return capability == CAPABILITY;
					}

					@Override
					@SuppressWarnings("unchecked")
					public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
						if (capability == CAPABILITY) return (T)state;
						return null;
					}

					@Override
					public IntNBT serializeNBT() {
						return new IntNBT(state.brickCount);
					}

					@Override
					public void deserializeNBT(IntNBT nbt) {
						state.brickCount = nbt.getInt();
					}
				});
			}
		}
	}

	@SubscribeEvent
	public void onEntityDeath(LivingDropsEvent evt) {
		if (evt.getEntity().world.isRemote) return;

		BowelContents tag = getProperty(evt.getEntity());

		if (tag != null) {
			for (int i = 0; i < Math.min(tag.brickCount, 16); i++) {
				ItemEntity entityItem = createBrick(evt.getEntity());
				evt.getDrops().add(entityItem);
			}
		}
	}

	private static boolean tryDecrementBrick(PlayerEntity player) {
		if (player.capabilities.isCreativeMode) return true;

		BowelContents tag = getProperty(player);
		if (tag != null && tag.brickCount > 0) {
			tag.brickCount--;
			return true;
		}

		return false;
	}

	@SubscribeEvent
	public void onPlayerScared(PlayerActionEvent evt) {
		if (evt.type == Type.BOO && evt.sender instanceof ServerPlayerEntity) {
			final ServerPlayerEntity player = (ServerPlayerEntity)evt.sender;
			player.world.playSound(null, player.getPosition(), OpenBlocks.Sounds.PLAYER_WHOOPS, SoundCategory.PLAYERS, 1, 1);

			if (tryDecrementBrick(player)) {
				ItemEntity drop = createBrick(player);
				drop.setDefaultPickupDelay();
				player.world.spawnEntity(drop);
				Criterions.brickDropped.trigger(player);
				player.addStat(OpenBlocks.brickStat, 1);
			}
		}
	}

	private static ItemEntity createBrick(Entity dropper) {
		ItemStack brick = new ItemStack(Items.BRICK);
		ItemEntity drop = ItemUtils.createDrop(dropper, brick);
		double rotation = Math.toRadians(dropper.rotationYaw) - Math.PI / 2;
		double dx = Math.cos(rotation);
		double dz = Math.sin(rotation);

		drop.move(MoverType.SELF, 0.75 * dx, 0.5, 0.75 * dz);

		drop.motionX = 0.5 * dx;
		drop.motionY = 0.2;
		drop.motionZ = 0.5 * dz;

		return drop;
	}

}
