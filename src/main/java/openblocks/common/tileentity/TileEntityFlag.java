package openblocks.common.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import openmods.api.IActivateAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.colors.ColorMeta;
import openmods.sync.SyncableEnum;
import openmods.sync.SyncableFloat;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;

public class TileEntityFlag extends SyncedTileEntity implements IPlaceAwareTile, IActivateAwareTile {

	private SyncableFloat angle;
	private SyncableEnum<ColorMeta> colorIndex;

	public TileEntityFlag() {}

	@Override
	protected void createSyncedFields() {
		angle = new SyncableFloat();
		colorIndex = SyncableEnum.create(ColorMeta.GREEN);
	}

	public ColorMeta getColor() {
		return colorIndex.get();
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
		angle.set(ang);
		colorIndex.set(ColorMeta.fromBlockMeta(stack.getItemDamage() & 0xF));
	}
}
