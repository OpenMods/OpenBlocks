package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityFlag;
import openblocks.utils.BlockUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

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
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityFlag) {
			if (!world.isRemote) { 
				((TileEntityFlag)te).onActivated(player);
			}
			return false;
		}
		return true;
	}
}
