package openblocks.common.block;

import openblocks.Config;
import openblocks.common.tileentity.TileEntityOreCrusher;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockMachineOreCrusher extends OpenBlock {

	public BlockMachineOreCrusher() {
		super(Config.blockMachineOreCrusherId, Material.ground);
		setupBlock(this, "oreCrusher", TileEntityOreCrusher.class);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}
	
	@Override
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
		return super.canPlaceBlockAt(par1World, par2, par3, par4) &&
				par1World.isAirBlock(par2, par3 + 1, par4);
				
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

}
