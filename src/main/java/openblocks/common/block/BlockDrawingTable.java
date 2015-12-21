package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.block.BlockRotationMode;
import openmods.infobook.BookDocumentation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation
public class BlockDrawingTable extends OpenBlock {

	private IIcon topIcon;
	private IIcon frontIcon;

	public BlockDrawingTable() {
		super(Material.wood);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		super.registerBlockIcons(registry);
		this.topIcon = registry.registerIcon("openblocks:drawingtable_top");
		this.frontIcon = registry.registerIcon("openblocks:drawingtable_front");
		setTexture(ForgeDirection.UP, topIcon);
		setTexture(ForgeDirection.DOWN, blockIcon);
		setTexture(ForgeDirection.EAST, blockIcon);
		setTexture(ForgeDirection.WEST, blockIcon);
		setTexture(ForgeDirection.NORTH, blockIcon);
		setTexture(ForgeDirection.SOUTH, frontIcon);
	}
}
