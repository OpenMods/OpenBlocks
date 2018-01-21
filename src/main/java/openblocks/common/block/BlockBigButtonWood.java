package openblocks.common.block;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;

public class BlockBigButtonWood extends BlockBigButton {

	private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0, 0, 0, 1, 0.125, 1);

	private boolean hasStuckArrows(IBlockState state, World world, BlockPos pos) {
		final Orientation orientation = getOrientation(state);
		final AxisAlignedBB checkAabb = BlockSpaceTransform.instance.mapBlockToWorld(orientation, COLLISION_AABB).offset(pos);
		final List<? extends Entity> collidingArrows = world.getEntitiesWithinAABB(EntityArrow.class, checkAabb);

		return !collidingArrows.isEmpty();
	}

	@Override
	protected void updateAfterTimeout(IBlockState state, World world, BlockPos pos) {
		if (hasStuckArrows(state, world, pos)) {
			// don't pop, check again after some time
			scheduleUpdate(world, pos);
		} else {
			pop(state, world, pos);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entityIn) {
		if (!world.isRemote) {
			if (hasStuckArrows(state, world, pos)) {
				push(state, world, pos);
			}
		}
	}

}
