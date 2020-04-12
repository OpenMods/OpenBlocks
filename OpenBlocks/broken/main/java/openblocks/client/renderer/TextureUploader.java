package openblocks.client.renderer;

import com.google.common.collect.Queues;
import java.util.Queue;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openmods.utils.TextureUtils;

public class TextureUploader {

	public static final TextureUploader INSTANCE = new TextureUploader();

	private final Queue<Runnable> texturesToUpload = Queues.newConcurrentLinkedQueue();

	public void scheduleTextureUpload(Runnable uploader) {
		texturesToUpload.add(uploader);
	}

	@SubscribeEvent
	public void onRenderEnd(RenderWorldLastEvent evt) {
		TextureUtils.bindTextureToClient(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

		Runnable t;
		while ((t = texturesToUpload.poll()) != null)
			t.run();
	}

	@SubscribeEvent
	public void onTextureStitchEvent(TextureStitchEvent.Pre evt) {
		texturesToUpload.clear();
	}
}
