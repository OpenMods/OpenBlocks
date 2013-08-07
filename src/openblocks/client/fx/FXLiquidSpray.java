package openblocks.client.fx;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.liquids.LiquidStack;

public class FXLiquidSpray extends EntityFX
{
    /** the scale of the flame FX */
    private float flameScale;

    public FXLiquidSpray(World par1World, LiquidStack liquid, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.motionX *= -0.10000000149011612D;
        this.motionY *= -0.10000000149011612D;
        this.motionZ *= -0.10000000149011612D;
        this.flameScale = this.particleScale;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleMaxAge = 4;
        this.noClip = true;
        Block block = null;
        Icon texture = null;
		try {
	        if (liquid.itemID < Block.blocksList.length && Block.blocksList[liquid.itemID] != null) {
				block = Block.blocksList[liquid.itemID];
					texture = getLiquidTexture(liquid);
			} else if (Item.itemsList[liquid.itemID] != null) {
				block = Block.waterStill;
				texture = getLiquidTexture(liquid);
			} else {
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setParticleIcon(Minecraft.getMinecraft().renderEngine, texture);
        
        //this.setParticleTextureIndex(0 + this.rand.nextInt(7));
    }
    
    public int getFXLayer()
    {
        return 1;
    }

    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float f6 = ((float)this.particleAge + par2) / (float)this.particleMaxAge;
        this.particleScale = this.flameScale * (1.0F - f6 * f6 * 0.5F);
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
    }

    
    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {	

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        this.motionY -= 0.09D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY)
        {
            this.motionX -= 1.1D;
            this.motionZ -= 1.1D;
        }

        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;

    }

	public static Icon getLiquidTexture(LiquidStack liquid) throws Exception {
		if (liquid == null || liquid.itemID <= 0) {
			return null;
		}
		LiquidStack canon = liquid.canonical();
		if (canon == null) {
			throw new Exception();
		}
		Icon icon = canon.getRenderingIcon();
		if (icon == null) {
			throw new Exception();
		}
		return icon;
	}
}
