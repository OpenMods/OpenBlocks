package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openblocks.client.radio.RadioManager;
import openblocks.common.item.ItemTunedCrystal;
import openmods.GenericInventory;
import openmods.api.*;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableBoolean;
import openmods.sync.SyncableString;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.ItemUtils;

import com.google.common.base.Strings;

public class TileEntityRadio extends SyncedTileEntity implements IActivateAwareTile, IBreakAwareTile, INeighbourAwareTile, IInventoryCallback, IExtendable {

	private SyncableString url;
	private SyncableBoolean isPowered;
	private String soundId;

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
	}

	@Override
	protected void createSyncedFields() {
		url = new SyncableString();
		isPowered = new SyncableBoolean();
	}

	@Override
	protected void initialize() {
		if (worldObj.isRemote) return;
		onNeighbourChanged(0);
	}

	private void updateURL(ItemStack stack) {
		if (stack == null) url.clear();
		else {
			NBTTagCompound tag = ItemUtils.getItemTag(stack);
			url.setValue(tag.getString(ItemTunedCrystal.TAG_URL));
		}
		sync();
	}

	@Override
	public void onInventoryChanged(IInventory inventory, int slotNumber) {
		if (worldObj.isRemote) return;
		updateURL(inventory.getStackInSlot(0));
	}

	private void killMusic() {
		if (soundId != null) {
			RadioManager.instance.stopPlaying(soundId);
			soundId = null;
		}
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {
		syncCommon(changes);
	}

	@Override
	public void onServerSync(Set<ISyncableObject> changed) {
		syncCommon(changed);
	}

	protected void syncCommon(Set<ISyncableObject> changes) {
		if (changes.isEmpty()) return;
		final boolean hasPower = isPowered.getValue();
		final String urlValue = url.getValue();

		final boolean hasUrl = !Strings.isNullOrEmpty(urlValue);
		final boolean canPlay = hasUrl && hasPower;

		if (worldObj.isRemote) {
			if (canPlay) soundId = RadioManager.instance.startPlaying(soundId, urlValue, xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f);
			else killMusic();
		} else if (canPlay) playStatic();
	}

	private void playStatic() {
		worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:radio", 1, 2f);
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		if (!worldObj.isRemote) {
			final boolean isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
			this.isPowered.setValue(isPowered);
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
				url.clear();
			} else if (held != null) {
				Item heldItem = held.getItem();
				if (heldItem instanceof ItemTunedCrystal) {
					inventory.setInventorySlotContents(0, held.copy());
					player.setCurrentItemOrArmor(0, current);
					updateURL(held);
				}
			}

			return true;
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

}
