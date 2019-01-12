package openblocks.common.block;

import javax.annotation.Nonnull;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.common.item.ItemTankBlock;
import openblocks.common.tileentity.TileEntityTank;
import openmods.Log;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;
import openmods.model.variant.VariantModelState;
import openmods.utils.ItemUtils;

@BookDocumentation(hasVideo = true)
public class BlockTank extends OpenBlock {

	public BlockTank() {
		super(Material.ROCK);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this,
				new IProperty[] { getPropertyOrientation() },
				new IUnlistedProperty[] { VariantModelState.PROPERTY });
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		final IExtendedBlockState oldState = (IExtendedBlockState)state;
		final TileEntityTank te = getTileEntity(world, pos, TileEntityTank.class);
		if (te == null) return state;
		final VariantModelState selectors = te.getModelState();
		return oldState.withProperty(VariantModelState.PROPERTY, selectors);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (!Config.tanksEmitLight) return 0;

		TileEntityTank tile = getTileEntity(world, pos, TileEntityTank.class);
		return tile != null? tile.getFluidLightLevel() : 0;
	}

	@Override
	@Nonnull
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		ItemStack result = new ItemStack(this);
		TileEntityTank tile = getTileEntity(world, pos, TileEntityTank.class);
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
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
		TileEntityTank tile = getTileEntity(world, pos, TileEntityTank.class);
		double value = tile.getFluidRatio() * 15;
		if (value == 0) return 0;
		int trunc = MathHelper.floor(value);
		return Math.max(trunc, 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> result) {
		result.add(new ItemStack(this));

		if (tab == CreativeTabs.SEARCH && Config.displayAllFilledTanks) {
			final ItemStack emptyTank = new ItemStack(this);
			for (Fluid fluid : FluidRegistry.getRegisteredFluids().values())
				try {
					final ItemStack tankStack = emptyTank.copy();
					if (ItemTankBlock.fillTankItem(tankStack, fluid)) result.add(tankStack);
					else Log.debug("Failed to create filled tank stack for fluid '%s'. Not registered?", fluid.getName());
				} catch (Throwable t) {
					throw new RuntimeException(String.format("Failed to create item for fluid '%s'. Until this is fixed, you can bypass this code with config option 'tanks.displayAllFluids'",
							fluid.getName()), t);
				}
		}
	}

}
