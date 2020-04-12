package openblocks.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityImaginary.Property;
import openblocks.common.tileentity.TileEntityImaginaryCrayon;
import openblocks.common.tileentity.TileEntityImaginaryPencil;
import openmods.colors.ColorMeta;
import openmods.utils.ItemUtils;
import openmods.utils.TranslationUtils;

public class ItemImaginationGlasses extends ArmorItem {

	private static final String TAG_COLOR = "Color";

	public static int getGlassesColor(@Nonnull ItemStack stack) {
		CompoundNBT tag = ItemUtils.getItemTag(stack);
		return tag.getInteger(TAG_COLOR);
	}

	@SideOnly(Side.CLIENT)
	public static class CrayonColorHandler implements IItemColor {
		@Override
		public int colorMultiplier(@Nonnull ItemStack stack, int tintIndex) {
			return getGlassesColor(stack);
		}
	}

	public static class ItemCrayonGlasses extends ItemImaginationGlasses {

		private static final String COLORED_OVERLAY = OpenBlocks.location("textures/models/glasses_crayon_overlay.png").toString();

		public ItemCrayonGlasses() {
			super(Type.CRAYON);
		}

		@Override
		public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> result) {
			if (isInCreativeTab(tab)) {
				for (ColorMeta color : ColorMeta.getAllColors())
					result.add(createCrayonGlasses(this, color.rgb));
			}
		}

		@Override
		public int getColor(@Nonnull ItemStack stack) {
			return getGlassesColor(stack);
		}

		@Nonnull
		public static ItemStack createCrayonGlasses(Item item, int color) {
			ItemStack stack = new ItemStack(item);

			CompoundNBT tag = ItemUtils.getItemTag(stack);
			tag.setInteger(TAG_COLOR, color);

			return stack;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> result, ITooltipFlag flag) {
			result.add(TranslationUtils.translateToLocalFormatted("openblocks.misc.color", getColor(stack)));
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
			if ("overlay".equals(type)) return COLORED_OVERLAY;
			return super.getArmorTexture(stack, entity, slot, type);
		}
	}

	public final Type type;

	public ItemImaginationGlasses(Type type) {
		super(ArmorMaterial.GOLD, 1, EquipmentSlotType.HEAD);
		this.type = type;
	}

	public enum Type {
		PENCIL("pencil") {
			@Override
			protected boolean checkBlock(Property property, @Nonnull ItemStack stack, TileEntityImaginary te) {
				return (te instanceof TileEntityImaginaryPencil) ^ te.isInverted();
			}
		},
		CRAYON("crayon") {
			@Override
			protected boolean checkBlock(Property property, @Nonnull ItemStack stack, TileEntityImaginary te) {
				return (te instanceof TileEntityImaginaryCrayon && getGlassesColor(stack) == ((TileEntityImaginaryCrayon)te).getColor()) ^ te.isInverted();
			}
		},
		TECHNICOLOR("technicolor") {
			@Override
			protected boolean checkBlock(Property property, @Nonnull ItemStack stack, TileEntityImaginary te) {
				if (property == Property.VISIBLE) return true;
				return te.isInverted();
			}
		},
		BASTARD("admin") {
			@Override
			protected boolean checkBlock(Property property, @Nonnull ItemStack stack, TileEntityImaginary te) {
				return true;
			}
		};

		public final String textureName;

		Type(String name) {
			this.textureName = OpenBlocks.location("textures/models/glasses_" + name + ".png").toString();
		}

		protected abstract boolean checkBlock(Property property, @Nonnull ItemStack stack, TileEntityImaginary te);

		public static final Type[] VALUES = values();
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return this.type.textureName;
	}

	public boolean checkBlock(Property property, @Nonnull ItemStack stack, TileEntityImaginary te) {
		return type.checkBlock(property, stack, te);
	}
}
