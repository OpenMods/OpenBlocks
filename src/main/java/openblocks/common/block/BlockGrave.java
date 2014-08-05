package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityGrave;

public class BlockGrave extends OpenBlock {

	public BlockGrave() {
		super(Material.ground);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setBlockBounds(0, 0, 0, 1f, 0.2f, 1f);
		setCreativeTab(null);
		setResistance(2000.0F);
	}

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
        TileEntity tile = world.getTileEntity(x,y,z);
        if (tile == null || !(tile instanceof TileEntityGrave)) return;

        TileEntityGrave grave = (TileEntityGrave)tile;
        // prevent grave from being destroyed by same explosion that caused it
        if(grave.killer != null &&  grave.killer == explosion.exploder) {
            // delete killer so the next explosion from it can possibly destroy the grave (e.g. wither)
            grave.setKiller(null);
            return;
        }

        this.breakBlock(world, x,y,z, this,0);
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }

    @Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 0;
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}
}
