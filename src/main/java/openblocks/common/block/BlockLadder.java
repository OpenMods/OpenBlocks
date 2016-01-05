package openblocks.common.block;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockLadder extends BlockTrapDoor {

	public static boolean disableValidation = false;

	public BlockLadder() {
		super(Material.wood);
		setHardness(3.0F);
		setStepSound(soundTypeWood);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setBlockBounds(0f, 0f, 0f, 1.5f, 1f, 1.5f);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	@Override
	public void func_150117_b(int metadata) {
		float f = 0.125f;
		final boolean isOpen = (metadata & 4) != 0;
		if (isOpen) {
			switch (metadata & 3) {
				case 0:
					setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
					break;

				case 1:
					setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
					break;

				case 2:
					setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
					break;

				case 3:
				default:
					setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
					break;
			}
		} else {
			boolean isOnTop = (metadata & 8) != 0;
			if (isOnTop) setBlockBounds(0.0F, 1.0F - f, 0.0F, 1.0F, 1.0F, 1.0F);
			else setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
		}
	}

	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
		int metadata = world.getBlockMetadata(x, y, z);
		return (metadata & 4) != 0;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 0;
	}
}
