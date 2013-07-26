package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityFlag;

public class BlockFlag extends OpenBlock {

	public BlockFlag() {
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
		if (tile != null && tile instanceof TileEntityFlag) {
			TileEntityFlag flag = (TileEntityFlag) tile;
			flag.setRotation(entity.rotationYawHead);
		}
	}
}
