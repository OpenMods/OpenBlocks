package openblocks.common.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockLever;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openmods.infobook.BookDocumentation;
import org.apache.commons.lang3.ArrayUtils;

@BookDocumentation
public class ItemWrench extends Item {

	private final Set<Class<? extends Block>> sneakOnly = Sets.newIdentityHashSet();

	public ItemWrench() {
		setMaxStackSize(1);

		sneakOnly.add(BlockLever.class);
		sneakOnly.add(BlockButton.class);
		sneakOnly.add(BlockChest.class);
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	private boolean requiresSneaking(final Block block) {
		return sneakOnly.stream().anyMatch(input -> input.isInstance(block));
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		final Block block = world.getBlockState(pos).getBlock();

		if (requiresSneaking(block) && !player.isSneaking()) return EnumActionResult.FAIL;

		final EnumFacing[] rotations = block.getValidRotations(world, pos);
		if (ArrayUtils.contains(rotations, side)) {
			if (world.isRemote) return EnumActionResult.PASS;
			if (block.rotateBlock(world, pos, side)) return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.FAIL;
	}

}
