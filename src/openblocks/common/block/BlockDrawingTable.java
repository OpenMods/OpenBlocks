package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityDrawingTable;
import openmods.block.OpenBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDrawingTable extends OpenBlock {

	Icon topIcon, frontIcon;
	
	public BlockDrawingTable() {
		super(Config.blockDrawingTable, Material.wood);
		setupBlock(this, "drawingtable", TileEntityDrawingTable.class);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		super.registerIcons(registry);
		this.topIcon = registry.registerIcon("openblocks:drawingtable_top");
		this.frontIcon = registry.registerIcon("openblocks:drawingtable_front");
		setTexture(ForgeDirection.UP, topIcon);
		setTexture(ForgeDirection.DOWN, blockIcon);
		setTexture(ForgeDirection.EAST, blockIcon);
		setTexture(ForgeDirection.WEST, blockIcon);
		setTexture(ForgeDirection.NORTH, blockIcon);
		setTexture(ForgeDirection.SOUTH, frontIcon);
		setDefaultTexture(blockIcon);
	}
}
