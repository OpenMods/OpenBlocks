package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiPaintMixer;
import openblocks.common.GenericInventory;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.api.IHasGui;
import openblocks.common.api.IInventoryCallback;
import openblocks.common.container.ContainerPaintMixer;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableFloat;
import openblocks.sync.SyncableInt;
import openblocks.sync.SyncableProgress;
import openblocks.utils.ColorUtils;

public class TileEntityPaintMixer extends SyncedTileEntity implements IInventory, IHasGui, IActivateAwareTile, IInventoryCallback {
	
	private static final ItemStack PAINT_CAN = new ItemStack(OpenBlocks.Blocks.paintCan);
	private static final ItemStack WATER_BUCKET = new ItemStack(Item.bucketMilk);
	private static final int PROGRESS_TICKS = 500;
	private static final ItemStack DYE_BLACK = new ItemStack(Item.dyePowder, 1, 0);
	private static final ItemStack DYE_CYAN = new ItemStack(Item.dyePowder, 1, 6);
	private static final ItemStack DYE_MAGENTA = new ItemStack(Item.dyePowder, 1, 13);
	private static final ItemStack DYE_YELLOW = new ItemStack(Item.dyePowder, 1, 11);
	
	public static enum Slots {
		input,
		output,
		dyeCyan,
		dyeMagenta,
		dyeYellow,
		dyeBlack
	}
	
	public enum Flags {
		hasPaint
	}
	
	private SyncableInt color;
	private boolean enabled;
	private SyncableProgress progress;
	private SyncableFlags flags;
	private int chosenColor;
	// These could be optimized with a byte array later
	// Not important for release
	// Levels should be 0-2, so that if there is 0.3 left, 1 can be consumed and not overflow ;)
	
	public SyncableFloat lvlCyan, lvlMagenta, lvlYellow, lvlBlack; /* Black is key ;) */
	
	public TileEntityPaintMixer() {
		setInventory(new GenericInventory("paintmixer", true, 6));
		inventory.addCallback(this);
	}
	
