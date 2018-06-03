package openblocks.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import openblocks.client.gui.GuiInfoBook;

public class ItemInfoBook extends Item {

	public ItemInfoBook() {}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		final ItemStack heldItem = player.getHeldItem(hand);
		if (hand != EnumHand.MAIN_HAND) return ActionResult.newResult(EnumActionResult.PASS, heldItem);

		if (world.isRemote) FMLCommonHandler.instance().showGuiScreen(new GuiInfoBook());
		return ActionResult.newResult(EnumActionResult.SUCCESS, heldItem);
	}
}
