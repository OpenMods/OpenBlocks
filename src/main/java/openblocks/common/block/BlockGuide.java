package openblocks.common.block;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.tileentity.TileEntityGuide;
import openmods.api.ISelectionAware;
import openmods.block.BlockRotationMode;
import openmods.block.OpenBlock;
import openmods.geometry.*;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockGuide extends OpenBlock implements ISelectionAware {

	@Override
	public BlockRotationMode getRotationMode() {
		return BlockRotationMode.THREE_FOUR_DIRECTIONS;
	}

	private AxisAlignedBB selection;

	private static double P = 1.0 / 16.0;

	private static final double SELECTION_BOX_DEPTH = 0.01;

	private interface IShapeManipulator {
		public boolean activate(TileEntityGuide te, EntityPlayerMP player);
	}

	private static IShapeManipulator createHalfAxisIncrementer(EnumFacing dir) {
		final HalfAxis halfAxis = HalfAxis.fromEnumFacing(dir);

		return new IShapeManipulator() {
			@Override
			public boolean activate(TileEntityGuide te, EntityPlayerMP player) {
				return te.incrementHalfAxis(halfAxis, player);
			}
		};
	}

	private static IShapeManipulator createHalfAxisDecrementer(EnumFacing dir) {
		final HalfAxis halfAxis = HalfAxis.fromEnumFacing(dir);

		return new IShapeManipulator() {
			@Override
			public boolean activate(TileEntityGuide te, EntityPlayerMP player) {
				return te.decrementHalfAxis(halfAxis, player);
			}
		};
	}

	private static IShapeManipulator createHalfAxisCopier(EnumFacing dir) {
		final HalfAxis halfAxis = HalfAxis.fromEnumFacing(dir);

		return new IShapeManipulator() {
			@Override
			public boolean activate(TileEntityGuide te, EntityPlayerMP player) {
				return te.copyHalfAxis(halfAxis, halfAxis.negate(), player);
			}
		};
	}

	private IShapeManipulator createRotationManipulator(final HalfAxis ha) {
		return new IShapeManipulator() {
			@Override
			public boolean activate(TileEntityGuide te, EntityPlayerMP player) {
				final World world = te.getWorld();
				final BlockPos pos = te.getPos();
				final IBlockState state = world.getBlockState(pos);
				final PropertyEnum<Orientation> orientationProperty = getRotationMode().property;
				final Orientation orientation = state.getValue(orientationProperty);
				final Orientation newOrientation = orientation.rotateAround(ha);
				world.setBlockState(pos, state.withProperty(orientationProperty, newOrientation));
				return true;
			}
		};
	}

	private static AxisAlignedBB addButton(EnumFacing side, BlockTextureTransform transform, int left, int top, int width, int height) {
		final BlockTextureTransform.WorldCoords min = transform.textureCoordsToWorldVec(side, left * P, top * P, -SELECTION_BOX_DEPTH);
		final BlockTextureTransform.WorldCoords max = transform.textureCoordsToWorldVec(side, (left + width) * P, (top + height) * P, SELECTION_BOX_DEPTH);
		return AabbUtils.createAabb(min.x, min.y, min.z, max.x, max.y, max.z);
	}

	private void createNSWESide(BoundingBoxMap<BoundingBoxMap<IShapeManipulator>> output, EnumFacing face) {
		final BlockTextureTransform transform = getRotationMode().textureTransform;

		final BoundingBoxMap<IShapeManipulator> subBoxes = BoundingBoxMap.create();

		// TODO 1.8.9 verify
		final EnumFacing right = face.rotateYCCW();
		final EnumFacing left = right.getOpposite();

		final EnumFacing top = EnumFacing.UP;
		final EnumFacing bottom = EnumFacing.DOWN;

		subBoxes.addBox(addButton(face, transform, 4, 1, 3, 3), createHalfAxisDecrementer(top));
		subBoxes.addBox(addButton(face, transform, 9, 1, 3, 3), createHalfAxisIncrementer(top));
		subBoxes.addBox(addButton(face, transform, 6, 4, 4, 2), createHalfAxisCopier(top));

		subBoxes.addBox(addButton(face, transform, 12, 4, 3, 3), createHalfAxisDecrementer(right));
		subBoxes.addBox(addButton(face, transform, 12, 9, 3, 3), createHalfAxisIncrementer(right));
		subBoxes.addBox(addButton(face, transform, 10, 6, 2, 4), createHalfAxisCopier(right));

		subBoxes.addBox(addButton(face, transform, 9, 12, 3, 3), createHalfAxisDecrementer(bottom));
		subBoxes.addBox(addButton(face, transform, 4, 12, 3, 3), createHalfAxisIncrementer(bottom));
		subBoxes.addBox(addButton(face, transform, 6, 10, 4, 2), createHalfAxisCopier(bottom));

		subBoxes.addBox(addButton(face, transform, 1, 9, 3, 3), createHalfAxisDecrementer(left));
		subBoxes.addBox(addButton(face, transform, 1, 4, 3, 3), createHalfAxisIncrementer(left));
		subBoxes.addBox(addButton(face, transform, 4, 6, 2, 4), createHalfAxisCopier(left));

		final BlockTextureTransform.WorldCoords overboxMin = transform.textureCoordsToWorldVec(face, P, P, -SELECTION_BOX_DEPTH);
		final BlockTextureTransform.WorldCoords overboxMax = transform.textureCoordsToWorldVec(face, 1 - P, 1 - P, SELECTION_BOX_DEPTH);
		output.addBox(AabbUtils.createAabb(overboxMin.x, overboxMin.y, overboxMin.z, overboxMax.x, overboxMax.y, overboxMax.z), subBoxes);
	}

	private void createTopBottomSide(BoundingBoxMap<BoundingBoxMap<IShapeManipulator>> output, EnumFacing face) {
		final BlockTextureTransform transform = getRotationMode().textureTransform;

		final BoundingBoxMap<IShapeManipulator> subBoxes = BoundingBoxMap.create();

		subBoxes.addBox(addButton(face, transform, 1, 7, 4, 7), createRotationManipulator(HalfAxis.NEG_Y));
		subBoxes.addBox(addButton(face, transform, 11, 7, 4, 7), createRotationManipulator(HalfAxis.POS_Y));

		subBoxes.addBox(addButton(face, transform, 5, 2, 6, 3), new IShapeManipulator() {
			@Override
			public boolean activate(TileEntityGuide te, EntityPlayerMP player) {
				te.incrementMode(player);
				return true;
			}
		});

		subBoxes.addBox(addButton(face, transform, 5, 8, 6, 3), new IShapeManipulator() {
			@Override
			public boolean activate(TileEntityGuide te, EntityPlayerMP player) {
				te.decrementMode(player);
				return true;
			}
		});

		final BlockTextureTransform.WorldCoords overboxMin = transform.textureCoordsToWorldVec(face, P, P, -SELECTION_BOX_DEPTH);
		final BlockTextureTransform.WorldCoords overboxMax = transform.textureCoordsToWorldVec(face, 1 - P, 1 - P, SELECTION_BOX_DEPTH);
		output.addBox(AabbUtils.createAabb(overboxMin.x, overboxMin.y, overboxMin.z, overboxMax.x, overboxMax.y, overboxMax.z), subBoxes);
	}

	private BoundingBoxMap<BoundingBoxMap<IShapeManipulator>> buttons = BoundingBoxMap.create();
	{
		createTopBottomSide(buttons, EnumFacing.UP);
		createNSWESide(buttons, EnumFacing.NORTH);
		createNSWESide(buttons, EnumFacing.SOUTH);
		createNSWESide(buttons, EnumFacing.EAST);
		createNSWESide(buttons, EnumFacing.WEST);
		createTopBottomSide(buttons, EnumFacing.DOWN);
	}

	public BlockGuide() {
		super(Material.rock);
		setLightLevel(0.6f);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.TRANSLUCENT;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
		return selection != null? selection : super.getSelectedBoundingBox(world, pos);
	}

	private Map.Entry<AxisAlignedBB, IShapeManipulator> findClickBox(Vec3 pos) {
		final Entry<AxisAlignedBB, BoundingBoxMap<IShapeManipulator>> sideBox = buttons.findEntryContainingPoint(pos);
		if (sideBox != null) {
			final Entry<AxisAlignedBB, IShapeManipulator> subSideBox = sideBox.getValue().findEntryContainingPoint(pos);
			if (subSideBox != null) return subSideBox;
		}

		return null;
	}

	protected boolean areButtonsActive(EntityPlayer player) {
		return true;
	}

	protected boolean onItemUse(EntityPlayerMP player, TileEntityGuide guide, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public boolean onSelected(World world, BlockPos pos, DrawBlockHighlightEvent evt) {
		if (areButtonsActive(evt.player)) {
			final Vec3 hitVec = evt.target.hitVec;

			final Orientation orientation = getOrientation(world, pos);
			final Vec3 localHit = BlockSpaceTransform.instance.mapWorldToBlock(orientation, hitVec.xCoord - pos.getX(), hitVec.yCoord - pos.getY(), hitVec.zCoord - pos.getZ());
			final Entry<AxisAlignedBB, IShapeManipulator> clickBox = findClickBox(localHit);
			selection = clickBox != null? BlockSpaceTransform.instance.mapBlockToWorld(orientation, clickBox.getKey()).offset(pos.getX(), pos.getY(), pos.getZ()) : null;
		} else selection = null;

		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player instanceof EntityPlayerMP) {
			final TileEntityGuide guide = getTileEntity(world, pos, TileEntityGuide.class);

			if (guide != null) {
				final EntityPlayerMP playerMP = (EntityPlayerMP)player;

				if (areButtonsActive(playerMP)) {
					final Orientation orientation = getOrientation(world, pos);
					final Vec3 localHit = BlockSpaceTransform.instance.mapWorldToBlock(orientation, hitX, hitY, hitZ);
					final Entry<AxisAlignedBB, IShapeManipulator> clickBox = findClickBox(localHit);
					if (clickBox != null) return clickBox.getValue().activate(guide, playerMP);
				}

				final ItemStack heldStack = playerMP.getHeldItem();
				if (heldStack != null) {
					if (guide.onItemUse(playerMP, heldStack, side, hitX, hitY, hitZ)) return true;
				}
			}
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random random) {
		final float x = pos.getX() + 0.5f;
		final float y = pos.getY() + 0.7f;
		final float z = pos.getZ() + 0.5f;

		world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
		world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
	}
}
