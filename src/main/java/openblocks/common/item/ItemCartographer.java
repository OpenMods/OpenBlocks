package openblocks.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.entity.EntityAssistant;
import openblocks.common.entity.EntityCartographer;

public class ItemCartographer extends Item {

	// just in case
	public enum AssistantType {
		CARTOGRAPHER("cartographer", "cartographer") {
			@Override
			public EntityAssistant createAssistant(World world, EntityPlayer owner, ItemStack stack) {
				return new EntityCartographer(world, owner, stack);
			}
		};

		public final String untranslatedName;

		private AssistantType(String name, String iconName) {
			this.untranslatedName = "openblocks.assistant_" + name;
		}

		public abstract EntityAssistant createAssistant(World world, EntityPlayer owner, ItemStack stack);

		public final static AssistantType VALUES[] = values();
	}

	public ItemCartographer() {
		setMaxDamage(0);
		setHasSubtypes(true);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> result) {
		for (AssistantType type : AssistantType.VALUES)
			result.add(new ItemStack(this, 1, type.ordinal()));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		if (!player.capabilities.isCreativeMode) stack.shrink(1);

		if (!world.isRemote) {
			AssistantType type = getTypeFromItem(stack);
			if (type != null) {
				EntityAssistant cartographer = type.createAssistant(world, player, stack);
				world.spawnEntity(cartographer);
			}
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	public static AssistantType getTypeFromItem(ItemStack stack) {
		int typeId = stack.getItemDamage();
		if (typeId < 0 || typeId > AssistantType.VALUES.length) typeId = 0;

		return AssistantType.VALUES[typeId];
	}

	public ItemStack createStack(AssistantType type) {
		return new ItemStack(this, 1, type.ordinal());
	}
}
