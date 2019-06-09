package openblocks.client;

import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockCanvas;
import openblocks.common.block.BlockElevator;
import openblocks.common.block.BlockElevatorRotating;
import openblocks.common.block.BlockFlag;
import openblocks.common.block.BlockPaintCan;
import openblocks.common.block.BlockPaintMixer;
import openblocks.common.item.ItemDevNull;
import openblocks.common.item.ItemGlyph;
import openblocks.common.item.ItemImaginaryCrayon;
import openblocks.common.item.ItemImaginationGlasses;
import openblocks.common.item.ItemPaintBrush;
import openblocks.common.item.ItemPaintCan;
import openblocks.common.item.ItemTankBlock;
import openmods.colors.BlockFixedColorHandler;
import openmods.colors.ColorMeta;
import openmods.colors.ItemFixedColorHandler;

@EventBusSubscriber(Side.CLIENT)
public class BlockColorHandlerRegistration {

	@SubscribeEvent
	public static void registerItemColorHandlers(ColorHandlerEvent.Item evt) {
		final ItemColors itemColors = evt.getItemColors();
		final BlockColors blockColors = evt.getBlockColors();

		if (OpenBlocks.Items.paintBrush != null) {
			itemColors.registerItemColorHandler(new ItemPaintBrush.ColorHandler(), OpenBlocks.Items.paintBrush);
		}

		if (OpenBlocks.Items.crayonGlasses != null) {
			itemColors.registerItemColorHandler(new ItemImaginationGlasses.CrayonColorHandler(), OpenBlocks.Items.crayonGlasses);
		}

		if (OpenBlocks.Blocks.paintCan != null) {
			itemColors.registerItemColorHandler(new ItemPaintCan.ItemColorHandler(), OpenBlocks.Blocks.paintCan);
			blockColors.registerBlockColorHandler(new BlockPaintCan.BlockColorHandler(), OpenBlocks.Blocks.paintCan);
		}

		if (OpenBlocks.Blocks.imaginaryCrayon != null) {
			itemColors.registerItemColorHandler(new ItemImaginaryCrayon.ColorHandler(), OpenBlocks.Blocks.imaginaryCrayon);
		}

		if (OpenBlocks.Items.devNull != null) {
			itemColors.registerItemColorHandler(new ItemDevNull.NestedItemColorHandler(itemColors), OpenBlocks.Items.devNull);
		}

		registerFixedColorHandlers(BlockElevator::colorToBlock, itemColors, blockColors);
		registerFixedColorHandlers(BlockElevatorRotating::colorToBlock, itemColors, blockColors);
		registerFixedColorHandlers(BlockFlag::colorToBlock, itemColors, blockColors);

		if (OpenBlocks.Items.glyph != null) {
			itemColors.registerItemColorHandler(new ItemGlyph.ColorHandler(), OpenBlocks.Items.glyph);
		}

		if (OpenBlocks.Blocks.tank != null) {
			itemColors.registerItemColorHandler(new ItemTankBlock.ColorHandler(), OpenBlocks.Blocks.tank);
		}
	}

	private static void registerFixedColorHandlers(final Function<ColorMeta, Block> blockSupplier, final ItemColors itemColors, final BlockColors blockColors) {
		ColorMeta.getAllColors().forEach(color -> {
			final Block block = blockSupplier.apply(color);
			if (block != null) {
				blockColors.registerBlockColorHandler(new BlockFixedColorHandler(color), block);
				itemColors.registerItemColorHandler(new ItemFixedColorHandler(color), block);
			}
		});
	}

	@SubscribeEvent
	public static void registerBlockHandler(ColorHandlerEvent.Block evt) {
		final BlockColors blockColors = evt.getBlockColors();

		if (OpenBlocks.Blocks.canvas != null) {
			blockColors.registerBlockColorHandler(new BlockCanvas.InnerBlockColorHandler(blockColors), OpenBlocks.Blocks.canvas);
		}

		if (OpenBlocks.Blocks.paintMixer != null) {
			blockColors.registerBlockColorHandler(new BlockPaintMixer.BlockColorHandler(), OpenBlocks.Blocks.paintMixer);
		}
	}

}
