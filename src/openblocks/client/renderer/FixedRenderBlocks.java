package openblocks.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

public class FixedRenderBlocks extends RenderBlocks {

	@Override
	public void renderFaceZNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
		Tessellator tessellator = Tessellator.instance;

		if (hasOverrideBlockTexture()) {
			par8Icon = this.overrideBlockTexture;
		}

		double d3 = par8Icon.getInterpolatedU(this.renderMinX * 16.0D);
		double d4 = par8Icon.getInterpolatedU(this.renderMaxX * 16.0D);
		double d5 = par8Icon.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
		double d6 = par8Icon.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
		double d7;

		if (this.flipTexture) {
			d7 = d3;
			d3 = d4;
			d4 = d7;
		}

		if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
			d3 = par8Icon.getMinU();
			d4 = par8Icon.getMaxU();
		}

		if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
			d5 = par8Icon.getMinV();
			d6 = par8Icon.getMaxV();
		}

		d7 = d4;
		double d8 = d3;
		double d9 = d5;
		double d10 = d6;

		if (this.uvRotateEast == 2) {
			d3 = par8Icon.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
			d5 = par8Icon.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
			d4 = par8Icon.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
			d6 = par8Icon.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
			d9 = d5;
			d10 = d6;
			d7 = d3;
			d8 = d4;
			d5 = d6;
			d6 = d9;
		} else if (this.uvRotateEast == 1) {
			d3 = par8Icon.getInterpolatedU(this.renderMaxY * 16.0D);
			d5 = par8Icon.getInterpolatedV(this.renderMaxX * 16.0D);
			d4 = par8Icon.getInterpolatedU(this.renderMinY * 16.0D);
			d6 = par8Icon.getInterpolatedV(this.renderMinX * 16.0D);
			d7 = d4;
			d8 = d3;
			d3 = d4;
			d4 = d8;
			d9 = d6;
			d10 = d5;
		} else if (this.uvRotateEast == 3) {
			d3 = par8Icon.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
			d4 = par8Icon.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
			d5 = par8Icon.getInterpolatedV(this.renderMaxY * 16.0D);
			d6 = par8Icon.getInterpolatedV(this.renderMinY * 16.0D);
			d7 = d4;
			d8 = d3;
			d9 = d5;
			d10 = d6;
		}

		double d11 = par2 + this.renderMinX;
		double d12 = par2 + this.renderMaxX;
		double d13 = par4 + this.renderMinY;
		double d14 = par4 + this.renderMaxY;
		double d15 = par6 + this.renderMinZ;

		if (this.enableAO) {
			tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			tessellator.setBrightness(this.brightnessTopLeft);
			tessellator.addVertexWithUV(d11, d14, d15, d7, d9);
			tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			tessellator.setBrightness(this.brightnessBottomLeft);
			tessellator.addVertexWithUV(d12, d14, d15, d3, d5);
			tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			tessellator.setBrightness(this.brightnessBottomRight);
			tessellator.addVertexWithUV(d12, d13, d15, d8, d10);
			tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			tessellator.setBrightness(this.brightnessTopRight);
			tessellator.addVertexWithUV(d11, d13, d15, d4, d6);
		} else {
			tessellator.addVertexWithUV(d11, d14, d15, d7, d9);
			tessellator.addVertexWithUV(d12, d14, d15, d3, d5);
			tessellator.addVertexWithUV(d12, d13, d15, d8, d10);
			tessellator.addVertexWithUV(d11, d13, d15, d4, d6);
		}
	}

	@Override
	public void renderFaceXPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
		Tessellator tessellator = Tessellator.instance;

		if (hasOverrideBlockTexture()) {
			par8Icon = this.overrideBlockTexture;
		}

		double d3 = par8Icon.getInterpolatedU(this.renderMinZ * 16.0D);
		double d4 = par8Icon.getInterpolatedU(this.renderMaxZ * 16.0D);
		double d5 = par8Icon.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
		double d6 = par8Icon.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
		double d7;

		if (this.flipTexture) {
			d7 = d3;
			d3 = d4;
			d4 = d7;
		}

		if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
			d3 = par8Icon.getMinU();
			d4 = par8Icon.getMaxU();
		}

		if (this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
			d5 = par8Icon.getMinV();
			d6 = par8Icon.getMaxV();
		}

		d7 = d4;
		double d8 = d3;
		double d9 = d5;
		double d10 = d6;

		if (this.uvRotateSouth == 2) {
			d3 = par8Icon.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
			d5 = par8Icon.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
			d4 = par8Icon.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
			d6 = par8Icon.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
			d9 = d5;
			d10 = d6;
			d7 = d3;
			d8 = d4;
			d5 = d6;
			d6 = d9;
		} else if (this.uvRotateSouth == 1) {
			d3 = par8Icon.getInterpolatedU(this.renderMaxY * 16.0D);
			d5 = par8Icon.getInterpolatedV(this.renderMaxZ * 16.0D);
			d4 = par8Icon.getInterpolatedU(this.renderMinY * 16.0D);
			d6 = par8Icon.getInterpolatedV(this.renderMinZ * 16.0D);
			d7 = d4;
			d8 = d3;
			d3 = d4;
			d4 = d8;
			d9 = d6;
			d10 = d5;
		} else if (this.uvRotateSouth == 3) {
			d3 = par8Icon.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
			d4 = par8Icon.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
			d5 = par8Icon.getInterpolatedV(this.renderMaxY * 16.0D);
			d6 = par8Icon.getInterpolatedV(this.renderMinY * 16.0D);
			d7 = d4;
			d8 = d3;
			d9 = d5;
			d10 = d6;
		}

		double d11 = par2 + this.renderMaxX;
		double d12 = par4 + this.renderMinY;
		double d13 = par4 + this.renderMaxY;
		double d14 = par6 + this.renderMinZ;
		double d15 = par6 + this.renderMaxZ;

		if (this.enableAO) {
			tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			tessellator.setBrightness(this.brightnessTopLeft);
			tessellator.addVertexWithUV(d11, d12, d15, d8, d10);
			tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			tessellator.setBrightness(this.brightnessBottomLeft);
			tessellator.addVertexWithUV(d11, d12, d14, d4, d6);
			tessellator.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			tessellator.setBrightness(this.brightnessBottomRight);
			tessellator.addVertexWithUV(d11, d13, d14, d7, d9);
			tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			tessellator.setBrightness(this.brightnessTopRight);
			tessellator.addVertexWithUV(d11, d13, d15, d3, d5);
		} else {
			tessellator.addVertexWithUV(d11, d12, d15, d8, d10);
			tessellator.addVertexWithUV(d11, d12, d14, d4, d6);
			tessellator.addVertexWithUV(d11, d13, d14, d7, d9);
			tessellator.addVertexWithUV(d11, d13, d15, d3, d5);
		}
	}

	public void setWorld(IBlockAccess world) {
		this.blockAccess = world;
	}
}
