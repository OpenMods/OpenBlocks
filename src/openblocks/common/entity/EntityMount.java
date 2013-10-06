package openblocks.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityMount extends Entity {

	public EntityMount(World world, int x, int y, int z) {
		this(world);
		setPosition(x + 0.5, y, z + 0.5);
	}

	public EntityMount(World par1World) {
		super(par1World);
		setSize(0F, 0F);
	}

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (riddenByEntity == null) setDead();

		// if((int) posY == posY) // Fix the client sometimes derping for some
		// odd reason...
		// posY -= 0.5;
	}
}
