package openblocks.enchantments;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.OpenBlocks.Enchantments;
import openmods.OpenMods;

public class ExplosiveEnchantmentsHandler {

	private static final double VERTICAL_FACTOR = 5;

	private final List<EquipmentSlotType> protectionParts = Lists.newArrayList(EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.HEAD);

	private static final Set<String> ALLOWED_DAMAGE_SOURCE = ImmutableSet.of("arrow", "player", "mob");

	private static class EnchantmentLevel {
		public final double multiplier;
		public final double maxHeightBonus;
		public final float jumpExplosionPower;
		public final float armorExplosionPower;
		public final boolean isDestructive;
		public final int gunpowderNeeded;

		private EnchantmentLevel(double multiplier, double maxHeightBonus, float jumpExplosionPower, float armorExplosionPower, boolean isDestructive, int gunpowderNeeded) {
			this.multiplier = multiplier;
			this.maxHeightBonus = maxHeightBonus;
			this.jumpExplosionPower = jumpExplosionPower;
			this.armorExplosionPower = armorExplosionPower;
			this.isDestructive = isDestructive;
			this.gunpowderNeeded = gunpowderNeeded;
		}

		protected double calculateHeightBonus(float height) {
			return Math.min(Math.sqrt(height), maxHeightBonus);
		}

		public double calculateMultipler(float height) {
			return multiplier + calculateHeightBonus(height);
		}

		public void createJumpExplosion(Entity entity) {
			createExplosionForEntity(entity, jumpExplosionPower, isDestructive);
		}

		public void createArmorExplosion(Entity entity) {
			createExplosionForEntity(entity, armorExplosionPower, isDestructive);
		}
	}

	private final static EnchantmentLevel LEVELS[] = new EnchantmentLevel[] {
			new EnchantmentLevel(0.10, 5, 5, 1, false, 1),
			new EnchantmentLevel(0.75, 7.5, 10, 2, false, 2),
			new EnchantmentLevel(1.00, 10, 5, 4, Config.explosiveEnchantGrief, 4)
	};

	private static class JumpInfo {
		public final EnchantmentLevel level;
		public final float height;

		private JumpInfo(EnchantmentLevel level, float height) {
			this.level = level;
			this.height = height;
		}

		public void modifyVelocity(Entity entity) {
			double multiplier = level.calculateMultipler(height);

			entity.motionX *= multiplier * VERTICAL_FACTOR;
			entity.motionZ *= multiplier * VERTICAL_FACTOR;
			entity.motionY *= multiplier;
		}
	}

	public static void createExplosionForEntity(Entity entity, float power, boolean isDestructive) {
		if (!entity.world.isRemote) {
			entity.world.createExplosion(entity, entity.posX, entity.getEntityBoundingBox().minY, entity.posZ, power, isDestructive);
		}
	}

	private final Map<Entity, JumpInfo> jumpBoosts = new MapMaker().weakKeys().makeMap();

	private static final ItemStack gunpowder = new ItemStack(Items.GUNPOWDER);

	private static void useItems(PlayerEntity player, @Nonnull ItemStack resource, EquipmentSlotType armorSlot, int gunpowderAmout) {
		if (player.capabilities.isCreativeMode) return;

		ItemStack armor = player.getItemStackFromSlot(armorSlot);
		armor.damageItem(1, player);

		resource.shrink(gunpowderAmout);
	}

	private static EnchantmentLevel tryUseEnchantment(PlayerEntity player, EquipmentSlotType slot) {
		ItemStack armor = player.getItemStackFromSlot(slot);
		if (armor.isEmpty() || !(armor.getItem() instanceof ArmorItem)) return null;

		final PlayerInventory inventory = player.inventory;
		int explosiveLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.explosive, armor);
		if (explosiveLevel <= 0 || explosiveLevel > LEVELS.length) return null;
		EnchantmentLevel level = LEVELS[explosiveLevel - 1];

		for (ItemStack stack : inventory.mainInventory) {
			if (gunpowder.isItemEqual(stack) && stack.getCount() >= level.gunpowderNeeded) {
				useItems(player, stack, slot, level.gunpowderNeeded);
				return level;
			}
		}

		return null;
	}

	private EnchantmentLevel tryUseUpperArmor(PlayerEntity player) {
		for (EquipmentSlotType armorPart : protectionParts) {
			EnchantmentLevel result = tryUseEnchantment(player, armorPart);
			if (result != null) return result;
		}

		return null;
	}

	@SubscribeEvent
	public void onFall(LivingFallEvent evt) {
		final Entity e = evt.getEntityLiving();
		if (evt.getDistance() > 4 && !e.isSneaking() && e instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)e;

			EnchantmentLevel level = tryUseEnchantment(player, EquipmentSlotType.FEET);
			if (level == null) return;
			JumpInfo boost = new JumpInfo(level, evt.getDistance());
			level.createJumpExplosion(player);
			if (OpenMods.proxy.isClientPlayer(player)) {
				// And Now, Ladies and Gentlemen!
				// Logic Defying...
				// Loved By Everyone...
				// Possibly Buggy
				// TEEERRRRRIIIIBLE HAAAAAACK!
				KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode(), true);
				// no, seriously, can't find better way to make jump
				jumpBoosts.put(player, boost);
			}
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onJump(LivingJumpEvent e) {
		final Entity entity = e.getEntity();
		JumpInfo boost = jumpBoosts.remove(entity);
		if (boost != null) {
			boost.modifyVelocity(entity);

			if (OpenMods.proxy.isClientPlayer(entity)) {
				KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode(), false);
			}
		}
	}

	private static boolean checkSource(DamageSource source) {
		return source instanceof EntityDamageSource && ALLOWED_DAMAGE_SOURCE.contains(source.damageType);
	}

	@SubscribeEvent
	public void onDamage(LivingAttackEvent e) {
		final Entity victim = e.getEntity();
		if (victim instanceof ServerPlayerEntity && checkSource(e.getSource())) {
			EnchantmentLevel level = tryUseUpperArmor(((PlayerEntity)victim));

			if (level != null) level.createArmorExplosion(victim);
		}
	}
}
