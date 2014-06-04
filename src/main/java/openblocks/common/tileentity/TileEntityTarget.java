package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Blocks;
import openblocks.OpenBlocks.ClassReferences;
import openmods.api.INeighbourAwareTile;
import openmods.api.ISurfaceAttachment;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableBoolean;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;
import openmods.utils.EntityUtils;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTarget extends SyncedTileEntity implements ISurfaceAttachment, INeighbourAwareTile {

	private int strength = 0;
	private int tickCounter = -1;

	private SyncableBoolean active;

	private final static List<Class<?>> predictedProjectileClasses = Lists.newArrayList();

	static {
		predictedProjectileClasses.add(ClassReferences.flansmodsEntityBullet);
	}

	public TileEntityTarget() {}

	@Override
	protected void createSyncedFields() {
		active = new SyncableBoolean();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		tickCounter--;
		predictOtherProjectiles();
		if (tickCounter == 0) {
			tickCounter = -1;
			strength = 0;
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, OpenBlocks.Blocks.target);
		}
	}

	private void predictOtherProjectiles() {
		if (!worldObj.isRemote) {
			for (Class<?> klazz : predictedProjectileClasses) {
				if (klazz == null) continue;
				@SuppressWarnings("unchecked")
				List<Entity> projectiles = worldObj.getEntitiesWithinAABB(klazz, getBB().expand(20, 20, 20));
				for (Entity projectile : projectiles) {
					MovingObjectPosition hit = EntityUtils.raytraceEntity(projectile);
					if (BlockUtils.isBlockHit(hit, this)) {
						Blocks.target.onTargetHit(worldObj, xCoord, yCoord, zCoord, hit.hitVec);
					}
				}
			}
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
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, OpenBlocks.Blocks.target);
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
				ItemStack newStack = new ItemStack(Items.arrow, pickableCount, 0);
				BlockUtils.dropItemStackInWorld(worldObj, xCoord, yCoord, zCoord, newStack);
			}
		}
		worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, isPowered? "openblocks:target.open" : "openblocks:target.close", 0.5f, 1.0f);

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
	public void onNeighbourChanged() {
		onRedstoneChanged();
	}
}
