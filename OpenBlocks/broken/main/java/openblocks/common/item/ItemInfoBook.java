package openblocks.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import openblocks.client.gui.GuiInfoBook;

public class ItemInfoBook extends Item {

	public ItemInfoBook() {}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		final ItemStack heldItem = player.getHeldItem(hand);
		if (hand != Hand.MAIN_HAND) return ActionResult.newResult(ActionResultType.PASS, heldItem);

		if (world.isRemote) FMLCommonHandler.instance().showGuiScreen(new GuiInfoBook());
		return ActionResult.newResult(ActionResultType.SUCCESS, heldItem);
	}
}
