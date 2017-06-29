package openblocks.common;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openmods.config.properties.ConfigurationChange;

public class CanvasReplaceBlacklist {
	private Set<ResourceLocation> idBlacklist = createBlacklist();

	private static Set<ResourceLocation> createBlacklist() {
		return Sets.newHashSet(Collections2.transform(Arrays.asList(Config.canvasBlacklist), new Function<String, ResourceLocation>() {
			@Override
			@Nullable
			public ResourceLocation apply(@Nullable String input) {
				return new ResourceLocation(input);
			}
		}));
	}

	public static final CanvasReplaceBlacklist instance = new CanvasReplaceBlacklist();

	public boolean isAllowedToReplace(IBlockState state) {
		final Block block = state.getBlock();
		if (block.hasTileEntity(state)) return false;

		if (filterVanillaBlocks(block)) return false;

		return !idBlacklist.contains(block.getRegistryName());
	}

	private static boolean filterVanillaBlocks(Block block) {
		// two-part blocks do not work nice with canvas
		return block instanceof BlockDoor ||
				block instanceof BlockBed;
	}

	@SubscribeEvent
	public void onReconfig(ConfigurationChange.Post evt) {
		if (evt.check("canvas", "replaceBlacklist")) idBlacklist = createBlacklist();
	}

	public boolean isAllowedToReplace(World world, BlockPos pos) {
		if (world.isAirBlock(pos)) { return false; }
		return isAllowedToReplace(world.getBlockState(pos));
	}
}
