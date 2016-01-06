package openblocks.common.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import openblocks.common.block.BlockFlag;
import openmods.api.IActivateAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.colors.RGB;
import openmods.sync.SyncableByte;
import openmods.sync.SyncableFloat;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;

public class TileEntityFlag extends SyncedTileEntity implements IPlaceAwareTile, IActivateAwareTile {

	private SyncableFloat angle;
	private SyncableByte colorIndex;

	public TileEntityFlag() {}

	@Override
	protected void createSyncedFields() {
		angle = new SyncableFloat();
		colorIndex = new SyncableByte();
	}

	public void setColorIndex(byte index) {
		colorIndex.set(index);
	}

	public void setAngle(float ang) {
		angle.set(ang);
	}

	public RGB getColor() {
		return BlockFlag.COLORS[colorIndex.get() & 0xF];
	}

	public float getAngle() {
		return angle.get();
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player != null && player.isSneaking()) { return true; }
		if (!worldObj.isRemote) {
			if (getOrientation().down() == EnumFacing.DOWN) {
				angle.set(angle.get() + 10f);
				sync();
				return false;
			}
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, ItemStack stack) {
		float ang = placer.rotationYawHead;
		EnumFacing rotation = getOrientation().up();
		if (rotation != EnumFacing.DOWN) {
			ang = -BlockUtils.getRotationFromDirection(rotation);
		}
		setAngle(ang);
		setColorIndex((byte)(stack.getItemDamage() & 0xF));
	}
}
