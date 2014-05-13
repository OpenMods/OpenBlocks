package openblocks.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.Stencil;
import openblocks.common.tileentity.TileEntityCanvas;

public class BlockCanvas extends OpenBlock {

	private int layer = 0;
	private int renderSide = 0;
	public Icon baseIcon;
	public Icon wallpaper;

	public BlockCanvas() {
		super(Config.blockCanvasId, Material.rock);
	}

	public BlockCanvas(int id, Material material) {
		super(id, material);
	}

	@Override
	public void registerIcons(IconRegister registry) {
		baseIcon = registry.registerIcon("openblocks:canvas");
		wallpaper = registry.registerIcon("openblocks:wallpaper");
		for (Stencil stencil : Stencil.values()) {
			stencil.registerBlockIcons(registry);
		}
		super.registerIcons(registry);
	}

	@Override
	protected void getCustomTileEntityDrops(TileEntity te, List<ItemStack> result) {
		if (!(te instanceof TileEntityCanvas)) return;
		TileEntityCanvas tile = (TileEntityCanvas)te;
		int maskedBlockId = tile.paintedBlockId.getValue();

		Block maskedBlock = Block.blocksList[maskedBlockId];
		if (maskedBlock == null) return;
		int maskedMeta = tile.paintedBlockMeta.getValue();

		for (int i = 0; i < maskedBlock.quantityDropped(te.worldObj.rand); i++) {
			int droppedId = maskedBlock.idDropped(maskedMeta, te.worldObj.rand, 0);
			Block dropped = Block.blocksList[droppedId];
			if (dropped != null) result.add(new ItemStack(dropped, 1, maskedBlock.damageDropped(maskedMeta)));
		}
	}

	@Override
	protected boolean hasNormalDrops() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	public void setLayerForRender(int layer) {
		this.layer = layer;
	}

	public void setSideForRender(int side) {
		this.renderSide = side;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return side == renderSide && super.shouldSideBeRendered(world, x, y, z, side);
	}

	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		return tile != null? tile.getColorForRender(renderSide, layer) : 0xFFFFFFFF;
	}

	@Override
	public Icon getUnrotatedTexture(ForgeDirection direction, IBlockAccess world, int x, int y, int z) {
		TileEntityCanvas tile = getTileEntity(world, x, y, z, TileEntityCanvas.class);
		if (tile != null) { return tile.getTextureForRender(renderSide, layer); }
		return super.getUnrotatedTexture(direction, world, x, y, z);

	}

	public static void replaceBlock(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		Material material = world.getBlockMaterial(x, y, z);
		if (material == Material.glass) {
			world.setBlock(x, y, z, OpenBlocks.Blocks.canvasGlass.blockID);
		} else {
			world.setBlock(x, y, z, OpenBlocks.Blocks.canvas.blockID);
		}
		TileEntityCanvas tile = (TileEntityCanvas)world.getBlockTileEntity(x, y, z);
		tile.paintedBlockId.setValue(id);
		tile.paintedBlockMeta.setValue(meta);
	}
}
