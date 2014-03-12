package openblocks.common.tileentity;

import java.util.*;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.entity.EntityMiniMe;
import openblocks.common.entity.EntityMutant;
import openmods.OpenMods;
import openmods.api.IPlaceAwareTile;
import openmods.entity.EntityBlock;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableInt;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityGoldenEgg extends SyncedTileEntity implements IPlaceAwareTile {

	private static final int STAGE_CHANGE_TICK = 600;
	public static final int ANIMATION_TIME = 400;
	private static final double STAGE_CHANGE_CHANCE = 0.8;
	public int animationStageTicks = 0;
	public float rotation;

	private ArrayList<EntityBlock> blocks = new ArrayList<EntityBlock>();
	private SyncableInt stage;

	private String owner;

	@Override
	protected void createSyncedFields() {
		stage = new SyncableInt(0);
	}

	private boolean stageElapsed() {
		return animationStageTicks > 0? animationStageTicks >= ANIMATION_TIME : OpenMods.proxy.getTicks(worldObj) % STAGE_CHANGE_TICK == 0 && worldObj.rand.nextDouble() < STAGE_CHANGE_CHANCE;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			if (stageElapsed()) {
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
				if (blocks != null && posX != xCoord && posY != yCoord && posZ != zCoord && worldObj.rand.nextInt(10) == 0) {
					EntityBlock block = EntityBlock.create(worldObj, posX, posY, posZ);
					if (block != null) {
						block.setHasAirResistance(false);
						block.setHasGravity(false);
						block.setShouldDrop(false);
						block.motionY = 0.1;
						// block.setPositionAndRotation(posX, posY, posZ, 0, 0);
						blocks.add(block);
						worldObj.spawnEntityInWorld(block);
					}
				}
				if (ANIMATION_TIME - animationStageTicks < 20 && blocks != null) {
					for (EntityBlock block : blocks) {
						block.setShouldDrop(true);
						block.motionY = -0.9;
						block.setHasGravity(true);
					}
					blocks.clear();
					blocks = null;
				}
			}
			if (stage.getValue() >= 5) {
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
				worldObj.createExplosion(null, 0.5 + xCoord, 0.5 + yCoord, 0.5 + zCoord, 2, true);
				EntityMiniMe miniMe = new EntityMiniMe(worldObj, "Mikeemoo");
				miniMe.setPositionAndRotation(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 0, 0);
				worldObj.spawnEntityInWorld(miniMe);
				return;
			}
		}
		if (stage.getValue() >= 4 && animationStageTicks < ANIMATION_TIME) {
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

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString("owner", owner);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		owner = nbt.getString("owner");
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		if (player != null) {
			owner = player.username;
		}
	}

}
