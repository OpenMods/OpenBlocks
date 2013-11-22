package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityBlockPlacer;
import openmods.block.OpenBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBlockPlacer extends OpenBlock {

	public static class Icons {
		public static Icon top;
		public static Icon bottom;
		public static Icon sides;
	}

	public BlockBlockPlacer() {
		super(Config.blockBlockPlacerId, Material.rock);
		setupBlock(this, "blockPlacer", TileEntityBlockPlacer.class);
		setRotationMode(BlockRotationMode.SIX_DIRECTIONS);
		setInventoryRenderRotation(ForgeDirection.EAST);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		Icons.top = registry.registerIcon("openblocks:blockPlacer");
		Icons.sides = registry.registerIcon("openblocks:blockPlacer_side");
		Icons.bottom = registry.registerIcon("openblocks:blockPlacer_bottom");
		setTexture(ForgeDirection.UP, Icons.top);
		setTexture(ForgeDirection.EAST, Icons.sides);
		setTexture(ForgeDirection.WEST, Icons.sides);
		setTexture(ForgeDirection.NORTH, Icons.sides);
		setTexture(ForgeDirection.SOUTH, Icons.sides);
		setTexture(ForgeDirection.DOWN, Icons.bottom);
		setDefaultTexture(Icons.top);
	}
}
