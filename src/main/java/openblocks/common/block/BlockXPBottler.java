package openblocks.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.block.BlockRotationMode;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockXPBottler extends OpenBlock {

	public static class Icons {
		public static IIcon back;
		public static IIcon top;
		public static IIcon side;
		public static IIcon front;
		public static IIcon bottom;
	}

	public BlockXPBottler() {
		super(Material.rock);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		super.registerBlockIcons(registry);
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
	}
}
