package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import openblocks.client.radio.RadioManager;
import openblocks.client.radio.RadioManager.RadioException;
import openblocks.common.item.ItemTunedCrystal;
import openmods.OpenMods;
import openmods.api.*;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.sync.*;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;

import com.google.common.base.Strings;

public class TileEntityRadio extends SyncedTileEntity implements IActivateAwareTile, IBreakAwareTile, INeighbourAwareTile, IInventoryCallback, IExtendable, ISyncListener {

	private static final byte NO_CRYSTAL = (byte)-1;
	private SyncableString url;
	private SyncableString streamName;
	private SyncableByte crystalColor;
	private SyncableBoolean isPowered;
	private SyncableFloat volume;
	private ISound sound;

	@IncludeInterface(IInventory.class)
	private final GenericInventory inventory = new GenericInventory("openblocks.radio", false, 1) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			if (itemstack == null) return true;
			Item item = itemstack.getItem();
			return item instanceof ItemTunedCrystal;
		}
	};

	public TileEntityRadio() {
		inventory.addCallback(this);
		syncMap.addUpdateListener(this);
	}

	@Override
	protected void createSyncedFields() {
		url = new SyncableString();
		crystalColor = new SyncableByte(NO_CRYSTAL);
		isPowered = new SyncableBoolean();
		streamName = new SyncableString();
		volume = new SyncableFloat(1f);
	}

	@Override
	protected void initialize() {
		if (worldObj.isRemote) return;
		onNeighbourChanged();
	}

	private void updateURL(ItemStack stack) {
		if (stack == null) {
			url.clear();
			crystalColor.set(NO_CRYSTAL);
		} else {
			url.setValue(ItemTunedCrystal.getUrl(stack));
			streamName.setValue(stack.getDisplayName());
			crystalColor.set((byte)stack.getItemDamage());
		}
		sync();
	}

	@Override
	public void onInventoryChanged(IInventory inventory, int slotNumber) {
		if (worldObj.isRemote) return;
		updateURL(inventory.getStackInSlot(0));
		markUpdated();
	}

	@Override
	public void onSync(Set<ISyncableObject> changes) {
		if (changes.isEmpty()) return;

		if (changes.contains(crystalColor)) worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

		if (changes.contains(isPowered) || changes.contains(url)) {
			final boolean hasPower = isPowered.get();
			final String urlValue = url.getValue();

			final boolean hasUrl = !Strings.isNullOrEmpty(urlValue);
			final boolean canPlay = hasUrl && hasPower;

			if (canPlay) {
				playStatic();
				startPlayClient(urlValue);
			} else killMusic();
		} else if (changes.contains(volume)) {
			if (sound != null) {
				// TODO Implement, once we are sure rest is working
				// RadioManager.instance.setVolume(sound, volume.getValue());
			}
		}
	}

	private void startPlayClient(String urlValue) {
		try {
			sound = RadioManager.instance.startPlaying(urlValue, xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, 0.5f * volume.get());
			OpenMods.proxy.setNowPlayingTitle(streamName.getValue());
		} catch (RadioException e) {
			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentTranslation(e.getMessage()));
			sound = null;
		}
	}

	private void playStatic() {
		// TODO: verify if it's good method for client side
		worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:radio.activate", 1, 2f);
	}

	private void killMusic() {
		if (sound != null) {
			RadioManager.instance.stopPlaying(sound);
			sound = null;
		}
	}

	@Override
	public void onNeighbourChanged() {
		if (!worldObj.isRemote) {
			final boolean isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
			this.isPowered.set(isPowered);
			this.volume.set(worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord) / 15f);
			sync();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		killMusic();
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			ItemStack held = player.getHeldItem();
			ItemStack current = inventory.getStackInSlot(0);
			if (held == null && current != null) {
				player.setCurrentItemOrArmor(0, current.copy());
				inventory.setInventorySlotContents(0, null);
				markDirty();
				updateURL(null);
				return true;
			} else if (held != null) {
				Item heldItem = held.getItem();
				if (heldItem instanceof ItemTunedCrystal) {
					inventory.setInventorySlotContents(0, held.copy());
					player.setCurrentItemOrArmor(0, current);
					markDirty();
					updateURL(held);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void onChunkUnload() {
		killMusic();
	}

	@Override
	public void onBlockBroken() {
		killMusic();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

	public Integer getCrystalColor() {
		if (Strings.isNullOrEmpty(url.getValue())) return null;
		ColorMeta color = ColorUtils.vanillaToColor(crystalColor.get());
		return color != null? color.rgb : null;
	}

}
