package openblocks.client.gui;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openmods.gui.SyncedGuiContainer;
import openmods.gui.component.BaseComposite;
import openmods.gui.component.GuiComponentLabel;
import openmods.gui.component.GuiComponentSideSelector;
import openmods.gui.component.GuiComponentTab;
import openmods.gui.component.GuiComponentTabWrapper;
import openmods.gui.component.GuiComponentTankLevel;
import openmods.gui.logic.ValueCopyAction;
import openmods.utils.TranslationUtils;
import openmods.utils.bitmap.IReadableBitMap;
import openmods.utils.bitmap.IWriteableBitMap;

public class GuiVacuumHopper extends SyncedGuiContainer<ContainerVacuumHopper> {
	public GuiVacuumHopper(ContainerVacuumHopper container, PlayerInventory player, ITextComponent name) {
		super(container, player, name, 176, 151);
	}

	@Override
	protected BaseComposite createRoot() {
		final TileEntityVacuumHopper te = getContainer().getOwner();

		BaseComposite main = super.createRoot();
		final GuiComponentTankLevel tankLevel = new GuiComponentTankLevel(140, 18, 17, 37, TileEntityVacuumHopper.TANK_CAPACITY);
		addSyncUpdateListener(ValueCopyAction.create(te.getFluidProvider(), tankLevel.fluidReceiver()));
		main.addComponent(tankLevel);

		GuiComponentTabWrapper tabs = new GuiComponentTabWrapper(0, 0, main);

		final BlockState state = te.getWorld().getBlockState(te.getPos());
		{
			GuiComponentTab itemTab = new GuiComponentTab(StandardPalette.LIGHT_BLUE.color, new ItemStack(Blocks.CHEST), 100, 100);
			final GuiComponentSideSelector sideSelector = new GuiComponentSideSelector(15, 15, 40.0f, state, te, false);
			wireSideSelector(sideSelector, te.getReadableItemOutputs(), te.getWriteableItemOutputs());

			itemTab.addComponent(new GuiComponentLabel(24, 10, TranslationUtils.translateToLocal("openblocks.gui.item_outputs")));
			itemTab.addComponent(sideSelector);
			tabs.addComponent(itemTab);
		}

		{
			GuiComponentTab xpTab = new GuiComponentTab(StandardPalette.BLUE.color, new ItemStack(Items.EXPERIENCE_BOTTLE, 1), 100, 100);
			GuiComponentSideSelector sideSelector = new GuiComponentSideSelector(15, 15, 40.0f, state, te, false);
			wireSideSelector(sideSelector, te.getReadableXpOutputs(), te.getWriteableXpOutputs());
			xpTab.addComponent(sideSelector);
			xpTab.addComponent(new GuiComponentLabel(24, 10, TranslationUtils.translateToLocal("openblocks.gui.xp_outputs")));
			tabs.addComponent(xpTab);
		}

		return tabs;
	}

	private static void wireSideSelector(final GuiComponentSideSelector sideSelector, final IReadableBitMap<Direction> readableSides, final IWriteableBitMap<Direction> writeableSides) {
		sideSelector.setListener((side, currentState) -> writeableSides.toggle(side));
	}
}
