package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.tileentity.TileEntityFlag;
import openmods.block.BlockRotationMode;
import openmods.utils.ColorUtils.RGB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFlag extends OpenBlock {

	public static final RGB[] COLORS = {
			new RGB(20, 198, 0),
			new RGB(41, 50, 156),
			new RGB(221, 0, 0),
			new RGB(255, 174, 201),
			new RGB(185, 122, 87),
			new RGB(181, 230, 29),
			new RGB(0, 162, 232),
			new RGB(128, 0, 64),
			new RGB(255, 242, 0),
			new RGB(255, 127, 39),
			new RGB(255, 45, 45),
			new RGB(255, 23, 151),
			new RGB(195, 195, 195),
			new RGB(163, 73, 164),
			new RGB(0, 0, 0),
			new RGB(255, 255, 255)
	};

	public BlockFlag() {
		super(Material.circuits);
		setupDimensionsFromCenter(0.5f, 0f, 0.5f, 1 / 16f, 1f, 1 / 16f);
		setRotationMode(BlockRotationMode.SIX_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.SURFACE);
		setInventoryRenderRotation(ForgeDirection.DOWN);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int side) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		TileEntityFlag flag = getTileEntity(world, x, y, z, TileEntityFlag.class);
		if (flag != null) {
			ForgeDirection onSurface = flag.getSurfaceDirection();
			if (onSurface == ForgeDirection.DOWN) {
				setupDimensionsFromCenter(0.5f, 0f, 0.5f, 1 / 16f, 1f, 1 / 16f);
			} else if (onSurface == ForgeDirection.EAST || onSurface == ForgeDirection.WEST) {
				setupDimensionsFromCenter(0.5f, 0f, 0.5f, 5 / 16f, 1f, 1 / 16f);
			} else {
				setupDimensionsFromCenter(0.5f, 0f, 0.5f, 1 / 16f, 1f, 5 / 16f);
			}
		}
	}

	@Override
	public boolean canPlaceBlock(World world, EntityPlayer player, ItemStack stack, int x, int y, int z, ForgeDirection sideDir, ForgeDirection blockDirection, float hitX, float hitY, float hitZ, int newMeta) {
		if (blockDirection == ForgeDirection.UP) return false;
		if (blockDirection == ForgeDirection.DOWN) {
			Block belowBlock = world.getBlock(x, y - 1, z);
			if (belowBlock != null) {
				if (belowBlock == Blocks.fence) return true;
				if (belowBlock == this) {
					TileEntityFlag flag = getTileEntity(world, x, y - 1, z, TileEntityFlag.class);
					if (flag != null && flag.getSurfaceDirection().equals(ForgeDirection.DOWN)) return true;
				}
			}
		}

		return isNeighborBlockSolid(world, x, y, z, blockDirection);
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		blockIcon = registry.registerIcon("planks_oak");
	}
}
