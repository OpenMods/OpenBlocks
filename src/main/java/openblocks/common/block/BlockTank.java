package openblocks.common.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidTank;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemTankBlock;
import openblocks.common.tileentity.TileEntityTank;
import openmods.Log;
import openmods.infobook.BookDocumentation;
import openmods.utils.ItemUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation
public class BlockTank extends OpenBlock {

	public BlockTank() {
		super(Material.rock);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	protected void getCustomTileEntityDrops(TileEntity te, List<ItemStack> result, int fortune) {
		ItemStack stack = new ItemStack(OpenBlocks.Blocks.tank);
		if (!(te instanceof TileEntityTank)) return;
		TileEntityTank tank = (TileEntityTank)te;
		if (tank.getTank().getFluidAmount() > 0) {
			NBTTagCompound tankTag = tank.getItemNBT();

			NBTTagCompound itemTag = ItemUtils.getItemTag(stack);
			itemTag.setTag("tank", tankTag);
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
				nbt.setTag("tank", tankTag);
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

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(Item item, CreativeTabs tab, List result) {
		result.add(new ItemStack(item));

		if (tab == null && Config.displayAllFilledTanks) {
			for (Fluid fluid : FluidRegistry.getRegisteredFluids().values())
				try {
					final ItemStack tankStack = ItemTankBlock.createFilledTank(fluid);

					if (tankStack != null) result.add(tankStack);
					else Log.debug("Failed to create filled tank stack for fluid '%s'. Not registered?", fluid.getName());
				} catch (Throwable t) {
					throw new RuntimeException(String.format("Failed to create item for fluid '%s'" +
							"Until this is fixed, you can bypass this code with config option 'tanks.displayAllFluids'",
							fluid.getName()), t);
				}
		}
	}

}
