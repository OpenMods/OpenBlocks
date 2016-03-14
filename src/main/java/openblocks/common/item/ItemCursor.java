package openblocks.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.infobook.BookDocumentation;
import openmods.utils.EnchantmentUtils;
import openmods.utils.ItemUtils;

@BookDocumentation(hasVideo = true)
public class ItemCursor extends Item {

	public ItemCursor() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setMaxStackSize(1);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 50;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		tag.setInteger("dimension", world.provider.dimensionId);
		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
		tag.setInteger("side", side);
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if (world.isRemote) return itemStack;

		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag != null && tag.hasKey("x") && tag.hasKey("y") && tag.hasKey("z") && tag.hasKey("dimension")) {
			final int x = tag.getInteger("x");
			final int y = tag.getInteger("y");
			final int z = tag.getInteger("z");
			final int dimension = tag.getInteger("dimension");

			if (world.provider.dimensionId == dimension && world.blockExists(x, y, z)) clickBlock(world, player, x, y, z, tag.getInteger("side"));
		}
		return itemStack;
	}

	private static void clickBlock(World world, EntityPlayer player, final int x, final int y, final int z, int side) {
		Block block = world.getBlock(x, y, z);
		if (block != Blocks.air) {
			final double distanceToLinkedBlock = getDistanceToLinkedBlock(player, x, y, z);
			if (distanceToLinkedBlock < Config.cursorDistanceLimit) {
				if (player.capabilities.isCreativeMode) block.onBlockActivated(world, x, y, z, player, side, 0, 0, 0);
				else {
					final int cost = (int)Math.max(0, distanceToLinkedBlock - 10);
					final int playerExperience = EnchantmentUtils.getPlayerXP(player);
					if (cost <= playerExperience) {
						block.onBlockActivated(world, x, y, z, player, side, 0, 0, 0);
						EnchantmentUtils.addPlayerXP(player, -cost);
					}
				}
			}
		}
	}

	private static double getDistanceToLinkedBlock(EntityPlayer player, double x, double y, double z) {
		double xd = player.posX - x;
		double yd = player.posY - y;
		double zd = player.posZ - z;
		return Math.sqrt(xd * xd + yd * yd + zd * zd);
	}

}
