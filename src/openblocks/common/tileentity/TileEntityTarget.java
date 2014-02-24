package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.OpenBlocks.Blocks;
import openmods.Mods;
import openmods.api.INeighbourAwareTile;
import openmods.api.ISurfaceAttachment;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableBoolean;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTarget extends SyncedTileEntity implements ISurfaceAttachment, INeighbourAwareTile {

	private int strength = 0;
	private int tickCounter = -1;

	private SyncableBoolean active;

	private Class flansBulletClass;
	private boolean triedFlans = false;

	public TileEntityTarget() {}

	@Override
	protected void createSyncedFields() {
		active = new SyncableBoolean();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		tickCounter--;
		if (!worldObj.isRemote && Loader.isModLoaded(Mods.FLANSMOD)) {
			if (!triedFlans && flansBulletClass == null) {
				try {
					flansBulletClass = Class.forName("co.uk.flansmods.common.guns.EntityBullet");
				} catch (ClassNotFoundException e) {}
				triedFlans = true;
			}
			if (flansBulletClass != null) {
				List<Entity> bullets = worldObj.getEntitiesWithinAABB(flansBulletClass, getBB().expand(8, 8, 8));
				if (bullets.size() > 0) {
					for (Entity bullet : bullets) {
						Vec3 posVec = Vec3.createVectorHelper(bullet.posX, bullet.posY, bullet.posZ);
						Vec3 nextPosVec = Vec3.createVectorHelper(bullet.posX + bullet.motionX, bullet.posY + bullet.motionY, bullet.posZ + bullet.motionZ);
						MovingObjectPosition hit = worldObj.rayTraceBlocks_do_do(posVec, nextPosVec, false, true);
						if (hit != null && hit.blockX == xCoord && hit.blockY == yCoord && hit.blockZ == zCoord) {
							Blocks.target.onTargetHit(worldObj, xCoord, yCoord, zCoord, hit.hitVec);
						}
					}
				}
			}
		}
		if (tickCounter == 0) {
			tickCounter = -1;
			strength = 0;
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, Config.blockTargetId);
		}
	}

	public void setEnabled(boolean en) {
		active.setValue(en);
	}

	public boolean isEnabled() {
		return active.getValue();
	}

	public float getTargetRotation() {
		return isEnabled()? 0 : -(float)(Math.PI / 2);
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
		tickCounter = 10;
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, Config.blockTargetId);
	}

	private void onRedstoneChanged() {
		boolean isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

		if (isPowered == isEnabled()) return;

		if (!isPowered) {
			final AxisAlignedBB aabb = AxisAlignedBB.getAABBPool().getAABB(xCoord - 0.1, yCoord - 0.1, zCoord - 0.1, xCoord + 1.1, yCoord + 1.1, zCoord + 1.1);

			@SuppressWarnings("unchecked")
			List<EntityArrow> arrows = worldObj.getEntitiesWithinAABB(EntityArrow.class, aabb);

			int pickableCount = 0;

			for (EntityArrow arrow : arrows) {
				if (arrow.canBePickedUp == 1) pickableCount++;
				arrow.setDead();
			}

			if (pickableCount > 0) {
				ItemStack newStack = new ItemStack(Item.arrow, pickableCount, 0);
				BlockUtils.dropItemStackInWorld(worldObj, xCoord, yCoord, zCoord, newStack);
			}
		}
		worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, isPowered? "openblocks:open" : "openblocks:close", 0.5f, 1.0f);

		setEnabled(isPowered);
		sync();
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void prepareForInventoryRender(Block block, int metadata) {
		super.prepareForInventoryRender(block, metadata);
		setEnabled(true);
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

	@Override
	public void onNeighbourChanged(int blockId) {
		onRedstoneChanged();
	}
}
