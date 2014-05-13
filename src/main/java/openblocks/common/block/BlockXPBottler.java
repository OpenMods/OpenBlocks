package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;

public class BlockXPBottler extends OpenBlock {

	public static class Icons {
		public static Icon back;
		public static Icon top;
		public static Icon side;
		public static Icon front;
		public static Icon bottom;
	}

	public BlockXPBottler() {
		super(Config.blockXPBottlerId, Material.rock);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public void registerIcons(IconRegister registry) {
		Icons.front = registry.registerIcon("openblocks:xpbottler_front");
		Icons.top = registry.registerIcon("openblocks:xpbottler_top");
		Icons.side = registry.registerIcon("openblocks:xpbottler_sides");
		Icons.bottom = registry.registerIcon("openblocks:xpbottler_bottom");
		Icons.back = registry.registerIcon("openblocks:xpbottler_back");

		setTexture(ForgeDirection.EAST, Icons.side);
		setTexture(ForgeDirection.WEST, Icons.side);
		setTexture(ForgeDirection.SOUTH, Icons.front);
		setTexture(ForgeDirection.NORTH, Icons.back);
		setTexture(ForgeDirection.UP, Icons.top);
		setTexture(ForgeDirection.DOWN, Icons.bottom);
		setDefaultTexture(Icons.front);
	}
}
