package openblocks.rubbish;

import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.OpenBlocks;
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

	public static BowelContents getProperty(Entity entity) {
		return entity.getCapability(CAPABILITY, EnumFacing.UP);
	}

	public static void registerCapability() {
		CapabilityManager.INSTANCE.register(BowelContents.class, new Capability.IStorage<BowelContents>() {
			@Override
			public NBTBase writeNBT(Capability<BowelContents> capability, BowelContents instance, EnumFacing side) {
				return new NBTTagInt(instance.brickCount);
			}

			@Override
			public void readNBT(Capability<BowelContents> capability, BowelContents instance, EnumFacing side, NBTBase nbt) {
				instance.brickCount = ((NBTTagInt)nbt).getInt();
			}

		}, new Callable<BowelContents>() {
			@Override
			public BowelContents call() throws Exception {
				return new BowelContents();
			}
		});
	}

	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent<Entity> evt) {
		if (evt.getObject() instanceof EntityPlayer) {
			evt.addCapability(CAPABILITY_KEY, new ICapabilityProvider() {

				private final BowelContents state = new BowelContents();

				@Override
				public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
					return capability == CAPABILITY;
				}

				@Override
				@SuppressWarnings("unchecked")
				public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
					if (capability == CAPABILITY) return (T)state;
					return null;
				}
			});
		}
	}

	@SubscribeEvent
	public void onEntityDeath(LivingDropsEvent evt) {
		if (evt.getEntity().worldObj.isRemote) return;

		BowelContents tag = getProperty(evt.getEntity());

		if (tag != null) {
			for (int i = 0; i < Math.min(tag.brickCount, 16); i++) {
				EntityItem entityItem = createBrick(evt.getEntity());
				evt.getDrops().add(entityItem);
			}
		}
	}

	private static boolean tryDecrementBrick(EntityPlayer player) {
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
		if (evt.type == Type.BOO && evt.sender != null) {
			final EntityPlayer player = evt.sender;
			player.worldObj.playSound(null, player.getPosition(), OpenBlocks.Sounds.PLAYER_WHOOPS, SoundCategory.PLAYERS, 1, 1);

			if (tryDecrementBrick(player)) {
				EntityItem drop = createBrick(player);
				drop.setDefaultPickupDelay();
				player.worldObj.spawnEntityInWorld(drop);
				player.addStat(OpenBlocks.brickAchievement);
				player.addStat(OpenBlocks.brickStat, 1);
			}
		}
	}

	private static EntityItem createBrick(Entity dropper) {
		ItemStack brick = new ItemStack(Items.BRICK);
		EntityItem drop = ItemUtils.createDrop(dropper, brick);
		double rotation = Math.toRadians(dropper.rotationYaw) - Math.PI / 2;
		double dx = Math.cos(rotation);
		double dz = Math.sin(rotation);

		drop.moveEntity(0.75 * dx, 0.5, 0.75 * dz);

		drop.motionX = 0.5 * dx;
		drop.motionY = 0.2;
		drop.motionZ = 0.5 * dz;

		return drop;
	}

}
