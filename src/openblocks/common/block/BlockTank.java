package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemTankBlock;
import openblocks.common.tileentity.TileEntityTank;

public class BlockTank extends OpenBlock {

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

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		if (!OpenBlocks.Config.tanksEmitLight) return 0;
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		if (ent == null) return 0;
		if (ent instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank)ent;
			if (tank.containsValidLiquid()) {
				try {
					int blockId = tank.getClientLiquidId();
					if(blockId < 0 || blockId > Block.blocksList.length) return 0;
					if (Block.blocksList[blockId] == null) return 0;
					return (int)Math.min(Block.lightValue[blockId], Math.max(0, 5 + tank.getPercentFull() * 15));
				}catch(Exception e) {
					System.out.println("[OpenModsMonitor] Hello, It's OpenBlocks here. We've got a " + e.toString() + " at " + x + "," + y + "," + z + ". Please report this to the OpenMods team, they'll patch this bug up as soon as possible.");
					return 0;
				}
			}
		}
		return 0;
	}

	@Override
	public int getLightOpacity(World world, int x, int y, int z) {
		if (!OpenBlocks.Config.tanksAreTransparent) return 255;
		if (!OpenBlocks.Config.tanksHaveDynamicTransparency) return 0;
		/*
		 * As per docs, the tile entity is not guaranteed to exist at the time
		 * of calling
		 */
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		if (ent == null) return 255;
		if (ent instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank)ent;
			if (tank.containsValidLiquid()) {
				return (int)Math.min(255, Math.max(0, (tank.getPercentFull() * 255)));
			} else {
				return 0;
			}
		}
		return 255;
	}

	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (!world.isRemote
				&& world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
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
