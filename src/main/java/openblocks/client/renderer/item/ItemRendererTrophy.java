package openblocks.client.renderer.item;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import openblocks.OpenBlocks;
import openblocks.client.renderer.tileentity.TileEntityTrophyRenderer;
import openblocks.common.TrophyHandler.Trophy;
import openblocks.common.item.ItemTrophyBlock;
import openmods.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class ItemRendererTrophy implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (data.length > 0 && data[0] instanceof RenderBlocks) {
			final RenderBlocks renderer = (RenderBlocks)data[0];

			if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) GL11.glTranslated(+0.5, +0.7, +0.5);
			else if (type == ItemRenderType.INVENTORY) GL11.glTranslated(0, -0.1, 0);

			final int meta = OpenBlocks.Blocks.trophy.getInventoryRenderMetadata(0);
			RenderUtils.renderInventoryBlock(renderer, OpenBlocks.Blocks.trophy, meta, 0xFFFFFF);

			Trophy trophy = ItemTrophyBlock.getTrophy(item);
			if (trophy != null) TileEntityTrophyRenderer.renderTrophy(trophy, 0, -0.5, 0, 270);
		}
	}

}
