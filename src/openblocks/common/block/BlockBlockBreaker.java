package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityBlockBreaker;
import openblocks.utils.BlockUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBlockBreaker extends OpenBlock {

	@SideOnly(Side.CLIENT)
	private Icon faceIcon;

	public BlockBlockBreaker() {
		super(Config.blockBlockBreakerId, Material.rock);
		setupBlock(this, "blockBreaker", TileEntityBlockBreaker.class);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		super.registerIcons(registry);
		this.faceIcon = registry.registerIcon(String.format("%s:%s", modKey, "blockBreaker_face"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int metadata) {
		ForgeDirection rot = BlockUtils.get3dBlockRotationFromMetadata(metadata);
		return side == rot.ordinal()? faceIcon : blockIcon;
	}

}
