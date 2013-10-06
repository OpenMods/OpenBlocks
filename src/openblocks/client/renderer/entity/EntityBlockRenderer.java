package openblocks.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import openblocks.common.entity.EntityBlock;

import org.lwjgl.opengl.GL11;

public class EntityBlockRenderer extends Render {

	private final RenderBlocks renderBlocks = new RenderBlocks();

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
		final EntityBlock block = (EntityBlock)entity;

		GL11.glPushMatrix();
		GL11.glTranslated(x, y + 0.5, z);
		GL11.glRotated(yaw, 0, 1, 0);

		Block blockType = block.getBlock();
		int blockMeta = block.getBlockMeta();

		bindEntityTexture(entity);
		renderBlocks.renderBlockAsItem(blockType, blockMeta, 1.0f);

		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TextureMap.locationBlocksTexture;
	}

}
