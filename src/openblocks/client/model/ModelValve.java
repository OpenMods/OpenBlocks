package openblocks.client.model;

import openblocks.common.tileentity.tank.TileEntityTankValve;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class ModelValve extends ModelBase {
	// fields
	ModelRenderer center;
	ModelRenderer acceptorwest;
	ModelRenderer tunneleast;
	ModelRenderer tunneldown;
	ModelRenderer acceptoreast;
	ModelRenderer tunnelwest;
	ModelRenderer acceptordown;
	ModelRenderer tunnelsouth;
	ModelRenderer acceptorsouth;
	ModelRenderer tunnelup;
	ModelRenderer acceptorup;

	public ModelValve() {
		textureWidth = 64;
		textureHeight = 32;

		center = new ModelRenderer(this, 0, 0);
		center.addBox(-3F, -3F, -3F, 6, 6, 11);
		center.setRotationPoint(0F, 8F, 0F);
		center.setTextureSize(64, 32);
		center.mirror = true;
		setRotation(center, 0F, 0F, 0F);
		acceptorwest = new ModelRenderer(this, 22, 18);
		acceptorwest.addBox(-3F, -3F, -8F, 6, 6, 1);
		acceptorwest.setRotationPoint(0F, 8F, 0F);
		acceptorwest.setTextureSize(64, 32);
		acceptorwest.mirror = true;
		setRotation(acceptorwest, 0F, 1.570796F, 0F);
		tunneleast = new ModelRenderer(this, 0, 18);
		tunneleast.addBox(-2F, -2F, -7F, 4, 4, 7);
		tunneleast.setRotationPoint(0F, 8F, 0F);
		tunneleast.setTextureSize(64, 32);
		tunneleast.mirror = true;
		setRotation(tunneleast, 0F, -1.570796F, 0F);
		tunneldown = new ModelRenderer(this, 0, 18);
		tunneldown.addBox(-2F, -2F, -7F, 4, 4, 7);
		tunneldown.setRotationPoint(0F, 8F, 0F);
		tunneldown.setTextureSize(64, 32);
		tunneldown.mirror = true;
		setRotation(tunneldown, 1.570796F, 0F, 0F);
		acceptoreast = new ModelRenderer(this, 22, 18);
		acceptoreast.addBox(-3F, -3F, -8F, 6, 6, 1);
		acceptoreast.setRotationPoint(0F, 8F, 0F);
		acceptoreast.setTextureSize(64, 32);
		acceptoreast.mirror = true;
		setRotation(acceptoreast, 0F, -1.570796F, 0F);
		tunnelwest = new ModelRenderer(this, 0, 18);
		tunnelwest.addBox(-2F, -2F, -7F, 4, 4, 7);
		tunnelwest.setRotationPoint(0F, 8F, 0F);
		tunnelwest.setTextureSize(64, 32);
		tunnelwest.mirror = true;
		setRotation(tunnelwest, 0F, 1.570796F, 0F);
		acceptordown = new ModelRenderer(this, 22, 18);
		acceptordown.addBox(-3F, -3F, -8F, 6, 6, 1);
		acceptordown.setRotationPoint(0F, 8F, 0F);
		acceptordown.setTextureSize(64, 32);
		acceptordown.mirror = true;
		setRotation(acceptordown, 1.570796F, 0F, 0F);
		tunnelsouth = new ModelRenderer(this, 0, 18);
		tunnelsouth.addBox(-2F, -2F, -7F, 4, 4, 7);
		tunnelsouth.setRotationPoint(0F, 8F, 0F);
		tunnelsouth.setTextureSize(64, 32);
		tunnelsouth.mirror = true;
		setRotation(tunnelsouth, 0F, 0F, 0F);
		acceptorsouth = new ModelRenderer(this, 22, 18);
		acceptorsouth.addBox(-3F, -3F, -8F, 6, 6, 1);
		acceptorsouth.setRotationPoint(0F, 8F, 0F);
		acceptorsouth.setTextureSize(64, 32);
		acceptorsouth.mirror = true;
		setRotation(acceptorsouth, 0F, 0F, 0F);
		tunnelup = new ModelRenderer(this, 0, 18);
		tunnelup.addBox(-2F, -2F, -7F, 4, 4, 7);
		tunnelup.setRotationPoint(0F, 8F, 0F);
		tunnelup.setTextureSize(64, 32);
		tunnelup.mirror = true;
		setRotation(tunnelup, -1.570796F, 0F, 0F);
		acceptorup = new ModelRenderer(this, 22, 18);
		acceptorup.addBox(-3F, -3F, -8F, 6, 6, 1);
		acceptorup.setRotationPoint(0F, 8F, 0F);
		acceptorup.setTextureSize(64, 32);
		acceptorup.mirror = true;
		setRotation(acceptorup, -1.570796F, 0F, 0F);
	}

	public void render(TileEntity te, float f) {
		TileEntityTankValve valve = (TileEntityTankValve)te;
		float f5 = 0.0625F;
		center.render(f5);
		ForgeDirection rotation = valve.getRotation();
		ForgeDirection r = rotation;
		if (rotation != ForgeDirection.UP && rotation != ForgeDirection.DOWN) {
			if (valve.hasBlockOnSide(ForgeDirection.DOWN)) {
				tunnelup.render(f5);
				acceptorup.render(f5);
			}
			if (valve.hasBlockOnSide(ForgeDirection.UP)) {
				tunneldown.render(f5);
				acceptordown.render(f5);
			}

			rotation = rotation.getOpposite();
			ForgeDirection g = rotation.getRotation(ForgeDirection.UP);
			if (valve.hasBlockOnSide(g)) {
				tunneleast.render(f5);
				acceptoreast.render(f5);
			}
			g = g.getRotation(ForgeDirection.UP);
			g = g.getRotation(ForgeDirection.UP);
			if (valve.hasBlockOnSide(g)) {
				tunnelwest.render(f5);
				acceptorwest.render(f5);
			}
			if (valve.hasBlockOnSide(rotation)) {
				tunnelsouth.render(f5);
				acceptorsouth.render(f5);
			}
		} else {

			if (rotation == ForgeDirection.UP) {
				rotation = rotation.getRotation(ForgeDirection.UP);
				rotation = rotation.getRotation(ForgeDirection.UP);
			}

			rotation = rotation.getRotation(ForgeDirection.NORTH);
			if (valve.hasBlockOnSide(rotation)) {

				if (r == ForgeDirection.UP) {

					tunnelwest.render(f5);
					acceptorwest.render(f5);
				} else {
					tunneleast.render(f5);
					acceptoreast.render(f5);
				}
			}
			rotation = rotation.getRotation(ForgeDirection.UP);
			if (valve.hasBlockOnSide(rotation)) {
				tunneldown.render(f5);
				acceptordown.render(f5);
			}
			rotation = rotation.getRotation(ForgeDirection.UP);
			if (valve.hasBlockOnSide(rotation)) {
				if (r == ForgeDirection.UP) {
					tunneleast.render(f5);
					acceptoreast.render(f5);
				} else {
					tunnelwest.render(f5);
					acceptorwest.render(f5);
				}
			}
			rotation = rotation.getRotation(ForgeDirection.UP);
			if (valve.hasBlockOnSide(rotation)) {
				tunnelup.render(f5);
				acceptorup.render(f5);
			}

			rotation = rotation.getRotation(ForgeDirection.WEST);

			if (valve.hasBlockOnSide(rotation)) {
				tunnelsouth.render(f5);
				acceptorsouth.render(f5);
			}

		}
	}

	public void renderAcceptor(TileEntity te, float f) {
		float f5 = 0.0625F;
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
