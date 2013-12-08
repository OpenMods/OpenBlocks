package openblocks.common.block;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityTank;

public class BlockTank extends OpenBlock {

	public BlockTank() {
		super(Config.blockTankId, Material.ground);
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
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ItemStack stack = new ItemStack(OpenBlocks.Blocks.tank);
		TileEntityTank tile = getTileEntity(world, x, y, z, TileEntityTank.class);
		if (tile != null && tile.getTank().getFluidAmount() > 10) {
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagCompound tankTag = tile.getItemNBT();
			nbt.setCompoundTag("tank", tankTag);
			stack.setTagCompound(nbt);
		}
		ret.add(stack);
		return ret;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		if (!Config.tanksEmitLight) return 0;
		TileEntityTank tile = getTileEntity(world, x, y, z, TileEntityTank.class);
		if (tile != null) { return tile.getFluidLightLevel(); }
		return 0;
	}
}
