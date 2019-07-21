package openblocks.client.renderer.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.client.renderer.HeightMapRenderer;
import openblocks.common.HeightMapData;
import openblocks.common.MapDataManager;
import openblocks.common.tileentity.TileEntityProjector;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

public class TileEntityProjectorRenderer extends TileEntitySpecialRenderer<TileEntityProjector> {

	private static final float BLOCK_CENTRE_TRANSLATION = 0.5F;

	private final ModelResourceLocation spinnerModelLocation;

	public TileEntityProjectorRenderer(ModelResourceLocation spinnerModel) {
		this.spinnerModelLocation = spinnerModel;
	}

	@Override
	public void render(TileEntityProjector projector, double x, double y, double z, float partialTickTime, int destroyProgess, float alpha) {
		int pass = MinecraftForgeClient.getRenderPass();

		GlStateManager.color(1, 1, 1);

		GlStateManager.disableLighting();
		int mapId = projector.mapId();
		if (pass <= 0) {
			renderProjector(projector, partialTickTime, x, y, z);
		} else {

			renderMap(projector, mapId, x, y, z);

		}
	}

	private static void renderMap(final TileEntityProjector projector, int mapId, double x, double y, double z) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + BLOCK_CENTRE_TRANSLATION, y, z + BLOCK_CENTRE_TRANSLATION);
		GL11.glRotated(90 * projector.rotation(), 0, 1, 0);

		GL11.glTranslated(-BLOCK_CENTRE_TRANSLATION, 0, -BLOCK_CENTRE_TRANSLATION);

		final World world = projector.getWorld();
		if (world != null) {
			HeightMapData data = MapDataManager.getMapData(world, mapId);
			if (data.isValid()) {
				GL11.glTranslatef(0, 1, 0);
				HeightMapRenderer.instance.render(mapId, data);
			}
		}

		GL11.glPopMatrix();
	}

	private IBakedModel bakedSpinnerModel;

	private static BlockModelRenderer blockModelRenderer;

	private void renderProjector(TileEntityProjector projector, float partialTickTime, double x, double y, double z) {
		if (bakedSpinnerModel == null) return;
		if (!projector.hasCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null)) return;

		if (blockModelRenderer == null) {
			blockModelRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();
		}

		final BlockPos pos = projector.getPos();
		final IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(projector.getWorld(), pos);
		final BlockState state = world.getBlockState(pos);

		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState exState = (IExtendedBlockState)state;
			if (exState.getUnlistedNames().contains(Properties.AnimationProperty)) {
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder vb = tessellator.getBuffer();
				bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();

				if (Minecraft.isAmbientOcclusionEnabled()) {
					GlStateManager.shadeModel(GL11.GL_SMOOTH);
				} else {
					GlStateManager.shadeModel(GL11.GL_FLAT);
				}

				vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

				float time = Animation.getWorldTime(getWorld(), partialTickTime);
				final Pair<IModelState, Iterable<Event>> pair = projector.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null).apply(time);
				exState = exState.withProperty(Properties.AnimationProperty, pair.getLeft());

				vb.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

				blockModelRenderer.renderModel(world, bakedSpinnerModel, exState, pos, vb, false);

				vb.setTranslation(0, 0, 0);

				tessellator.draw();

				RenderHelper.enableStandardItemLighting();
			}
		}
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent evt) {
		// if something went wrong, it was already captured by model with dependencies
		final IModel model = ModelLoaderRegistry.getModelOrMissing(spinnerModelLocation);
		this.bakedSpinnerModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
	}
}
