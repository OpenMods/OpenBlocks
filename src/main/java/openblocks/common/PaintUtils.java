package openblocks.common;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PaintUtils {
	// TODO expand blacklist
	private final Set<ResourceLocation> blacklist = Sets.newHashSet();

	public static final PaintUtils instance = new PaintUtils();

	public boolean isAllowedToReplace(IBlockState state) {
		if (state.canProvidePower()) return false;
		final Block block = state.getBlock();
		if (block.hasTileEntity(state)) return false;
		return !blacklist.contains(block.getRegistryName());
	}

	public boolean isAllowedToReplace(World world, BlockPos pos) {
		if (world.isAirBlock(pos)) { return false; }
		return isAllowedToReplace(world.getBlockState(pos));
	}
}
