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
import openblocks.Config;
import openblocks.Log;
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
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		if (!Config.tanksEmitLight) return 0;
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		if (ent == null) return 0;
		if (ent instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank)ent;
			if (tank.containsValidLiquid()) {
				try {
					int blockId = tank.getClientLiquidId();
					if (blockId < 0 || blockId > Block.blocksList.length) return 0;
					if (Block.blocksList[blockId] == null) return 0;
					return (int)Math.min(Block.lightValue[blockId], Math.max(0, 5 + tank.getPercentFull() * 15));
				} catch (Exception e) {
					Log.warn(e, "Hello, It's OpenBlocks here. We've got exception at (%d,%d,%d). Please report this to the OpenMods team, they'll patch this bug up as soon as possible", x, y, z);
					return 0;
				}
			}
		}
		return 0;
	}

	@Override
	public int getLightOpacity(World world, int x, int y, int z) {
		if (!Config.tanksAreTransparent) return 16;
		if (!Config.tanksHaveDynamicTransparency) return 0;
		/*
		 * As per docs, the tile entity is not guaranteed to exist at the time
		 * of calling
		 */
		TileEntity ent = world.getBlockTileEntity(x, y, z);
		if (ent == null) return 16;
		if (ent instanceof TileEntityTank) {
			TileEntityTank tank = (TileEntityTank)ent;
			if (tank.containsValidLiquid()) {
				return (int)Math.min(16, Math.max(0, (tank.getPercentFull() * 16)));
			} else {
				return 0;
			}
		}
		return 255;
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
