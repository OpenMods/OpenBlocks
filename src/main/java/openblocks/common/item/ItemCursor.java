package openblocks.common.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.Config;
import openmods.infobook.BookDocumentation;
import openmods.utils.EnchantmentUtils;
import openmods.utils.ItemUtils;
import openmods.utils.NbtUtils;

@BookDocumentation(hasVideo = true)
public class ItemCursor extends Item {

	// TODO maybe allow off-hand item use?
	public ItemCursor() {
		setMaxStackSize(1);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 50;
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		tag.setInteger("dimension", world.provider.getDimension());
		NbtUtils.store(tag, pos);
		tag.setInteger("side", facing.ordinal());
		return EnumActionResult.SUCCESS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag != null && NbtUtils.hasCoordinates(tag) && tag.hasKey("dimension")) {
				final int dimension = tag.getInteger("dimension");

				final BlockPos pos = NbtUtils.readBlockPos(tag);
				if (world.provider.getDimension() == dimension && world.isBlockLoaded(pos)) {
					final EnumFacing side = NbtUtils.readEnum(tag, "side", EnumFacing.UP);
					clickBlock(world, player, hand, stack, pos, side);
				}
			}
		}
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	private static void clickBlock(World world, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, BlockPos pos, EnumFacing side) {
		if (!world.isAirBlock(pos)) {
			final IBlockState state = world.getBlockState(pos);
			final double distanceToLinkedBlock = player.getDistanceSq(pos);
			if (distanceToLinkedBlock < Config.cursorDistanceLimit) {
				final Block block = state.getBlock();
				if (player.capabilities.isCreativeMode) {
					block.onBlockActivated(world, pos, state, player, hand, heldItem, side, 0, 0, 0);
				} else {
					final int cost = (int)Math.max(0, distanceToLinkedBlock - 10);
					final int playerExperience = EnchantmentUtils.getPlayerXP(player);
					if (cost <= playerExperience) {
						block.onBlockActivated(world, pos, state, player, hand, heldItem, side, 0, 0, 0);
						EnchantmentUtils.addPlayerXP(player, -cost);
					}
				}
			}
		}
	}

}
