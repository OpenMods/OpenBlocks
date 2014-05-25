package openblocks.common.block;

import javax.swing.Icon;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDrawingTable extends OpenBlock {

	Icon topIcon, frontIcon;

	public BlockDrawingTable() {
		super(Material.wood);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister registry) {
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
