package openblocks.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.entity.EntityAssistant;
import openblocks.common.entity.EntityCartographer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
		public final String iconName;
		private Icon icon;

		private AssistantType(String name, String iconName) {
			this.untranslatedName = "openblocks.assistant_" + name;
			this.iconName = "openblocks:assistant_" + iconName;
		}

		public abstract EntityAssistant createAssistant(World world, EntityPlayer owner, ItemStack stack);

		public final static AssistantType VALUES[] = values();
	}

	public ItemCartographer() {
		super(Config.itemCartographerId);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconIndex(ItemStack stack) {
		return getTypeFromItem(stack).icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister register) {
		for (AssistantType type : AssistantType.VALUES)
			type.icon = register.registerIcon(type.iconName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubItems(int id, CreativeTabs tab, List result) {
		for (AssistantType type : AssistantType.VALUES)
			result.add(new ItemStack(id, 1, type.ordinal()));
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode) --stack.stackSize;

		if (!world.isRemote) {
			AssistantType type = getTypeFromItem(stack);
			if (type != null) {
				EntityAssistant cartographer = type.createAssistant(world, player, stack);
				world.spawnEntityInWorld(cartographer);
			}
		}

		return stack;
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
