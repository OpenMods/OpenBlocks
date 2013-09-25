package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.api.ISurfaceAttachment;

public class TileEntityTarget extends OpenTileEntity implements
		ISurfaceAttachment {

	private int strength = 0;
	private int tickCounter = -1;

	public TileEntityTarget() {
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		tickCounter--;
		if (tickCounter == 0) {
			tickCounter = -1;
			strength = 0;
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord,
					Config.blockTargetId);
		}
	}

	public void setEnabled(boolean en) {
		setFlag1(en);
	}

	public boolean isEnabled() {
		return getFlag1();
	}

	public float getTargetRotation() {
		return isEnabled() ? 0 : -(float) (Math.PI / 2);
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
		tickCounter = 10;
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord,
				Config.blockTargetId);
	}

	private void onRedstoneChanged() {
		boolean isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord,
				yCoord, zCoord);

		if (isPowered == isEnabled())
			return;

		if (!isPowered) {
			@SuppressWarnings("unchecked")
			List<EntityArrow> arrows = worldObj.getEntitiesWithinAABB(
					EntityArrow.class,
					AxisAlignedBB.getAABBPool().getAABB(xCoord - 0.1,
							yCoord - 0.1, zCoord - 0.1, xCoord + 1.1,
							yCoord + 1.1, zCoord + 1.1));

			if (arrows.size() > 0) {
				ItemStack newStack = new ItemStack(Item.arrow, arrows.size(), 0);
				EntityItem item = new EntityItem(worldObj, xCoord + 0.5,
						yCoord + 0.5, zCoord + 0.5, newStack);
				worldObj.spawnEntityInWorld(item);
			}
			for (EntityArrow arrow : arrows) {
				arrow.setDead();
			}

		}
		worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5,
				isPowered ? "openblocks:open" : "openblocks:close", 0.5f, 1.0f);

		setEnabled(isPowered);
		sync();
	}

	public void neighbourBlockChanged() {
		onRedstoneChanged();
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return ForgeDirection.DOWN;
	}
}
