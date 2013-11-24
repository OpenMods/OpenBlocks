package openblocks.common.entity;

import java.util.HashMap;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openblocks.api.IMutant;
import openblocks.api.IMutantDefinition;
import openblocks.api.MutantRegistry;
import openmods.utils.CollectionUtils;

import com.google.common.base.Objects;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityMutant extends EntityTameable implements IEntityAdditionalSpawnData, IMutant {

	private Class<? extends EntityLivingBase> head;
	private Class<? extends EntityLivingBase> body;
	private Class<? extends EntityLivingBase> arms;
	private Class<? extends EntityLivingBase> wings;
	private Class<? extends EntityLivingBase> legs;
	private Class<? extends EntityLivingBase> tail;

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
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
		setTamed(true);
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
		data.writeUTF(getEntityIdForClass(head));
		data.writeUTF(getEntityIdForClass(body));
		data.writeUTF(getEntityIdForClass(arms));
		data.writeUTF(getEntityIdForClass(wings));
		data.writeUTF(getEntityIdForClass(legs));
		data.writeUTF(getEntityIdForClass(tail));
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		head = getEntityClassForId(data.readUTF());
		body = getEntityClassForId(data.readUTF());
		arms = getEntityClassForId(data.readUTF());
		wings = getEntityClassForId(data.readUTF());
		legs = getEntityClassForId(data.readUTF());
		tail = getEntityClassForId(data.readUTF());
	}

	private String getEntityIdForClass(Class<? extends EntityLivingBase> klazz) {
		return Objects.firstNonNull((String)EntityList.classToStringMapping.get(klazz), "");
	}

	@SuppressWarnings("unchecked")
	private Class<? extends EntityLivingBase> getEntityClassForId(String id) {
		return (Class<? extends EntityLivingBase>)EntityList.stringToClassMapping.get(id);
	}

	@Override
	public IMutantDefinition getBody() {
		return MutantRegistry.getDefinition(body);
	}

	@Override
	public IMutantDefinition getHead() {
		return MutantRegistry.getDefinition(head);
	}

	@Override
	public IMutantDefinition getArms() {
		return MutantRegistry.getDefinition(arms);
	}

	@Override
	public IMutantDefinition getWings() {
		return MutantRegistry.getDefinition(wings);
	}

	@Override
	public IMutantDefinition getLegs() {
		return MutantRegistry.getDefinition(legs);
	}

	@Override
	public IMutantDefinition getTail() {
		return MutantRegistry.getDefinition(tail);
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
		head = getEntityClassForId(CollectionUtils.getWeightedRandom(dnas));
		body = getEntityClassForId(CollectionUtils.getWeightedRandom(dnas));
		arms = getEntityClassForId(CollectionUtils.getWeightedRandom(dnas));
		legs = getEntityClassForId(CollectionUtils.getWeightedRandom(dnas));
		wings = getEntityClassForId(CollectionUtils.getWeightedRandom(dnas));
		tail = getEntityClassForId(CollectionUtils.getWeightedRandom(dnas));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setString("head", getEntityIdForClass(head));
		tag.setString("body", getEntityIdForClass(body));
		tag.setString("arms", getEntityIdForClass(arms));
		tag.setString("legs", getEntityIdForClass(legs));
		tag.setString("wings", getEntityIdForClass(wings));
		tag.setString("tail", getEntityIdForClass(tail));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		head = getEntityClassForId(tag.getString("head"));
		body = getEntityClassForId(tag.getString("body"));
		arms = getEntityClassForId(tag.getString("arms"));
		legs = getEntityClassForId(tag.getString("legs"));
		wings = getEntityClassForId(tag.getString("wings"));
		tail = getEntityClassForId(tag.getString("tail"));
	}
}
