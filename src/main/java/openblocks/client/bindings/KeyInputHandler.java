package openblocks.client.bindings;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import openblocks.Config;
import openblocks.events.PlayerActionEvent;
import org.lwjgl.input.Keyboard;

public class KeyInputHandler {

	private KeyBinding brickBinding;

	private boolean brickKeyPressed;

	public void setup() {
		if (!Config.soSerious) {
			brickBinding = new KeyBinding("openblocks.keybind.drop_brick", Keyboard.KEY_B, "openblocks.keybind.category");
			ClientRegistry.registerKeyBinding(brickBinding);
		}
		FMLCommonHandler.instance().bus().register(this);
	}

	private static boolean isNastyStuffAllowed() {
		return !Config.soSerious && (Config.fartTypying || Minecraft.getMinecraft().currentScreen == null);
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent evt) {
		if (brickBinding != null && brickBinding.isPressed()) {
			if (!brickKeyPressed) {
				if (isNastyStuffAllowed()) new PlayerActionEvent(PlayerActionEvent.Type.BOO).sendToServer();
				brickKeyPressed = true;
			}
		} else brickKeyPressed = false;
	}

}
