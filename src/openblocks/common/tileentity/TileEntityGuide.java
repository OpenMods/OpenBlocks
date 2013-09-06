package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Log;
import openblocks.api.IShapeProvider;
import openblocks.shapes.IShapeable;
import openblocks.shapes.ShapeFactory;
import openblocks.shapes.ShapeFactory.Mode;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableInt;
import openblocks.utils.CompatibilityUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityGuide extends NetworkedTileEntity implements IShapeable,
		IShapeProvider {

	private boolean shape[][][];
	private boolean previousShape[][][];
	private float timeSinceChange = 0;

	protected SyncableInt width = new SyncableInt(8);
	protected SyncableInt height = new SyncableInt(8);
	protected SyncableInt depth = new SyncableInt(8);
	protected SyncableInt mode = new SyncableInt(0);

	public enum Keys {
		width, height, depth, mode
	}

	public TileEntityGuide() {
		addSyncedObject(Keys.width, width);
		addSyncedObject(Keys.height, height);
		addSyncedObject(Keys.depth, depth);
		addSyncedObject(Keys.mode, mode);
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

	public void setWidth(int w) {
		width.setValue(w);
	}

	public void setDepth(int d) {
		depth.setValue(d);
	}

	public void setHeight(int h) {
		height.setValue(h);
	}

	public Mode getCurrentMode() {
		return Mode.values()[mode.getValue()];
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
		shape = new boolean[getHeight() * 2 + 1][getWidth() * 2 + 1][getDepth() * 2 + 1];
		ShapeFactory.generateShape(getWidth(), getHeight(), getDepth(), this, getCurrentMode());
		timeSinceChange = 0;
	}

	@Override
	public void setBlock(int x, int y, int z) {
		try {
			shape[getHeight() + y][getWidth() + x][getDepth() + z] = true;
		} catch (IndexOutOfBoundsException iobe) {
			Log.warn(iobe, "Index out of bounds setting block at %d,%d,%d", x, y, z);
		}
	}

	public boolean[][][] getShape() {
		return shape;
	}

	public boolean[][][] getPreviousShape() {
		return previousShape;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		width.readFromNBT(tag, "width");
		height.readFromNBT(tag, "height");
		depth.readFromNBT(tag, "depth");
		mode.readFromNBT(tag, "mode");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		width.writeToNBT(tag, "width");
		height.writeToNBT(tag, "height");
		depth.writeToNBT(tag, "depth");
		mode.writeToNBT(tag, "mode");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB box = super.getRenderBoundingBox();
		return box.expand(getWidth(), getHeight(), getDepth());
	}

	public void switchMode(EntityPlayer player) {
		switchMode();
		if (player != null) {
			CompatibilityUtils.sendChatToPlayer(player, String.format("Changing to %s mode", getCurrentMode().getDisplayName()));
		}
	}

	public void switchMode() {
		int nextMode = mode.getValue() + 1;
		if (nextMode >= Mode.values().length) {
			nextMode = 0;
		}
		mode.setValue(nextMode);
		if (getCurrentMode().isFixedRatio()) {
			setHeight(getWidth());
			setDepth(getWidth());
		}
		recreateShape();
		sync();
	}

	public void changeDimensions(EntityPlayer player, ForgeDirection orientation) {
		changeDimensions(orientation);
		CompatibilityUtils.sendChatToPlayer(player, String.format("Changing size to %sx%sx%s", width.getValue(), height.getValue(), depth.getValue()));
	}

	public void changeDimensions(ForgeDirection orientation) {
		if (width.getValue() > 0 && orientation == ForgeDirection.EAST) {
			width.modify(-1);
		} else if (orientation == ForgeDirection.WEST) {
			width.modify(1);
		} else if (orientation == ForgeDirection.NORTH) {
			depth.modify(1);
		} else if (depth.getValue() > 0 && orientation == ForgeDirection.SOUTH) {
			depth.modify(-1);
		} else if (orientation == ForgeDirection.UP) {
			height.modify(1);
		} else if (height.getValue() > 0 && orientation == ForgeDirection.DOWN) {
			height.modify(-1);
		}
		if (getCurrentMode().isFixedRatio()) {
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
		sync();
	}

	@Override
	public ChunkCoordinates[] getShapeCoordinates() {
		if (shape == null) {
			recreateShape();
		}
		ArrayList<ChunkCoordinates> coords = new ArrayList<ChunkCoordinates>();
		if (shape != null) {
			for (int y2 = 0; y2 < shape.length; y2++) {
				for (int x2 = 0; x2 < shape[y2].length; x2++) {
					for (int z2 = 0; z2 < shape[y2][x2].length; z2++) {
						if (shape[y2][x2][z2]) {
							coords.add(new ChunkCoordinates(xCoord + x2 - getWidth(), yCoord
									+ y2 - getHeight(), zCoord + z2 - getDepth()));
						}
					}
				}
			}
		}
		return coords.toArray(new ChunkCoordinates[coords.size()]);
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		recreateShape();
	}

	public void fill(EntityPlayer player) {
		if (player.getHeldItem() != null) {
			ItemStack held = player.getHeldItem();
			if (held.getItem() instanceof ItemBlock) {
				ItemBlock itemblock = (ItemBlock) held.getItem();
				for (ChunkCoordinates coord : getShapeCoordinates()) {
					worldObj.setBlock(coord.posX, coord.posY, coord.posZ, itemblock.getBlockID());
				}
			}
		}
	}

}
