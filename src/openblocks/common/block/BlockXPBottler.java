package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityXPBottler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockXPBottler extends OpenBlock {

	public static class icons {
		public static Icon back;
		public static Icon top;
		public static Icon sides;
		public static Icon front;
		public static Icon bottom;
	}

	public BlockXPBottler() {
		super(Config.blockXPBottlerId, Material.ground);
		setupBlock(this, "xpbottler", TileEntityXPBottler.class);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public void registerIcons(IconRegister registry) {
		icons.top = registry.registerIcon(String.format("%s:%s", modKey, "xpbottler_top"));
		icons.bottom = registry.registerIcon(String.format("%s:%s", modKey, "xpbottler_bottom"));
		icons.back = registry.registerIcon(String.format("%s:%s", modKey, "xpbottler_back"));
		icons.sides = registry.registerIcon(String.format("%s:%s", modKey, "xpbottler_sides"));
		icons.front = registry.registerIcon(String.format("%s:%s", modKey, "xpbottler_front"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Icon getIcon(int side, int meta) {
		ForgeDirection sidedir = ForgeDirection.getOrientation(side);
		ForgeDirection dir = ForgeDirection.getOrientation((meta & 0x3) + 2);
		if (sidedir == ForgeDirection.UP) {
			return icons.top;
		} else if (sidedir == ForgeDirection.DOWN) {
			return icons.bottom;
		} else {
			if (sidedir == dir) {
				return icons.back;
			} else if (sidedir == dir.getOpposite()) { return icons.front; }
		}
		return icons.sides;
	}
}
