package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.SideOnly;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TileEntityTarget extends SyncedTileEntity implements ISurfaceAttachment, INeighbourAwareTile, IAddAwareTile {

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

	private final static IEntitySelector PROJECTILE_SELECTOR = new IEntitySelector() {
		@Override
		public boolean isEntityApplicable(Entity p_82704_1_) {
			return EXTRA_PROJECTILE_CLASSES.contains(p_82704_1_.getClass());
		}
	};

	public TileEntityTarget() {}

	@Override
	protected void createSyncedFields() {
		active = new SyncableBoolean();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) predictOtherProjectiles();

		tickCounter--;
		if (tickCounter == 0) {
			tickCounter = -1;
			strength = 0;
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, OpenBlocks.Blocks.target);
		}
	}

	private void predictOtherProjectiles() {
		@SuppressWarnings("unchecked")
		List<Entity> projectiles = worldObj.selectEntitiesWithinAABB(Entity.class, getBB().expand(10, 10, 10), PROJECTILE_SELECTOR);

		for (Entity projectile : projectiles) {
			MovingObjectPosition hit = EntityUtils.raytraceEntity(projectile);
			if (BlockUtils.isBlockHit(hit, this)) {
				Blocks.target.onTargetHit(worldObj, xCoord, yCoord, zCoord, hit.hitVec);
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
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, OpenBlocks.Blocks.target);
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

		boolean isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

		if (isPowered != isEnabled()) {
			dropArrowsAsItems(world);
			worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, isPowered? "openblocks:target.open" : "openblocks:target.close", 0.5f, 1.0f);

			setEnabled(isPowered);

			sync();
		}
	}

	private void dropArrowsAsItems(WorldServer world) {
		final AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xCoord - 0.1, yCoord - 0.1, zCoord - 0.1, xCoord + 1.1, yCoord + 1.1, zCoord + 1.1);

		@SuppressWarnings("unchecked")
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
			BlockUtils.dropItemStackInWorld(worldObj, xCoord, yCoord, zCoord, drop);

		if (failed > 0) BlockUtils.dropItemStackInWorld(worldObj, xCoord, yCoord, zCoord, new ItemStack(Items.arrow, failed));
	}
}
