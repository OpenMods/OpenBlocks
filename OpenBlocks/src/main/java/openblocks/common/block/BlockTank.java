package openblocks.common.block;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemTankBlock;
import openblocks.common.tileentity.TileEntityTank;
import openmods.Log;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockTank extends OpenBlock {
	private final Set<Vector3i> OFFSETS = ImmutableSet.of(
			new Vector3i(-1, -1, 0),
			new Vector3i(-1, +1, 0),
			new Vector3i(+1, -1, 0),
			new Vector3i(+1, +1, 0),

			new Vector3i(-1, 0, -1),
			new Vector3i(+1, 0, -1),
			new Vector3i(+1, 0, +1),
			new Vector3i(-1, 0, +1),

			new Vector3i(0, -1, -1),
			new Vector3i(0, -1, +1),
			new Vector3i(0, +1, +1),
			new Vector3i(0, +1, -1)
	);

	public BlockTank(final Block.Properties properties) {
		super(properties);
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		if (!Config.tanksEmitLight) {
			return 0;
		}

		TileEntityTank tile = getTileEntity(world, pos, TileEntityTank.class);
		return tile != null? tile.getFluidLightLevel() : 0;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		ItemStack result = new ItemStack(this);
		TileEntityTank tile = getTileEntity(world, pos, TileEntityTank.class);
		if (tile != null) {
			IFluidTank tank = tile.getTank();
			if (tank.getFluidAmount() > 0) {
				CompoundNBT tankTag = tile.getItemNBT();
				if (tankTag.contains("Amount")) {
					tankTag.putInt("Amount", tank.getCapacity());
				}

				CompoundNBT nbt = result.getOrCreateTag();
				nbt.put("tank", tankTag);
			}
		}
		return result;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		TileEntityTank tile = getTileEntity(world, pos, TileEntityTank.class);
		double value = tile.getFluidRatio() * 15;
		if (value == 0) {
			return 0;
		}
		int trunc = MathHelper.floor(value);
		return Math.max(trunc, 1);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		if (facingState.isAir() || facingState.isIn(OpenBlocks.Blocks.tank)) {
			TileEntityTank tile = getTileEntity(world, pos, TileEntityTank.class);
			if (tile != null) {
				tile.neighborChanged();
			}
		}
		return super.updatePostPlacement(stateIn, facing, facingState, world, pos, facingPos);
	}

	@Override
	public void updateDiagonalNeighbors(BlockState state, IWorld worldIn, BlockPos pos, int flags, int recursionLeft) {
		final BlockPos.Mutable cursor = new BlockPos.Mutable();
		for (final Vector3i offset : OFFSETS) {
			final BlockState diagonal = worldIn.getBlockState(cursor.setAndOffset(pos, offset.getX(), offset.getY(), offset.getZ()));
			if (diagonal.isIn(OpenBlocks.Blocks.tank)) {
				TileEntityTank tile = getTileEntity(worldIn, cursor, TileEntityTank.class);
				if (tile != null) {
					tile.neighborChanged();
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> result) {
		result.add(new ItemStack(this));

		if (tab == ItemGroup.SEARCH.SEARCH && Config.displayAllFilledTanks) {
			final ItemStack emptyTank = new ItemStack(this);
			for (Fluid fluid : ForgeRegistries.FLUIDS) {
				FluidState state = fluid.getDefaultState();
				if (state.isSource()) {
					try {
						final ItemStack tankStack = emptyTank.copy();
						if (ItemTankBlock.fillTankItem(tankStack, fluid)) {
							result.add(tankStack);
						} else {
							Log.debug("Failed to create filled tank stack for fluid '%s'. Not registered?", fluid.getRegistryName());
						}
					} catch (Throwable t) {
						throw new RuntimeException(String.format("Failed to create item for fluid '%s'. Until this is fixed, you can bypass this code with config option 'tanks.displayAllFluids'",
								fluid.getRegistryName()), t);
					}
				}
			}
		}
	}

}
