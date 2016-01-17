package openblocks.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityImaginary.Property;
import openmods.colors.ColorMeta;
import openmods.utils.ItemUtils;

public class ItemImaginationGlasses extends ItemArmor {

	private static final String TAG_COLOR = "Color";

	public static int getGlassesColor(ItemStack stack) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		return tag.getInteger(TAG_COLOR);
	}

	public static class ItemCrayonGlasses extends ItemImaginationGlasses {

		public ItemCrayonGlasses() {
			super(Type.CRAYON);
		}

		@Override
		public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> result) {
			for (ColorMeta color : ColorMeta.getAllColors())
				result.add(createCrayonGlasses(color.rgb));
		}

		@Override
		public int getColor(ItemStack stack) {
			return getGlassesColor(stack);
		}

		public ItemStack createCrayonGlasses(int color) {
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
		public void addInformation(ItemStack stack, EntityPlayer player, List<String> result, boolean extended) {
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

	public ItemImaginationGlasses(Type type) {
		super(ArmorMaterial.GOLD, 1, ARMOR_HELMET);
		this.type = type;
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


		public final String textureName;

		private Type(String name) {
			this.textureName = "openblocks:textures/models/glasses_" + name
					+ ".png";
		}

		protected abstract boolean checkBlock(Property property, ItemStack stack, TileEntityImaginary te);

		public static final Type[] VALUES = values();
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return this.type.textureName;
	}

	public boolean checkBlock(Property property, ItemStack stack, TileEntityImaginary te) {
		return type.checkBlock(property, stack, te);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> result) {
		result.add(new ItemStack(this));
	}
}
