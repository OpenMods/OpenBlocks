package openblocks.common.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.IFluidTank;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityTank;
import openmods.utils.ItemUtils;

public class BlockTank extends OpenBlock {

	public BlockTank() {
		super(Config.blockTankId, Material.rock);
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
	public boolean shouldRenderBlock() {
		return false;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	protected void getCustomTileEntityDrops(TileEntity te, List<ItemStack> result) {
		ItemStack stack = new ItemStack(OpenBlocks.Blocks.tank);
		if (!(te instanceof TileEntityTank)) return;
		TileEntityTank tank = (TileEntityTank)te;
		if (tank.getTank().getFluidAmount() > 10) {
			NBTTagCompound tankTag = tank.getItemNBT();

			NBTTagCompound itemTag = ItemUtils.getItemTag(stack);
			itemTag.setCompoundTag("tank", tankTag);
		}
		result.add(stack);
	}

	@Override
	protected boolean hasNormalDrops() {
		return false;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		if (!Config.tanksEmitLight) return 0;
		TileEntityTank tile = getTileEntity(world, x, y, z, TileEntityTank.class);
		return tile != null? tile.getFluidLightLevel() : 0;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		ItemStack result = new ItemStack(this);
		TileEntityTank tile = getTileEntity(world, x, y, z, TileEntityTank.class);
		if (tile != null) {
			IFluidTank tank = tile.getTank();
			if (tank.getFluidAmount() > 0) {
				NBTTagCompound tankTag = tile.getItemNBT();
				if (tankTag.hasKey("Amount")) tankTag.setInteger("Amount", tank.getCapacity());

				NBTTagCompound nbt = ItemUtils.getItemTag(result);
				nbt.setCompoundTag("tank", tankTag);
			}
		}
		return result;
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		TileEntityTank tile = getTileEntity(world, x, y, z, TileEntityTank.class);
		double value = tile.getFluidRatio() * 15;
		if (value == 0) return 0;
		int trunc = MathHelper.floor_double(value);
		return Math.max(trunc, 1);
	}

}
