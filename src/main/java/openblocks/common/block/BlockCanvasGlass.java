package openblocks.common.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class BlockCanvasGlass extends BlockCanvas {

	public BlockCanvasGlass() {
		super(Material.GLASS);
		setSoundType(SoundType.GLASS);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs creativeTabs, NonNullList<ItemStack> list) {}
}
