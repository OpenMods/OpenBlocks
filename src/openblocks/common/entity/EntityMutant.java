package openblocks.common.entity;

import java.util.HashMap;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import openblocks.api.IMutant;
import openblocks.api.IMutantDefinition;
import openblocks.api.MutantRegistry;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityMutant extends EntityTameable implements IEntityAdditionalSpawnData, IMutant {

	public EntityMutant(World world) {
		super(world);
		setSize(0.6F, 1.8F);
		getNavigator().setAvoidsWater(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackOnCollide(this, 1.0D, true));
        this.tasks.addTask(2, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
		this.tasks.addTask(3, new EntityAIPanic(this, 1.25D));
		this.tasks.addTask(4, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtTarget(this));
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(10.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25D);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return new EntityMutant(worldObj);
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		data.writeInt(0);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		data.readInt();
	}

	@Override
	public IMutantDefinition getBody() {
		return MutantRegistry.getDefinition(EntityEnderman.class);
	}

	@Override
	public IMutantDefinition getHead() {
		return MutantRegistry.getDefinition(EntityCreeper.class);
	}

	@Override
	public IMutantDefinition getArms() {
		return MutantRegistry.getDefinition(EntityZombie.class);
	}

	@Override
	public IMutantDefinition getWings() {
		return MutantRegistry.getDefinition(EntityOcelot.class);
	}

	@Override
	public IMutantDefinition getLegs() {
		return MutantRegistry.getDefinition(EntitySpider.class);
	}

	@Override
	public IMutantDefinition getTail() {
		return MutantRegistry.getDefinition(EntityOcelot.class);
	}

	@Override
	public int getLegHeight() {
		return getLegs().getLegHeight();
	}

	@Override
	public int getBodyHeight() {
		return getBody().getBodyHeight();
	}

	@Override
	public float getArmSwingProgress(float scale) {
		return getSwingProgress(scale);
	}

	@Override
	public int getNumberOfLegs() {
		return getLegs().getNumberOfLegs();
	}

	public void setTraitsFromMap(HashMap<String, Integer> dnas) {
		// TODO Auto-generated method stub

	}

}
