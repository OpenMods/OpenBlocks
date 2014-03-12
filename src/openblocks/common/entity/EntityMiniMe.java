package openblocks.common.entity;

import openblocks.common.entity.ai.EntityAICollectItem;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.world.World;

public class EntityMiniMe extends EntityTameable implements IEntityAdditionalSpawnData {

	private String username = "Mini me";
	
	public EntityMiniMe(World world, String username) {
		this(world);
		this.username = username;
	}
	
	public EntityMiniMe(World world) {
		super(world);
		setSize(0.5F, 0.5F);
		setAIMoveSpeed(0.7F);
		setMoveForward(0);
		setTamed(true);
		func_110163_bv();
		getNavigator().setAvoidsWater(true);
		getNavigator().setCanSwim(true);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIFollowOwner(this, getAIMoveSpeed(), 10.0F, 2.0F));
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeUTF(username);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		username = data.readUTF();
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return null;
	}
	
	@Override
    public boolean isChild() {
        return true;
    }

}
