package openblocks.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.Stencil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStencil extends Item {

	public ItemStencil() {
		super(Config.itemStencilId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setUnlocalizedName("openblocks.stencil");
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getSpriteNumber() {
		return 0;
	}

	@Override
	public Icon getIconFromDamage(int dmg) {
		return Stencil.values()[dmg].getCoverBlockIcon();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(int id, CreativeTabs par2CreativeTabs, List list) {
		for (Stencil stencil : Stencil.values()) {
			list.add(new ItemStack(id, 1, stencil.ordinal()));
		}
	}

	@Override
	public boolean shouldPassSneakingClickToBlock(World world, int x, int y, int z) {
		return world.getBlockId(x, y, z) == OpenBlocks.Blocks.canvas.blockID;
	}
}
