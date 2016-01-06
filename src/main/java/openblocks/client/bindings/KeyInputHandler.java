package openblocks.client.bindings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
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
		MinecraftForge.EVENT_BUS.register(this);
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
