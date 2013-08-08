package openblocks.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.block.OpenBlock;
import openblocks.utils.BlockUtils;

public class ItemOpenBlock extends ItemBlock {

	public ItemOpenBlock(int par1) {
		super(par1);
	}

	/**
	 * Replicates the super method, except we're not switching the metadata on
	 * onBlockPlaced()
	 */
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		int i1 = par3World.getBlockId(par4, par5, par6);

		if (i1 == Block.snow.blockID
				&& (par3World.getBlockMetadata(par4, par5, par6) & 7) < 1) {
			par7 = 1;
		} else if (i1 != Block.vine.blockID
				&& i1 != Block.tallGrass.blockID
				&& i1 != Block.deadBush.blockID
				&& (Block.blocksList[i1] == null || !Block.blocksList[i1].isBlockReplaceable(par3World, par4, par5, par6))) {
			if (par7 == 0) {
				--par5;
			}

			if (par7 == 1) {
				++par5;
			}

			if (par7 == 2) {
				--par6;
			}

			if (par7 == 3) {
				++par6;
			}

			if (par7 == 4) {
				--par4;
			}

			if (par7 == 5) {
				++par4;
			}
		}
		if (par1ItemStack.stackSize == 0) {
			return false;
		} else if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)) {
			return false;
		} else if (par5 == 255
				&& Block.blocksList[getBlockID()].blockMaterial.isSolid()) {
			return false;
		} else if (par3World.canPlaceEntityOnSide(getBlockID(), par4, par5, par6, false, par7, par2EntityPlayer, par1ItemStack)) {
			Block block = Block.blocksList[getBlockID()];
			int j1 = this.getMetadata(par1ItemStack.getItemDamage());
			// int k1 = Block.blocksList[getBlockID()].onBlockPlaced(par3World,
			// par4, par5, par6, par7, par8, par9, par10, j1);

			OpenBlock openBlock = (OpenBlock)Block.blocksList[getBlockID()];

			ForgeDirection direction = BlockUtils.sideToDirection(par7);

			if (!openBlock.canPlaceBlockOnSide(par3World, par4, par5, par6, direction.getOpposite())) { return false; }
			// dont replace it!
			if (placeBlockAt(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10, j1)) {

				openBlock.onBlockPlacedBy(par3World, par2EntityPlayer, par1ItemStack, par4, par5, par6, direction, par8, par9, par10, j1);
				par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
				--par1ItemStack.stackSize;
			}

			return true;
		} else {
			return false;
		}
	}
}
