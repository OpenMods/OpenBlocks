package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemTankBlock;
import openblocks.common.tileentity.TileEntityTank;

public class BlockTank extends OpenBlock {

	public BlockTank() {
		super(Config.blockTankId, Material.ground);
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

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
			ItemStack itemStack = new ItemStack(OpenBlocks.Blocks.tank);
			TileEntityTank tank = getTileEntity(world, x, y, z, TileEntityTank.class);
			/*
			 * Maybe you lose a small amount of liquid, but you ARE breaking a
			 * block here
			 */
			if (tank != null && tank.getAmount() > 10) {
				NBTTagCompound nbt = new NBTTagCompound();
				NBTTagCompound tankTag = tank.getItemNBT();
				nbt.setCompoundTag("tank", tankTag);
				itemStack.setTagCompound(nbt);
			}
			float f = 0.7F;
			float d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
			float d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
			float d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
			EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
			entityitem.delayBeforeCanPickup = 10;
			world.spawnEntityInWorld(entityitem);
		}
		return world.setBlockToAir(x, y, z);
	}

	@Override
	protected void dropBlockAsItem_do(World world, int x, int y, int z, ItemStack itemStack) {

	}
}
