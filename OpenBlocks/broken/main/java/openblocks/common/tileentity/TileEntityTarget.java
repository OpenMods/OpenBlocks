package openblocks.common.tileentity;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockTarget;
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

	public TileEntityTarget() {}

	@Override
	public void update() {
		if (!world.isRemote && !EXTRA_PROJECTILE_CLASSES.isEmpty()) predictOtherProjectiles();

		tickCounter--;
		if (tickCounter == 0) {
			tickCounter = -1;
			strength = 0;
			world.notifyNeighborsOfStateChange(pos, OpenBlocks.Blocks.target, false);
		}
	}

	private void predictOtherProjectiles() {
		final List<Entity> projectiles = world.getEntitiesWithinAABB(Entity.class, getBB().grow(10), target -> EXTRA_PROJECTILE_CLASSES.contains(target.getClass()));

		BlockState state = null;

		for (Entity projectile : projectiles) {
			RayTraceResult hit = EntityUtils.raytraceEntity(projectile);
			if (hit.typeOfHit == Type.BLOCK && pos.equals(hit.getBlockPos())) {
				if (state == null) state = world.getBlockState(getPos());
				((BlockTarget)state.getBlock()).onTargetHit(world, pos, state, hit.hitVec);
			}
		}
	}

	public int getRedstoneStrength() {
		return strength;
	}

	public void setRedstoneStrength(int strength) {
		this.strength = strength;
		tickCounter = 10;
		world.notifyNeighborsOfStateChange(pos, OpenBlocks.Blocks.target, false);
	}

	@Override
	public Direction getSurfaceDirection() {
		return Direction.DOWN;
	}
}
