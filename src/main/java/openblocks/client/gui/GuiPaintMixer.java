package openblocks.client.gui;

import java.util.List;

import net.minecraft.util.StatCollector;
import openblocks.common.container.ContainerPaintMixer;
import openblocks.common.tileentity.TileEntityPaintMixer;
import openblocks.common.tileentity.TileEntityPaintMixer.DyeSlot;
import openblocks.rpc.IColorChanger;
import openmods.api.IValueProvider;
import openmods.gui.SyncedGuiContainer;
import openmods.gui.component.*;
import openmods.gui.component.GuiComponentPalettePicker.PaletteEntry;
import openmods.gui.listener.IMouseDownListener;
import openmods.gui.listener.IValueChangedListener;
import openmods.gui.logic.IValueUpdateAction;
import openmods.gui.logic.ValueCopyAction;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class GuiPaintMixer extends SyncedGuiContainer<ContainerPaintMixer> {

	private static final int CYAN = 0xFF4B9FC1;
	private static final int MAGENTA = 0xFFDB7AD5;
	private static final int YELLOW = 0xFFE7E72A;
	private static final int KEY = 0xFF000000;

	private int selectedColor;

	public GuiPaintMixer(ContainerPaintMixer container) {
		super(container, 176, 200, "openblocks.gui.paintmixer");
		TileEntityPaintMixer mixer = container.getOwner();
		final IColorChanger rpcIntf = mixer.createRpcProxy();

		root.addComponent(new GuiComponentRect(121, 74, 20, 20, CYAN));
		root.addComponent(new GuiComponentRect(141, 74, 20, 20, MAGENTA));
		root.addComponent(new GuiComponentRect(121, 94, 20, 20, YELLOW));
		root.addComponent(new GuiComponentRect(141, 94, 20, 20, KEY));

		{
			final GuiComponentLevel level = new GuiComponentLevel(118, 74 + 6, 2, 14, CYAN, 0xFF888888, 0f, 2f, 0);
			addSyncUpdateListener(ValueCopyAction.create(mixer.getDyeSlot(DyeSlot.cyan), level));
			root.addComponent(level);
		}

		{
			final GuiComponentLevel level = new GuiComponentLevel(141 + 21, 74 + 6, 2, 14, MAGENTA, 0xFF888888, 0f, 2f, 0);
			addSyncUpdateListener(ValueCopyAction.create(mixer.getDyeSlot(DyeSlot.magenta), level));
			root.addComponent(level);
		}

		{
			final GuiComponentLevel level = new GuiComponentLevel(118, 94 + 6, 2, 14, YELLOW, 0xFF888888, 0f, 2f, 0);
			addSyncUpdateListener(ValueCopyAction.create(mixer.getDyeSlot(DyeSlot.yellow), level));
			root.addComponent(level);
		}

		{
			final GuiComponentLevel level = new GuiComponentLevel(141 + 21, 94 + 6, 2, 14, KEY, 0xFF888888, 0f, 2f, 0);
			addSyncUpdateListener(ValueCopyAction.create(mixer.getDyeSlot(DyeSlot.black), level));
			root.addComponent(level);
		}

		{
			GuiComponentProgress progress = new GuiComponentProgress(125, 43, TileEntityPaintMixer.PROGRESS_TICKS);
			addSyncUpdateListener(ValueCopyAction.create(mixer.getProgress(), progress.progressReceiver()));
			root.addComponent(progress);
		}

		{
			GuiComponentTextButton buttonMix = new GuiComponentTextButton(125, 57, 30, 13, 0xFFFFFF);
			buttonMix.setText("Mix")
					.setListener(new IMouseDownListener() {
						@Override
						public void componentMouseDown(BaseComponent component, int x, int y, int button) {
							rpcIntf.changeColor(selectedColor);
						}
					});
			root.addComponent(buttonMix);
		}

		{
			final GuiComponentTextbox textbox = new GuiComponentTextbox(65, 90, 44, 10);
			root.addComponent(textbox);

			final GuiComponentColorPicker colorPicker = new GuiComponentColorPicker(10, 20);
			root.addComponent(colorPicker);

			final GuiComponentSlider slider = new GuiComponentSlider(10, 75, 100, 0, 255, 0, false);
			root.addComponent(slider);

			final GuiComponentRect colorBox = new GuiComponentRect(10, 90, 45, 10, 0xFFFFFF);
			root.addComponent(colorBox);

			final GuiComponentPalettePicker palettePicker = new GuiComponentPalettePicker(112, 25);
			palettePicker.setAreaSize(5);
			palettePicker.setDrawTooltip(true);
			root.addComponent(palettePicker);

			{
				final List<PaletteEntry> vanillaPalette = Lists.newArrayList();
				for (ColorMeta color : ColorUtils.getAllColors()) {
					vanillaPalette.add(new PaletteEntry(color.vanillaBlockId, color.rgb, StatCollector.translateToLocal(color.unlocalizedName)));
				}
				palettePicker.setPalette(vanillaPalette);
			}

			palettePicker.setListener(new IValueChangedListener<PaletteEntry>() {
				@Override
				public void valueChanged(PaletteEntry value) {
					final int rgb = value.rgb;
					selectedColor = rgb;

					textbox.setValue(String.format("%06X", rgb));
					colorBox.setValue(rgb);
					colorPicker.setValue(rgb);
					slider.setValue(colorPicker.tone);
				}
			});

			textbox.setListener(new IValueChangedListener<String>() {
				@Override
				public void valueChanged(String value) {
					try {
						int parsed = Integer.parseInt(value, 16);

						selectedColor = parsed;
						colorPicker.setValue(parsed);
						slider.setValue(colorPicker.tone);
						colorBox.setValue(parsed);
					} catch (NumberFormatException e) {
						// NO-OP, user derp
					}
				}
			});

			colorPicker.setListener(new IValueChangedListener<Integer>() {
				@Override
				public void valueChanged(Integer value) {
					selectedColor = value;
					textbox.setValue(String.format("%06X", value));
					colorBox.setValue(value);
				}
			});

			slider.setListener(new IValueChangedListener<Integer>() {
				@Override
				public void valueChanged(Integer value) {
					colorPicker.tone = value;
					int color = colorPicker.getColor();
					textbox.setValue(String.format("%06X", color));
					colorBox.setValue(color);
				}
			});

			final IValueProvider<Integer> color = mixer.getColor();

			addSyncUpdateListener(new IValueUpdateAction() {
				@Override
				public Iterable<?> getTriggers() {
					return ImmutableList.of(color);
				}

				@Override
				public void execute() {
					int value = color.getValue();
					selectedColor = value;
					textbox.setValue(String.format("%06X", value));
					colorPicker.setValue(value);
					slider.setValue(colorPicker.tone);
					colorBox.setValue(value);
				}
			});

			dispatcher().triggerAll();
		}

	}
}
