package openblocks.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import openblocks.common.entity.EntityGlyph;

public class EntityGlyphRenderer extends EntityRenderer<EntityGlyph> {

	private final RenderItem itemRenderer;

	public EntityGlyphRenderer(EntityRendererManager renderManager, RenderItem itemRenderer) {
		super(renderManager);
		this.itemRenderer = itemRenderer;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGlyph entity) {
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}

	@Override
	public void doRender(EntityGlyph entity, double x, double y, double z, float entityYaw, float partialTicks) {
		final ItemStack itemStack = entity.getStack();

		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);

		GlStateManager.scale(0.5, 0.5, 0.5);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(getTeamColor(entity));
		}

		bindEntityTexture(entity);
		this.itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED);

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
}
