package openblocks.client.gui;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openmods.gui.SyncedGuiContainer;
import openmods.gui.component.*;
import openmods.gui.component.GuiComponentSideSelector.ISideSelectedListener;
import openmods.gui.logic.ValueCopyAction;
import openmods.utils.bitmap.IReadableBitMap;
import openmods.utils.bitmap.IWriteableBitMap;

public class GuiVacuumHopper extends SyncedGuiContainer<ContainerVacuumHopper> {
	public GuiVacuumHopper(ContainerVacuumHopper container) {
		super(container, 176, 151, "openblocks.gui.vacuumhopper");
	}

	@Override
	protected BaseComposite createRoot() {
		final TileEntityVacuumHopper te = getContainer().getOwner();

		BaseComposite main = super.createRoot();
		final GuiComponentTankLevel tankLevel = new GuiComponentTankLevel(140, 18, 17, 37, TileEntityVacuumHopper.TANK_CAPACITY);
		addSyncUpdateListener(ValueCopyAction.create(te.getFluidProvider(), tankLevel.fluidReceiver()));
		main.addComponent(tankLevel);

		GuiComponentTabWrapper tabs = new GuiComponentTabWrapper(0, 0, main);

		{
			GuiComponentTab itemTab = new GuiComponentTab(StandardPalette.lightblue.getColor(), new ItemStack(Blocks.chest), 100, 100);
			final GuiComponentSideSelector sideSelector = new GuiComponentSideSelector(15, 15, 40.0, null, 0, te, false);
			wireSideSelector(sideSelector, te.getReadableItemOutputs(), te.getWriteableItemOutputs());

			itemTab.addComponent(new GuiComponentLabel(24, 10, StatCollector.translateToLocal("openblocks.gui.item_outputs")));
			itemTab.addComponent(sideSelector);
			tabs.addComponent(itemTab);
		}

		{
			GuiComponentTab xpTab = new GuiComponentTab(StandardPalette.blue.getColor(), new ItemStack(Items.experience_bottle, 1), 100, 100);
			GuiComponentSideSelector sideSelector = new GuiComponentSideSelector(15, 15, 40.0, null, 0, te, false);
			wireSideSelector(sideSelector, te.getReadableXpOutputs(), te.getWriteableXpOutputs());
			xpTab.addComponent(sideSelector);
			xpTab.addComponent(new GuiComponentLabel(24, 10, StatCollector.translateToLocal("openblocks.gui.xp_outputs")));
			tabs.addComponent(xpTab);
		}

		return tabs;
	}

	private static void wireSideSelector(final GuiComponentSideSelector sideSelector, final IReadableBitMap<ForgeDirection> readableSides, final IWriteableBitMap<ForgeDirection> writeableSides) {
		sideSelector.setListener(new ISideSelectedListener() {
			@Override
			public void onSideToggled(ForgeDirection side, boolean currentState) {
				writeableSides.toggle(side);
			}
		});
	}
}
