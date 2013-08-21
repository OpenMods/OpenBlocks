package openblocks.client.fx;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.liquids.LiquidStack;

public class FXLiquidSpray extends EntityFX {

	private Vec3 direction;

	public FXLiquidSpray(World par1World, LiquidStack liquid, double x, double y, double z, Vec3 vec, float spread) {
		super(par1World, x, y, z, 0, 0, 0);

		vec.xCoord = Math.abs(vec.xCoord);
		vec.yCoord = Math.abs(vec.yCoord);
		vec.zCoord = Math.abs(vec.zCoord);

		double sinPitch = Math.sin(vec.xCoord);
		double cosPitch = Math.cos(vec.xCoord);
		double sinYaw = Math.sin(vec.zCoord);
		double cosYaw = Math.cos(vec.zCoord);

		if (vec.xCoord == 0) {
			vec.xCoord = -cosYaw * cosPitch;
			vec.yCoord = sinYaw;
			vec.zCoord = -cosYaw * sinPitch;
		} else {
			vec.xCoord = -cosPitch * sinYaw;
			vec.yCoord = sinPitch;
			vec.zCoord = -cosPitch * cosYaw;
		}

		this.posX = x;
		this.posY = y;
		this.posZ = z;

		direction = vec;
		particleGravity = 0.7f;
		this.particleMaxAge = 50;
		setSize(0.5F, 0.5F);
		this.particleScale = 0.3f;
		this.noClip = false;
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (vec.zCoord == 0.0) {
			vec.zCoord = (rand.nextDouble() - 0.5) * spread;
		} else {
			vec.xCoord = (rand.nextDouble() - 0.5) * spread;
		}
		motionX = vec.xCoord / 2;
		motionY = vec.yCoord / 2;
		motionZ = vec.zCoord / 2;

		Block block = null;
		Icon texture = null;
		try {
			if (liquid.itemID < Block.blocksList.length
					&& Block.blocksList[liquid.itemID] != null) {
				block = Block.blocksList[liquid.itemID];
				texture = getLiquidTexture(liquid);
			} else if (Item.itemsList[liquid.itemID] != null) {
				block = Block.waterStill;
				texture = getLiquidTexture(liquid);
			} else {}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setParticleIcon(Minecraft.getMinecraft().renderEngine, texture);

		// this.setParticleTextureIndex(0 + this.rand.nextInt(7));
	}

	public int getFXLayer() {
		return 1;
	}

	public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
		float f6 = ((float)this.particleAge + par2)
				/ (float)this.particleMaxAge;
		// this.particleScale = this.flameScale * (1.0F - f6 * f6 * 0.5F);
		super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		super.onUpdate();
	}

	public static Icon getLiquidTexture(LiquidStack liquid) throws Exception {
		if (liquid == null || liquid.itemID <= 0) { return null; }
		LiquidStack canon = liquid.canonical();
		if (canon == null) { throw new Exception(); }
		Icon icon = canon.getRenderingIcon();
		if (icon == null) { throw new Exception(); }
		return icon;
	}
}
