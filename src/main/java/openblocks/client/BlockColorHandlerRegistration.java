package openblocks.client;

import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockCanvas;
import openblocks.common.block.BlockElevator;
import openblocks.common.block.BlockFlag;
import openblocks.common.block.BlockPaintCan;
import openblocks.common.block.BlockPaintMixer;
import openblocks.common.item.ItemDevNull;
import openblocks.common.item.ItemElevator;
import openblocks.common.item.ItemFlagBlock;
import openblocks.common.item.ItemGlyph;
import openblocks.common.item.ItemImaginary;
import openblocks.common.item.ItemImaginationGlasses;
import openblocks.common.item.ItemPaintBrush;
import openblocks.common.item.ItemPaintCan;
import openblocks.common.item.ItemTankBlock;

@EventBusSubscriber(Side.CLIENT)
public class BlockColorHandlerRegistration {

	@SubscribeEvent
	public static void registerItemColormHandlers(ColorHandlerEvent.Item evt) {
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

		if (OpenBlocks.Blocks.imaginary != null) {
			itemColors.registerItemColorHandler(new ItemImaginary.CrayonColorHandler(), OpenBlocks.Blocks.imaginary);
		}

		if (OpenBlocks.Items.devNull != null) {
			itemColors.registerItemColorHandler(new ItemDevNull.NestedItemColorHandler(itemColors), OpenBlocks.Items.devNull);
		}

		if (OpenBlocks.Blocks.elevator != null) {
			blockColors.registerBlockColorHandler(new BlockElevator.BlockColorHandler(), OpenBlocks.Blocks.elevator);
			itemColors.registerItemColorHandler(new ItemElevator.ItemColorHandler(), OpenBlocks.Blocks.elevator);
		}

		if (OpenBlocks.Blocks.elevatorRotating != null) {
			blockColors.registerBlockColorHandler(new BlockElevator.BlockColorHandler(), OpenBlocks.Blocks.elevatorRotating);
			itemColors.registerItemColorHandler(new ItemElevator.ItemColorHandler(), OpenBlocks.Blocks.elevatorRotating);
		}

		if (OpenBlocks.Blocks.flag != null) {
			itemColors.registerItemColorHandler(new ItemFlagBlock.ItemColorHandler(), OpenBlocks.Blocks.flag);
			blockColors.registerBlockColorHandler(new BlockFlag.BlockColorHandler(), OpenBlocks.Blocks.flag);
		}

		if (OpenBlocks.Items.glyph != null) {
			itemColors.registerItemColorHandler(new ItemGlyph.ColorHandler(), OpenBlocks.Items.glyph);
		}

		if (OpenBlocks.Blocks.tank != null) {
			itemColors.registerItemColorHandler(new ItemTankBlock.ColorHandler(), OpenBlocks.Blocks.tank);
		}
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
