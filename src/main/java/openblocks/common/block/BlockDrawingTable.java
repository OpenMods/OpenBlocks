package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.block.BlockRotationMode;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
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
