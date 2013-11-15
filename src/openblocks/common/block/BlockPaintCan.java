package openblocks.common.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityPaintCan;

public class BlockPaintCan extends OpenBlock {

	public int renderPass = 0;

	public static class Icons {
		public static Icon top;
		public static Icon back;
		public static Icon left;
		public static Icon right;
		public static Icon front;
		public static Icon bottom;
	}

	public BlockPaintCan() {
		super(Config.blockPaintCanId, Material.ground);
		setupBlock(this, "paintcan", TileEntityPaintCan.class);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
		setBlockBounds(0.25f, 0f, 0.25f, 0.7f, 0.6875f, 0.75f);
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ItemStack stack = new ItemStack(OpenBlocks.Blocks.paintCan);
		TileEntityPaintCan tile = getTileEntity(world, x, y, z, TileEntityPaintCan.class);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("color", tile.getColor());
		stack.setTagCompound(tag);
		ret.add(stack);
		return ret;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		int color = getColorFromNBT(itemStack);
		if (color < 0) color = 0;
		list.add("#" + Integer.toHexString(color).toUpperCase());
	}

	@Override
	public void registerIcons(IconRegister register) {
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
		TileEntityPaintCan tile = this.getTileEntity(world, x, y, z, TileEntityPaintCan.class);
		if (tile != null) { return tile.getColor(); }
		return 16777215;
	}

	public static int getColorFromNBT(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("color")) { return tag.getInteger("color"); }
		return 0;
	}

	public static int getAmountFromNBT(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag != null && tag.hasKey("amount")) { return tag.getInteger("amount"); }
		return 0;
	}

}
