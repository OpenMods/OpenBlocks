package openblocks.common.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.CraneRegistry;
import openblocks.common.entity.EntityMagnet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCraneControl extends Item {

	private Icon iconDown;
	private Icon iconUp;
	private Icon iconLocked;
	private Icon iconDetected;

	public ItemCraneControl() {
		super(Config.itemCraneControl);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setUnlocalizedName("openblocks.crane_control");
	}

	private static void toggleMagnet(final EntityPlayer player) {
		final EntityMagnet magnet = CraneRegistry.instance.getOrCreateMagnet(player);
		magnet.toggleMagnet();
	}

	private long debouncerTime;

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		final World world = entityLiving.worldObj;
		if (!world.isRemote && entityLiving instanceof EntityPlayer) {
			long time = OpenBlocks.proxy.getTicks(world);
			if (time - debouncerTime > 10) {
				final EntityPlayer player = (EntityPlayer)entityLiving;
				debouncerTime = time;

				toggleMagnet(player);
			}
		}
		return true;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		CraneRegistry.Data data = CraneRegistry.instance.getData(player, false);

		if (data != null) {
			data.switchDirection();
			player.setItemInUse(stack, getMaxItemUseDuration(stack));
		}
		return stack;
	}

	@Override
	public void onUsingItemTick(ItemStack stack, EntityPlayer player, int count) {
		if (player instanceof EntityPlayerMP) {
			CraneRegistry.Data data = CraneRegistry.instance.getData(player, true);
			data.updateLength();
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000; // quite long time!
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:manipulator_idle");
		iconLocked = registry.registerIcon("openblocks:manipulator_locked");
		iconDetected = registry.registerIcon("openblocks:manipulator_detected");
		iconDown = registry.registerIcon("openblocks:manipulator_down");
		iconUp = registry.registerIcon("openblocks:manipulator_up");
	}

	@Override
	public Icon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if (player != null) {
			ItemStack armor = player.getCurrentArmor(2);

			if (armor != null && armor.getItem() instanceof ItemCraneBackpack) {
				CraneRegistry.Data data = CraneRegistry.instance.getData(player, false);
				if (data != null) {
					if (usingItem == stack) { return data.isExtending? iconDown : iconUp; }

					EntityMagnet magnet = CraneRegistry.instance.magnetData.get(player);

					if (magnet != null) {
						if (magnet.isLocked()) return iconLocked;
						else if (magnet.isAboveTarget()) return iconDetected;
					}
				}
			}
		}

		return itemIcon;
	}

	@Override
	public float getStrVsBlock(ItemStack itemstack, Block block, int metadata) {
		return 0;
	}

}
