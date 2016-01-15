package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.WorldServer;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Blocks;
import openmods.Log;
import openmods.api.IAddAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.api.ISurfaceAttachment;
import openmods.fakeplayer.FakePlayerPool;
import openmods.fakeplayer.FakePlayerPool.PlayerUserReturning;
import openmods.fakeplayer.OpenModsFakePlayer;
import openmods.reflection.SafeClassLoad;
import openmods.sync.SyncableBoolean;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;
import openmods.utils.EntityUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TileEntityTarget extends SyncedTileEntity implements ISurfaceAttachment, INeighbourAwareTile, IAddAwareTile, ITickable {

	private int strength = 0;
	private int tickCounter = -1;

	private SyncableBoolean active;

	private final static SafeClassLoad FLANS_BULLET = SafeClassLoad.create("com.flansmod.common.guns.EntityBullet");

	public final static Set<Class<?>> EXTRA_PROJECTILE_CLASSES = Sets.newHashSet();

	private static void addClass(SafeClassLoad cls) {
		if (cls.tryLoad()) EXTRA_PROJECTILE_CLASSES.add(cls.get());
		else Log.debug("Class %s not found, skipping target path prediction from FlansMod", cls.clsName);
	}

	static {
		addClass(FLANS_BULLET);
	}

	private final static Predicate<Entity> PROJECTILE_SELECTOR = new Predicate<Entity>() {
		@Override
		public boolean apply(Entity target) {
			return EXTRA_PROJECTILE_CLASSES.contains(target.getClass());
		}
	};

	public TileEntityTarget() {}

	@Override
	protected void createSyncedFields() {
		active = new SyncableBoolean();
	}

	@Override
	public void update() {
		if (!worldObj.isRemote) predictOtherProjectiles();

		tickCounter--;
		if (tickCounter == 0) {
			tickCounter = -1;
			strength = 0;
			worldObj.notifyNeighborsOfStateChange(pos, OpenBlocks.Blocks.target);
		}
	}

	private void predictOtherProjectiles() {
		@SuppressWarnings("unchecked")
		List<Entity> projectiles = worldObj.getEntitiesWithinAABB(Entity.class, getBB().expand(10, 10, 10), PROJECTILE_SELECTOR);

		for (Entity projectile : projectiles) {
			MovingObjectPosition hit = EntityUtils.raytraceEntity(projectile);
			if (pos.equals(hit.getBlockPos())) {
				Blocks.target.onTargetHit(worldObj, pos, hit.hitVec);
			}
		}
	}

	public void setEnabled(boolean en) {
		active.set(en);
	}

	public boolean isEnabled() {
		return active.get();
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
		worldObj.notifyNeighborsOfStateChange(pos, OpenBlocks.Blocks.target);
	}

	@Override
	public EnumFacing getSurfaceDirection() {
		return EnumFacing.DOWN;
	}

	@Override
	public void onNeighbourChanged(Block block) {
		updateRedstone();
	}

	@Override
	public void onAdded() {
		updateRedstone();
	}

	private void updateRedstone() {
		if (!(worldObj instanceof WorldServer)) return;
		WorldServer world = (WorldServer)worldObj;

		boolean isPowered = worldObj.isBlockIndirectlyGettingPowered(pos) > 0;

		if (isPowered != isEnabled()) {
			dropArrowsAsItems(world);
			playSoundAtBlock(isPowered? "openblocks:target.open" : "openblocks:target.close", 0.5f, 1.0f);

			setEnabled(isPowered);

			sync();
		}
	}

	private void dropArrowsAsItems(WorldServer world) {
		// TODO 1.8.9 verify range
		final AxisAlignedBB aabb = BlockUtils.aabbOffset(pos, -0.1, -0.1, -0.1, +1.1, +1.1, +1.1);

		final List<EntityArrow> arrows = worldObj.getEntitiesWithinAABB(EntityArrow.class, aabb);

		final List<ItemStack> drops = Lists.newArrayList();

		int failed = FakePlayerPool.instance.executeOnPlayer(world, new PlayerUserReturning<Integer>() {

			@Override
			public Integer usePlayer(OpenModsFakePlayer fakePlayer) {
				int failed = 0;

				for (EntityArrow arrow : arrows) {
					try {
						arrow.onCollideWithPlayer(fakePlayer);
					} catch (Throwable t) {
						Log.warn(t, "Failed to collide arrow %s with fake player, returing vanilla one", arrow);
						failed++;
					}
				}

				IInventory inventory = fakePlayer.inventory;
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					ItemStack stack = inventory.getStackInSlot(i);
					if (stack != null) {
						drops.add(stack);
						inventory.setInventorySlotContents(i, null);
					}
				}

				return failed;

			}
		});

		for (ItemStack drop : drops)
			BlockUtils.dropItemStackInWorld(worldObj, pos, drop);

		if (failed > 0) BlockUtils.dropItemStackInWorld(worldObj, pos, new ItemStack(Items.arrow, failed));
	}
}
