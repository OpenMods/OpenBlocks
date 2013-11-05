package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityGuide;

public class BlockGuide extends OpenBlock {

	public static class Icons {
		public static Icon ends;
		public static Icon side;
	}

	public BlockGuide() {
		super(Config.blockGuideId, Material.ground);
		setupBlock(this, "guide", TileEntityGuide.class);
	}

	@Override
	public boolean isOpaqueCube() {
		return true;
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}
	
	@Override
	public boolean useTESRForInventory() {
		return false;
	}

	@Override
	public void registerIcons(IconRegister registry) {
		Icons.ends = registry.registerIcon("openblocks:guide");
		Icons.side = registry.registerIcon("openblocks:guide_side");
		
		setTexture(ForgeDirection.UP, Icons.ends);
		setTexture(ForgeDirection.DOWN, Icons.ends);
		setTexture(ForgeDirection.EAST, Icons.side);
		setTexture(ForgeDirection.WEST, Icons.side);
		setTexture(ForgeDirection.NORTH, Icons.side);
		setTexture(ForgeDirection.SOUTH, Icons.side);
		setDefaultTexture(Icons.ends);
	}

	@Override
	public boolean canBeReplacedByLeaves(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float what, float are, float you) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity == null || !(tileEntity instanceof TileEntityGuide)) { return false; }
		if (!world.isRemote) {
			if (player.isSneaking()) {
				((TileEntityGuide)tileEntity).switchMode(player);
			} else {
				if (player.capabilities.isCreativeMode
						&& world.getBlockId(x, y + 1, z) == Block.obsidian.blockID) {
					((TileEntityGuide)tileEntity).fill(player);
				} else {
					((TileEntityGuide)tileEntity).changeDimensions(player, ForgeDirection.getOrientation(side));
				}
			}
		}
		return true;
	}

}
