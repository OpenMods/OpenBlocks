package openblocks.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.Config;
import openmods.infobook.BookDocumentation;

import java.util.Random;

@BookDocumentation
public class BlockScaffolding extends OpenBlock {
    public BlockScaffolding() {
        super(Material.wood);
        setTickRandomly(true);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (random.nextInt(Config.scaffoldingDespawnRate) != 0)
            return;
        dropBlockAsItem(world, x, y, z, 0, 0);
        world.setBlockToAir(x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
        // Code adapted from BlockBreakable
        Block block = blockAccess.getBlock(x, y, z);

        return block != this && super.shouldSideBeRendered(blockAccess, x, y, z, side);
    }
}
