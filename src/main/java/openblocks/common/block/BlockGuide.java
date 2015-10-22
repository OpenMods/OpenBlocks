package openblocks.common.block;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.tileentity.TileEntityGuide;
import openmods.api.ISelectionAware;
import openmods.block.BlockRotationMode;
import openmods.geometry.*;
import openmods.infobook.BookDocumentation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation
public class BlockGuide extends OpenBlock implements ISelectionAware {

	private AxisAlignedBB selection;

	private static double P = 1.0 / 16.0;

	private static final double SELECTION_BOX_DEPTH = 0.01;

	private interface IShapeManipulator {
		public boolean activate(TileEntityGuide te, EntityPlayerMP player);
	}

	private static IShapeManipulator createHalfAxisIncrementer(ForgeDirection dir) {
		final HalfAxis halfAxis = HalfAxis.fromDirection(dir);

		return new IShapeManipulator() {
			@Override
			public boolean activate(TileEntityGuide te, EntityPlayerMP player) {
				return te.incrementHalfAxis(halfAxis, player);
			}
		};
	}

	private static IShapeManipulator createHalfAxisDecrementer(ForgeDirection dir) {
		final HalfAxis halfAxis = HalfAxis.fromDirection(dir);

		return new IShapeManipulator() {
			@Override
			public boolean activate(TileEntityGuide te, EntityPlayerMP player) {
				return te.decrementHalfAxis(halfAxis, player);
			}
		};
	}

	private static IShapeManipulator createRotationManipulator(ForgeDirection dir) {
		if (dir == ForgeDirection.UP) {
			return new IShapeManipulator() {
				@Override
				public boolean activate(TileEntityGuide te, EntityPlayerMP player) {
					te.rotateCCW();
					return true;
				}
			};
		} else {
			return new IShapeManipulator() {
				@Override
				public boolean activate(TileEntityGuide te, EntityPlayerMP player) {
					te.rotateCW();
					return true;
				}
			};
		}
	}

	private static AxisAlignedBB addButton(ForgeDirection side, BlockTextureTransform transform, int left, int top, int width, int height) {
		final BlockTextureTransform.WorldCoords min = transform.textureCoordsToWorldVec(side, left * P, top * P, -SELECTION_BOX_DEPTH);
		final BlockTextureTransform.WorldCoords max = transform.textureCoordsToWorldVec(side, (left + width) * P, (top + height) * P, SELECTION_BOX_DEPTH);
		return AabbUtils.createAabb(min.x, min.y, min.z, max.x, max.y, max.z);
	}

	private void createNSWESide(BoundingBoxMap<BoundingBoxMap<IShapeManipulator>> output, ForgeDirection face) {
		final BlockTextureTransform transform = getRotationMode().textureTransform;

		final BoundingBoxMap<IShapeManipulator> subBoxes = BoundingBoxMap.create();

		final ForgeDirection right = face.getRotation(ForgeDirection.DOWN);
		final ForgeDirection left = right.getOpposite();

		final ForgeDirection top = ForgeDirection.UP;
		final ForgeDirection bottom = ForgeDirection.DOWN;

		subBoxes.addBox(addButton(face, transform, 4, 1, 3, 3), createHalfAxisDecrementer(top));
		subBoxes.addBox(addButton(face, transform, 9, 1, 3, 3), createHalfAxisIncrementer(top));

		subBoxes.addBox(addButton(face, transform, 12, 4, 3, 3), createHalfAxisDecrementer(right));
		subBoxes.addBox(addButton(face, transform, 12, 9, 3, 3), createHalfAxisIncrementer(right));

		subBoxes.addBox(addButton(face, transform, 9, 12, 3, 3), createHalfAxisDecrementer(bottom));
		subBoxes.addBox(addButton(face, transform, 4, 12, 3, 3), createHalfAxisIncrementer(bottom));

		subBoxes.addBox(addButton(face, transform, 1, 9, 3, 3), createHalfAxisDecrementer(left));
		subBoxes.addBox(addButton(face, transform, 1, 4, 3, 3), createHalfAxisIncrementer(left));

		final BlockTextureTransform.WorldCoords overboxMin = transform.textureCoordsToWorldVec(face, P, P, -SELECTION_BOX_DEPTH);
		final BlockTextureTransform.WorldCoords overboxMax = transform.textureCoordsToWorldVec(face, 1 - P, 1 - P, SELECTION_BOX_DEPTH);
		output.addBox(AabbUtils.createAabb(overboxMin.x, overboxMin.y, overboxMin.z, overboxMax.x, overboxMax.y, overboxMax.z), subBoxes);
	}

