package openblocks.client;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
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
	@Deprecated
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}
}
