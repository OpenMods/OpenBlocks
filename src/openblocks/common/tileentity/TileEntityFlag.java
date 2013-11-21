package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockFlag;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableFloat;
import openblocks.sync.SyncableInt;
import openblocks.utils.BlockUtils;
import openmods.common.api.IAwareTileLite;
import openmods.common.api.ISurfaceAttachment;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityFlag extends SyncedTileEntity implements ISurfaceAttachment, IAwareTileLite {

	private SyncableFloat angle;
	private SyncableInt colorIndex;

	public TileEntityFlag() {}

	@Override
	protected void createSyncedFields() {
		angle = new SyncableFloat(0.0f);
		colorIndex = new SyncableInt(0);
	}

	@Override
	protected void initialize() {}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

	public Icon getIcon() {
		return OpenBlocks.Blocks.flag.getIcon(0, 0);
	}

	public void setColorIndex(int index) {
		colorIndex.setValue(index);
	}

	public void setAngle(float ang) {
		angle.setValue(ang);
	}

	public int getColor() {
		if (colorIndex.getValue() >= BlockFlag.COLORS.length) colorIndex.setValue(0);
		return BlockFlag.COLORS[colorIndex.getValue()];
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return getRotation();
	}

	public float getAngle() {
		return angle.getValue();
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
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		float ang = player.rotationYawHead;
		ForgeDirection rotation = getRotation();
		if (rotation != ForgeDirection.DOWN) {
			ang = -BlockUtils.getRotationFromDirection(side.getOpposite());
		}
		setAngle(ang);
		setColorIndex(stack.getItemDamage());
		sync();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void prepareForInventoryRender(Block block, int metadata) {
		super.prepareForInventoryRender(block, metadata);
		setColorIndex(metadata);
	}
}