	private void createTopBottomSide(BoundingBoxMap<BoundingBoxMap<IShapeManipulator>> output, ForgeDirection face) {
		final BlockTextureTransform transform = getRotationMode().textureTransform;

		final BoundingBoxMap<IShapeManipulator> subBoxes = BoundingBoxMap.create();

		subBoxes.addBox(addButton(face, transform, 1, 3, 4, 11), createRotationManipulator(face));
		subBoxes.addBox(addButton(face, transform, 11, 3, 4, 11), createRotationManipulator(face.getOpposite()));

		subBoxes.addBox(addButton(face, transform, 5, 3, 6, 3), new IShapeManipulator() {
			@Override
			public boolean activate(TileEntityGuide te, EntityPlayerMP player) {
				te.decrementMode(player);
				return true;
			}
		});

		subBoxes.addBox(addButton(face, transform, 5, 10, 6, 3), new IShapeManipulator() {
			@Override
			public boolean activate(TileEntityGuide te, EntityPlayerMP player) {
				te.incrementMode(player);
				return true;
			}
		});

		final BlockTextureTransform.WorldCoords overboxMin = transform.textureCoordsToWorldVec(face, P, P, -SELECTION_BOX_DEPTH);
		final BlockTextureTransform.WorldCoords overboxMax = transform.textureCoordsToWorldVec(face, 1 - P, 1 - P, SELECTION_BOX_DEPTH);
		output.addBox(AabbUtils.createAabb(overboxMin.x, overboxMin.y, overboxMin.z, overboxMax.x, overboxMax.y, overboxMax.z), subBoxes);
	}

	private BoundingBoxMap<BoundingBoxMap<IShapeManipulator>> buttons = BoundingBoxMap.create();
	{
		createTopBottomSide(buttons, ForgeDirection.UP);
		createNSWESide(buttons, ForgeDirection.NORTH);
		createNSWESide(buttons, ForgeDirection.SOUTH);
		createNSWESide(buttons, ForgeDirection.EAST);
		createNSWESide(buttons, ForgeDirection.WEST);
		createTopBottomSide(buttons, ForgeDirection.DOWN);
	}

	public static class Icons {
		public static IIcon marker;
	}

	public BlockGuide() {
		super(Material.rock);
		setLightLevel(0.6f);
		setRotationMode(BlockRotationMode.THREE_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}

	protected IIcon centerIcon;

	@Override
	public void registerBlockIcons(IIconRegister registry) {
		Icons.marker = registry.registerIcon("openblocks:guide");
		this.centerIcon = registry.registerIcon("openblocks:guide_center_normal");
		setupCommonTextures(registry);
	}

	protected void setupCommonTextures(IIconRegister registry) {
		setTextures(registry.registerIcon("openblocks:guide_top_new"), ForgeDirection.UP, ForgeDirection.DOWN);
		setTextures(registry.registerIcon("openblocks:guide_side_new"), ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST);
		setDefaultTexture(Icons.marker);
	}

	public IIcon getCenterTexture() {
		return centerIcon;
	}

	@Override
	public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return selection != null? selection : super.getSelectedBoundingBoxFromPool(world, x, y, z);
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
	public boolean onSelected(World world, int x, int y, int z, DrawBlockHighlightEvent evt) {
		if (areButtonsActive(evt.player)) {
			final Vec3 hitVec = evt.target.hitVec;

			final int metadata = world.getBlockMetadata(x, y, z);
			final BlockRotationMode rotationMode = getRotationMode();
			final ForgeDirection rotation = rotationMode.fromValue(metadata);
			final Orientation orientation = rotationMode.getBlockOrientation(rotation);
			final Vec3 localHit = BlockSpaceTransform.instance.mapWorldToBlock(orientation, hitVec.xCoord - x, hitVec.yCoord - y, hitVec.zCoord - z);
			final Entry<AxisAlignedBB, IShapeManipulator> clickBox = findClickBox(localHit);
			selection = clickBox != null? BlockSpaceTransform.instance.mapBlockToWorld(orientation, clickBox.getKey()).offset(x, y, z) : null;
		} else selection = null;

		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player instanceof EntityPlayerMP) {
			final TileEntityGuide guide = getTileEntity(world, x, y, z, TileEntityGuide.class);

			if (guide != null) {
				final EntityPlayerMP playerMP = (EntityPlayerMP)player;

				if (areButtonsActive(playerMP)) {
					final int metadata = world.getBlockMetadata(x, y, z);
					final BlockRotationMode rotationMode = getRotationMode();
					final ForgeDirection rotation = rotationMode.fromValue(metadata);
					final Orientation orientation = rotationMode.getBlockOrientation(rotation);
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
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		world.spawnParticle("smoke", x + 0.5f, y + 0.7f, z + 0.5f, 0.0D, 0.0D, 0.0D);
		world.spawnParticle("flame", x + 0.5f, y + 0.7f, z + 0.5f, 0.0D, 0.0D, 0.0D);
	}
}
