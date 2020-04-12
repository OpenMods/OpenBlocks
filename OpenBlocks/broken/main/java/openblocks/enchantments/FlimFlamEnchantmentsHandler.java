package openblocks.enchantments;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Enchantments;
import openblocks.api.IFlimFlamDescription;
import openblocks.enchantments.flimflams.FlimFlamRegistry;
import openmods.Log;

public class FlimFlamEnchantmentsHandler {

	public static final int LUCK_MARGIN = -30;

	public static final int EFFECT_DELAY = 20 * 15; // 15s cooldown

	private static final Random RANDOM = new Random();

	private static class Luck {

		public int luck;

		public int cooldown;

		public boolean forceNext;

	}

	private static final ResourceLocation CAPABILITY_KEY = OpenBlocks.location("luck");

	@CapabilityInject(Luck.class)
	private static Capability<Luck> CAPABILITY;

	public static void registerCapability() {
		CapabilityManager.INSTANCE.register(Luck.class, new Capability.IStorage<Luck>() {
			@Override
			public NBTBase writeNBT(Capability<Luck> capability, Luck instance, Direction side) {
				return new IntNBT(instance.luck);
			}

			@Override
			public void readNBT(Capability<Luck> capability, Luck instance, Direction side, NBTBase nbt) {
				instance.luck = ((IntNBT)nbt).getInt();
			}

		}, Luck::new);

		MinecraftForge.EVENT_BUS.register(new CapabilityInjector());
	}

	@Nullable
	private static Luck getProperty(Entity entity) {
		return CAPABILITY != null? entity.getCapability(CAPABILITY, Direction.UP) : null;
	}

	private static class CapabilityInjector {

		@SubscribeEvent
		public void attachCapability(AttachCapabilitiesEvent<Entity> evt) {
			if (evt.getObject() instanceof ServerPlayerEntity) {
				evt.addCapability(CAPABILITY_KEY, new ICapabilitySerializable<IntNBT>() {

					private final Luck state = new Luck();

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
						return new IntNBT(state.luck);
					}

					@Override
					public void deserializeNBT(IntNBT nbt) {
						state.luck = nbt.getInt();
					}
				});
			}
		}
	}

	@SubscribeEvent
	public void onDamage(LivingHurtEvent e) {
		final LivingEntity entityLiving = e.getEntityLiving();
		if (!(entityLiving instanceof PlayerEntity)) return;
		if (entityLiving.world.isRemote) return;

		final PlayerEntity targetPlayer = (PlayerEntity)entityLiving;

		if (e.getSource() == null) return;
		final Entity damageSource = e.getSource().getTrueSource();

		if (!(damageSource instanceof PlayerEntity)) return;
		final PlayerEntity sourcePlayer = (PlayerEntity)damageSource;

		// flim flam yerself?
		if (sourcePlayer == targetPlayer) return;

		final int sourceFlimFlam = getFlimFlamToolLevel(sourcePlayer);
		final int targetFlimFlam = getFlimFlamArmorLevel(targetPlayer);

		// armor is less effective, since we can have more levels
		final int flimFlamDiff = targetFlimFlam / 3 - sourceFlimFlam;

		final PlayerEntity flimFlamTarget;
		final int flimFlamsToApply;
		if (flimFlamDiff == 0) return;

		if (flimFlamDiff > 0) {
			// target is better protected
			flimFlamTarget = sourcePlayer;
			flimFlamsToApply = flimFlamDiff;
		} else {
			flimFlamTarget = targetPlayer;
			flimFlamsToApply = -flimFlamDiff;
		}

		Luck victimLuck = getProperty(flimFlamTarget);
		if (victimLuck != null) {
			for (int i = 0; i < flimFlamsToApply; i++) {
				int roll = rollD20();
				// critical
				if (roll == 20) victimLuck.forceNext = true;
				victimLuck.luck -= roll;
			}

			if (victimLuck.luck < LUCK_MARGIN) victimLuck.forceNext = true;
		}
	}

	private static int rollD20() {
		return RANDOM.nextInt(20) + 1;
	}

	public static void deliverKarma(ServerPlayerEntity player) {
		if (player.isDead) return;
		Luck property = getProperty(player);
		if (property == null || !canFlimFlam(property)) return;
		final int luck = property.luck;

		int totalWeight = 0;
		List<IFlimFlamDescription> selectedEffects = Lists.newArrayList();
		for (IFlimFlamDescription effectMeta : FlimFlamRegistry.instance.getFlimFlams())
			if (effectMeta.canApply(luck) && !FlimFlamRegistry.BLACKLIST.isBlacklisted(effectMeta)) {
				selectedEffects.add(effectMeta);
				totalWeight += effectMeta.weight();
			}

		if (selectedEffects.isEmpty()) return;

		Collections.shuffle(selectedEffects);

		while (!selectedEffects.isEmpty()) {
			final int selectedWeight = RANDOM.nextInt(totalWeight);
			int currentWeight = 0;
			Iterator<IFlimFlamDescription> it = selectedEffects.iterator();

			while (it.hasNext()) {
				final IFlimFlamDescription effectMeta = it.next();
				currentWeight += effectMeta.weight();
				if (selectedWeight <= currentWeight) {
					try {
						if (effectMeta.action().execute(player)) {
							property.luck -= effectMeta.cost();
							Log.debug("Player %s flim-flammed with %s, current luck: %s", player, effectMeta.name(), property.luck);
							if (!effectMeta.isSilent()) player.sendMessage(new TranslationTextComponent("openblocks.flim_flammed"));
							return;
						}
					} catch (Throwable t) {
						Log.warn(t, "Error during flimflam '%s' execution", effectMeta.name());
					}
					totalWeight -= effectMeta.weight();
					it.remove();
					break;
				}
			}
		}
	}

	public static int getLuck(PlayerEntity player) {
		Luck property = getProperty(player);
		return property != null? property.luck : 0;
	}

	public static int modifyLuck(PlayerEntity player, int amount) {
		Luck property = getProperty(player);
		if (property == null) return 0;
		property.luck += amount;
		return property.luck;
	}

	private static boolean canFlimFlam(Luck property) {
		if (property.forceNext) {
			property.forceNext = false;
			property.cooldown = EFFECT_DELAY;
			return true;
		}

		if (property.luck > -LUCK_MARGIN || property.cooldown-- > 0) return false;
		property.cooldown = EFFECT_DELAY;
		double probability = 0.75 * 2.0 * Math.abs(Math.atan(property.luck / 250.0) / Math.PI);
		double r = RANDOM.nextDouble();
		return r < probability;
	}

	private static int getFlimFlamToolLevel(PlayerEntity player) {
		return EnchantmentHelper.getEnchantmentLevel(Enchantments.flimFlam, player.getHeldItemMainhand());
	}

	private static int getFlimFlamArmorLevel(PlayerEntity player) {
		int sum = 0;
		for (ItemStack stack : player.inventory.armorInventory) {
			sum += EnchantmentHelper.getEnchantmentLevel(Enchantments.flimFlam, stack);
		}
		return sum;
	}
}
