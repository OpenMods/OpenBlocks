package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockVacuumHopper extends OpenBlock {
	private static final VoxelShape SELECTION_AABB = VoxelShapes.create(0.3, 0.3, 0.3, 0.7, 0.7, 0.7);
	private static final VoxelShape COLLISION_AABB = VoxelShapes.create(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);
	private static final VoxelShape STANDARD_AABB = VoxelShapes.create(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);

	public BlockVacuumHopper(final Block.Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return STANDARD_AABB;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos) {
		return COLLISION_AABB;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		TileEntityVacuumHopper te = getTileEntity(world, pos, OpenBlocks.TileEntities.vacuumHopper);
		if (te != null) {
			te.onEntityCollidedWithBlock(entity);
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ActionResultType result = super.onBlockActivated(state, world, blockPos, player, hand, hit);

		if (world.isRemote) {
			return ActionResultType.SUCCESS;
		}

		if (player instanceof ServerPlayerEntity && result == ActionResultType.PASS) {
			TileEntityVacuumHopper te = getTileEntity(world, blockPos, OpenBlocks.TileEntities.vacuumHopper);
			if (te != null) {
				NetworkHooks.openGui((ServerPlayerEntity)player, te, blockPos);
				return ActionResultType.SUCCESS;
			}
		}
		return result;
	}
}
