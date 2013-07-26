package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityGuide;

public class BlockGuide extends OpenBlock {

	public BlockGuide() {
		super(OpenBlocks.Config.blockGuideId, Material.ground);
		setupBlock(this, "guide", "Guide", TileEntityGuide.class);
	}

	protected TileEntityGuide getTileEntity(World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntityGuide) {
			return (TileEntityGuide) tile;
		}
		return null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z,
			ForgeDirection side) {
		return true;
	}

	@Override
	public void registerIcons(IconRegister registry) {
		blockIcon = registry.registerIcon("openblocks:guide");
	}

	@Override
	public boolean canBeReplacedByLeaves(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z,
			int metadata, ForgeDirection face) {
		return false;
	}

	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float what, float are, float you) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity == null || !(tileEntity instanceof TileEntityGuide)) {
			return false;
		}

		if (!world.isRemote) {
			if (player.isSneaking()) {
				((TileEntityGuide) tileEntity).switchMode(player);
			} else {
				((TileEntityGuide) tileEntity).changeDimensions(ForgeDirection
						.getOrientation(side));
			}
		}

		return true;
	}
}
