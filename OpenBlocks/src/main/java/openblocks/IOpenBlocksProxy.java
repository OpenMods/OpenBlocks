package openblocks;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public interface IOpenBlocksProxy {
	default void eventInit() {
	}

	default void clientInit(FMLClientSetupEvent event) {
	}

	default void syncInit() {
	}
}
