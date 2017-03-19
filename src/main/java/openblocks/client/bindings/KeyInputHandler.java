package openblocks.client.bindings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import openblocks.Config;
import openblocks.common.Vario;
import openblocks.events.PlayerActionEvent;
import org.lwjgl.input.Keyboard;

public class KeyInputHandler {

	private KeyBinding brickBinding;
	private KeyBinding varioSwitchBinding;
	private KeyBinding varioVolUpBinding;
	private KeyBinding varioVolDownBinding;

	private boolean brickKeyPressed;
	private boolean varioSwitchKeyPressed;
	private boolean varioVolUpKeyPressed;
	private boolean varioVolDownKeyPressed;

	public void setup() {
		if (!Config.soSerious) {
			brickBinding = new KeyBinding("openblocks.keybind.drop_brick", Keyboard.KEY_B, "openblocks.keybind.category");
			ClientRegistry.registerKeyBinding(brickBinding);
		}

		if (Config.hanggliderEnableThermal) {
			varioSwitchBinding = new KeyBinding("openblocks.keybind.vario_switch", Keyboard.KEY_V, "openblocks.keybind.category");
			varioVolUpBinding = new KeyBinding("openblocks.keybind.vario_vol_up", Keyboard.KEY_NONE, "openblocks.keybind.category");
			varioVolDownBinding = new KeyBinding("openblocks.keybind.vario_vol_down", Keyboard.KEY_NONE, "openblocks.keybind.category");
			ClientRegistry.registerKeyBinding(varioSwitchBinding);
			ClientRegistry.registerKeyBinding(varioVolUpBinding);
			ClientRegistry.registerKeyBinding(varioVolDownBinding);
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
		if (varioSwitchBinding != null && varioSwitchBinding.isPressed()) {
			if (!varioSwitchKeyPressed) {
				Vario.instance.toggle();
				varioSwitchKeyPressed = true;
			}
		} else varioSwitchKeyPressed = false;
		if (varioVolUpBinding != null && varioVolUpBinding.isPressed()) {
			if (!varioVolUpKeyPressed) {
				Vario.instance.incVolume();
				varioVolUpKeyPressed = true;
			}
		} else varioVolUpKeyPressed = false;
		if (varioVolDownBinding != null && varioVolDownBinding.isPressed()) {
			if (!varioVolDownKeyPressed) {
				Vario.instance.decVolume();
				varioVolDownKeyPressed = true;
			}
		} else varioVolDownKeyPressed = false;
	}

}
