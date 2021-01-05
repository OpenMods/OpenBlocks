package openblocks.common.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Items;
import openblocks.client.gui.GuiDrawingTable;
import openblocks.common.StencilPattern;
import openblocks.common.container.ContainerDrawingTable;
import openblocks.common.item.ItemGlyph;
import openblocks.common.item.ItemStencil;
import openblocks.rpc.IStencilCrafter;
import openmods.api.ICustomBreakDrops;
import openmods.api.IValueProvider;
import openmods.fixers.GenericInventoryTeFixerWalker;
import openmods.fixers.RegisterFixer;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.TileEntityInventory;
import openmods.sync.SyncMap;
import openmods.sync.SyncableEnum;
import openmods.sync.SyncableInt;
import openmods.sync.SyncableString;
import openmods.tileentity.SyncedTileEntity;
import org.apache.commons.lang3.ArrayUtils;

@RegisterFixer(GenericInventoryTeFixerWalker.class)
public class TileEntityDrawingTable extends SyncedTileEntity implements IHasGui, IInventoryProvider, IStencilCrafter, ICustomBreakDrops {

	private static final int MAX_PRINT_SIZE = 32;

	public static final int SLOT_INPUT = 0;

	public static final int SLOT_OUTPUT = 1;

	private SyncableEnum<StencilPattern> selectedPattern;

	private SyncableInt selectedGlyph;

	private SyncableEnum<IStencilCrafter.Mode> selectedMode;

	private SyncableString textToPrint;

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "drawingtable", true, 2) {
		@Override
		public boolean isItemValidForSlot(int slotId, ItemStack itemstack) {
			return itemstack.isEmpty() || (slotId == SLOT_INPUT && itemstack.getItem() == Items.unpreparedStencil);
		}

		@Override
		public void onInventoryChanged(int slotNumber) {
			if (Items.unpreparedStencil != null) {
				if (slotNumber == SLOT_INPUT) {
					final ItemStack input = inventoryContents.get(SLOT_INPUT);
					if (input.getItem() == Items.unpreparedStencil) {
						final ItemStack output = createOutput(input.getCount());
						inventoryContents.set(SLOT_OUTPUT, output);
					} else {
						inventoryContents.set(SLOT_OUTPUT, ItemStack.EMPTY);
					}
				} else if (slotNumber == SLOT_OUTPUT) {
					final ItemStack output = inventoryContents.get(SLOT_OUTPUT);
					if (isValidOutput(output)) {
						final ItemStack input = new ItemStack(Items.unpreparedStencil, output.getCount());
						inventoryContents.set(SLOT_INPUT, input);
					} else {
						inventoryContents.set(SLOT_INPUT, ItemStack.EMPTY);
					}
				}
			}
		}

		private boolean isValidOutput(ItemStack item) {
			switch (selectedMode.get()) {
				case GLYPHS:
					return item.getItem() instanceof ItemGlyph;
				case STENCILS:
					return item.getItem() instanceof ItemStencil;
			}

			return false;
		}

		private ItemStack createOutput(int count) {
			switch (selectedMode.get()) {
				case GLYPHS: {
					if (OpenBlocks.Items.glyph != null) { return ItemGlyph.createStack(OpenBlocks.Items.glyph, count, selectedGlyph.get()); }
					break;
				}
				case STENCILS: {
					if (OpenBlocks.Items.stencil != null) { return ItemStencil.createItemStack(OpenBlocks.Items.stencil, count, selectedPattern.get()); }
					break;
				}
			}

			return ItemStack.EMPTY;
		}
	});

	public TileEntityDrawingTable() {}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addSyncListener(changes -> inventory.onInventoryChanged(SLOT_INPUT));
	}

	@Override
	protected void createSyncedFields() {
		selectedMode = SyncableEnum.create(IStencilCrafter.Mode.STENCILS);
		selectedGlyph = new SyncableInt(ArrayUtils.indexOf(ItemGlyph.ALMOST_ASCII, 'A'));
		selectedPattern = SyncableEnum.create(StencilPattern.CREEPER_FACE);
		textToPrint = new SyncableString();
	}

	@Override
	public void selectionUp() {
		switch (selectedMode.get()) {
			case GLYPHS:
				final int glyph = selectedGlyph.get();
				selectedGlyph.set((glyph + 1) % ItemGlyph.ALMOST_ASCII.length);
				break;
			case STENCILS:
				selectedPattern.increment();
				break;
		}
		sync();
	}

	@Override
	public void selectionDown() {
		switch (selectedMode.get()) {
			case GLYPHS:
				final int glyph = selectedGlyph.get();
				selectedGlyph.set(Math.floorMod(glyph - 1, ItemGlyph.ALMOST_ASCII.length));
				break;
			case STENCILS:
				selectedPattern.decrement();
				break;
		}
		sync();
	}

	@Override
	public void cycleMode() {
		selectedMode.increment();
		sync();
	}

	@Override
	public void printGlyphs(String text) {
		if (OpenBlocks.Items.glyph == null) return;

		final ItemStack resources = inventory.getStackInSlot(SLOT_INPUT);
		final int resourceCount = resources.getCount();
		final int letterCount = text.length();

		final float dropX = pos.getX() + 0.5f;
		final float dropY = pos.getY() + 1f;
		final float dropZ = pos.getZ() + 0.5f;

		final int opCount = Math.min(MAX_PRINT_SIZE, Math.min(resourceCount, letterCount));
		int i = 0;
		for (; i < opCount; i++) {
			final char ch = text.charAt(i);
			final int glyph = ArrayUtils.indexOf(ItemGlyph.ALMOST_ASCII, ch);
			if (glyph == -1) {
				break;
			}

			final ItemStack drop = ItemGlyph.createStack(OpenBlocks.Items.glyph, glyph);
			final ItemEntity entityitem = new ItemEntity(getWorld(), dropX, dropY, dropZ, drop);
			entityitem.setDefaultPickupDelay();
			getWorld().spawnEntity(entityitem);
		}

		if (i > 0) {
			resources.setCount(resourceCount - i);
			inventory.setInventorySlotContents(SLOT_INPUT, resources);
		}

		if (letterCount > i) {
			textToPrint.setValue(text.substring(i));
		} else {
			textToPrint.setValue("");
		}
		sync();
	}

	public IValueProvider<IStencilCrafter.Mode> getMode() {
		return selectedMode;
	}

	public IValueProvider<String> getTextToPrint() {
		return textToPrint;
	}

	@Override
	public Object getServerGui(PlayerEntity player) {
		return new ContainerDrawingTable(player.inventory, this);
	}

	@Override
	public Object getClientGui(PlayerEntity player) {
		return new GuiDrawingTable(new ContainerDrawingTable(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(PlayerEntity player) {
		return true;
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag = super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		return tag;
	}

	@Override
	public void readFromNBT(CompoundNBT tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

	@Override
	public List<ItemStack> getDrops(List<ItemStack> originalDrops) {
		List<ItemStack> drops = Lists.newArrayList();
		drops.add(inventory.getStackInSlot(SLOT_INPUT));
		return drops; // original drops ignored
	}

}
