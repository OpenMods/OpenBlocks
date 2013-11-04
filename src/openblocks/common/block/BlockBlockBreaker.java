package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
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
	Icon closedIcon;
	
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
		closedIcon = registry.registerIcon("openblocks:blockBreaker_active");
		textureHelper.setSideUp(registry.registerIcon("openblocks:blockBreaker_sideup"));
		textureHelper.setSideRight(registry.registerIcon("openblocks:blockBreaker_sideright"));
		textureHelper.setSideDown(registry.registerIcon("openblocks:blockBreaker_sidedown"));
		textureHelper.setSideLeft(registry.registerIcon("openblocks:blockBreaker_sideleft"));
		textureHelper.setTop(blockIcon);
		textureHelper.setBottom(registry.registerIcon("openblocks:blockBreaker_bottom"));
	}

	
	
	@Override
	public Icon getBlockTexture(IBlockAccess blockAccess, int x,int y, int z, int side) {
		int metadata = blockAccess.getBlockMetadata(x, y, z);
		ForgeDirection rotation = OpenBlock.getRotation(blockAccess, x, y, z);
		ForgeDirection ori = ForgeDirection.getOrientation(side);
		TileEntityBlockBreaker te = getTileEntity(blockAccess, x, y, z, TileEntityBlockBreaker.class);		
		if(te.isActivated() && rotation.equals(ori)) {
			return closedIcon;
		}
		return getIcon(side, metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int metadata) {
		ForgeDirection rot = BlockUtils.get3dBlockRotationFromMetadata(metadata);
		return textureHelper.getIconForDirection(rot, ForgeDirection.getOrientation(side));
	}

}
