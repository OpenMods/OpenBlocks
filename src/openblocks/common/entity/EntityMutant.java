package openblocks.common.entity;

import java.io.DataOutput;
import java.io.IOException;

import openblocks.api.IMutant;
import openblocks.api.IMutantDefinition;
import openblocks.api.MutantRegistry;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.World;

public class EntityMutant extends EntityAnimal implements IEntityAdditionalSpawnData, IMutant {

	public EntityMutant(World world) {
		super(world);
		setSize(0.6F, 1.8F);
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
		return MutantRegistry.getDefinition(EntityOcelot.class);
	}

	@Override
	public IMutantDefinition getHead() {
		return MutantRegistry.getDefinition(EntityEnderman.class);
	}

	@Override
	public IMutantDefinition getArms() {
		return MutantRegistry.getDefinition(EntityOcelot.class);
	}

	@Override
	public IMutantDefinition getWings() {
		return MutantRegistry.getDefinition(EntityOcelot.class);
	}

	@Override
	public IMutantDefinition getLegs() {
		return MutantRegistry.getDefinition(EntityEnderman.class);
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

}
