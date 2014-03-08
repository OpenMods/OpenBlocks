package openblocks.enchantments;

import java.util.Map;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import openblocks.OpenBlocks.Enchantments;

public class FlimFlamEnchantmentsHandler {

	public static final String LUCK_PROPERTY = "OpenBlocks-Luck";

	private static class Luck implements IExtendedEntityProperties {

		public int luck;

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
		if (targetLuck != null) targetLuck.luck -= calculateLuckChange(sourceFlimFlam);

		Luck sourceLuck = getProperty(sourcePlayer);
		if (sourceLuck != null) sourceLuck.luck -= calculateLuckChange(targetFlimFlam);
	}

	private static int calculateLuckChange(int sourceFlimFlam) {
		// TODO
		return 20 * sourceFlimFlam;
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
	public static int getFlimFlamLevel(ItemStack stack) {
		if (stack == null) return 0;
		Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		Integer result = enchantments.get(Enchantments.flimFlam.effectId);
		return result != null? result : 0;
	}
}
