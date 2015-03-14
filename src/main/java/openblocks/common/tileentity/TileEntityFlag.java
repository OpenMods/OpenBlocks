package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockFlag;
import openmods.api.IActivateAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.api.ISurfaceAttachment;
import openmods.sync.SyncableByte;
import openmods.sync.SyncableFloat;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;
import openmods.utils.ColorUtils.RGB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityFlag extends SyncedTileEntity implements ISurfaceAttachment, IPlaceAwareTile, IActivateAwareTile {

	private SyncableFloat angle;
	private SyncableByte colorIndex;

	public TileEntityFlag() {}

	@Override
	protected void createSyncedFields() {
		angle = new SyncableFloat();
		colorIndex = new SyncableByte();
	}

	@Override
	protected void initialize() {}

	public IIcon getIcon() {
		return OpenBlocks.Blocks.flag.getIcon(0, 0);
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

	@Override
	public ForgeDirection getSurfaceDirection() {
		return getRotation();
	}

	public float getAngle() {
		return angle.get();
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player != null && player.isSneaking()) { return true; }
		if (!worldObj.isRemote) {
			if (getSurfaceDirection() == ForgeDirection.DOWN) {
				angle.set(angle.get() + 10f);
				sync();
				return false;
			}
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		float ang = player.rotationYawHead;
		ForgeDirection rotation = getRotation();
		if (rotation != ForgeDirection.DOWN) {
			ang = -BlockUtils.getRotationFromDirection(side.getOpposite());
		}
		setAngle(ang);
		setColorIndex((byte)(stack.getItemDamage() & 0xF));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void prepareForInventoryRender(Block block, int metadata) {
		super.prepareForInventoryRender(block, metadata);
		setColorIndex((byte)metadata);
	}
}
