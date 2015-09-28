package openblocks.common.block;

import static openblocks.common.tileentity.TileEntityGuide.ShapeManipulator.*;

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
import openblocks.common.tileentity.TileEntityGuide.ShapeManipulator;
import openmods.api.ISelectionAware;
import openmods.geometry.AabbUtils;
import openmods.geometry.BlockTextureTransform;
import openmods.geometry.BoundingBoxMap;
import openmods.infobook.BookDocumentation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation
public class BlockGuide extends OpenBlock implements ISelectionAware {

	private AxisAlignedBB selection;

	private static double P = 1.0 / 16.0;

	private static final double SELECTION_BOX_DEPTH = 0.01;

	private static void addButton(BlockTextureTransform transform, BoundingBoxMap<ShapeManipulator> output, int left, int top, int right, int bottom, ShapeManipulator action) {
		final BlockTextureTransform.WorldCoords min = transform.textureCoordsToWorldVec(left * P, top * P, -SELECTION_BOX_DEPTH);
		final BlockTextureTransform.WorldCoords max = transform.textureCoordsToWorldVec(right * P, bottom * P, SELECTION_BOX_DEPTH);
		output.addBox(AabbUtils.createAabb(min.x, min.y, min.z, max.x, max.y, max.z), action);
	}

	private static void createSide(BoundingBoxMap<BoundingBoxMap<ShapeManipulator>> output, ForgeDirection face, ShapeManipulator middleClick, ShapeManipulator horMinClick, ShapeManipulator horPlusClick, ShapeManipulator vertMinClick, ShapeManipulator vertPlusClick) {
		final BlockTextureTransform transform = new BlockTextureTransform(face);

		final BoundingBoxMap<ShapeManipulator> subBoxes = BoundingBoxMap.create();

		addButton(transform, subBoxes, 1, 6, 5, 10, horPlusClick);
		addButton(transform, subBoxes, 6, 1, 10, 5, vertMinClick);
		addButton(transform, subBoxes, 11, 6, 15, 10, horMinClick);
		addButton(transform, subBoxes, 6, 11, 10, 15, vertPlusClick);

		addButton(transform, subBoxes, 6, 6, 10, 10, middleClick);

		final BlockTextureTransform.WorldCoords overboxMin = transform.textureCoordsToWorldVec(P, P, -SELECTION_BOX_DEPTH);
		final BlockTextureTransform.WorldCoords overboxMax = transform.textureCoordsToWorldVec(1 - P, 1 - P, SELECTION_BOX_DEPTH);
		output.addBox(AabbUtils.createAabb(overboxMin.x, overboxMin.y, overboxMin.z, overboxMax.x, overboxMax.y, overboxMax.z), subBoxes);

	}

	private BoundingBoxMap<BoundingBoxMap<ShapeManipulator>> buttons = BoundingBoxMap.create();
	{
		createSide(buttons, ForgeDirection.NORTH, MIDDLE, X_MINUS, X_PLUS, Y_MINUS, Y_PLUS);
		createSide(buttons, ForgeDirection.SOUTH, MIDDLE, X_MINUS, X_PLUS, Y_MINUS, Y_PLUS);

		createSide(buttons, ForgeDirection.EAST, MIDDLE, Z_MINUS, Z_PLUS, Y_MINUS, Y_PLUS);
		createSide(buttons, ForgeDirection.WEST, MIDDLE, Z_MINUS, Z_PLUS, Y_MINUS, Y_PLUS);

		createSide(buttons, ForgeDirection.UP, MIDDLE, X_MINUS, X_PLUS, Z_MINUS, Z_PLUS);
		createSide(buttons, ForgeDirection.DOWN, MIDDLE, X_MINUS, X_PLUS, Z_MINUS, Z_PLUS);
	}

	public static class Icons {
		public static IIcon marker;
	}

	public BlockGuide() {
		super(Material.rock);
		setLightLevel(0.6f);
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
		this.blockIcon = registry.registerIcon("openblocks:guide_new");
		this.centerIcon = registry.registerIcon("openblocks:guide_center_normal");
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

	private Map.Entry<AxisAlignedBB, ShapeManipulator> findClickBox(Vec3 pos) {
		final Entry<AxisAlignedBB, BoundingBoxMap<ShapeManipulator>> sideBox = buttons.findEntryContainingPoint(pos);
		if (sideBox != null) {
			final Entry<AxisAlignedBB, ShapeManipulator> subSideBox = sideBox.getValue().findEntryContainingPoint(pos);
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
			final Vec3 relPos = hitVec.addVector(-x, -y, -z);

			final Entry<AxisAlignedBB, ShapeManipulator> clickBox = findClickBox(relPos);
			selection = clickBox != null? clickBox.getKey().getOffsetBoundingBox(x, y, z) : null;
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
					final Vec3 relPos = Vec3.createVectorHelper(hitX, hitY, hitZ);
					final Entry<AxisAlignedBB, ShapeManipulator> clickBox = findClickBox(relPos);
					if (clickBox != null) return clickBox.getValue().activate(playerMP, guide);
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
