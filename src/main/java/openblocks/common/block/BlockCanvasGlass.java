package openblocks.common.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockCanvasGlass extends BlockCanvas {

	public BlockCanvasGlass() {
		super(Material.glass);
		setStepSound(soundTypeGlass);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isFullBlock() {
		return false;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs creativeTabs, List<ItemStack> list) {}
}
