package jadedladder.common.block;

import java.util.List;

import jadedladder.JadedLadder;
import jadedladder.common.tileentity.TileEntityGuide;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockGuide extends BlockContainer {
	
	public BlockGuide() {
		super(JadedLadder.Config.blockGuideId, Material.ground);
		setHardness(3.0F);
		GameRegistry.registerBlock(this, "openblocks_guide");
		GameRegistry.registerTileEntity(TileEntityGuide.class, "openblocks_guide");
		
		LanguageRegistry.instance().addStringLocalization("tile.jadedladder.guide.name", "Guide");
		setUnlocalizedName("jadedladder.guide");
		setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityGuide();
	}

	protected TileEntityGuide getTileEntity(World world, int x, int y, int z) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntityGuide) {
			return (TileEntityGuide) tile;
		}
		return null;
	}

	public void updateRedstoneState(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		TileEntityGuide tile = getTileEntity(world, x, y, z);
		if (tile != null) {
			tile.updateRedstoneState();
		}
	}

	public void onNeighborBlockChange(World world, int x, int y, int z,
			int blockId) {
		if (!world.isRemote) {
			updateRedstoneState(world, x, y, z);
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
		return JadedLadder.renderId;
	}
	
	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return true;
    }

	@Override
	public void registerIcons(IconRegister registry) {
		blockIcon = registry.registerIcon("jadedladder:guide");
	}
	
	@Override
	public boolean canBeReplacedByLeaves(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return false;
	}
	
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
    	TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (player.isSneaking() || tileEntity == null || !(tileEntity instanceof TileEntityGuide)) {
			return false;
		}
		((TileEntityGuide)tileEntity).switchMode();
		
		return true;
    }
}
