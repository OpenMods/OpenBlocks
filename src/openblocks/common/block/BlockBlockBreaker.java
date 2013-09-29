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
import openblocks.common.tileentity.TileEntityBlockBreaker;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksander
 * Date: 25.09.13
 * Time: 18:06
 * To change this template use File | Settings | File Templates.
 */
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
        return side == metadata ? faceIcon : blockIcon;
    }

//    @Override
//    public void onBlockAdded(World world, int x, int y, int z) {
//        super.onBlockAdded(world, x, y, z);
//        this.setDefaultDirection(world, x, y, z);
//    }
//
//    @Override
//    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placedBy, ItemStack stack) {
//        int side = MathHelper.floor_double((double) (placedBy.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
//        switch(side) {
//            case 0:
//                world.setBlockMetadataWithNotify(x, y, z, 3, 2);
//                break;
//
//            case 1:
//                world.setBlockMetadataWithNotify(x, y, z, 4, 2);
//                break;
//
//            case 2:
//                world.setBlockMetadataWithNotify(x, y, z, 2, 2);
//                break;
//
//            case 3:
//                world.setBlockMetadataWithNotify(x, y, z, 5, 2);
//                break;
//        }
//    }
//
//    private void setDefaultDirection(World world, int x, int y, int z) {
//        if(!world.isRemote) {
//            int n1 = world.getBlockId(x, y, z - 1);
//            int n2 = world.getBlockId(x, y, z + 1);
//            int n3 = world.getBlockId(x - 1, y, z);
//            int n4 = world.getBlockId(x + 1, y, z);
//
//            byte direction = 3;
//
//            if(Block.opaqueCubeLookup[n1] && !Block.opaqueCubeLookup[n2]) {
//                direction = 3;
//            }
//
//            if(Block.opaqueCubeLookup[n2] && !Block.opaqueCubeLookup[n1]) {
//                direction = 2;
//            }
//
//            if(Block.opaqueCubeLookup[n3] && !Block.opaqueCubeLookup[n4]) {
//                direction = 5;
//            }
//
//            if(Block.opaqueCubeLookup[n4] && !Block.opaqueCubeLookup[n3]) {
//                direction = 4;
//            }
//
//            world.setBlockMetadataWithNotify(x, y, z, direction, 2);
//        }
//    }

}