	public void initialize() {
		if (!worldObj.isRemote) {
			sync();
		}
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			
			if (chosenColor != color.getValue()) {
				progress.reset();
				enabled = false;
			}
			
			if (enabled) {
				if (!hasValidInput() || !hasCMYK() || hasOutputStack()) {
					progress.reset();
					enabled = false;
					return;
				}
				if (!progress.isComplete()) {
					progress.increase();
				} else {
					inventory.decrStackSize(Slots.input.ordinal(), 1);
					consumeInk();
					ItemStack output = new ItemStack(OpenBlocks.Blocks.paintCan);
					setPaintCanColor(output);
					inventory.setInventorySlotContents(Slots.output.ordinal(), output);
					progress.reset();
					enabled = false;
					sync();
				}
			}
			checkAutoConsumption();
		}
	}
	

	private void checkAutoConsumption() {
		if(lvlCyan.getValue() <= 1f) { /* We can store 2.0, so <= */
			if(tryUseInk(Slots.dyeCyan, 1)) {
				lvlCyan.setValue(lvlCyan.getValue() + 1f);
			}
		}		
		if(lvlMagenta.getValue() <= 1f) {
			if(tryUseInk(Slots.dyeMagenta, 1)) {
				lvlMagenta.setValue(lvlMagenta.getValue() + 1f);
			}
		}		
		if(lvlYellow.getValue() <= 1f) {
			if(tryUseInk(Slots.dyeYellow, 1)) {
				lvlYellow.setValue(lvlYellow.getValue() + 1f);
			}
		}		
		if(lvlBlack.getValue() <= 1f) {
			if(tryUseInk(Slots.dyeBlack, 1)) {
				lvlBlack.setValue(lvlBlack.getValue() + 1f);
			}
		}
		
	}

	public boolean hasOutputStack() {
		return inventory.getStackInSlot(Slots.output) != null;
	}
	
	public boolean hasCMYK() {
		return hasSufficientInk();
	}
	
	private void consumeInk() {
		ColorUtils.CYMK cymk = new ColorUtils.RGB(color.getValue()).toCYMK();
		lvlCyan.setValue(lvlCyan.getValue() - cymk.getCyan());
		lvlBlack.setValue(lvlBlack.getValue() - cymk.getKey());
		lvlYellow.setValue(lvlYellow.getValue() - cymk.getYellow());
		lvlMagenta.setValue(lvlMagenta.getValue() - cymk.getMagenta());
	}
	
	private boolean hasSufficientInk() {
		ColorUtils.CYMK cymk = new ColorUtils.RGB(color.getValue()).toCYMK();
		if(cymk.getCyan() > lvlCyan.getValue()) {
			if(tryUseInk(Slots.dyeCyan, 1)) {
				lvlCyan.setValue(lvlCyan.getValue() + 1f);
			}else {
				return false;
			}
		}
		if(cymk.getYellow() > lvlYellow.getValue()) {
			if(tryUseInk(Slots.dyeYellow, 1)) {
				lvlYellow.setValue(lvlYellow.getValue() + 1f);
			}else {
				return false;
			}
		}
		if(cymk.getMagenta() > lvlMagenta.getValue()) {
			if(tryUseInk(Slots.dyeMagenta, 1)) {
				lvlMagenta.setValue(lvlMagenta.getValue() + 1f);
			}else {
				return false;
			}
		}
		if(cymk.getKey() > lvlBlack.getValue()) {
			if(tryUseInk(Slots.dyeBlack, 1)) {
				lvlBlack.setValue(lvlBlack.getValue() + 1f);
			}else {
				return false;
			}
		}
		return true;		
	}
	
	public boolean tryUseInk(Slots slot, int consume) {
		switch(slot) {
			case dyeBlack: if(!hasStack(slot, DYE_BLACK)) return false; break;
			case dyeMagenta: if(!hasStack(slot, DYE_MAGENTA)) return false; break;
			case dyeYellow: if(!hasStack(slot, DYE_YELLOW)) return false; break;
			case dyeCyan: if(!hasStack(slot, DYE_CYAN)) return false; break;
			default:
				return false;
		}
		return inventory.decrStackSize(slot.ordinal(), consume) != null;
	}
	
	public boolean hasStack(Slots slot, ItemStack stack) {
		ItemStack gotStack = inventory.getStackInSlot(slot);
		if (gotStack == null) {
			return false;
		}
		return gotStack.isItemEqual(stack);
	}
	
	@Override
	protected void createSyncedFields() {
		color = new SyncableInt(0xFF0000);
		flags = new SyncableFlags();
		progress = new SyncableProgress(PROGRESS_TICKS);
		lvlBlack = new SyncableFloat();
		lvlCyan = new SyncableFloat();
		lvlMagenta = new SyncableFloat();
		lvlYellow = new SyncableFloat();		
	}
	
	public SyncableInt getColor() {
		return color;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) { return false; }
		if (!worldObj.isRemote) {
			openGui(player);
		}
		return true;
	}
    
    public boolean hasPaint() {
    	return flags.get(Flags.hasPaint);
    }
	
	private void setPaintCanColor(ItemStack stack) {
		if (stack != null && stack.isItemEqual(PAINT_CAN)) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("color", color.getValue());
			stack.setTagCompound(tag);
		}
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
	}

    public void onSync() {
    }

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return inventory.getInvName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return inventory.isInvNameLocalized();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory.isItemValidForSlot(i, itemstack);
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerPaintMixer(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiPaintMixer(new ContainerPaintMixer(player.inventory, this));
	}

	public void tryStartMixer() {
		if (!worldObj.isRemote) {
			enabled = true;
			chosenColor = color.getValue();
		}
	}

	public SyncableProgress getProgress() {
		return progress;
	}
	
	public boolean isEnabled() {
		return progress.getValue() > 0;
	}
	
	public boolean hasValidInput() {
		return hasStack(Slots.input, PAINT_CAN) || hasStack(Slots.input, WATER_BUCKET);
	}

	@Override
	public void onInventoryChanged(IInventory invent, int slotNumber) {
		if (worldObj.isRemote) {
			flags.set(Flags.hasPaint, hasValidInput() || hasOutputStack());
			sync();
		}
	}

}
