package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityBlockBreaker;
import openblocks.utils.SimpleBlockTextureHelper;
import openblocks.utils.BlockUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBlockBreaker extends OpenBlock {

	@SideOnly(Side.CLIENT)
	private Icon faceIcon;
	
	private SimpleBlockTextureHelper textureHelper = new SimpleBlockTextureHelper();

	public BlockBlockBreaker() {
		super(Config.blockBlockBreakerId, Material.rock);
		setupBlock(this, "blockbreaker", TileEntityBlockBreaker.class);
		setRotationMode(BlockRotationMode.SIX_DIRECTIONS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		blockIcon = registry.registerIcon("openblocks:blockBreaker");
		textureHelper.setSideUp(registry.registerIcon("openblocks:blockBreaker_sideup"));
		textureHelper.setSideRight(registry.registerIcon("openblocks:blockBreaker_sideright"));
		textureHelper.setSideDown(registry.registerIcon("openblocks:blockBreaker_sidedown"));
		textureHelper.setSideLeft(registry.registerIcon("openblocks:blockBreaker_sideleft"));
		textureHelper.setTop(blockIcon);
		textureHelper.setBottom(registry.registerIcon("openblocks:blockBreaker_bottom"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int metadata) {
		ForgeDirection rot = BlockUtils.get3dBlockRotationFromMetadata(metadata);
		return textureHelper.getIconForDirection(rot, ForgeDirection.getOrientation(side));
	}

}
