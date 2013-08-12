package openblocks.client.renderer;
 
import openblocks.OpenBlocks;
import openblocks.client.renderer.tileentity.OpenRenderHelper;
 
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;
 
public class ItemRendererHangGlider implements IItemRenderer {
       
        @Override
        public boolean handleRenderType(ItemStack item, ItemRenderType type) {
                return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
        }
 
        @Override
        public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
                return true;
        }
 
        @Override
        public void renderItem(ItemRenderType type, ItemStack par2ItemStack, Object... data) {
 
        EntityLiving par1EntityLiving = (EntityLiving)data[1];
        if(OpenBlocks.proxy.gliderClientMap.containsKey(par1EntityLiving)) return;
        Icon icon = par1EntityLiving.getItemIcon(par2ItemStack, 0);
 
        if (icon == null)
        {
            GL11.glPopMatrix();
            return;
        }
 
        if (par2ItemStack.getItemSpriteNumber() == 0)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture("/terrain.png");
        }
        else
        {
                Minecraft.getMinecraft().renderEngine.bindTexture("/gui/items.png");
        }
 
        Tessellator tessellator = Tessellator.instance;
        float f = icon.getMinU();
        float f1 = icon.getMaxU();
        float f2 = icon.getMinV();
        float f3 = icon.getMaxV();
        float f4 = 0.0F;
        float f5 = 0.3F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef(-f4, -f5, 0.0F);
        float f6 = 1.5F;
        GL11.glScalef(f6, f6, f6);
        GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
        ItemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, icon.getSheetWidth(), icon.getSheetHeight(), 0.0625F);
 
        }
}
