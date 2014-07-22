package openblocks.client.gui;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import openblocks.common.container.ContainerXPBottler;
import openblocks.common.tileentity.TileEntityXPBottler;
import openblocks.common.tileentity.TileEntityXPBottler.AutoSlots;
import openmods.gui.GuiConfigurableSlots;
import openmods.gui.component.*;

import com.google.common.collect.ImmutableList;

public class GuiXPBottler extends GuiConfigurableSlots<TileEntityXPBottler, ContainerXPBottler, TileEntityXPBottler.AutoSlots> {

	public GuiXPBottler(ContainerXPBottler container) {
		super(container, 176, 151, "openblocks.gui.xpbottler");
	}

	@Override
	protected Iterable<AutoSlots> getSlots() {
		return ImmutableList.of(AutoSlots.input, AutoSlots.xp, AutoSlots.output);
	}

	@Override
	protected void addCustomizations(BaseComponent root) {
		TileEntityXPBottler te = getContainer().getOwner();
		root.addComponent(new GuiComponentTankLevel(140, 18, 17, 37, te.getTank()));
		root.addComponent(new GuiComponentProgress(72, 33, 0));
	}

	@Override
	protected GuiComponentTab createTab(AutoSlots slot) {
		switch (slot) {
			case input:
				return new GuiComponentTab(StandardPalette.blue.getColor(), new ItemStack(Items.glass_bottle, 1), 100, 100);
			case output:
				return new GuiComponentTab(StandardPalette.lightblue.getColor(), new ItemStack(Items.experience_bottle), 100, 100);
			case xp:
				return new GuiComponentTab(StandardPalette.green.getColor(), new ItemStack(Items.bucket), 100, 100);
			default:
				throw new IllegalArgumentException(slot.toString());
		}
	}

	@Override
	protected GuiComponentLabel createLabel(AutoSlots slot) {
		switch (slot) {
			case input:
				return new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoextract"));
			case output:
				return new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autoeject"));
			case xp:
				return new GuiComponentLabel(22, 82, StatCollector.translateToLocal("openblocks.gui.autodrink"));
			default:
				throw new IllegalArgumentException(slot.toString());

		}
	}

}
