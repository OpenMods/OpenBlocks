package openblocks.common.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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

		sneakOnly.add(LeverBlock.class);
		sneakOnly.add(AbstractButtonBlock.class);
		sneakOnly.add(ChestBlock.class);
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, PlayerEntity player) {
		return true;
	}

	private boolean requiresSneaking(final Block block) {
		return sneakOnly.stream().anyMatch(input -> input.isInstance(block));
	}

	@Override
	public ActionResultType onItemUseFirst(PlayerEntity player, World world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, Hand hand) {
		final Block block = world.getBlockState(pos).getBlock();

		if (requiresSneaking(block) && !player.isSneaking()) return ActionResultType.FAIL;

		final Direction[] rotations = block.getValidRotations(world, pos);
		if (ArrayUtils.contains(rotations, side)) {
			if (world.isRemote) return ActionResultType.PASS;
			if (block.rotateBlock(world, pos, side)) return ActionResultType.SUCCESS;
		}

		return ActionResultType.FAIL;
	}

}
