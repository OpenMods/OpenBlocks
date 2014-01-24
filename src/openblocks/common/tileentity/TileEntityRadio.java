package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.RadioRegistry;
import openblocks.client.radio.StreamPlayer;
import openmods.OpenMods;
import openmods.api.IAwareTile;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableBoolean;
import openmods.sync.SyncableString;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityRadio extends SyncedTileEntity implements IAwareTile {

	private SyncableString url;
	private SyncableBoolean enabled;
	private StreamPlayer radioPlayer;
	private float currentVolume = 1f;
	private float targetVolume = 1f;

	@Override
	protected void createSyncedFields() {
		url = new SyncableString(Config.radioStations.get(0));
		enabled = new SyncableBoolean();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		killMusic();
	}

	@Override
	protected void initialize() {
		if (!worldObj.isRemote) {
			onNeighbourChanged(0);
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote) {
			EntityPlayer clientPlayer = OpenMods.proxy.getThePlayer();
			if (clientPlayer != null && radioPlayer != null) {

				MovingObjectPosition mop = worldObj.clip(
						clientPlayer.getPosition(1.0f),
						Vec3.createVectorHelper(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5));

				float targetVolume = 1f;

				double dist = clientPlayer.getDistance(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
				if (dist > 30) {
					targetVolume = 1f;
				} else if (dist > 5) {
					targetVolume = 1f - ((float)dist / 30);
				}

				if (mop.typeOfHit == EnumMovingObjectType.TILE) {
					if (worldObj.blockExists(mop.blockX, mop.blockY, mop.blockZ)) {
						int blockId = worldObj.getBlockId(mop.blockX, mop.blockY, mop.blockZ);
						if (blockId != OpenBlocks.Blocks.radio.blockID) {
							targetVolume *= 0.5f;
						}
					}
				}

				currentVolume = currentVolume + ((targetVolume - currentVolume) * 0.1f);

				radioPlayer.setVolume(currentVolume);
			}
		}
	}

	private void killMusic() {
		if (worldObj.isRemote && radioPlayer != null) {
			radioPlayer.stop();
			radioPlayer = null;
		}
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {
		if (worldObj.isRemote && changes.size() > 0) {
			killMusic();
			if (enabled.getValue()) {
				if (radioPlayer == null) {
					RadioRegistry.registerPlayer(radioPlayer = new StreamPlayer("icy://" + url.getValue()));
				}
			}
		}
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		enabled.setValue(
				worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) &&
						Config.radioStations.size() > 0
				);
		sync();
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (enabled.getValue()) {
			worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:radio", 1, 2f);
			if (!worldObj.isRemote) {
				int index = Config.radioStations.indexOf(url.getValue());
				index++;
				if (index >= Config.radioStations.size()) {
					index = 0;
				}
				url.setValue(Config.radioStations.get(index));
				sync();
			}
		}
		return true;
	}

	public void onChunkUnload() {
		killMusic();
	}

	@Override
	public void onBlockBroken() {
		killMusic();
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {}

}
