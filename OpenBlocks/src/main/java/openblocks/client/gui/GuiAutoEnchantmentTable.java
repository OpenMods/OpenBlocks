package openblocks.client.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import openblocks.common.FluidXpUtils;
import openblocks.common.container.ContainerAutoEnchantmentTable;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable.AutoSlots;
import openblocks.rpc.ILevelChanger;
import openmods.gui.GuiConfigurableSlots;
import openmods.gui.Icon;
import openmods.gui.component.BaseComposite;
import openmods.gui.component.GuiComponentLabel;
import openmods.gui.component.GuiComponentPanel;
import openmods.gui.component.GuiComponentSlider;
import openmods.gui.component.GuiComponentTab;
import openmods.gui.component.GuiComponentTankLevel;
import openmods.gui.component.GuiComponentToggleButton;
import openmods.gui.listener.IMouseDownListener;
import openmods.gui.logic.ValueCopyAction;
import openmods.utils.MiscUtils;
import openmods.utils.TranslationUtils;
import openmods.utils.VanillaEnchantLogic.Level;

public class GuiAutoEnchantmentTable extends GuiConfigurableSlots<TileEntityAutoEnchantmentTable, ContainerAutoEnchantmentTable, AutoSlots> {

	public GuiAutoEnchantmentTable(ContainerAutoEnchantmentTable container) {
		super(container, 176, 175, "openblocks.gui.autoenchantmenttable");
	}

	@Override
	protected Iterable<AutoSlots> getSlots() {
		return ImmutableList.of(AutoSlots.toolInput, AutoSlots.lapisInput, AutoSlots.xp, AutoSlots.output);
	}

	private static final ResourceLocation VANILLA_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");

	private static final Map<Level, Icon> icons = ImmutableMap.of(
			Level.L1, Icon.createSheetIcon(VANILLA_TEXTURE, 16 * 0, 223, 16, 16),
			Level.L2, Icon.createSheetIcon(VANILLA_TEXTURE, 16 * 1, 223, 16, 16),
			Level.L3, Icon.createSheetIcon(VANILLA_TEXTURE, 16 * 2, 223, 16, 16));

	private static final Icon LAPIS_SLOT = Icon.createSheetIcon(VANILLA_TEXTURE, 34, 46, 18, 18);

	@Override
	protected void addCustomizations(BaseComposite root) {
		final TileEntityAutoEnchantmentTable te = getContainer().getOwner();

		final ILevelChanger rpc = te.createClientRpcProxy(ILevelChanger.class);

		((GuiComponentPanel)root).setSlotRenderer(1, GuiComponentPanel.customIconSlot(LAPIS_SLOT, -1, -1));

		final GuiComponentSlider slider = new GuiComponentSlider(44, 39, 45, 1, 30, 1, true, TranslationUtils.translateToLocal("openblocks.gui.limit"));
		slider.setListener(value -> rpc.changePowerLimit(value.intValue()));

		addSyncUpdateListener(ValueCopyAction.create(te.getLevelProvider(), slider, Integer::doubleValue));

		root.addComponent(slider);

		final GuiComponentLabel maxPower = new GuiComponentLabel(40, 25, "0");
		maxPower.setMaxWidth(100);
		addSyncUpdateListener(ValueCopyAction.create(te.getAvailablePowerProvider(), value -> maxPower.setText(TranslationUtils.translateToLocalFormatted("openblocks.gui.available_power", value))));
		root.addComponent(maxPower);

		final GuiComponentTankLevel tankLevel = new GuiComponentTankLevel(140, 30, 17, 37, TileEntityAutoEnchantmentTable.MAX_STORED_LEVELS);
		tankLevel.setDisplayFluidNameInTooltip(false);
		addSyncUpdateListener(ValueCopyAction.create(te.getFluidProvider(), tankLevel.fluidReceiver(), FluidXpUtils.FLUID_TO_LEVELS));
		root.addComponent(tankLevel);

		final GuiComponentToggleButton<Level> levelSelect = new GuiComponentToggleButton<>(16, 60, 0xFFFFFF, icons);
		levelSelect.setListener((IMouseDownListener)(component, x, y, button) -> {
			final Level currentValue = te.getSelectedLevelProvider().getValue();
			final Level[] values = Level.values();
			final Level nextValue = values[(currentValue.ordinal() + 1) % values.length];
			rpc.changeLevel(nextValue);
		});
		addSyncUpdateListener(ValueCopyAction.create(te.getSelectedLevelProvider(), levelSelect));
		root.addComponent(levelSelect);
	}

	@Override
	protected GuiComponentTab createTab(AutoSlots slot) {
		switch (slot) {
			case toolInput:
				return new GuiComponentTab(StandardPalette.blue.getColor(), new ItemStack(Items.DIAMOND_PICKAXE, 1), 100, 100);
			case lapisInput:
				return new GuiComponentTab(StandardPalette.blue.getColor(), new ItemStack(Items.DYE, 1, 4), 100, 100);
			case output: {
				ItemStack enchantedAxe = new ItemStack(Items.DIAMOND_PICKAXE, 1);
				enchantedAxe.addEnchantment(Enchantments.FORTUNE, 1);
				return new GuiComponentTab(StandardPalette.lightblue.getColor(), enchantedAxe, 100, 100);
			}
			case xp:
				return new GuiComponentTab(StandardPalette.green.getColor(), new ItemStack(Items.BUCKET, 1), 100, 100);
			default:
				throw MiscUtils.unhandledEnum(slot);
		}
	}

	@Override
	protected GuiComponentLabel createLabel(AutoSlots slot) {
		switch (slot) {
			case lapisInput:
			case toolInput:
				return new GuiComponentLabel(22, 82, TranslationUtils.translateToLocal("openblocks.gui.autoextract"));
			case output:
				return new GuiComponentLabel(22, 82, TranslationUtils.translateToLocal("openblocks.gui.autoeject"));
			case xp:
				return new GuiComponentLabel(22, 82, TranslationUtils.translateToLocal("openblocks.gui.autodrink"));
			default:
				throw MiscUtils.unhandledEnum(slot);

		}
	}

}
