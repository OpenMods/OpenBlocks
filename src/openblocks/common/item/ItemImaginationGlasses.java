package openblocks.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityImaginary.Property;
import openmods.utils.ColorUtils;
import openmods.utils.ItemUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemImaginationGlasses extends ItemArmor {

	private static final String TAG_COLOR = "Color";

	public static int getGlassesColor(ItemStack stack) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		return tag.getInteger(TAG_COLOR);
	}

	public static class ItemCrayonGlasses extends ItemImaginationGlasses {

		public ItemCrayonGlasses(int itemId) {
			super(itemId, Type.CRAYON);
		}

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void getSubItems(int itemId, CreativeTabs tab, List result) {
			for (Integer color : ColorUtils.COLORS.values())
				result.add(createCrayon(color));
		}

		@Override
		public int getColor(ItemStack stack) {
			return getGlassesColor(stack);
		}

		public ItemStack createCrayon(int color) {
			ItemStack stack = new ItemStack(this);

			NBTTagCompound tag = ItemUtils.getItemTag(stack);
			tag.setInteger(TAG_COLOR, color);

			return stack;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public int getColorFromItemStack(ItemStack stack, int pass) {
			return getGlassesColor(stack);
		}

		@Override
		@SideOnly(Side.CLIENT)
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void addInformation(ItemStack stack, EntityPlayer player, List result, boolean extended) {
			result.add(StatCollector.translateToLocalFormatted("openblocks.misc.color", getColor(stack)));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
			if ("overlay".equals(type)) return "openblocks:textures/models/glasses_crayon_overlay.png";

			return super.getArmorTexture(stack, entity, slot, type);
		}
	}

	private static final int ARMOR_HELMET = 0;

	public final Type type;

	public ItemImaginationGlasses(int itemId, Type type) {
		super(itemId, EnumArmorMaterial.GOLD, 1, ARMOR_HELMET);
		this.type = type;
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setUnlocalizedName("openblocks.imagination");
		setHasSubtypes(true);
	}

	public enum Type {
		PENCIL("pencil") {
			@Override
			protected boolean checkBlock(Property property, ItemStack stack, TileEntityImaginary te) {
				return te.isPencil() ^ te.isInverted();
			}
		},
		CRAYON("crayon") {
			@Override
			protected boolean checkBlock(Property property, ItemStack stack, TileEntityImaginary te) {
				return (!te.isPencil() && getGlassesColor(stack) == te.color)
						^ te.isInverted();
			}
		},
		TECHNICOLOR("technicolor") {
			@Override
			protected boolean checkBlock(Property property, ItemStack stack, TileEntityImaginary te) {
				if (property == Property.VISIBLE) return true;
				return te.isInverted();
			}
		},
		BASTARD("admin") {
			@Override
			protected boolean checkBlock(Property property, ItemStack stack, TileEntityImaginary te) {
				return true;
			}
		};

		public final String name;
		public final String iconName;
		public final String textureName;

		private Type(String name) {
			this.name = "item.openblocks.glasses." + name;
			this.iconName = "openblocks:glasses_" + name;
			this.textureName = "openblocks:textures/models/glasses_" + name
					+ ".png";
		}

		protected abstract boolean checkBlock(Property property, ItemStack stack, TileEntityImaginary te);

		public static final Type[] VALUES = values();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		itemIcon = registry.registerIcon(type.iconName);
	}

	@Override
	public Icon getIcon(ItemStack stack, int pass) {
		return itemIcon;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return this.type.textureName;
	}

	public boolean checkBlock(Property property, ItemStack stack, TileEntityImaginary te) {
		return type.checkBlock(property, stack, te);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubItems(int itemId, CreativeTabs tab, List result) {
		result.add(new ItemStack(this));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return type.name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return 1;
	}
}
