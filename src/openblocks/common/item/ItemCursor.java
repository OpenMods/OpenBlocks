package openblocks.common.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.utils.EnchantmentUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCursor extends Item {

	public ItemCursor() {
		super(Config.itemCursorId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:cursor");
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 50;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int par7, float par8, float par9, float par10) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("dimension", world.provider.dimensionId);
		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
		stack.setTagCompound(tag);
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {
		if (world.isRemote) return itemStack;
		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag != null) {
			if (tag.hasKey("x") && tag.hasKey("y") && tag.hasKey("z")
					&& tag.hasKey("dimension")) {
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				int dimension = tag.getInteger("dimension");
				if (world.provider.dimensionId == dimension
						&& world.blockExists(x, y, z)) {
					int distance = (int)getDistanceToLinkedBlock(world, player, itemStack);
					distance = Math.max(0, distance - 10);
					int playerExperience = EnchantmentUtils.getPlayerXP(player);
					if (distance > playerExperience) { return itemStack; }
					int blockId = world.getBlockId(x, y, z);
					Block block = Block.blocksList[blockId];
					if (block != null) {
						block.onBlockActivated(world, x, y, z, player, 0, 0, 0, 0);
						EnchantmentUtils.drainPlayerXP(player, distance);
					}
				}
			}
		}
		return itemStack;
	}

	public static double getDistanceToLinkedBlock(World world, EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {
			if (tag.hasKey("x") && tag.hasKey("y") && tag.hasKey("z")
					&& tag.hasKey("dimension")) {
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				int dimension = tag.getInteger("dimension");
				if (dimension == world.provider.dimensionId) {
					double xd = player.posX - x;
					double yd = player.posY - y;
					double zd = player.posZ - z;
					return Math.sqrt(xd * xd + yd * yd + zd * zd);
				} else {
					return 0;
				}
			}
		}
		return 0;
	}

}
