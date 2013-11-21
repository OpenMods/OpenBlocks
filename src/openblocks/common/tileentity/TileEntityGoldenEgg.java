package openblocks.common.tileentity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import openblocks.OpenBlocks;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.entity.EntityBlock;
import openblocks.common.entity.EntityMutant;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableInt;

public class TileEntityGoldenEgg extends SyncedTileEntity {

	private static final String TALLY_NBT_KEY = "tally";
	private static final String STAGE_NBT_KEY = "stage";
	private static final int STAGE_CHANGE_TICK = 50;
	private static final double STAGE_CHANGE_CHANCE = 0.5;

	private HashMap<String, Integer> dnas = new HashMap<String, Integer>();
	private SyncableInt stage;

	@Override
	protected void createSyncedFields() {
		stage = new SyncableInt();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			if (OpenBlocks.proxy.getTicks(worldObj) % STAGE_CHANGE_TICK == 0 && worldObj.rand.nextDouble() < STAGE_CHANGE_CHANCE) {
				incrementStage();
			}
			switch (stage.getValue()) {
				case 1:
					// nothing, eh? During this stage injections still work
					break;
				case 2:
					// dunno
					break;
				case 3:
					// dunno
					break;
				case 4:
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
					break;
				case 5:
					worldObj.setBlockToAir(xCoord, yCoord, zCoord);
					worldObj.createExplosion(null, 0.5 + xCoord, 0.5 + yCoord, 0.5 + zCoord, 7, true);
					EntityMutant mutant = new EntityMutant(worldObj);
					mutant.setTraitsFromMap(dnas);
					mutant.setPositionAndRotation(0.5 + xCoord, 0.5 + yCoord, 0.5 + zCoord, 0, 0);
					worldObj.spawnEntityInWorld(mutant);
			}
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
