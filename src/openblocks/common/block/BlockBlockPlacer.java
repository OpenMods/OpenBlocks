package openblocks.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityBlockPlacer;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksander
 * Date: 28.09.13
 * Time: 22:16
 * To change this template use File | Settings | File Templates.
 */
public class BlockBlockPlacer extends OpenBlock {
    @SideOnly(Side.CLIENT)
    private Icon faceIcon;

    public BlockBlockPlacer() {
        super(Config.blockPlacerId, Material.rock);
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
        if(side < 2) return blockIcon;
        return side == metadata ? faceIcon : blockIcon;
    }
}
