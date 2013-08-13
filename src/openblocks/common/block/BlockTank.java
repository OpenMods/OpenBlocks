package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemTankBlock;
import openblocks.common.tileentity.tank.TileEntityTank;

public class BlockTank extends OpenBlock {

	public static int itemId;

	public BlockTank() {
		super(OpenBlocks.Config.blockTankId, Material.ground);
		setupBlock(this, "tank", TileEntityTank.class, ItemTankBlock.class);
	}

	@Override
	public boolean canBeReplacedByLeaves(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return false;
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

	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer) {
		// System.out.println(getTileEntity(par1World, par2, par3, par3,
		// TileEntityTank.class));
	}

	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (!world.isRemote
				&& world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
			ItemStack itemStack = new ItemStack(OpenBlocks.Blocks.tank);
			TileEntityTank tank = getTileEntity(world, x, y, z, TileEntityTank.class);
			/* Maybe you lose a small amount of liquid, but you ARE breaking a block here */
			if (tank != null && tank.getHeightForRender() > 0.09) {
				NBTTagCompound nbt = new NBTTagCompound();
				NBTTagCompound tankTag = tank.getItemNBT();
				nbt.setCompoundTag("tank", tankTag);
				itemStack.setTagCompound(nbt);
			}
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
		return world.setBlockToAir(x, y, z);
	}

	@Override
	protected void dropBlockAsItem_do(World world, int x, int y, int z, ItemStack itemStack) {

	}
}
