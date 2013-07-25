package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityHealBlock;
import openblocks.utils.BlockUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
		this.blockIcon = registry.registerIcon(OpenBlocks.proxy.getModId()
				+ ":" + uniqueBlockId);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		BlockUtils.dropInventoryItems(tile);
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
				"tile." + OpenBlocks.proxy.getModId() + "." + uniqueName
						+ ".name", friendlyName);
		instance.setUnlocalizedName(OpenBlocks.proxy.getModId() + "."
				+ uniqueName);
		if (tileEntity != null) {
			GameRegistry.registerTileEntity(tileEntity,
					OpenBlocks.proxy.getModId() + "_" + uniqueName);
			this.teClass = tileEntity;
		}
	}

}
