package openblocks.common.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.item.ItemPaintCan;
import openblocks.common.tileentity.TileEntityPaintCan;

public class BlockPaintCan extends OpenBlock {

	public int renderPass = 0;

	public static class Icons {
		public static IIcon top;
		public static IIcon back;
		public static IIcon left;
		public static IIcon right;
		public static IIcon front;
		public static IIcon bottom;
	}

	public BlockPaintCan() {
		super(Material.rock);
		setHardness(0);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
		setBlockBounds(0.25f, 0f, 0.25f, 0.7f, 0.6875f, 0.75f);
	}

	@Override
	protected void getCustomTileEntityDrops(TileEntity te, List<ItemStack> result, int fortune) {
		if (te instanceof TileEntityPaintCan) {
			TileEntityPaintCan can = (TileEntityPaintCan)te;
			result.add(ItemPaintCan.createStack(can.getColor(), can.getAmount()));
		}
	}

	@Override
	protected boolean hasNormalDrops() {
		return false;
	}

	private static ItemStack createStackForBlock(World world, int x, int y, int z) {
		TileEntityPaintCan tile = getTileEntity(world, x, y, z, TileEntityPaintCan.class);
		if (tile == null) return null;
		return ItemPaintCan.createStack(tile.getColor(), tile.getAmount());
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		Icons.back = register.registerIcon("openblocks:paintcan_side");
		Icons.front = register.registerIcon("openblocks:paintcan_front");
		Icons.left = register.registerIcon("openblocks:paintcan_left");
		Icons.right = register.registerIcon("openblocks:paintcan_right");
		Icons.top = register.registerIcon("openblocks:paintcan_top");
		Icons.bottom = register.registerIcon("openblocks:paintcan_bottom");

		setTexture(ForgeDirection.EAST, Icons.right);
		setTexture(ForgeDirection.WEST, Icons.left);
		setTexture(ForgeDirection.NORTH, Icons.back);
		setTexture(ForgeDirection.SOUTH, Icons.front);
		setTexture(ForgeDirection.UP, Icons.top);
		setTexture(ForgeDirection.DOWN, Icons.bottom);
		setDefaultTexture(Icons.back);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		if (renderPass == 0 && side == 1) {
			return false;
		} else if (renderPass == 1 && side != 1) { return false; }
		return super.shouldSideBeRendered(world, x, y, z, side);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		if (renderPass == 0) { return 0xFFFFFF; }
		TileEntityPaintCan tile = getTileEntity(world, x, y, z, TileEntityPaintCan.class);
		if (tile != null) { return tile.getColor(); }
		return 0xFFFFFF;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return createStackForBlock(world, x, y, z);
	}

}
