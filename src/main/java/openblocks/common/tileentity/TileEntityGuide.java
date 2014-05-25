package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.shapes.GuideShape;
import openmods.api.IActivateAwareTile;
import openmods.shapes.IShapeable;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableInt;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.*;
import openmods.utils.ColorUtils.ColorMeta;

import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityGuide extends SyncedTileEntity implements IShapeable, IActivateAwareTile {

	private Set<Coord> shape;
	private Set<Coord> previousShape;
	private float timeSinceChange = 0;

	protected SyncableInt width;
	protected SyncableInt height;
	protected SyncableInt depth;
	protected SyncableInt mode;
	protected SyncableInt color;

	public TileEntityGuide() {}

	@Override
	protected void createSyncedFields() {
		width = new SyncableInt(8);
		height = new SyncableInt(8);
		depth = new SyncableInt(8);
		mode = new SyncableInt(0);
		color = new SyncableInt(0xFFFFFF);
	}

	public int getWidth() {
		return width.getValue();
	}

	public int getHeight() {
		return height.getValue();
	}

	public int getDepth() {
		return depth.getValue();
	}

	public int getColor() {
		return color.getValue();
	}

	public void setWidth(int w) {
		width.setValue(w);
	}

	public void setDepth(int d) {
		depth.setValue(d);
	}

	public void setHeight(int h) {
		height.setValue(h);
	}

	public GuideShape getCurrentMode() {
		return GuideShape.values()[mode.getValue()];
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote) {
			if (timeSinceChange < 1.0) {
				timeSinceChange = (float)Math.min(1.0f, timeSinceChange + 0.1);
			}
		}
	}

	public float getTimeSinceChange() {
		return timeSinceChange;
	}

	private void recreateShape() {
		previousShape = shape;
		shape = Sets.newHashSet();
		getCurrentMode().generator.generateShape(getWidth(), getHeight(), getDepth(), this);
		timeSinceChange = 0;
	}

	@Override
	public void setBlock(int x, int y, int z) {
		shape.add(new Coord(x, y, z));
	}

	public Set<Coord> getShape() {
		return shape;
	}

	public Set<Coord> getPreviousShape() {
		return previousShape;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB box = super.getRenderBoundingBox();
		return box.expand(getWidth(), getHeight(), getDepth());
	}

	private void switchMode(EntityPlayer player) {
		switchMode();
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.change_mode", getCurrentMode().getLocalizedName()));
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.total_blocks", shape.size()));
	}

	private void switchMode() {
		int nextMode = mode.getValue() + 1;
		if (nextMode >= GuideShape.values().length) {
			nextMode = 0;
		}
		mode.setValue(nextMode);
		if (getCurrentMode().fixedRatio) {
			setHeight(getWidth());
			setDepth(getWidth());
		}
		recreateShape();
		if (!worldObj.isRemote) sync();
	}

	private void changeDimensions(EntityPlayer player, ForgeDirection orientation) {
		changeDimensions(orientation);
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.change_size", width.getValue(), height.getValue(), depth.getValue()));
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.total_blocks", shape.size()));
	}

	private static void inc(SyncableInt v) {
		v.modify(+1);
	}

	private static void dec(SyncableInt v) {
		if (v.getValue() > 0) v.modify(-1);
	}

	private void changeDimensions(ForgeDirection orientation) {
		switch (orientation) {
			case EAST:
				dec(width);
				break;
			case WEST:
				inc(width);
				break;

			case SOUTH:
				dec(depth);
				break;
			case NORTH:
				inc(depth);
				break;

			case DOWN:
				dec(height);
				break;
			case UP:
				inc(height);
				break;

			default:
				return;
		}

		if (getCurrentMode().fixedRatio) {
			int h = getHeight();
			int w = getWidth();
			int d = getDepth();
			if (w != h && w != d) {
				setHeight(w);
				setDepth(w);
			} else if (h != w && h != d) {
				depth.setValue(h);
				width.setValue(h);
			} else if (d != w && d != h) {
				width.setValue(d);
				height.setValue(d);
			}
		}
		recreateShape();
		if (!worldObj.isRemote) sync();
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {
		recreateShape();
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (worldObj.isRemote) return true;

		if (player.isSneaking()) switchMode(player);
		else {
			ItemStack heldStack = player.getHeldItem();
			if (heldStack == null || !tryUseItem(player, heldStack)) changeDimensions(player, ForgeDirection.getOrientation(side));
		}

		return true;
	}

	protected boolean tryUseItem(EntityPlayer player, ItemStack heldStack) {
		if (player.capabilities.isCreativeMode && isInFillMode()) {
			final Item heldItem = heldStack.getItem();
			if (heldItem instanceof ItemBlock) {
				replaceBlocks(heldStack, heldItem);
				return true;
			}
		}

		ColorMeta color = ColorUtils.stackToColor(heldStack);
		if (color != null) {
			changeColor(color.rgb);
			return true;
		}

		return false;
	}

	protected void replaceBlocks(ItemStack heldStack, final Item heldItem) {
		if (shape == null) recreateShape();

		final ItemBlock itemBlock = (ItemBlock)heldItem;
		final Block block = itemBlock.field_150939_a;
		final int blockMeta = itemBlock.getMetadata(heldStack.getItemDamage());
		for (Coord coord : shape)
			worldObj.setBlock(xCoord + coord.x, yCoord + coord.y, zCoord + coord.z, block, blockMeta, BlockNotifyFlags.ALL);
	}

	protected void changeColor(int color) {
		this.color.setValue(color);
		if (!worldObj.isRemote) sync();
	}

	private boolean isInFillMode() {
		return worldObj.getBlock(xCoord, yCoord + 1, zCoord) == Blocks.obsidian;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
}
