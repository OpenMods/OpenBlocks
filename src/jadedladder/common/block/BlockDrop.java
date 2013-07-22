package jadedladder.common.block;

import java.util.List;

import jadedladder.JadedLadder;
import jadedladder.common.tileentity.TileEntityDropBlock;
import jadedladder.common.tileentity.TileEntityGuide;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockDrop extends BlockContainer {

	public BlockDrop() {
		super(JadedLadder.Config.blockDropId, Material.ground);
		setHardness(3.0F);
		GameRegistry.registerBlock(this, "openblocks_dropblock");
		GameRegistry.registerTileEntity(TileEntityDropBlock.class, "openblocks_dropblock");
		LanguageRegistry.instance().addStringLocalization("tile.jadedladder.dropblock.name", "Drop Block");
		setUnlocalizedName("jadedladder.dropblock");
		setCreativeTab(CreativeTabs.tabMisc);
	}
	
	public void registerIcons(IconRegister registry) {
		this.blockIcon = registry.registerIcon("jadedladder:drop");
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityDropBlock();
	}

}
