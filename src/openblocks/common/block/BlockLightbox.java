package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.utils.BlockUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockLightbox extends BlockContainer {

	public BlockLightbox() {
		super(OpenBlocks.Config.blockLightboxId, Material.ground);
		setHardness(3.0F);
		GameRegistry.registerBlock(this, "openblocks_lightbox");
		GameRegistry.registerTileEntity(TileEntityLightbox.class,
				"openblocks_lightbox");

		LanguageRegistry.instance().addStringLocalization(
				"tile.openblocks.lightbox.name", "Lightbox");
		setUnlocalizedName("openblocks.lightbox");
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityLightbox();
	}

	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving entity, ItemStack itemstack) {

	      TileEntity tile = world.getBlockTileEntity(x, y, z);
	      if (tile != null && tile instanceof TileEntityLightbox) {
	    	  TileEntityLightbox lightbox = (TileEntityLightbox) tile;
	    	  lightbox.setSurface(BlockUtils.get3dOrientation(entity));
	    	  lightbox.setRotation(BlockUtils.get2dOrientation(entity));
	      }
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
}
