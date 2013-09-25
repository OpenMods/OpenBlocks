package openblocks.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.block.OpenBlock;

public class ItemOpenBlock extends ItemBlock {

	public ItemOpenBlock(int id) {
		super(id);
	}

	private static boolean canReplace(Block block, World world, int x, int y, int z) {
		return block != null && block.isBlockReplaceable(world, x, y, z);
	}

	protected void afterBlockPlaced(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
		stack.stackSize--;
	}

	protected boolean isStackValid(ItemStack stack, EntityPlayer player) {
		return stack.stackSize >= 0;
	}

	/**
	 * Replicates the super method, but with our own hooks
	 */
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!isStackValid(stack, player)) return false;

		int blockId = world.getBlockId(x, y, z);
		Block block = Block.blocksList[blockId];

		if (blockId == Block.snow.blockID
				&& (world.getBlockMetadata(x, y, z) & 7) < 1) side = 1;

		ForgeDirection sideDir = ForgeDirection.getOrientation(side);

		if (!canReplace(block, world, x, y, z)) {
			x += sideDir.offsetX;
			y += sideDir.offsetY;
			z += sideDir.offsetZ;
		}

		if (!player.canPlayerEdit(x, y, z, side, stack)) return false;

		int ownBlockId = getBlockID();
		Block ownBlock = Block.blocksList[ownBlockId];
		if (y == 255 && ownBlock.blockMaterial.isSolid()) return false;

		if (!world.canPlaceEntityOnSide(ownBlockId, x, y, z, false, side, player, stack)) return false;

		// B: it's alread called in World.canPlaceEntityOnSide?
		// if (ownBlock instanceof OpenBlock &&
		// !((OpenBlock)ownBlock).canPlaceBlockOnSide(world, x, y, z,
		// sideDir.getOpposite())) return false;

		int newMeta = getMetadata(stack.getItemDamage());
		newMeta = ownBlock.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, newMeta);

		if (!placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, newMeta)) return false;

		if (ownBlock instanceof OpenBlock) ((OpenBlock)ownBlock).onBlockPlacedBy(world, player, stack, x, y, z, sideDir, hitX, hitY, hitZ, newMeta);

		world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, ownBlock.stepSound.getPlaceSound(), (ownBlock.stepSound.getVolume() + 1.0F) / 2.0F, ownBlock.stepSound.getPitch() * 0.8F);
		afterBlockPlaced(stack, player, world, x, y, z);

		return true;
	}
}
