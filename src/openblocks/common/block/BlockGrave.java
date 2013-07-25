package openblocks.common.block;

import java.util.Random;

import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Config;
import openblocks.common.entity.EntityGhost;
import openblocks.common.tileentity.TileEntityGrave;
import openblocks.utils.BlockUtils;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
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
	public int quantityDropped(Random rand){
        return 0;
    }

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int par5) {
		
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		if (!world.isRemote) { 
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			if (tile != null && tile instanceof TileEntityGrave) {
				TileEntityGrave grave = (TileEntityGrave) tile;
				if (world.difficultySetting == 0) {
					BlockUtils.dropInventory(grave.getLoot(), world, x, y, z);
				}else {
					EntityGhost ghost = new EntityGhost(world, grave.getUsername(), grave.getLoot());
					ghost.setPositionAndRotation(x, y, z, 0, 0);
					world.spawnEntityInWorld(ghost);
				}
			}
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x,
			int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x,
			int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y,
			int z) {
		this.setBlockBounds(0, 0, 0, 1f, 0.1f, 1f);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving living, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, living, stack);
		TileEntityGrave grave = (TileEntityGrave)world.getBlockTileEntity(x, y, z);
		if(living instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) living;
			grave.setUsername(player.username);
			grave.setLoot(player.inventory);
		}
	}

}
