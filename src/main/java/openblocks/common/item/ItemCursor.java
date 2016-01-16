package openblocks.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openmods.infobook.BookDocumentation;
import openmods.utils.EnchantmentUtils;
import openmods.utils.ItemUtils;
import openmods.utils.NbtUtils;

@BookDocumentation(hasVideo = true)
public class ItemCursor extends Item {

	public ItemCursor() {
		setMaxStackSize(1);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 50;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		tag.setInteger("dimension", world.provider.getDimensionId());
		NbtUtils.store(tag, pos);
		tag.setInteger("side", side.ordinal());
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if (world.isRemote) return itemStack;

		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag != null && NbtUtils.hasCoordinates(tag) && tag.hasKey("dimension")) {
			final int dimension = tag.getInteger("dimension");

			final BlockPos pos = NbtUtils.readBlockPos(tag);
			if (world.provider.getDimensionId() == dimension && world.isBlockLoaded(pos)) {
				final EnumFacing side = NbtUtils.readEnum(tag, "side", EnumFacing.UP);
				clickBlock(world, player, pos, side);
			}
		}
		return itemStack;
	}

	private static void clickBlock(World world, EntityPlayer player, BlockPos pos, EnumFacing side) {
		if (!world.isAirBlock(pos)) {
			final IBlockState state = world.getBlockState(pos);
			final Block block = state.getBlock();
			if (player.capabilities.isCreativeMode) block.onBlockActivated(world, pos, state, player, side, 0, 0, 0);
			else {
				final int cost = (int)Math.max(0, Math.sqrt(player.getDistanceSq(pos)) - 10);
				final int playerExperience = EnchantmentUtils.getPlayerXP(player);
				if (cost <= playerExperience) {
					block.onBlockActivated(world, pos, state, player, side, 0, 0, 0);
					EnchantmentUtils.addPlayerXP(player, -cost);
				}
			}
		}
	}

}
