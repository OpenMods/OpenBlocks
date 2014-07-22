package openblocks.client.gui;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import openblocks.common.container.ContainerAutoEnchantmentTable;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable.AutoSlots;
import openmods.gui.GuiConfigurableSlots;
import openmods.gui.component.*;

import com.google.common.collect.ImmutableList;

public class GuiAutoEnchantmentTable extends GuiConfigurableSlots<TileEntityAutoEnchantmentTable, ContainerAutoEnchantmentTable, TileEntityAutoEnchantmentTable.AutoSlots> {

	public GuiAutoEnchantmentTable(ContainerAutoEnchantmentTable container) {
		super(container, 176, 175, "openblocks.gui.autoenchantmenttable");
	}

	@Override
	protected Iterable<AutoSlots> getSlots() {
		return ImmutableList.of(AutoSlots.input, AutoSlots.xp, AutoSlots.output);
	}

	@Override
	protected void addCustomizations(BaseComponent root) {
		TileEntityAutoEnchantmentTable te = getContainer().getOwner();
		root.addComponent(new GuiComponentTankLevel(140, 30, 17, 37, te.getTank()));
		root.addComponent(new GuiComponentSlider(44, 39, 45, 1, 30, 0));
	}

	@Override
	protected GuiComponentTab createTab(AutoSlots slot) {
		switch (slot) {
			case input:
				return new GuiComponentTab(StandardPalette.blue.getColor(), new ItemStack(Items.diamond_pickaxe, 1), 100, 100);
			case output: {
				ItemStack enchantedAxe = new ItemStack(Items.diamond_pickaxe, 1);
				enchantedAxe.addEnchantment(Enchantment.fortune, 1);
				return new GuiComponentTab(StandardPalette.lightblue.getColor(), enchantedAxe, 100, 100);
			}
			case xp:
				return new GuiComponentTab(StandardPalette.green.getColor(), new ItemStack(Items.bucket, 1), 100, 100);
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
