package openblocks.client.renderer.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockGrave;
import openblocks.common.tileentity.TileEntityGrave;
import openmods.OpenMods;
import openmods.geometry.Hitbox;
import openmods.geometry.IHitboxSupplier;
import openmods.utils.BlockUtils;
import org.lwjgl.opengl.GL11;

public class TileEntityGraveRenderer extends TileEntitySpecialRenderer<TileEntityGrave> {

	private final IHitboxSupplier textBoxes = OpenMods.proxy.getHitboxes(OpenBlocks.location("grave_text"));

	@Override
	public void renderTileEntityAt(TileEntityGrave target, double x, double y, double z, float partialTicks, int destroyStage) {
		final FontRenderer renderer = getFontRenderer();
		if (renderer != null) {

			final BlockPos pos = target.getPos();
			final IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(target.getWorld(), pos);
			final IBlockState state = world.getBlockState(pos).getActualState(world, pos);

			if (state.getBlock() instanceof BlockGrave) {
				final Hitbox box = selectBox(state.getValue(BlockGrave.HAS_BASE));
				if (box != null) {
					GL11.glPushMatrix();
					GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
					GL11.glRotatef(BlockUtils.getRotationFromOrientation(target.getOrientation()), 0, 1, 0);
					GL11.glTranslated(-0.5, -0.5, -0.5);

					final String username = target.getUsername();

					float boxWidth = (float)(box.to.xCoord - box.from.xCoord);

					int stringWidth = renderer.getStringWidth(username);
					float textScale = boxWidth / stringWidth;
					textScale = Math.min(textScale, 0.02f);

					float textX = (float)((box.to.xCoord + box.from.xCoord) / 2 - stringWidth * textScale / 2);
					float textY = (float)((box.to.yCoord + box.from.yCoord) / 2 + renderer.FONT_HEIGHT * textScale / 2);
					float textZ = 1 - (float)((box.to.zCoord + box.from.zCoord) / 2) + 0.001f;

					GL11.glTranslatef(textX, textY, textZ);
					GL11.glScalef(textScale, textScale, textScale);
					GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

					GlStateManager.depthMask(false);
					renderer.drawString(username, 0, 0, 0);
					GlStateManager.depthMask(true);

					GL11.glPopMatrix();
				}
			}
		}

	}

	private Hitbox selectBox(boolean hasBase) {
		return textBoxes.asMap().get(hasBase? "with_base" : "without_base");
	}

}
