package openmods.utils;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ColorUtils {

	public static final Map<String, Integer> COLORS =
			new ImmutableMap.Builder<String, Integer>()
					.put("dyeBlack", 0x1E1B1B)
					.put("dyeRed", 0xB3312C)
					.put("dyeGreen", 0x3B511A)
					.put("dyeBrown", 0x51301A)
					.put("dyeBlue", 0x253192)
					.put("dyePurple", 0x7B2FBE)
					.put("dyeCyan", 0x287697)
					.put("dyeLightGray", 0xABABAB)
					.put("dyeGray", 0x434343)
					.put("dyePink", 0xD88198)
					.put("dyeLime", 0x41CD34)
					.put("dyeYellow", 0xDECF2A)
					.put("dyeLightBlue", 0x6689D3)
					.put("dyeMagenta", 0xC354CD)
					.put("dyeOrange", 0xEB8844)
					.put("dyeWhite", 0xF0F0F0)
					.build();

	public static class RGB {
		private int r, g, b;

		public RGB(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public RGB(int color) {
			this(((color & 0xFF0000) >> 16), ((color & 0x00FF00) >> 8), (color & 0x0000FF));
		}

		public RGB() {
			this(0);
		}

		public int getR() {
			return r;
		}

		public void setR(int r) {
			this.r = r;
		}

		public int getG() {
			return g;
		}

		public void setG(int g) {
			this.g = g;
		}

		public int getB() {
			return b;
		}

		public void setB(int b) {
			this.b = b;
		}

		public void setColor(int r, int g, int b) {
			setR(r);
			setG(g);
			setB(b);
		}

		public void setColor(int color) {
			setColor(((color & 0xFF0000) >> 16), ((color & 0x00FF00) >> 8), (color & 0x0000FF));
		}

		public int getColor() {
			return r << 16 | g << 8 | b;
		}

		public RGB interpolate(RGB other, double amount) {
			int iPolR = (int)(r * (1D - amount) + other.r * amount);
			int iPolG = (int)(g * (1D - amount) + other.g * amount);
			int iPolB = (int)(b * (1D - amount) + other.b * amount);
			return new RGB(iPolR, iPolG, iPolB);
		}

		public CYMK toCYMK() {
			float cyan = 1f - (r / 255f);
			float magenta = 1f - (g / 255f);
			float yellow = 1f - (b / 255f);
			float K = 1;
			if (cyan < K) {
				K = cyan;
			}
			if (magenta < K) {
				K = magenta;
			}
			if (yellow < K) {
				K = yellow;
			}
			if (K == 1) {
				cyan = 0;
				magenta = 0;
				yellow = 0;
			} else {
				cyan = (cyan - K) / (1f - K);
				magenta = (magenta - K) / (1f - K);
				yellow = (yellow - K) / (1f - K);
			}
			return new CYMK(cyan, yellow, magenta, K);
		}
	}

	public static class CYMK {
		private float cyan, yellow, magenta, key;

		public CYMK(float c, float y, float m, float k) {
			this.cyan = c;
			this.yellow = y;
			this.magenta = m;
			this.key = k;
		}

		public float getCyan() {
			return cyan;
		}

		public void setCyan(float cyan) {
			this.cyan = cyan;
		}

		public float getYellow() {
			return yellow;
		}

		public void setYellow(float yellow) {
			this.yellow = yellow;
		}

		public float getMagenta() {
			return magenta;
		}

		public void setMagenta(float magenta) {
			this.magenta = magenta;
		}

		public float getKey() {
			return key;
		}

		public void setKey(float key) {
			this.key = key;
		}

	}

}
