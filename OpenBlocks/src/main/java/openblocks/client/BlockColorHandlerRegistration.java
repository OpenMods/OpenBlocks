package openblocks.client;

import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockElevator;
import openblocks.common.block.BlockRotatingElevator;
import openblocks.common.item.ItemTankBlock;
import openmods.colors.BlockFixedColorHandler;
import openmods.colors.ColorMeta;
import openmods.colors.ItemFixedColorHandler;

public class BlockColorHandlerRegistration {
	public static void registerItemColorHandlers(ColorHandlerEvent.Item evt) {
		final ItemColors itemColors = evt.getItemColors();
		final BlockColors blockColors = evt.getBlockColors();

		registerFixedColorHandlers(BlockElevator::colorToBlock, itemColors, blockColors);
		registerFixedColorHandlers(BlockRotatingElevator::colorToBlock, itemColors, blockColors);

		itemColors.register(new ItemTankBlock.ColorHandler(), OpenBlocks.Blocks.tank);
	}

	private static void registerFixedColorHandlers(final Function<ColorMeta, Block> blockSupplier, final ItemColors itemColors, final BlockColors blockColors) {
		ColorMeta.getAllColors().forEach(color -> {
			final Block block = blockSupplier.apply(color);
			if (block != null) {
				blockColors.register(new BlockFixedColorHandler(color), block);
				itemColors.register(new ItemFixedColorHandler(color), block);
			}
		});
	}
}
