package openblocks.enchantments;

import java.util.*;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import openblocks.OpenBlocks.Enchantments;
import openblocks.api.FlimFlamRegistry;
import openblocks.api.IFlimFlamEffect;
import openmods.Log;

import com.google.common.collect.Lists;

public class FlimFlamEnchantmentsHandler {

	public static final String LUCK_PROPERTY = "OpenBlocks-Luck";

	public static final int LUCK_MARGIN = 5;

	public static final int EFFECT_DELAY = 20;

	private static final Random random = new Random();

	private static class Luck implements IExtendedEntityProperties {

		public int luck;

		public int cooldown;

		@Override
		public void saveNBTData(NBTTagCompound entityTag) {
			entityTag.setInteger(LUCK_PROPERTY, luck);
		}

		@Override
		public void loadNBTData(NBTTagCompound entityTag) {
			luck = entityTag.getInteger(LUCK_PROPERTY);
		}

		@Override
		public void init(Entity entity, World world) {}
	}

	private static Luck getProperty(Entity entity) {
		IExtendedEntityProperties prop = entity.getExtendedProperties(LUCK_PROPERTY);
		return (prop instanceof Luck)? (Luck)prop : null;
	}

	@ForgeSubscribe
	public void onEntityConstruct(EntityEvent.EntityConstructing evt) {
		if (evt.entity instanceof EntityPlayer) evt.entity.registerExtendedProperties(LUCK_PROPERTY, new Luck());
	}

	@ForgeSubscribe
	public void onDamage(LivingAttackEvent e) {
		if (!(e.entityLiving instanceof EntityPlayer)) return;
		if (e.entityLiving.worldObj.isRemote) return;

		final EntityPlayer targetPlayer = (EntityPlayer)e.entityLiving;

		if (e.source == null) return;
		final Entity damageSource = e.source.getEntity();

		if (!(damageSource instanceof EntityPlayer)) return;
		final EntityPlayer sourcePlayer = (EntityPlayer)damageSource;

		// flim flam yerself?
		if (sourcePlayer == targetPlayer) return;

		final int sourceFlimFlam = getFlimFlamToolLevel(sourcePlayer);
		final int targetFlimFlam = getFlimFlamArmorLevel(targetPlayer);

		Luck targetLuck = getProperty(e.entityLiving);
		if (targetLuck != null) targetLuck.luck -= calculateWeaponLuckChange(sourceFlimFlam);

		Luck sourceLuck = getProperty(sourcePlayer);
		if (sourceLuck != null) sourceLuck.luck -= calculateArmorLuckChange(targetFlimFlam);
	}

	private static int calculateWeaponLuckChange(int sourceFlimFlam) {
		return 40 * sourceFlimFlam;
	}

	private static int calculateArmorLuckChange(int sourceFlimFlam) {
		return 20 * sourceFlimFlam;
	}

	public static void deliverKarma(EntityPlayer player) {
		Luck property = getProperty(player);
		if (property == null || !canFlimFlam(property)) return;
		final int luck = property.luck;
		final int maxCost = Math.abs(luck);

		int totalWeight = 0;
		List<IFlimFlamEffect> selectedEffects = Lists.newArrayList();

		for (IFlimFlamEffect effect : FlimFlamRegistry.getFlimFlams())
			if (effect.cost() <= maxCost) {
				selectedEffects.add(effect);
				totalWeight += effect.weight();
			}

		if (selectedEffects.isEmpty()) return;

		Collections.shuffle(selectedEffects);

		while (!selectedEffects.isEmpty()) {
			final int selectedWeight = random.nextInt(totalWeight);
			int currentWeight = 0;
			Iterator<IFlimFlamEffect> it = selectedEffects.iterator();

			while (it.hasNext()) {
				final IFlimFlamEffect effect = it.next();
				currentWeight += effect.weight();
				if (selectedWeight <= currentWeight) {
					if (effect.execute(player)) {
						property.luck += effect.cost();
						Log.info("Player %s flim-flammed with %s, current luck: %s", player, effect.name(), property.luck);
						if (!effect.isSilent()) player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("openblocks.flim_flammed"));
						return;
					}
					totalWeight -= effect.weight();
					it.remove();
					break;
				}
			}
		}
	}

	public static int getLuck(EntityPlayer player) {
		Luck property = getProperty(player);
		return property != null? property.luck : 0;
	}

	public static int modifyLuck(EntityPlayer player, int amount) {
		Luck property = getProperty(player);
		if (property == null) return 0;
		property.luck += amount;
		return property.luck;
	}

	private static boolean canFlimFlam(Luck property) {
		if (property.luck > -LUCK_MARGIN || property.cooldown-- > 0) return false;
		property.cooldown = EFFECT_DELAY;
		double probability = Math.abs(2 * Math.atan(property.luck / 500.0) / Math.PI);
		double r = random.nextDouble();
		return r < probability;
	}

	private static int getFlimFlamToolLevel(EntityPlayer player) {
		return getFlimFlamLevel(player.getHeldItem());
	}

	private static int getFlimFlamArmorLevel(EntityPlayer player) {
		int sum = 0;
		for (ItemStack stack : player.inventory.armorInventory) {
			sum += getFlimFlamLevel(stack);
		}
		return sum;
	}

	@SuppressWarnings("unchecked")
	private static int getFlimFlamLevel(ItemStack stack) {
		if (stack == null) return 0;
		Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		Integer result = enchantments.get(Enchantments.flimFlam.effectId);
		return result != null? result : 0;
	}
}
