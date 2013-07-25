package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Config;
import openblocks.common.tileentity.TileEntityGrave;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockGrave extends OpenBlock {

	public BlockGrave() {
		super(Config.blockGraveId, Material.anvil); /* Requires tool and immovable */
		setupBlock(this, "grave", "Grave", TileEntityGrave.class);
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
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4,
			EntityLiving par5EntityLiving, ItemStack par6ItemStack) {
		super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLiving,
				par6ItemStack);
		TileEntityGrave grave = (TileEntityGrave)par1World.getBlockTileEntity(par2, par3, par4);
		if(par5EntityLiving instanceof EntityPlayer){
			grave.setUsername(((EntityPlayer)par5EntityLiving).username);
		}
	}

}
