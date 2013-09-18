package openblocks.common.block;

import static net.minecraftforge.common.ForgeDirection.DOWN;
import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.WEST;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemFlagBlock;
import openblocks.common.tileentity.TileEntityFlag;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFlag extends OpenBlock {

	public static final int[] COLORS;

	private static void setColor(int index, int red, int green, int blue) {
		COLORS[index] = red % 256 << 16 | green % 256 << 8 | blue % 256;
	}

	static {
		COLORS = new int[16];
		setColor(0, 20, 198, 0);
		setColor(1, 41, 50, 156);
		setColor(2, 221, 0, 0);
		setColor(3, 255, 174, 201);
		setColor(4, 185, 122, 87);
		setColor(5, 181, 230, 29);
		setColor(6, 0, 162, 232);
		setColor(7, 128, 0, 64);
		setColor(8, 255, 242, 0);
		setColor(9, 255, 127, 39);
		setColor(10, 255, 45, 45);
		setColor(11, 255, 23, 151);
		setColor(12, 195, 195, 195);
		setColor(13, 163, 73, 164);
		setColor(14, 0, 0, 0);
		setColor(15, 255, 255, 255);
	}

	public BlockFlag() {
		super(Config.blockFlagId, Material.ground);
		setupBlock(this, "flag", TileEntityFlag.class, ItemFlagBlock.class);
		setupDimensionsFromCenter(0.5f, 0f, 0.5f, 1 / 16f, 1f, 1 / 16f);
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
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
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
			if (onSurface == DOWN) {
				setupDimensionsFromCenter(0.5f, 0f, 0.5f, 1 / 16f, 1f, 1 / 16f);
			} else if (onSurface == EAST || onSurface == WEST) {
				setupDimensionsFromCenter(0.5f, 0f, 0.5f, 5 / 16f, 1f, 1 / 16f);
			} else {
				setupDimensionsFromCenter(0.5f, 0f, 0.5f, 1 / 16f, 1f, 5 / 16f);
			}
		}
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		if (side == DOWN) {
			int targetX = x + side.offsetX;
			int targetY = y + side.offsetY;
			int targetZ = z + side.offsetZ;
			int belowBlockId = world.getBlockId(targetX, targetY, targetZ);
			Block belowBlock = Block.blocksList[belowBlockId];
			if (belowBlock != null) {
				if (belowBlock == Block.fence) {
					return true;
				} else if (belowBlock == this) {
					TileEntityFlag flag = getTileEntity(world, targetX, targetY, targetZ, TileEntityFlag.class);
					if (flag != null && flag.getSurfaceDirection().equals(DOWN)) { return true; }
				}
			}
		}
		return super.canPlaceBlockOnSide(world, x, y, z, side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2) {
		return Block.planks.getIcon(par1, par2);
	}

}
