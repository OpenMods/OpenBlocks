package openblocks.client;

import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.client.IModGuiFactory;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import openblocks.OpenBlocks;
import openmods.config.gui.OpenModsConfigScreen;

public class ModGuiFactory implements IModGuiFactory {

	public static class ConfigScreen extends OpenModsConfigScreen {
		public ConfigScreen(GuiScreen parent) {
			super(parent, OpenBlocks.MODID, OpenBlocks.NAME);
		}
	}

	@Override
	public void initialize(Minecraft minecraftInstance) {}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return ConfigScreen.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return ImmutableSet.of();
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}
}
