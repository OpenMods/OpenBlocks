package openblocks.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openblocks.common.block.BlockFlag;

public class ItemFlagBlock extends ItemOpenBlock {

	public ItemFlagBlock(int id) {
		super(id);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
			int par4, int par5, int par6, int par7, float par8, float par9,
			float par10) {
		if (player.isSneaking()) {
			return false;
		}
		return super.onItemUse(stack, player, world, par4, par5, par6, par7,
				par8, par9, par10);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,
			EntityPlayer player) {
		stack.setItemDamage((stack.getItemDamage() + 1)
				% BlockFlag.COLORS.length);
		return stack.copy();
	}

}
