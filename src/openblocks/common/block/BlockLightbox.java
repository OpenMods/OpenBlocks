package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityLightbox;

public class BlockLightbox extends OpenBlock {

	public Icon sideIcon;

	public BlockLightbox() {
		super(Config.blockLightboxId, Material.glass);
		setupBlock(this, "lightbox", TileEntityLightbox.class);
		setLightValue(1.0f);
		setRotationMode(BlockRotationMode.TWENTYFOUR_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}


	@Override
	public boolean shouldRenderBlock() {
		return true;
	}
	
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		if (world instanceof World) {
			return ((World)world).isBlockIndirectlyGettingPowered(x, y, z) ? 15 : 0;
		}
		return 0;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void registerIcons(IconRegister registry) {
		super.registerIcons(registry);
		this.sideIcon = registry.registerIcon("openblocks:lightbox_side");
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		
		int metadata = world.getBlockMetadata(x, y, z);
		
		ForgeDirection direction = ForgeDirection.getOrientation(metadata);
		
		switch (direction) {
			case EAST:
				setBlockBounds(0.8f, 0, 0, 1f, 1f, 1f);
				break;
			case WEST:
				setBlockBounds(0, 0, 0, 0.2f, 1f, 1f);
				break;
			case NORTH:
				setBlockBounds(0, 0, 0, 1f, 1f, 0.2f);
				break;
			case SOUTH:
				setBlockBounds(0, 0, 0.8f, 1f, 1f, 1f);
				break;
			case UP:
				setBlockBounds(0, 0.8f, 0, 1f, 1f, 1f);
				break;
			case DOWN:
				setBlockBounds(0, 0, 0, 1f, 0.2f, 1f);
				break;
			default:
				setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public Icon getIcon(int side, int meta) {
		if (side == 2 || side == 3) { return super.getIcon(side, meta); }
		return sideIcon;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return isNeighborBlockSolid(world, x, y, z, side);
	}

}
