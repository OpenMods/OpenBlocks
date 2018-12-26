package openblocks.common.block;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockLadder extends BlockTrapDoor {

	public BlockLadder() {
		super(Material.WOOD);
		setHardness(3.0F);
		setSoundType(SoundType.WOOD);
	}

	// NOTE vanilla's ladder provides similar capability, but only when bottom block is actual ladder, so this is still useful
	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return world.getBlockState(pos).getValue(BlockTrapDoor.OPEN);
	}

}
