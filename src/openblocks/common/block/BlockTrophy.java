package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.item.ItemTrophyBlock;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityTrophy;
import openblocks.common.tileentity.tank.TileEntityTank;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class BlockTrophy extends OpenBlock {

	public BlockTrophy() {
		super(OpenBlocks.Config.blockTrophyId, Material.ground);
		setupBlock(this, "trophy", TileEntityTrophy.class, ItemTrophyBlock.class);
		setBlockBounds(0.3f, 0f, 0.3f, 0.7f, 0.8f, 0.7f);
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
	

	public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer) {
		// System.out.println(getTileEntity(par1World, par2, par3, par3,
		// TileEntityTank.class));
	}

	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
			TileEntityTrophy trophy = getTileEntity(world, x, y, z, TileEntityTrophy.class);
			if (trophy.trophyType != null) {
				ItemStack itemStack = trophy.trophyType.getItemStack();
				float f = 0.7F;
				double d0 = (double)(world.rand.nextFloat() * f)
						+ (double)(1.0F - f) * 0.5D;
				double d1 = (double)(world.rand.nextFloat() * f)
						+ (double)(1.0F - f) * 0.5D;
				double d2 = (double)(world.rand.nextFloat() * f)
						+ (double)(1.0F - f) * 0.5D;
				EntityItem entityitem = new EntityItem(world, (double)x + d0, (double)y
						+ d1, (double)z + d2, itemStack);
				entityitem.delayBeforeCanPickup = 10;
				world.spawnEntityInWorld(entityitem);
			}
		}
		return world.setBlockToAir(x, y, z);
	}

	@Override
	protected void dropBlockAsItem_do(World world, int x, int y, int z, ItemStack itemStack) {

	}
}
