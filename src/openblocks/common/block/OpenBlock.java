package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.utils.BlockUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public abstract class OpenBlock extends BlockContainer {

	private String uniqueBlockId;
	private Class<? extends TileEntity> teClass = null;

	protected OpenBlock(int id, Material material) {
		super(id, material);
		setCreativeTab(CreativeTabs.tabMisc);
		setHardness(3.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		try {
			if (teClass != null) {
				return teClass.getConstructor(new Class[0]).newInstance();
			}
		} catch (NoSuchMethodException nsm) {
			System.out
					.println("Notice: Cannot create TE automatically due to constructor requirements");
		} catch (Exception ex) {
			System.out.println("Notice: Error creating tile entity");
			ex.printStackTrace();
		}
		return null;
	}

	public void registerIcons(IconRegister registry) {
		this.blockIcon = registry.registerIcon(OpenBlocks.proxy.getModId().toLowerCase()
				+ ":" + uniqueBlockId);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		BlockUtils.dropTileInventory(tile);
		super.breakBlock(world, x, y, z, par5, par6);
	}

	public void setupBlock(Block instance, String uniqueName,
			String friendlyName) {
		setupBlock(instance, uniqueName, friendlyName, null);
	}

	public void setupBlock(Block instance, String uniqueName,
			String friendlyName, Class<? extends TileEntity> tileEntity) {
		uniqueBlockId = uniqueName;
		GameRegistry.registerBlock(instance, OpenBlocks.proxy.getModId() + "_"
				+ uniqueName);
		LanguageRegistry.instance().addStringLocalization(
				"tile." + OpenBlocks.proxy.getModId().toLowerCase() + "." + uniqueName + ".name", friendlyName);
		instance.setUnlocalizedName(OpenBlocks.proxy.getModId().toLowerCase() + "." + uniqueName);
		if (tileEntity != null) {
			GameRegistry.registerTileEntity(tileEntity, OpenBlocks.proxy.getModId().toLowerCase() + "_" + uniqueName);
			this.teClass = tileEntity;
		}
	}
	


	protected void setupDimensionsFromCenter(float x, float y, float z, float width, float height, float depth) {
		setupDimensions(x - width, y, z - depth, x + width, y + height, z + depth);
	}
	
	protected void setupDimensions(float minX, float minY, float minZ, float maxX, float maxY, float maxZ){
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;		
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

}
