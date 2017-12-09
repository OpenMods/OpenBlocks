package openblocks.common.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks;
import openblocks.api.IPaintableBlock;
import openblocks.client.renderer.block.canvas.CanvasState;
import openblocks.client.renderer.block.canvas.InnerBlockState;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.block.OpenBlock;
import openmods.colors.ColorMeta;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockCanvas extends OpenBlock implements IPaintableBlock {

	public static class InnerBlockColorHandler implements IBlockColor {

		private final BlockColors blockColors;

		public InnerBlockColorHandler(BlockColors blockColors) {
			this.blockColors = blockColors;
		}

		@Override
		public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
			if (state instanceof IExtendedBlockState) {
				final IExtendedBlockState extendedState = (IExtendedBlockState)state;
				final IBlockState innerState = extendedState.getValue(InnerBlockState.PROPERTY);
				if (innerState != null) return blockColors.colorMultiplier(innerState, worldIn, pos, tintIndex);
			}

			return 0xFFFFFFFF;
		}
	}

	public BlockCanvas() {
		this(Material.SPONGE);
	}

	public BlockCanvas(Material material) {
		super(material);
	}

	public static boolean replaceBlock(World world, BlockPos pos) {
		final IBlockState state = world.getBlockState(pos);

		final Block toReplace = (state.getMaterial() == Material.GLASS)? OpenBlocks.Blocks.canvasGlass : OpenBlocks.Blocks.canvas;
		if (toReplace == null) return false;
		if (state.getBlock() == toReplace) return true;

		world.setBlockState(pos, toReplace.getDefaultState());

		final TileEntityCanvas tile = getTileEntity(world, pos, TileEntityCanvas.class);
		if (tile != null) tile.setPaintedBlock(state);
		return true;
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, int color) {
		final TileEntityCanvas te = getTileEntity(world, pos, TileEntityCanvas.class);
		return te != null? te.applyPaint(0xFF000000 | color, side) : false;
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor colour) {
		ColorMeta color = ColorMeta.fromVanillaEnum(colour);
		return recolorBlock(world, pos, side, color.rgb);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this,
				new IProperty[] { getPropertyOrientation() },
				new IUnlistedProperty[] { CanvasState.PROPERTY, InnerBlockState.PROPERTY });
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		final IExtendedBlockState extendedState = (IExtendedBlockState)super.getExtendedState(state, world, pos);
		final TileEntityCanvas te = getTileEntity(world, pos, TileEntityCanvas.class);

		if (te != null) {
			return extendedState
					.withProperty(CanvasState.PROPERTY, te.getCanvasState())
					.withProperty(InnerBlockState.PROPERTY, te.getActualPaintedBlockState());
		} else {
			return extendedState;
		}
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	private interface IBlockPropertyGetter<T> {
		T get(IBlockState state, IBlockAccess world, BlockPos pos);
	}

	private static <T> T getPaintedBlockProperty(IBlockAccess world, @Nullable IBlockState state, BlockPos pos, IBlockPropertyGetter<T> getter, IBlockPropertyGetter<T> defaultValue) {
		final TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityCanvas) {
			final IBlockState paintedBlockState = ((TileEntityCanvas)te).getPaintedBlockState();
			if (paintedBlockState != Blocks.AIR.getDefaultState()) {
				final World actualWorld = te.getWorld();
				final TileEntityCanvas.UnpackingBlockAccess fakedWorldAccess = new TileEntityCanvas.UnpackingBlockAccess(actualWorld);
				if (actualWorld != null) {
					try {
						return getter.get(paintedBlockState, fakedWorldAccess, pos);
					} catch (Exception e) {
						// NO-OP, best effort
					}
				}
			}
		}

		return defaultValue.get(state, world, pos);
	}

	private static <T> T getPaintedBlockProperty(IBlockAccess world, IBlockState state, BlockPos pos, IBlockPropertyGetter<T> getter, final T defaultValue) {
		return getPaintedBlockProperty(world, state, pos, getter, new IBlockPropertyGetter<T>() {
			@Override
			public T get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return defaultValue;
			}
		});
	}

	private interface IWorldBlockPropertyGetter<T> {
		T get(IBlockState state, World world, BlockPos pos);
	}

	// NOTE is possible, every user of this method should be migrated to IBlockAccess version, once API changed
	private static <T> T getPaintedBlockProperty(World world, IBlockState state, BlockPos pos, IWorldBlockPropertyGetter<T> getter, IWorldBlockPropertyGetter<T> defaultValue) {
		final TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityCanvas) {
			final IBlockState paintedBlockState = ((TileEntityCanvas)te).getPaintedBlockState();
			if (paintedBlockState != Blocks.AIR.getDefaultState()) {
				try {
					return getter.get(paintedBlockState, world, pos);
				} catch (Exception e) {
					// NO-OP, best effort
				}
			}
		}

		return defaultValue.get(state, world, pos);
	}

	private static <T> T getPaintedBlockProperty(World world, IBlockState state, BlockPos pos, IWorldBlockPropertyGetter<T> getter, final T defaultValue) {
		return getPaintedBlockProperty(world, state, pos, getter, new IWorldBlockPropertyGetter<T>() {
			@Override
			public T get(IBlockState state, World world, BlockPos pos) {
				return defaultValue;
			}
		});
	}

	@Override
	public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
		return getPaintedBlockProperty(world, state, pos, new IBlockPropertyGetter<Integer>() {
			@Override
			public Integer get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.getLightOpacity(world, pos);
			}
		}, this.lightOpacity);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return getPaintedBlockProperty(world, state, pos, new IBlockPropertyGetter<Integer>() {
			@Override
			public Integer get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.getLightValue(world, pos);
			}
		}, this.lightValue);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
		return getPaintedBlockProperty(source, state, pos, new IBlockPropertyGetter<Integer>() {
			@Override
			public Integer get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.getPackedLightmapCoords(world, pos);
			}
		},
				new IBlockPropertyGetter<Integer>() {
					@Override
					@SuppressWarnings("deprecation")
					public Integer get(IBlockState state, IBlockAccess world, BlockPos pos) {
						return BlockCanvas.super.getPackedLightmapCoords(state, world, pos);
					}
				});
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, final EnumFacing side) {
		return getPaintedBlockProperty(blockAccess, blockState, pos, new IBlockPropertyGetter<Integer>() {
			@Override
			public Integer get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.getWeakPower(world, pos, side);
			}
		}, 0);
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, final EnumFacing side) {
		return getPaintedBlockProperty(blockAccess, blockState, pos, new IBlockPropertyGetter<Integer>() {
			@Override
			public Integer get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.getStrongPower(world, pos, side);
			}
		}, 0);
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		return getPaintedBlockProperty(worldIn, blockState, pos, new IWorldBlockPropertyGetter<Integer>() {
			@Override
			public Integer get(IBlockState state, World world, BlockPos pos) {
				return state.getComparatorInputOverride(world, pos);
			}
		}, 0);
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		return getPaintedBlockProperty(worldIn, blockState, pos, new IWorldBlockPropertyGetter<Float>() {
			@Override
			public Float get(IBlockState state, World world, BlockPos pos) {
				return state.getBlockHardness(world, pos);
			}
		}, this.blockHardness);
	}

	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, final EntityPlayer player, World worldIn, BlockPos pos) {
		return getPaintedBlockProperty(worldIn, state, pos, new IWorldBlockPropertyGetter<Float>() {
			@Override
			public Float get(IBlockState state, World world, BlockPos pos) {
				return state.getPlayerRelativeBlockHardness(player, world, pos);
			}
		}, new IWorldBlockPropertyGetter<Float>() {
			@Override
			@SuppressWarnings("deprecation")
			public Float get(IBlockState state, World world, BlockPos pos) {
				return BlockCanvas.super.getPlayerRelativeBlockHardness(state, player, world, pos);
			}
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return getPaintedBlockProperty(worldIn, state, pos, new IWorldBlockPropertyGetter<AxisAlignedBB>() {
			@Override
			public AxisAlignedBB get(IBlockState state, World world, BlockPos pos) {
				return state.getSelectedBoundingBox(world, pos);
			}
		}, new IWorldBlockPropertyGetter<AxisAlignedBB>() {
			@Override
			public AxisAlignedBB get(IBlockState state, World world, BlockPos pos) {
				return FULL_BLOCK_AABB.offset(pos);
			}
		});
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return getPaintedBlockProperty(worldIn, blockState, pos, new IBlockPropertyGetter<AxisAlignedBB>() {
			@Override
			public AxisAlignedBB get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.getCollisionBoundingBox(world, pos);
			}
		}, FULL_BLOCK_AABB);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return getPaintedBlockProperty(source, state, pos, new IBlockPropertyGetter<AxisAlignedBB>() {
			@Override
			public AxisAlignedBB get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.getBoundingBox(world, pos);
			}
		}, FULL_BLOCK_AABB);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, final AxisAlignedBB entityBox, final List<AxisAlignedBB> collidingBoxes, final @Nullable Entity entityIn, final boolean p_185477_7_) {
		getPaintedBlockProperty(worldIn, state, pos, new IWorldBlockPropertyGetter<Void>() {
			@Override
			public Void get(IBlockState state, World world, BlockPos pos) {
				state.addCollisionBoxToList(world, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
				return null;
			}
		}, new IWorldBlockPropertyGetter<Void>() {
			@Override
			public Void get(IBlockState state, World world, BlockPos pos) {
				addCollisionBoxToList(pos, entityBox, collidingBoxes, FULL_BLOCK_AABB);
				return null;
			}
		});
	}

	@Override
	@Nullable
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, final Vec3d start, final Vec3d end) {
		return getPaintedBlockProperty(worldIn, blockState, pos, new IWorldBlockPropertyGetter<RayTraceResult>() {
			@Override
			public RayTraceResult get(IBlockState state, World world, BlockPos pos) {
				return state.collisionRayTrace(world, pos, start, end);
			}
		}, new IWorldBlockPropertyGetter<RayTraceResult>() {
			@Override
			public RayTraceResult get(IBlockState state, World world, BlockPos pos) {
				return rayTrace(pos, start, end, FULL_BLOCK_AABB);
			}
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, final EnumFacing side) {
		return getPaintedBlockProperty(blockAccess, blockState, pos, new IBlockPropertyGetter<Boolean>() {
			@Override
			public Boolean get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.shouldSideBeRendered(world, pos, side);
			}
		}, true);
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, final EnumFacing face) {
		return getPaintedBlockProperty(world, state, pos, new IBlockPropertyGetter<Boolean>() {
			@Override
			public Boolean get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.doesSideBlockRendering(world, pos, face);
			}
		}, Boolean.TRUE);
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, final EnumFacing side) {
		return getPaintedBlockProperty(world, base_state, pos, new IBlockPropertyGetter<Boolean>() {
			@Override
			public Boolean get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.isSideSolid(world, pos, side);
			}
		}, Boolean.TRUE);
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return getPaintedBlockProperty(worldIn, null, pos, new IBlockPropertyGetter<Boolean>() {
			@Override
			public Boolean get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.getBlock().isPassable(world, pos);
			}
		}, Boolean.FALSE);
	}

	@Override
	public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
		return getPaintedBlockProperty(world, state, pos, new IBlockPropertyGetter<Boolean>() {
			@Override
			public Boolean get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.getBlock().canSustainLeaves(state, world, pos);
			}
		}, Boolean.FALSE);
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, final EnumFacing direction, final IPlantable plantable) {
		return getPaintedBlockProperty(world, state, pos, new IBlockPropertyGetter<Boolean>() {
			@Override
			public Boolean get(IBlockState state, IBlockAccess world, BlockPos pos) {
				// NOTE: maybe only unpainted sides?
				return state.getBlock().canSustainPlant(state, world, pos, direction, plantable);
			}
		}, Boolean.FALSE);
	}

	@Override
	public float getEnchantPowerBonus(World world, BlockPos pos) {
		return getPaintedBlockProperty(world, null, pos, new IWorldBlockPropertyGetter<Float>() {
			@Override
			public Float get(IBlockState state, World world, BlockPos pos) {
				return state.getBlock().getEnchantPowerBonus(world, pos);
			}
		}, 0.0f);
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, final Entity exploder, final Explosion explosion) {
		return getPaintedBlockProperty(world, null, pos, new IWorldBlockPropertyGetter<Float>() {
			@Override
			public Float get(IBlockState state, World world, BlockPos pos) {
				return state.getBlock().getExplosionResistance(world, pos, exploder, explosion);
			}
		}, super.getExplosionResistance(world, pos, exploder, explosion));
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, final EnumFacing face) {
		return getPaintedBlockProperty(world, null, pos, new IBlockPropertyGetter<Integer>() {
			@Override
			public Integer get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.getBlock().getFlammability(world, pos, face);
			}
		}, new IBlockPropertyGetter<Integer>() {
			@Override
			public Integer get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return BlockCanvas.super.getFlammability(world, pos, face);
			}
		});
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, final EnumFacing face) {
		return getPaintedBlockProperty(world, null, pos, new IBlockPropertyGetter<Integer>() {
			@Override
			public Integer get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.getBlock().getFireSpreadSpeed(world, pos, face);
			}
		}, 200);
	}

	@Override
	public boolean isFlammable(IBlockAccess world, BlockPos pos, final EnumFacing face) {
		return getPaintedBlockProperty(world, null, pos, new IBlockPropertyGetter<Boolean>() {
			@Override
			public Boolean get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.getBlock().isFlammable(world, pos, face);
			}
		}, Boolean.TRUE);
	}

	@Override
	public boolean isBurning(IBlockAccess world, BlockPos pos) {
		return getPaintedBlockProperty(world, null, pos, new IBlockPropertyGetter<Boolean>() {
			@Override
			public Boolean get(IBlockState state, IBlockAccess world, BlockPos pos) {
				return state.getBlock().isBurning(world, pos);
			}
		}, Boolean.FALSE);
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, final EnumFacing side) {
		return getPaintedBlockProperty(world, null, pos, new IWorldBlockPropertyGetter<Boolean>() {
			@Override
			public Boolean get(IBlockState state, World world, BlockPos pos) {
				return state.getBlock().isFireSource(world, pos, side);
			}
		}, Boolean.FALSE);
	}

	@Override
	public boolean isFertile(World world, BlockPos pos) {
		return getPaintedBlockProperty(world, null, pos, new IWorldBlockPropertyGetter<Boolean>() {
			@Override
			public Boolean get(IBlockState state, World world, BlockPos pos) {
				return state.getBlock().isFertile(world, pos);
			}
		}, Boolean.FALSE);
	}

	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, final Entity entityIn, final float fallDistance) {
		getPaintedBlockProperty(worldIn, null, pos, new IWorldBlockPropertyGetter<Void>() {
			@Override
			public Void get(IBlockState state, World world, BlockPos pos) {
				state.getBlock().onFallenUpon(world, pos, entityIn, fallDistance);
				return null;
			}
		}, new IWorldBlockPropertyGetter<Void>() {
			@Override
			public Void get(IBlockState state, World world, BlockPos pos) {
				BlockCanvas.super.onFallenUpon(world, pos, entityIn, fallDistance);
				return null;
			}
		});
	}

	@Override
	public void onLanded(World worldIn, final Entity entityIn) {
		final BlockPos pos = new BlockPos(entityIn.posX, entityIn.posY - 0.2, entityIn.posZ);
		getPaintedBlockProperty(worldIn, null, pos, new IWorldBlockPropertyGetter<Void>() {
			@Override
			public Void get(IBlockState state, World world, BlockPos pos) {
				state.getBlock().onLanded(world, entityIn);
				return null;
			}
		}, new IWorldBlockPropertyGetter<Void>() {
			@Override
			public Void get(IBlockState state, World world, BlockPos pos) {
				BlockCanvas.super.onLanded(world, entityIn);
				return null;
			}
		});
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, final Entity entityIn) {
		getPaintedBlockProperty(worldIn, null, pos, new IWorldBlockPropertyGetter<Void>() {
			@Override
			public Void get(IBlockState state, World world, BlockPos pos) {
				state.getBlock().onEntityWalk(world, pos, entityIn);
				return null;
			}
		}, new IWorldBlockPropertyGetter<Void>() {
			@Override
			public Void get(IBlockState state, World world, BlockPos pos) {
				BlockCanvas.super.onEntityWalk(world, pos, entityIn);
				return null;
			}
		});
	}
}
