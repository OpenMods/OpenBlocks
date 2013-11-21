package openblocks.common.tileentity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import openblocks.OpenBlocks;
import openmods.common.api.IActivateAwareTile;
import openmods.common.tileentity.OpenTileEntity;

public class TileEntityGoldenEgg extends OpenTileEntity {

	private static final String TALLY_NBT_KEY = "tally";
	private static final String STAGE_NBT_KEY = "stage";
	private static final int STAGE_CHANGE_TICK = 100;
	private static final double STAGE_CHANGE_CHANCE = 0.1;
	
	private HashMap<String, Integer> dnas = new HashMap<String, Integer>();
	private int stage = 0;
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote && OpenBlocks.proxy.getTicks(worldObj) % STAGE_CHANGE_TICK == 0 && worldObj.rand.nextDouble() < STAGE_CHANGE_CHANCE) {
			incrementStage();
		}
	}
	
	private void incrementStage() {
		stage++;
		System.out.println(stage);
	}

	public void addDNAFromItemStack(ItemStack itemStack) {
		if (itemStack != null) {
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
		nbt.setInteger(STAGE_NBT_KEY, stage);
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
		if (nbt.hasKey(STAGE_NBT_KEY)) {
			stage = nbt.getInteger(STAGE_NBT_KEY);
		}
	}
}
