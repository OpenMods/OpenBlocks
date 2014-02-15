package openblocks.common;

import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.OpenMods;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

public class ExplosiveEnchantmentsHandler {

	public static final int ARMOR_HELMET = 3;
	public static final int ARMOR_CHESTPIECE = 2;
	public static final int ARMOR_PANTS = 1;
	public static final int ARMOR_BOOTS = 0;

	private final List<Integer> protectionParts = Lists.newArrayList(ARMOR_CHESTPIECE, ARMOR_HELMET, ARMOR_PANTS);

	private static final double VERTICAL_FACTOR = 5;

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
			null, // 1-based
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
		if (!entity.worldObj.isRemote) {
			entity.worldObj.createExplosion(entity, entity.posX, entity.boundingBox.minY, entity.posZ, power, isDestructive);
		}
	}

	private Map<Entity, JumpInfo> jumpBoosts = new MapMaker().weakKeys().makeMap();

	private static final ItemStack gunpowder = new ItemStack(Item.gunpowder);

	private static void useItems(EntityPlayer player, int gunpowderSlot, int armorSlot, int gunpowderAmout) {
		if (player.capabilities.isCreativeMode) return;

		final InventoryPlayer inventory = player.inventory;

		ItemStack armor = inventory.armorItemInSlot(armorSlot);
		armor.damageItem(1, player);
		if (armor.stackSize <= 0) inventory.armorInventory[armorSlot] = null;

		ItemStack resource = inventory.mainInventory[gunpowderSlot];
		resource.stackSize -= gunpowderAmout;
		if (resource.stackSize <= 0) inventory.mainInventory[gunpowderSlot] = null;
	}

	private static EnchantmentLevel tryUseEnchantment(EntityPlayer player, int armorSlot) {
		final InventoryPlayer inventory = player.inventory;

		ItemStack armor = inventory.armorInventory[armorSlot];
		if (armor == null || !(armor.getItem() instanceof ItemArmor)) return null;
		@SuppressWarnings("unchecked")
		Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(armor);
		Integer ench = enchantments.get(OpenBlocks.explosiveEnch.effectId);
		if (ench == null || ench > LEVELS.length) return null;
		EnchantmentLevel level = LEVELS[ench];
		if (level == null) return null;

		for (int i = 0; i < inventory.mainInventory.length; i++) {
			ItemStack stack = inventory.mainInventory[i];
			if (stack != null && gunpowder.isItemEqual(stack) && stack.stackSize >= level.gunpowderNeeded) {
				useItems(player, i, armorSlot, level.gunpowderNeeded);
				return level;
			}
		}

		return null;
	}

	private EnchantmentLevel tryUseUpperArmor(EntityPlayer player) {
		Collections.shuffle(protectionParts);
		for (int armorPart : protectionParts) {
			EnchantmentLevel result = tryUseEnchantment(player, armorPart);
			if (result != null) return result;
		}

		return null;
	}

	@ForgeSubscribe
	public void onFall(LivingFallEvent evt) {
		final Entity e = evt.entityLiving;
		if (evt.distance > 4 && !e.isSneaking() && e instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)e;

			EnchantmentLevel level = tryUseEnchantment(player, ARMOR_BOOTS);
			if (level == null) return;
			JumpInfo boost = new JumpInfo(level, evt.distance);
			level.createJumpExplosion(player);

			if (OpenMods.proxy.isClientPlayer(player)) {
				// And Now, Ladies and Gentlemen!
				// Logic Defying...
				// Loved By Everyone...
				// Possibly Buggy
				// TEEERRRRRIIIIBLE HAAAAAACK!
				Minecraft.getMinecraft().gameSettings.keyBindJump.pressed = true;
				// no, seriously, can't find better way to make jump
				jumpBoosts.put(player, boost);
			} else if (e instanceof EntityPlayerMP) {
				EntityPlayerMP serverPlayer = (EntityPlayerMP)player;
				serverPlayer.playerNetServerHandler.ticksForFloatKick = 0;
			} else return;

			evt.setCanceled(true);
		}
	}

	@ForgeSubscribe
	public void onJump(LivingJumpEvent e) {
		final Entity entity = e.entity;
		JumpInfo boost = jumpBoosts.remove(entity);
		if (boost != null) {
			boost.modifyVelocity(entity);

			if (OpenMods.proxy.isClientPlayer(entity)) {
				Minecraft.getMinecraft().gameSettings.keyBindJump.pressed = false;
			}
		}
	}

	private static boolean checkSource(DamageSource source) {
		return source instanceof EntityDamageSource && ALLOWED_DAMAGE_SOURCE.contains(source.damageType);
	}

	@ForgeSubscribe
	public void onDamage(LivingAttackEvent e) {
		final Entity victim = e.entity;
		if (victim instanceof EntityPlayerMP && checkSource(e.source)) {
			EnchantmentLevel level = tryUseUpperArmor(((EntityPlayer)victim));

			if (level != null) level.createArmorExplosion(victim);
		}
	}
}
