package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockItemDropper extends OpenBlock {
	@SideOnly(Side.CLIENT)
	private IIcon downIcon;

	public BlockItemDropper() {
		super(Material.rock);
		setRotationMode(BlockRotationMode.NONE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		super.registerBlockIcons(registry);
		this.downIcon = registry.registerIcon("openblocks:itemDropper_down");
		setTexture(ForgeDirection.UP, blockIcon);
		setTexture(ForgeDirection.DOWN, downIcon);
		setTexture(ForgeDirection.EAST, blockIcon);
		setTexture(ForgeDirection.WEST, blockIcon);
		setTexture(ForgeDirection.NORTH, blockIcon);
		setTexture(ForgeDirection.SOUTH, blockIcon);
		setDefaultTexture(blockIcon);
	}

}
