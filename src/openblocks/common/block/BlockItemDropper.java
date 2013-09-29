package openblocks.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityBlockPlacer;
import openblocks.common.tileentity.TileEntityItemDropper;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksander
 * Date: 28.09.13
 * Time: 22:16
 * To change this template use File | Settings | File Templates.
 */
public class BlockItemDropper extends OpenBlock {
    @SideOnly(Side.CLIENT)
    private Icon downIcon;

    public BlockItemDropper() {
        super(Config.blockItemDropperId, Material.rock);
        setupBlock(this, "itemDropper", TileEntityItemDropper.class);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister registry) {
        super.registerIcons(registry);
        this.downIcon = registry.registerIcon(String.format("%s:%s", modKey, "itemDropper_down"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int metadata) {
        switch(side) {
            case 0: return downIcon;
            default: return blockIcon;
        }
    }
}
