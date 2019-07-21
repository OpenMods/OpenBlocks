package openblocks.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.Config;
import openmods.Log;
import openmods.block.OpenBlock;
import openmods.utils.BlockNotifyFlags;
import org.apache.logging.log4j.Level;

public class BlockGrave extends OpenBlock.FourDirections {

	public static final IProperty<Boolean> HAS_BASE = PropertyBool.create("base");

	private static final int MASK_HAS_BASE = 0x8;

	public BlockGrave() {
		super(Material.GROUND);
		setResistance(2000.0F);
		setDefaultState(getDefaultState().withProperty(HAS_BASE, true));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getPropertyOrientation(), HAS_BASE);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta)
				.withProperty(HAS_BASE, (meta & MASK_HAS_BASE) != 0);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return super.getMetaFromState(state) | (state.getValue(HAS_BASE)? MASK_HAS_BASE : 0);
	}

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.2, 1.0);

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 0;
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}

	@Override
	public boolean canEntityDestroy(BlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
		return false;
	}

	private static Level debugLevel() {
		return Config.debugGraves? Level.INFO : Level.DEBUG;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, BlockState state) {
		super.breakBlock(world, pos, state);
		Log.log(debugLevel(), "Grave @ (%s) dimension = %d destroyed", pos, world.provider.getDimension());
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
		Log.log(debugLevel(), "Grave @ (%s) dimension = %d destroyed by player %s", pos, world.provider.getDimension(), player);
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosionIn) {
		super.onBlockDestroyedByExplosion(world, pos, explosionIn);
		Log.log(debugLevel(), "Grave @ (%s) dimension = %d destroyed by explosion", pos, world.provider.getDimension());
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer)
				.withProperty(HAS_BASE, hasBase(world, pos));
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos blockPos, Block neighbour, BlockPos neighbourPos) {
		if (neighbourPos.equals(blockPos.down())) {
			final BlockState newState = state.withProperty(HAS_BASE, hasBase(world, blockPos));
			if (newState != state)
				world.setBlockState(blockPos, newState, BlockNotifyFlags.SEND_TO_CLIENTS);
			super.neighborChanged(newState, world, blockPos, neighbour, neighbourPos);
		} else {
			super.neighborChanged(state, world, blockPos, neighbour, neighbourPos);
		}
	}

	protected boolean hasBase(IBlockAccess world, BlockPos pos) {
		final BlockState block = world.getBlockState(pos.down());
		final Material material = block.getMaterial();
		return material == Material.GRASS || material == Material.GROUND;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
		return face == Direction.DOWN? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}
}
