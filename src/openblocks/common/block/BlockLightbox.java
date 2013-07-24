package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityHealBlock;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.utils.BlockUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockLightbox extends OpenBlock {

	public BlockLightbox() {
		super(OpenBlocks.Config.blockLightboxId, Material.glass);
		setHardness(3.0F);
		setupBlock(this, "lightbox", "Lightbox", TileEntityLightbox.class);
		setCreativeTab(CreativeTabs.tabMisc);
		setLightValue(1.0f);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) * 15;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z);
		world.setBlockMetadataWithNotify(x, y, z, powered ? 1 : 0, 3);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int par5) {
		boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z);
		world.setBlockMetadataWithNotify(x, y, z, powered ? 1 : 0, 3);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving entity, ItemStack itemstack) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntityLightbox) {
			TileEntityLightbox lightbox = (TileEntityLightbox) tile;
			lightbox.setSurfaceAndRotation(BlockUtils.get3dOrientation(entity),
					BlockUtils.get2dOrientation(entity));
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (!world.isRemote) {
			player.openGui(OpenBlocks.instance,
					OpenBlocks.Gui.Lightbox.ordinal(), world, x, y, z);
		}
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (player.isSneaking() || tileEntity == null) {
			return false;
		}
		return true;
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

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x,
			int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x,
			int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y,
			int z) {

		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile == null || !(tile instanceof TileEntityLightbox)) {
			return;
		}

		TileEntityLightbox lightbox = (TileEntityLightbox) tile;

		ForgeDirection direction = lightbox.getSurface();

		switch (direction) {
		case EAST:
			setBlockBounds(0.8f, 0, 0, 1f, 1f, 1f);
			break;
		case WEST:
			setBlockBounds(0, 0, 0, 0.2f, 1f, 1f);
			break;
		case NORTH:
			setBlockBounds(0, 0, 0, 1f, 1f, 0.2f);
			break;
		case SOUTH:
			setBlockBounds(0, 0, 0.8f, 1f, 1f, 1f);
			break;
		case UP:
			setBlockBounds(0, 0.8f, 0, 1f, 1f, 1f);
			break;
		case DOWN:
			setBlockBounds(0, 0, 0, 1f, 0.2f, 1f);
			break;
		default:
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
