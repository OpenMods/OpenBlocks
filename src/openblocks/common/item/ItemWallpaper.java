package openblocks.common.item;

import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockCanvas;
import openblocks.common.sync.SyncableBlockLayers;
import openblocks.common.sync.SyncableBlockLayers.Layer;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.utils.BlockUtils;
import openmods.utils.render.PaintUtils;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemWallpaper extends Item {

	public static class BlockSideTexture {

		public static final String TAG_BLOCK_ID = "blockId";
		public static final String TAG_BLOCK_META = "blockMeta";
		public static final String TAG_BLOCK_SIDE = "blockSide";

		private int blockId;
		private int blockMeta;
		private int blockSide;

		public BlockSideTexture(int id, int meta, int side) {
			blockId = id;
			blockMeta = meta;
			blockSide = side;
		}

		public Icon getIcon() {
			Block block = Block.blocksList[blockId];
			if (block != null) { return block.getIcon(blockSide, blockMeta); }
			return null;
		}

		public static BlockSideTexture fromItemStack(ItemStack stack) {

			if (stack.hasTagCompound()) {

				NBTTagCompound tag = stack.getTagCompound();

				if (tag.hasKey(TAG_BLOCK_ID) &&
						tag.hasKey(TAG_BLOCK_META) &&
						tag.hasKey(TAG_BLOCK_SIDE)) {

				return new BlockSideTexture(
						tag.getInteger(TAG_BLOCK_ID),
						tag.getInteger(TAG_BLOCK_META),
						tag.getInteger(TAG_BLOCK_SIDE)); }
			}
			return null;
		}

		public void writeToStack(ItemStack stack) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger(TAG_BLOCK_ID, blockId);
			tag.setInteger(TAG_BLOCK_META, blockMeta);
			tag.setInteger(TAG_BLOCK_SIDE, blockSide);
			stack.setTagCompound(tag);
		}

		public int getBlockId() {
			return blockId;
		}

		public int getBlockMeta() {
			return blockMeta;
		}

		public int getBlockSide() {
			return blockSide;
		}
	}

	public ItemWallpaper() {
		super(Config.itemWallpaperId);
		setHasSubtypes(true);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getSpriteNumber() {
		return 0;
	}

	@Override
	public Icon getIconIndex(ItemStack stack) {
		BlockSideTexture texture = BlockSideTexture.fromItemStack(stack);
		if (texture != null) { return texture.getIcon(); }
		return itemIcon;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

		TileEntity te = world.getBlockTileEntity(x, y, z);

		boolean canReplaceBlock = PaintUtils.instance.isAllowedToReplace(world, x, y, z);

		if (canReplaceBlock || te instanceof TileEntityCanvas) {

			BlockSideTexture texture = BlockSideTexture.fromItemStack(stack);

			if (texture != null) {

				if (canReplaceBlock) {
					BlockCanvas.replaceBlock(world, x, y, z);
				}

				te = world.getBlockTileEntity(x, y, z);

				if (te instanceof TileEntityCanvas) {

					TileEntityCanvas canvas = (TileEntityCanvas)te;
					canvas.setWallpaper(side, texture);

					stack.stackSize--;
				}

			} else if (!world.isRemote) {

				int blockId = world.getBlockId(x, y, z);
				int meta = world.getBlockMetadata(x, y, z);

				if (te instanceof TileEntityCanvas) {

					TileEntityCanvas canvas = (TileEntityCanvas)te;

					SyncableBlockLayers layer = canvas.getLayersForSide(side);

					blockId = layer.getBaseTextureBlockId();
					meta = layer.getBaseTextureMetadata();
					side = layer.getBaseTextureSide();
				}

				texture = new BlockSideTexture(blockId, meta, side);

				ItemStack cloneStack = stack.copy();
				cloneStack.stackSize = 1;
				texture.writeToStack(cloneStack);

				if (player == null || !player.inventory.addItemStackToInventory(cloneStack)) {
					BlockUtils.dropItemStackInWorld(world, hitX + x, y, z, cloneStack);
				} else {
					if (player instanceof EntityPlayerMP) {
						MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
						server.getConfigurationManager().syncPlayerInventory((EntityPlayerMP)player);
					}
				}

				stack.stackSize--;
			}
		}
		return true;
	}

}
