package openblocks.common;

import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import openblocks.common.entity.EntityTorchArrow;

public class BowEventHandler {

	Random rand = new Random();

	@ForgeSubscribe
	public void onArrowNock(ArrowNockEvent event) {
		if (!event.entity.worldObj.isRemote) {
			NBTTagCompound tag = event.result.getTagCompound();
			if (tag != null && tag.hasKey("openblocks_torchmode")) {
				// System.out.println("torch mode");
			}
		}
	}

	@ForgeSubscribe
	public void onArrowLoose(ArrowLooseEvent event) {
		// System.out.println("onArrowLoose");
		EntityPlayer player = event.entityPlayer;
		ItemStack bowStack = event.bow;
		if (player == null) { return; }

		if (!player.isSneaking()) { return; }

		if (!bowStack.hasTagCompound()) {
			// System.out.println("no nbt");
			return;
		}

		NBTTagCompound tag = bowStack.getTagCompound();

		if (!tag.hasKey("openblocks_torchmode")) {
			// System.out.println("no key");
			return;
		}

		if (!tag.getBoolean("openblocks_torchmode")) {
			// System.out.println("no torchmode");
			return;
		}

		// System.out.println("fine");
		int j = event.charge;

		boolean flag = player.capabilities.isCreativeMode
				|| EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, bowStack) > 0;

		if (flag || player.inventory.hasItem(Item.arrow.itemID)) {
			float f = (float)j / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if ((double)f < 0.1D) { return; }

			if (f > 1.0F) {
				f = 1.0F;
			}

			EntityArrow entityarrow = new EntityTorchArrow(player.worldObj, player, f * 2.0F);

			if (f == 1.0F) {
				entityarrow.setIsCritical(true);
			}

			int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, bowStack);

			if (k > 0) {
				entityarrow.setDamage(entityarrow.getDamage() + (double)k
						* 0.5D + 0.5D);
			}

			int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, bowStack);

			if (l > 0) {
				entityarrow.setKnockbackStrength(l);
			}

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, bowStack) > 0) {
				entityarrow.setFire(100);
			}

			bowStack.damageItem(1, player);
			player.worldObj.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F
					/ (rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

			if (flag) {
				entityarrow.canBePickedUp = 2;
			} else {
				player.inventory.consumeInventoryItem(Item.arrow.itemID);
			}

			if (!player.worldObj.isRemote) {
				player.worldObj.spawnEntityInWorld(entityarrow);
			}
			event.setCanceled(true);
		}
	}
}
