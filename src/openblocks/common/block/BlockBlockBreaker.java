package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityBlockBreaker;
import openmods.block.OpenBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBlockBreaker extends OpenBlock {

	@SideOnly(Side.CLIENT)
	private static class Icons {
		public static Icon top;
		public static Icon top_active;
		public static Icon bottom;
		public static Icon side;
	}

	public BlockBlockBreaker() {
		super(Config.blockBlockBreakerId, Material.rock);
		setupBlock(this, "blockbreaker", TileEntityBlockBreaker.class);
		setRotationMode(BlockRotationMode.SIX_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
		setInventoryRenderRotation(ForgeDirection.EAST);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {

		Icons.top = registry.registerIcon("openblocks:blockBreaker");
		Icons.top_active = registry.registerIcon("openblocks:blockBreaker_active");
		Icons.bottom = registry.registerIcon("openblocks:blockBreaker_bottom");
		Icons.side = registry.registerIcon("openblocks:blockBreaker_side");

		setTexture(ForgeDirection.UP, Icons.top);
		setTexture(ForgeDirection.DOWN, Icons.bottom);
		setTexture(ForgeDirection.EAST, Icons.side);
		setTexture(ForgeDirection.WEST, Icons.side);
		setTexture(ForgeDirection.NORTH, Icons.side);
		setTexture(ForgeDirection.SOUTH, Icons.side);
		setDefaultTexture(Icons.side);
	}

	@Override
	public Icon getUnrotatedTexture(ForgeDirection direction, IBlockAccess world, int x, int y, int z) {
		if (direction.equals(ForgeDirection.UP)) {
			TileEntityBlockBreaker tile = getTileEntity(world, x, y, z, TileEntityBlockBreaker.class);
			if (tile != null && tile.isActivated()) { return Icons.top_active; }
		}
		return super.getUnrotatedTexture(direction, world, x, y, z);
	}

}
