package openblocks.common.entity;

import net.minecraft.world.World;
import openblocks.api.IMagnetAware;
import openmods.entity.EntityBlock;

public class EntityMountedBlock extends EntityBlock implements IMagnetAware {

	public EntityMountedBlock(World world) {
		super(world);
	}

	@Override
	protected boolean shouldPlaceBlock() {
		return ridingEntity == null && !worldObj.isRemote;
	}

	@Override
	public double getMountedYOffset() {
		return height;
	}

	@Override
	public boolean canRelease() {
		return worldObj.isAirBlock(getPosition());
	}
}
