package openblocks.client.renderer.tileentity;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable;
import openmods.tileentity.renderer.OpenRenderHelper;

import org.lwjgl.opengl.GL11;

public class TileEntityAutoEnchantmentTableRenderer extends
		TileEntitySpecialRenderer {

	private static final ResourceLocation enchantingTableBookTextures = new ResourceLocation("textures/entity/enchanting_table_book.png");
	private ModelBook enchantmentBook = new ModelBook();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

		TileEntityAutoEnchantmentTable table = (TileEntityAutoEnchantmentTable)tileentity;
		GL11.glPushMatrix();
		bindTexture(TextureMap.locationBlocksTexture);
		GL11.glTranslatef((float)x, (float)y, (float)z);
		OpenRenderHelper.renderCube(0, 0, 0, 1, 0.75, 1, OpenBlocks.Blocks.autoEnchantmentTable, null);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
		float f1 = table.tickCount + f;
		GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(f1 * 0.1F) * 0.01F, 0.0F);
		float f2 = table.bookRotation2 - table.bookRotationPrev;

		while (f2 >= (float)Math.PI)
			f2 -= 2 * (float)Math.PI;

		while (f2 < -(float)Math.PI) {
			f2 += 2 * (float)Math.PI;
		}

		float f3 = table.bookRotationPrev + f2 * f;
		GL11.glRotatef(-f3 * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
		bindTexture(enchantingTableBookTextures);
		float f4 = table.pageFlipPrev + (table.pageFlip - table.pageFlipPrev)
				* f + 0.25F;
		float f5 = table.pageFlipPrev + (table.pageFlip - table.pageFlipPrev)
				* f + 0.75F;
		f4 = (f4 - MathHelper.truncateDoubleToInt(f4)) * 1.6F - 0.3F;
		f5 = (f5 - MathHelper.truncateDoubleToInt(f5)) * 1.6F - 0.3F;

		if (f4 < 0.0F) {
			f4 = 0.0F;
		}

		if (f5 < 0.0F) {
			f5 = 0.0F;
		}

		if (f4 > 1.0F) {
			f4 = 1.0F;
		}

		if (f5 > 1.0F) {
			f5 = 1.0F;
		}

		float f6 = table.bookSpreadPrev
				+ (table.bookSpread - table.bookSpreadPrev) * f;
		GL11.glEnable(GL11.GL_CULL_FACE);
		this.enchantmentBook.render((Entity)null, f1, f4, f5, f6, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glColor4f(1, 1, 1, 1);
	}
}
