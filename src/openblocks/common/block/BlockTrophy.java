package openblocks.common.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.common.TrophyHandler.Trophy;
import openblocks.common.tileentity.TileEntityTrophy;

public class BlockTrophy extends OpenBlock {

	public BlockTrophy() {
		super(Config.blockTrophyId, Material.ground);
		setBlockBounds(0.2f, 0, 0.2f, 0.8f, 0.2f, 0.8f);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	protected ItemStack getDroppedBlock(TileEntityTrophy te) {
		if (te != null) {
			Trophy trophy = te.getTrophy();
			if (trophy != null) return trophy.getItemStack();
		}

		return new ItemStack(this, 1, 0);
	}

	@Override
	protected void getCustomTileEntityDrops(TileEntity te, List<ItemStack> result) {
		TileEntityTrophy trophy = (te instanceof TileEntityTrophy)? (TileEntityTrophy)te : null;
		result.add(getDroppedBlock(trophy));
	}

	@Override
	protected boolean hasNormalDrops() {
		return false;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		TileEntityTrophy te = getTileEntity(world, x, y, z, TileEntityTrophy.class);
		return getDroppedBlock(te);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getAABBPool().getAABB(x + 0.2, y + 0.0, z + 0.2, x + 0.8, y + 0.8, z + 0.8);
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getAABBPool().getAABB(x + 0.2, y + 0.0, z + 0.2, x + 0.8, y + 1.0, z + 0.8);
	}

}
