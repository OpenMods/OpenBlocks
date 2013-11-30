package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockItemDropper extends OpenBlock {
	@SideOnly(Side.CLIENT)
	private Icon downIcon;

	public BlockItemDropper() {
		super(Config.blockItemDropperId, Material.rock);
		setRotationMode(BlockRotationMode.NONE);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		super.registerIcons(registry);
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
