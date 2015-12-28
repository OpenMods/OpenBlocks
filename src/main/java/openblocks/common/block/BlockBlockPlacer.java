package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.block.BlockRotationMode;
import openmods.infobook.BookDocumentation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation
public class BlockBlockPlacer extends OpenBlock {

	public static class Icons {
		public static IIcon top;
		public static IIcon bottom;
		public static IIcon sides;
	}

	public BlockBlockPlacer() {
		super(Material.rock);
		setRotationMode(BlockRotationMode.SIX_DIRECTIONS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		super.registerBlockIcons(registry);

		Icons.top = registry.registerIcon("openblocks:blockPlacer");
		Icons.sides = registry.registerIcon("openblocks:blockPlacer_side");
		Icons.bottom = registry.registerIcon("openblocks:blockPlacer_bottom");

		setTexture(ForgeDirection.UP, Icons.top);
		setTexture(ForgeDirection.EAST, Icons.sides);
		setTexture(ForgeDirection.WEST, Icons.sides);
		setTexture(ForgeDirection.NORTH, Icons.sides);
		setTexture(ForgeDirection.SOUTH, Icons.sides);
		setTexture(ForgeDirection.DOWN, Icons.bottom);
	}
}
