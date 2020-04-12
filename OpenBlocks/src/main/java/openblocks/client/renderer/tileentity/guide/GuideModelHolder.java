package openblocks.client.renderer.tileentity.guide;

import com.google.common.collect.Lists;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.BasicState;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import openblocks.OpenBlocks;

public class GuideModelHolder {
	public static final ResourceLocation MARKER_MODEL_LOCATION = OpenBlocks.location("block/guide_marker");
	public static final ResourceLocation BIT_MODEL_LOCATION = OpenBlocks.location("block/guide_bit");

	private ByteBuffer markerQuads;
	private ByteBuffer bitQuads;

	public void onModelBake(ModelBakeEvent evt) {
		final AtlasTexture textureMapBlocks = Minecraft.getInstance().getTextureMap();

		markerQuads = getModel(evt, MARKER_MODEL_LOCATION, textureMapBlocks);
		bitQuads = getModel(evt, BIT_MODEL_LOCATION, textureMapBlocks);
	}

	private static ByteBuffer getModel(ModelBakeEvent evt, ResourceLocation id, AtlasTexture textureMapBlocks) {
		final IModel model = ModelLoaderRegistry.getModelOrMissing(id);

		final IBakedModel marker = model.bake(evt.getModelLoader(),
				input -> textureMapBlocks.getAtlasSprite(input.toString()), new BasicState(model.getDefaultState(), false), DefaultVertexFormats.BLOCK);

		final Random rand = new Random(12);
		final List<BakedQuad> quads = Lists.newArrayList();
		for (Direction enumfacing : Direction.values()) {
			quads.addAll(marker.getQuads(null, enumfacing, rand));
		}
		quads.addAll(marker.getQuads(null, null, rand));
		final ByteBuffer result = ByteBuffer.allocateDirect(DefaultVertexFormats.BLOCK.getSize() * quads.size() * 4);
		final IntBuffer quadInts = result.asIntBuffer();
		for (BakedQuad quad : quads) {
			quadInts.put(quad.getVertexData());
		}
		return result;
	}

	public ByteBuffer getMarkerQuads() {
		return markerQuads.slice();
	}

	public ByteBuffer getBitQuads() {
		return bitQuads.slice();
	}

	public void registerModels() {
		ModelLoader.addSpecialModel(MARKER_MODEL_LOCATION);
		ModelLoader.addSpecialModel(BIT_MODEL_LOCATION);
	}
}
