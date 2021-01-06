package openblocks.api;

import net.minecraft.item.DyeColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// TODO 1.16 Is Forge even going to reimplement that?
public interface IPaintableBlock {
	boolean recolorBlock(World world, BlockPos pos, Direction side, DyeColor colour);
}
