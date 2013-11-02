package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityBlockPlacer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBlockPlacer extends OpenBlock {
    @SideOnly(Side.CLIENT)
    private Icon faceIcon;

    public BlockBlockPlacer() {
        super(Config.blockBlockPlacerId, Material.rock);
        setupBlock(this, "blockPlacer", TileEntityBlockPlacer.class);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        super.registerIcons(registry);
        this.faceIcon = registry.registerIcon(String.format("%s:%s", modKey, "blockPlacer_face"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int metadata) {
        return side == metadata ? faceIcon : blockIcon;
    }
}
