package openblocks.common.tileentity;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Blocks;
import openmods.Log;
import openmods.api.ISurfaceAttachment;
import openmods.reflection.SafeClassLoad;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.EntityUtils;

public class TileEntityTarget extends OpenTileEntity implements ISurfaceAttachment, ITickable {

	private int strength = 0;
	private int tickCounter = -1;

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
	public void update() {
		if (!worldObj.isRemote && !EXTRA_PROJECTILE_CLASSES.isEmpty()) predictOtherProjectiles();

		tickCounter--;
		if (tickCounter == 0) {
			tickCounter = -1;
			strength = 0;
			worldObj.notifyNeighborsOfStateChange(pos, OpenBlocks.Blocks.target);
		}
	}

	private void predictOtherProjectiles() {
		final List<Entity> projectiles = worldObj.getEntitiesWithinAABB(Entity.class, getBB().expand(10, 10, 10), PROJECTILE_SELECTOR);

		IBlockState state = null;

		for (Entity projectile : projectiles) {
			RayTraceResult hit = EntityUtils.raytraceEntity(projectile);
			if (hit.typeOfHit == Type.BLOCK && pos.equals(hit.getBlockPos())) {
				if (state == null) state = worldObj.getBlockState(getPos());
				Blocks.target.onTargetHit(worldObj, pos, state, hit.hitVec);
			}
		}
	}

	public int getRedstoneStrength() {
		return strength;
	}

	public void setRedstoneStrength(int strength) {
		this.strength = strength;
		tickCounter = 10;
		worldObj.notifyNeighborsOfStateChange(pos, OpenBlocks.Blocks.target);
	}

	@Override
	public EnumFacing getSurfaceDirection() {
		return EnumFacing.DOWN;
	}
}
