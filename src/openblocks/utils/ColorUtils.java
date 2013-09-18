package openblocks.utils;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ColorUtils {

	public static final Map<String, Integer> COLORS = new ImmutableMap.Builder<String, Integer>()
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

}
