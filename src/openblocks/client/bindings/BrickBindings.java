package openblocks.client.bindings;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import openblocks.Config;
import openblocks.events.PlayerActionEvent;
import openmods.binding.ActionBind;

public class BrickBindings extends ActionBind {

	@Override
	public KeyBinding createBinding() {
		return new KeyBinding("openblocks.keybind.drop_brick", Keyboard.KEY_B);
	}

	@Override
	public void keyDown(boolean tickEnd, boolean isRepeat) {
		if (tickEnd && isNastyStuffAllowed()) new PlayerActionEvent(PlayerActionEvent.Type.BOO).sendToServer();
	}

	private static boolean isNastyStuffAllowed() {
		final GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
		return Config.fartTypying || currentScreen == null /*|| currentScreen.allowUserInput*/;
	}

}
