package openblocks.common.block;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLadder extends BlockTrapDoor {

	public static boolean disableValidation = false;

	public BlockLadder() {
		super(OpenBlocks.Config.blockLadderId, Material.wood);
		setHardness(3.0F);
		setStepSound(soundWoodFootstep);
		GameRegistry.registerBlock(this, "ladder");
		// naughty
		LanguageRegistry.instance().addStringLocalization("tile.openblocks.ladder.name", "Jaded Ladder");
		setUnlocalizedName("openblocks.ladder");
		setCreativeTab(CreativeTabs.tabMisc);
		this.setBlockBounds(0f, 0f, 0f, 1.5f, 1f, 1.5f);
	}

	@Override
	public void registerIcons(IconRegister registry) {
		blockIcon = registry.registerIcon("openblocks:ladder");
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		this.setBlockBoundsForBlockRender(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
	}

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
	public boolean isLadder(World world, int x, int y, int z) {
		return true;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 0;
	}

}
