package openblocks.common.item;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks;
import openblocks.common.entity.EntityAssistant;
import openblocks.common.entity.EntityCartographer;
import openmods.fixers.NestedItemStackWalker;

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
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> result) {
		if (isInCreativeTab(tab)) {
			for (AssistantType type : AssistantType.VALUES)
				result.add(createStack(this, type));
		}
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

	public static AssistantType getTypeFromItem(@Nonnull ItemStack stack) {
		int typeId = stack.getItemDamage();
		if (typeId < 0 || typeId > AssistantType.VALUES.length) typeId = 0;

		return AssistantType.VALUES[typeId];
	}

	@Nonnull
	public static ItemStack createStack(Item item, AssistantType type) {
		return new ItemStack(item, 1, type.ordinal());
	}

	public static void registerFixes(DataFixer dataFixer) {
		if (OpenBlocks.Items.cartographer != null)
			dataFixer.registerWalker(FixTypes.ITEM_INSTANCE, new NestedItemStackWalker(OpenBlocks.Items.cartographer, EntityCartographer.TAG_MAP_ITEM));
	}
}
