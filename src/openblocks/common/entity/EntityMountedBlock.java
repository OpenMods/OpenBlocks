package openblocks.common.entity;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

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
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(posY);
		int z = MathHelper.floor_double(posZ);
		return worldObj.isAirBlock(x, y, z);
	}
}
