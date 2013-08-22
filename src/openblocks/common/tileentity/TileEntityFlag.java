package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.api.IAwareTile;
import openblocks.common.api.ISurfaceAttachment;
import openblocks.common.block.BlockFlag;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableFloat;
import openblocks.sync.SyncableInt;
import openblocks.utils.BlockUtils;

public class TileEntityFlag extends NetworkedTileEntity implements
		ISurfaceAttachment, IAwareTile {

	public enum Keys {
		angle, colorIndex
	}

	private SyncableFloat angle = new SyncableFloat(0.0f);
	private SyncableInt colorIndex = new SyncableInt(0);

	public TileEntityFlag() {
		addSyncedObject(Keys.angle, angle);
		addSyncedObject(Keys.colorIndex, colorIndex);
	}

	@Override
	protected void initialize() {}

	@Override
	public void onSynced(List<ISyncableObject> changes) {}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		colorIndex.readFromNBT(tag, "color");
		angle.readFromNBT(tag, "angle");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		colorIndex.writeToNBT(tag, "color");
		angle.writeToNBT(tag, "angle");
	}

	public Icon getIcon() {
		return OpenBlocks.Blocks.flag.getIcon(0, 0);
	}

	public void setColorIndex(int index) {
		colorIndex.setValue(index);
	}

	public void setAngle(float ang) {
		angle.setValue(ang);
	}

	public void setOnGround(boolean onGround) {
		setFlag1(onGround);
	}

	public boolean isOnGround() {
		return getFlag1();
	}

	public int getColor() {
		if (colorIndex.getValue() >= BlockFlag.COLORS.length) colorIndex.setValue(0);
		return BlockFlag.COLORS[colorIndex.getValue()];
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		ForgeDirection rotation;
		if (getFlag1()) {
			rotation = ForgeDirection.DOWN;
		} else {
			rotation = getRotation();
		}
		return rotation;
	}

	public float getAngle() {
		return angle.getValue();
	}

	@Override
	public void onBlockBroken() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockAdded() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player != null && player.isSneaking()) { return true; }
		if (!worldObj.isRemote) {
			if (getSurfaceDirection() == ForgeDirection.DOWN) {
				angle.setValue(angle.getValue() + 10f);
				sync();
				return false;
			}
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		float ang = player.rotationYawHead;
		ForgeDirection surface = side.getOpposite();

		if (surface != ForgeDirection.DOWN) {
			ang = -BlockUtils.getRotationFromDirection(side.getOpposite());
		}

		setAngle(ang);
		setColorIndex(stack.getItemDamage());
		setRotation(side.getOpposite());
		setOnGround(surface == ForgeDirection.DOWN);
		sync();
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		// TODO Auto-generated method stub
		return false;
	}

}
