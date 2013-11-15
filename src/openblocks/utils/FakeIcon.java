package openblocks.utils;

import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FakeIcon implements Icon {

	private final float minU;
	private final float maxU;
	private final float minV;
	private final float maxV;
	private final int width;
	private final int height;

	public FakeIcon(float minU, float maxU, float minV, float maxV, int width, int height) {
		this.minU = minU;
		this.maxU = maxU;
		this.minV = minV;
		this.maxV = maxV;
		this.width = width;
		this.height = height;
	}

	public static Icon createSheetIcon(int x, int y, int width, int height) {
		float minU = x / 256.0f;
		float minV = y / 256.0f;
		float maxU = (x + width) / 256.0f;
		float maxV = (y + height) / 256.0f;
		return new FakeIcon(minU, maxU, minV, maxV, Math.abs(width), Math.abs(height));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getIconWidth() {
		return width;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getIconHeight() {
		return height;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getMinU() {
		return minU;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getMaxU() {
		return maxU;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getInterpolatedU(double p) {
		return minU + (maxU - minU) * (float)p / 16.0f;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getMinV() {
		return minV;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getMaxV() {
		return maxV;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getInterpolatedV(double p) {
		return minV + (maxV - minV) * (float)p / 16.0f;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getIconName() {
		return "fake";
	}
}
