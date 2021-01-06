package openblocks.client.renderer.tileentity.guide;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import openblocks.OpenBlocks;

public class GuideModelHolder {
	public static final ResourceLocation MARKER_MODEL_LOCATION = OpenBlocks.location("block/guide_marker");
	public static final ResourceLocation BIT_MODEL_LOCATION = OpenBlocks.location("block/guide_bit");

	private List<BakedQuad> markerQuads;
	private List<BakedQuad> bitQuads;

	public void onModelBake(ModelBakeEvent evt) {
		markerQuads = getModel(evt, MARKER_MODEL_LOCATION);
		bitQuads = getModel(evt, BIT_MODEL_LOCATION);
	}

	private static List<BakedQuad> getModel(ModelBakeEvent evt, ResourceLocation id) {
		IBakedModel marker = evt.getModelRegistry().get(id);
		if (marker == null) {
			marker = evt.getModelManager().getMissingModel();
		}

		final Random rand = new Random(12);
		final List<BakedQuad> quads = Lists.newArrayList();
		for (Direction enumfacing : Direction.values()) {
			quads.addAll(marker.getQuads(null, enumfacing, rand));
		}
		quads.addAll(marker.getQuads(null, null, rand));
		return ImmutableList.copyOf(quads);
	}

	public List<BakedQuad> getMarkerQuads() {
		return markerQuads;
	}

	public List<BakedQuad> getBitQuads() {
		return bitQuads;
	}

	public void onModelRegister(ModelRegistryEvent evt) {
		ModelLoader.addSpecialModel(MARKER_MODEL_LOCATION);
		ModelLoader.addSpecialModel(BIT_MODEL_LOCATION);
	}
}
