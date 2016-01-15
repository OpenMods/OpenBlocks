package openblocks.client.gui;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import openblocks.common.container.ContainerAutoAnvil;
import openblocks.common.tileentity.TileEntityAutoAnvil;
import openblocks.common.tileentity.TileEntityAutoAnvil.AutoSlots;
import openmods.gui.GuiConfigurableSlots;
import openmods.gui.IComponentParent;
import openmods.gui.component.*;
import openmods.gui.logic.ValueCopyAction;
import openmods.utils.MiscUtils;

import com.google.common.collect.ImmutableList;

public class GuiAutoAnvil extends GuiConfigurableSlots<TileEntityAutoAnvil, ContainerAutoAnvil, TileEntityAutoAnvil.AutoSlots> {

	public GuiAutoAnvil(ContainerAutoAnvil container) {
		super(container, 176, 175, "openblocks.gui.autoanvil");
	}

	@Override
	protected void addCustomizations(IComponentParent parent, BaseComposite main) {
		TileEntityAutoAnvil te = getContainer().getOwner();
		main.addComponent(new GuiComponentSprite(parent, 80, 34, GuiComponentSprite.Sprites.hammer));
		main.addComponent(new GuiComponentSprite(parent, 36, 41, GuiComponentSprite.Sprites.plus));

		final GuiComponentTankLevel tankLevel = new GuiComponentTankLevel(parent, 140, 30, 17, 37, TileEntityAutoAnvil.TANK_CAPACITY);
		addSyncUpdateListener(ValueCopyAction.create(te.getFluidProvider(), tankLevel.fluidReceiver()));

		main.addComponent(tankLevel);
	}

	@Override
	protected Iterable<AutoSlots> getSlots() {
		return ImmutableList.of(AutoSlots.tool, AutoSlots.modifier, AutoSlots.xp, AutoSlots.output);
	}

	@Override
	protected GuiComponentTab createTab(IComponentParent parent, AutoSlots slot) {
		switch (slot) {
			case modifier:
				return new GuiComponentTab(parent, StandardPalette.lightblue.getColor(), new ItemStack(Items.enchanted_book, 1), 100, 100);
			case output: {
				ItemStack enchantedAxe = new ItemStack(Items.diamond_pickaxe, 1);
				enchantedAxe.addEnchantment(Enchantment.fortune, 1);
				return new GuiComponentTab(parent, StandardPalette.green.getColor(), enchantedAxe, 100, 100);
			}
			case tool:
				return new GuiComponentTab(parent, StandardPalette.blue.getColor(), new ItemStack(Items.diamond_pickaxe, 1), 100, 100);
			case xp:
				return new GuiComponentTab(parent, StandardPalette.yellow.getColor(), new ItemStack(Items.bucket, 1), 100, 100);
			default:
				throw MiscUtils.unhandledEnum(slot);
		}
	}

	@Override
	protected GuiComponentLabel createLabel(IComponentParent parent, AutoSlots slot) {
		switch (slot) {
			case modifier:
			case tool:
				return new GuiComponentLabel(parent, 22, 82, StatCollector.translateToLocal("openblocks.gui.autoextract"));
			case output:
				return new GuiComponentLabel(parent, 22, 82, StatCollector.translateToLocal("openblocks.gui.autoeject"));
			case xp:
				return new GuiComponentLabel(parent, 22, 82, StatCollector.translateToLocal("openblocks.gui.autodrink"));
			default:
				throw MiscUtils.unhandledEnum(slot);

		}
	}

}
