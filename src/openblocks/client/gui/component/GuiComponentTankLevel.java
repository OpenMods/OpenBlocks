package openblocks.client.gui.component;

import org.lwjgl.opengl.GL11;

import openblocks.utils.CompatibilityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class GuiComponentTankLevel extends Gui {
	
	private int x;
	private int y;
	public int width;
	public int height;
	
	public GuiComponentTankLevel(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void render(Minecraft minecraft, int mouseX, int mouseY, FluidStack stack, double percentFull) {
		
		CompatibilityUtils.bindTextureToClient("textures/gui/components.png");
		GL11.glColor3f(1, 1, 1);
		
		// top edge
		GL11.glPushMatrix();
		GL11.glTranslated((double)x + 3, (double)y, 0);
		GL11.glScaled((double)width - 6, 1, 0);
		drawTexturedModalRect(0, 0, 3, 0, 1, 3);
		GL11.glPopMatrix();
		
		// bottom edge
		GL11.glPushMatrix();
		GL11.glTranslated((double)x + 3, (double)y + height - 3, 0);
		GL11.glScaled((double)width - 6, 1, 0);
		drawTexturedModalRect(0, 0, 3, 64, 1, 3);
		GL11.glPopMatrix();
		
		// left edge
		GL11.glPushMatrix();
		GL11.glTranslated((double)x, (double)y + 3, 0);
		GL11.glScaled((double)1, height - 6, 0);
		drawTexturedModalRect(0, 0, 0, 3, 3, 1);
		GL11.glPopMatrix();
		
		// right edge
		GL11.glPushMatrix();
		GL11.glTranslated((double)(x + width - 3), (double)y + 3, 0);
		GL11.glScaled((double)1, height - 6, 0);
		drawTexturedModalRect(0, 0, 21, 3, 3, 1);
		GL11.glPopMatrix();
		
		// top left corner
		drawTexturedModalRect(x, y, 0, 0, 3, 3);
		// top right corner
		drawTexturedModalRect(x + width - 3, y, 21, 0, 3, 3);
		// bottom left corner
		drawTexturedModalRect(x, y + height - 3, 0, 64, 3, 3);
		// bottom right corner
		drawTexturedModalRect(x + width - 3, y + height - 3, 21, 64, 3, 3);
		// bottom right corner
		
		if (stack != null && stack.getFluid() != null) {

			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			this.drawTexturedModelRectFromIcon(x+3, y+3, stack.getFluid().getStillIcon(), width - 6, height - 6);
		}
	}
	
}
