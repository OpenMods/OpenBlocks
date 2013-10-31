package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.utils.BlockUtils;

public class BlockLightbox extends OpenBlock {

	public Icon sideIcon;

	public BlockLightbox() {
		super(Config.blockLightboxId, Material.glass);
		setupBlock(this, "lightbox", TileEntityLightbox.class);
		setLightValue(1.0f);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) * 15;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z);
		world.setBlockMetadataWithNotify(x, y, z, powered? 1 : 0, 3);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z);
		world.setBlockMetadataWithNotify(x, y, z, powered? 1 : 0, 3);
		super.onNeighborBlockChange(world, x, y, z, blockId);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
		return meta;
	}

	@Override
	public void onBlockPlacedBy(World world, EntityPlayer player, ItemStack stack, int x, int y, int z, ForgeDirection side, float hitX, float hitY, float hitZ, int meta) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntityLightbox) {
			TileEntityLightbox lightbox = (TileEntityLightbox)tile;
			lightbox.setSurfaceAndRotation(side.getOpposite(), BlockUtils.get2dOrientation(player));
		}
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
	}

	@Override
	public void registerIcons(IconRegister registry) {
		super.registerIcons(registry);
		this.sideIcon = registry.registerIcon(String.format("%s:%s", modKey, "lightbox_side"));
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile == null || !(tile instanceof TileEntityLightbox)) { return; }

		TileEntityLightbox lightbox = (TileEntityLightbox)tile;

		ForgeDirection direction = lightbox.getSurface();

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
