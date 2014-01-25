package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.radio.RadioRegistry;
import openblocks.client.radio.StreamPlayer;
import openmods.OpenMods;
import openmods.api.IActivateAwareTile;
import openmods.api.IBreakAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableBoolean;
import openmods.sync.SyncableString;
import openmods.tileentity.SyncedTileEntity;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Strings;

public class TileEntityRadio extends SyncedTileEntity implements IActivateAwareTile, IBreakAwareTile, INeighbourAwareTile {

	private SyncableString url;
	private SyncableBoolean enabled;
	private StreamPlayer radioPlayer;
	private float currentVolume = 1f;

	@Override
	protected void createSyncedFields() {
		url = new SyncableString();
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
			String urlValue = url.getValue();
			if (enabled.getValue() && !Strings.isNullOrEmpty(urlValue)) {
				if (radioPlayer == null) {
					RadioRegistry.registerPlayer(radioPlayer = new StreamPlayer("icy://" + urlValue));
				}
			}
		}
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		final boolean isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		final boolean hasStations = Config.radioStations.length > 0;
		final boolean isEnabled = isPowered && hasStations;
		enabled.setValue(isEnabled);
		if (!isEnabled) killMusic();
		sync();
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (enabled.getValue()) {
			worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:radio", 1, 2f);
			if (!worldObj.isRemote) {
				if (Config.radioStations.length > 0) {
					int index = ArrayUtils.indexOf(Config.radioStations, url.getValue());
					index++;
					if (index >= Config.radioStations.length) {
						index = 0;
					}
					url.setValue(Config.radioStations[index]);
					sync();
				} else url.setValue("");
			}
		}
		return true;
	}

	@Override
	public void onChunkUnload() {
		killMusic();
	}

	@Override
	public void onBlockBroken() {
		killMusic();
	}
}
