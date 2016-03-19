package openblocks.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import openmods.renderer.DisplayListWrapper;
import openmods.utils.TextureUtils;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ModelCartographer extends AbstractModel {
	private final ModelRenderer body;
	private final ModelRenderer base;

	private final DisplayListWrapper eyeList = new DisplayListWrapper() {

		@Override
		public void compile() {
			GL11.glColor3d(1, 1, 1);
			GL11.glScalef(SCALE * 3, SCALE * 3, SCALE);

			final Tessellator tes = new Tessellator();
			tes.setTranslation(-0.5, -0.5, 0);

			final IIcon icon = Items.ender_eye.getIconFromDamage(0);
			ItemRenderer.renderItemIn2D(
					tes,
					icon.getInterpolatedU(15), icon.getInterpolatedV(2),
					icon.getInterpolatedU(2), icon.getInterpolatedV(15),
					13, 13,
					0.5f
					);
		}
	};

	@SubscribeEvent
	public void onTextureChange(TextureStitchEvent evt) {
		if (evt.map.getTextureType() == TextureUtils.TEXTURE_MAP_ITEMS) eyeList.reset();
	}

	public ModelCartographer() {
		textureWidth = 32;
		textureHeight = 32;

		body = new ModelRenderer(this, 0, 0);
		body.addBox(-2.5f, -1.5f, -2.5f, 5, 2, 5);

		base = new ModelRenderer(this);
		body.addChild(base);

		base.setTextureOffset(0, 7);
		base.addBox(-1.5f, 0.5f, -1.5f, 3, 1, 3);

		base.setTextureOffset(0, 11);
		base.addBox(-0.5f, 0.5f, -2.5f, 1, 4, 1);

		base.setTextureOffset(4, 11);
		base.addBox(-0.5f, 0.5f, 1.5f, 1, 4, 1);

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void renderEye(float baseRotation, float eyeRotation) {
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0.25f, 0);
		GL11.glRotated(Math.toDegrees(baseRotation) + 90, 0, 1, 0);
		GL11.glRotated(Math.toDegrees(eyeRotation), 1, 0, 0);
		eyeList.render();
		GL11.glPopMatrix();
	}

	public void renderBase(float baseRotation) {
		base.rotateAngleY = baseRotation;
		body.render(SCALE);
	}
}
