package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class BlockSignSpinner extends OpenBlock {

	private Icon spinTop;
	
	public BlockSignSpinner() {
		super(OpenBlocks.Config.blockSignSpinner,Material.rock);
		/*setHardness(3.0F);
		setStepSound(soundWoodFootstep);
		GameRegistry.registerBlock(this, "signspinner");
		LanguageRegistry.instance().addStringLocalization("tile.openblocks.signspinner.name", "Sign Spinner");
		setUnlocalizedName("openblocks.signspinner");
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		*/
		setupBlock(this, "signspinner");
	}
	
	@Override
	public void registerIcons(IconRegister registry) {
		blockIcon = registry.registerIcon("openblocks:signspinner");
		spinTop = registry.registerIcon("openblocks:signspinner_top");
	}
	
	@Override
	public Icon getIcon(int par1, int par2) {
		return (par1 == 0 /*bottom*/ || par1 == 1 /*top*/) ? spinTop : blockIcon;
	}
	
	@Override
	public int tickRate(World par1World)
    {
        return OpenBlocks.Config.signTicksPerMovement;
    }
	
	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
		if(par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
			par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World));
	}
	
	@Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
		if(!par1World.isRemote && par1World.isBlockIndirectlyGettingPowered(par2, par3, par4) && par1World.getBlockId(par2, par3 + 1, par4) == Block.signPost.blockID) //TODO: use *block* instanceof BlockSign
			par1World.setBlockMetadataWithNotify(par2, par3 + 1, par4, (par1World.getBlockMetadata(par2, par3 + 1, par4) + 1) % 16, 3);
	}
}
