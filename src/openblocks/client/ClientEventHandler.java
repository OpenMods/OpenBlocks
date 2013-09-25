package openblocks.client;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class ClientEventHandler {

	public ClientEventHandler() {
	}

	@ForgeSubscribe
	public void onRenderWorldLast(RenderWorldLastEvent evt) {
		SoundEventsManager.instance.renderEvents(evt);
	}
}
