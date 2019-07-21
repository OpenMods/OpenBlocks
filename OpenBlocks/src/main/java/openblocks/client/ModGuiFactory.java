package openblocks.client;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.fml.client.IModGuiFactory;
import openblocks.OpenBlocks;
import openmods.config.gui.OpenModsConfigScreen;

public class ModGuiFactory implements IModGuiFactory {

	public static class ConfigScreen extends OpenModsConfigScreen {
		public ConfigScreen(Screen parent) {
			super(parent, OpenBlocks.MODID, OpenBlocks.NAME);
		}
	}

	@Override
	public void initialize(Minecraft minecraftInstance) {}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return ImmutableSet.of();
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public Screen createConfigGui(Screen parentScreen) {
		return new ConfigScreen(parentScreen);
	}
}
