package openblocks.common.block;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemOpenBlock;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLadder extends BlockTrapDoor {

	public static boolean disableValidation = false;

	public BlockLadder() {
		super(Config.blockLadderId, Material.wood);
		setHardness(3.0F);
		setStepSound(soundWoodFootstep);
		String modKey = OpenBlocks.getModId().toLowerCase();
		GameRegistry.registerBlock(this,ItemOpenBlock.class,modKey + "_ladder");
		setUnlocalizedName(modKey + ".ladder");
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		this.setBlockBounds(0f, 0f, 0f, 1.5f, 1f, 1.5f);
	}

	@Override
	public void registerIcons(IconRegister registry) {
		blockIcon = registry.registerIcon("openblocks:ladder");
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		this.setBlockBoundsForBlockRender(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
	}

	@Override
	public void setBlockBoundsForBlockRender(int par1) {

		float f = 0.125F;

		if ((par1 & 8) != 0) {
			this.setBlockBounds(0.0F, 1.0F - f, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
		}

		if (isTrapdoorOpen(par1)) {
			if ((par1 & 3) == 0) {
				this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
			}

			if ((par1 & 3) == 1) {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
			}

			if ((par1 & 3) == 2) {
				this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}

			if ((par1 & 3) == 3) {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
			}
		}
	}

	@Override
	public boolean isLadder(World world, int x, int y, int z, EntityLivingBase entity) {
		return true;
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
