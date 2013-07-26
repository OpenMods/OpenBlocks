package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityFlag;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.utils.BlockUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFlag extends OpenBlock {

	protected BlockFlag() {
		super(OpenBlocks.Config.blockFlagId, Material.ground);
		setupBlock(this, "flag", "Flag", TileEntityFlag.class);
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
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving entity, ItemStack itemstack) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntityLightbox) {
			TileEntityLightbox lightbox = (TileEntityLightbox) tile;
			lightbox.setSurfaceAndRotation(BlockUtils.get3dOrientation(entity),
					BlockUtils.get2dOrientation(entity));
		}
	}
}
