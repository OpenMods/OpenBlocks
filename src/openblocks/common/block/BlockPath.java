package openblocks.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import openblocks.Config;
import openblocks.OpenBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockPath extends Block {

	public BlockPath() {
		super(Config.blockPathId, Material.ground);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		GameRegistry.registerBlock(this, "openmods.path");
		setBlockBounds(0, 0, 0, 1f, 0.1f, 1f);
		setUnlocalizedName("openblocks.path");
	}

	public void registerIcons(IconRegister register) {
		blockIcon = register.registerIcon("openblocks:path");
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return isValidLocation(world, x, y, z) && super.canPlaceBlockAt(world, x, y, z);
	}
	
	protected boolean isValidLocation(World world, int x, int y, int z) {
		int bId = world.getBlockId(x, y - 1, z);
		Block below = Block.blocksList[bId];
		if (below != null) {
			return below.isBlockSolidOnSide(world, x, y - 1, z, ForgeDirection.UP);
		}
		return false;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int par5) {
		if (!world.isRemote && !isValidLocation(world, x, y, z)) {
			dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
		}
	}

	
}
