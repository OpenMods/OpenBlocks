package openblocks.common.tileentity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import openblocks.common.entity.EntityMutant;
import openmods.OpenMods;
import openmods.entity.EntityBlock;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableInt;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityGoldenEgg extends SyncedTileEntity {

	private static final String TALLY_NBT_KEY = "tally";
	// private static final String STAGE_NBT_KEY = "stage";
	public static final int STAGE_CHANGE_TICK = 200;
	private static final double STAGE_CHANGE_CHANCE = 1.0;
	public int animationStageTicks = 0;
	public float rotation;

	private HashMap<String, Integer> dnas = new HashMap<String, Integer>();
	private SyncableInt stage;

	@Override
	protected void createSyncedFields() {
		stage = new SyncableInt(3);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			if (OpenMods.proxy.getTicks(worldObj) % STAGE_CHANGE_TICK == 0 && worldObj.rand.nextDouble() < STAGE_CHANGE_CHANCE) {
				incrementStage();
				System.out.println("Egg entering stage" + stage.getValue());
			}
			if (stage.getValue() >= 1) {

			}
			if (stage.getValue() >= 2) {

			}
			if (stage.getValue() >= 3) {

			}
			if (stage.getValue() >= 4) {
				// TODO: check whitelist
				// maybe this should be more.. interesting. shapes or
				// something?!
				int posX = xCoord + worldObj.rand.nextInt(20) - 10;
				int posY = yCoord + worldObj.rand.nextInt(2) - 1;
				int posZ = zCoord + worldObj.rand.nextInt(20) - 10;
				if (posX != xCoord && posY != yCoord && posZ != zCoord) {
					EntityBlock block = EntityBlock.create(worldObj, posX, posY, posZ);
					if (block != null) {
						block.setHasGravity(true);
						block.motionY = 0.9;
						block.setPositionAndRotation(posX, posY, posZ, 0, 0);
						worldObj.spawnEntityInWorld(block);
					}
				}
			}
			if (stage.getValue() >= 5) {
				worldObj.weatherEffects.clear();
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
				//worldObj.createExplosion(null, 0.5 + xCoord, 0.5 + yCoord, 0.5 + zCoord, 7, true);
				EntityMutant mutant = new EntityMutant(worldObj);
				mutant.setTraitsFromMap(dnas);
				mutant.setPositionAndRotation(0.5 + xCoord, 0.5 + yCoord, 0.5 + zCoord, 0, 0);
				worldObj.spawnEntityInWorld(mutant);
			}
		}
		if(stage.getValue() >= 4) {
			animationStageTicks++;
		}
	}

	public int getStage() {
		return stage.getValue();
	}

	private void incrementStage() {
		stage.modify(1);
		sync();
	}

	public void addDNAFromItemStack(ItemStack itemStack) {
		if (itemStack != null && stage.getValue() == 1) {
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag != null && tag.hasKey("entity")) {
				String entity = tag.getString("entity");
				int count = 0;
				if (dnas.containsKey(entity)) {
					count = dnas.get(entity);
				}
				dnas.put(entity, count + 1);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound entitiesTag = new NBTTagCompound();
		for (Entry<String, Integer> dnaTally : dnas.entrySet()) {
			entitiesTag.setInteger(dnaTally.getKey(), dnaTally.getValue());
		}
		nbt.setCompoundTag(TALLY_NBT_KEY, entitiesTag);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		dnas.clear();
		if (nbt.hasKey(TALLY_NBT_KEY)) {
			NBTTagCompound tallyTag = nbt.getCompoundTag(TALLY_NBT_KEY);
			for (NBTBase tag : (Collection<NBTBase>)tallyTag.getTags()) {
				dnas.put(tag.getName(), tallyTag.getInteger(tag.getName()));
			}
		}
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {

	}

}
