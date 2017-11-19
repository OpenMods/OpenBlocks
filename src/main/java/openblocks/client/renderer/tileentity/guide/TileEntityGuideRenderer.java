package openblocks.client.renderer.tileentity.guide;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityGuide;
import org.lwjgl.opengl.GL11;

public class TileEntityGuideRenderer<T extends TileEntityGuide> extends TileEntitySpecialRenderer<T> {

	private static final ModelResourceLocation MARKER_MODEL_LOCATION = new ModelResourceLocation(OpenBlocks.location("guide"), "marker");
	private final IGuideRenderer renderer;

	public TileEntityGuideRenderer() {
		this.renderer = new GuideRendererSelector().getRenderer();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean isGlobalRenderer(T te) {
		return te.shouldRender(); // TODO 1.11 verify if it's still needed. In 1.10 beacon may dissapear when not looking at guide directly
		/// using that option causes glitches due to MC-112730
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent evt) {
		final IModel model = ModelLoaderRegistry.getModelOrMissing(MARKER_MODEL_LOCATION);

		final TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
		final IBakedModel bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
				new Function<ResourceLocation, TextureAtlasSprite>() {
					@Override
					public TextureAtlasSprite apply(ResourceLocation input) {
						return textureMapBlocks.getAtlasSprite(input.toString());
					}
				});

		renderer.onModelBake(new Supplier<BufferBuilder>() {

			@Override
			public BufferBuilder get() {
				final Tessellator tessellator = Tessellator.getInstance();

				BufferBuilder vertexBuffer = tessellator.getBuffer();
				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

				for (EnumFacing enumfacing : EnumFacing.values())
					renderQuads(vertexBuffer, bakedModel.getQuads(null, enumfacing, 0L));

				renderQuads(vertexBuffer, bakedModel.getQuads(null, null, 0L));

				vertexBuffer.finishDrawing();

				return vertexBuffer;
			}
		});
	}

	private static void renderQuads(BufferBuilder vb, List<BakedQuad> quads) {
		for (BakedQuad quad : quads)
			vb.addVertexData(quad.getVertexData());
	}

	@Override
	public void render(T tileentity, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		renderer.renderShape(tileentity);
		GL11.glPopMatrix();
	}
}
