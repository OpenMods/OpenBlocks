package openblocks.client.renderer;

import com.google.common.collect.Queues;
import java.util.Queue;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openmods.utils.TextureUtils;

public class TextureUploader {

	public interface IUploadableTexture {
		public void upload();
	}

	public static final TextureUploader INSTANCE = new TextureUploader();

	private final Queue<IUploadableTexture> texturesToUpload = Queues.newConcurrentLinkedQueue();

	public void scheduleTextureUpload(IUploadableTexture texture) {
		texturesToUpload.add(texture);
	}

	@SubscribeEvent
	public void onRenderEnd(RenderWorldLastEvent evt) {
		TextureUtils.bindTextureToClient(TextureMap.LOCATION_BLOCKS_TEXTURE);

		IUploadableTexture t;
		while ((t = texturesToUpload.poll()) != null)
			t.upload();
	}

	@SubscribeEvent
	public void onTextureStitchEvent(TextureStitchEvent.Pre evt) {
		texturesToUpload.clear();
	}
}
