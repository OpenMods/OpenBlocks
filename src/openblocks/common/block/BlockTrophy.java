package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityTrophy;

public class BlockTrophy extends OpenBlock {

	public BlockTrophy() {
		super(Config.blockTrophyId, Material.ground);
		setBlockBounds(0.3f, 0f, 0.3f, 0.7f, 0.8f, 0.7f);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
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
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (!world.isRemote
				&& world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
			TileEntityTrophy trophy = getTileEntity(world, x, y, z, TileEntityTrophy.class);
			if (trophy.getTrophy() != null) {
				ItemStack itemStack = trophy.getTrophy().getItemStack();
				float f = 0.7F;
				float d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
				float d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
				float d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5F;
				EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z
						+ d2, itemStack);
				entityitem.delayBeforeCanPickup = 10;
				world.spawnEntityInWorld(entityitem);
			}
		}
		return world.setBlockToAir(x, y, z);
	}

	@Override
	protected void dropBlockAsItem_do(World world, int x, int y, int z, ItemStack itemStack) {

	}
}
