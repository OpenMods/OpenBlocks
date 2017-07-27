package openblocks.common.item;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockImaginary;
import openblocks.common.tileentity.TileEntityImaginary;
import openmods.colors.ColorMeta;
import openmods.config.game.ICustomItemModelProvider;
import openmods.item.ItemOpenBlock;
import openmods.utils.ItemUtils;
import openmods.utils.TranslationUtils;

public class ItemImaginary extends ItemOpenBlock {

	@SideOnly(Side.CLIENT)
	public static class CrayonColorHandler implements IItemColor {
		@Override
		public int getColorFromItemstack(ItemStack stack, int tintIndex) {
			if (tintIndex == 1) {
				if (isCrayon(stack)) { return ItemUtils.getItemTag(stack).getInteger(TAG_COLOR); }
			}

			return 0xFFFFFFFF;
		}
	}

	public static class ModelProvider implements ICustomItemModelProvider {
		@Override
		public void addCustomItemModels(Item item, ResourceLocation itemId, IModelRegistrationSink modelsOut) {
			final ResourceLocation location = OpenBlocks.location("imaginary");
			ModelLoader.setCustomModelResourceLocation(item, DAMAGE_CRAYON, new ModelResourceLocation(location, "inventory_crayon"));
			ModelLoader.setCustomModelResourceLocation(item, DAMAGE_PENCIL, new ModelResourceLocation(location, "inventory_pencil"));
		}
	}

	public static final float CRAFTING_COST = 1.0f;
	public static final String TAG_COLOR = "Color";
	public static final String TAG_USES = "Uses";
	public static final String TAG_MODE = "Mode";

	public static final int DAMAGE_PENCIL = 0;
	public static final int DAMAGE_CRAYON = 1;

	public enum PlacementMode {
		BLOCK(1.0f, "block", false, BlockImaginary.Shape.BLOCK),
		PANEL(0.5f, "panel", false, BlockImaginary.Shape.PANEL),
		HALF_PANEL(0.5f, "half_panel", false, BlockImaginary.Shape.HALF_PANEL),
		STAIRS(0.75f, "stairs", false, BlockImaginary.Shape.STAIRS),

		INV_BLOCK(1.5f, "inverted_block", true, BlockImaginary.Shape.BLOCK),
		INV_PANEL(1.0f, "inverted_panel", true, BlockImaginary.Shape.PANEL),
		INV_HALF_PANEL(1.0f, "inverted_half_panel", true, BlockImaginary.Shape.HALF_PANEL),
		INV_STAIRS(1.25f, "inverted_stairs", true, BlockImaginary.Shape.STAIRS);

		public final float cost;
		public final String name;
		public final boolean isInverted;
		public final BlockImaginary.Shape shape;

		private PlacementMode(float cost, String name, boolean isInverted, BlockImaginary.Shape shape) {
			this.cost = cost;
			this.name = "openblocks.misc.mode." + name;
			this.isInverted = isInverted;
			this.shape = shape;
		}

		public static final PlacementMode[] VALUES = values();
	}

	private static final Table<BlockImaginary.Shape, Boolean, PlacementMode> shapeToMode;

	static {
		ImmutableTable.Builder<BlockImaginary.Shape, Boolean, PlacementMode> shapeToModeBuilder = ImmutableTable.builder();
		for (PlacementMode mode : PlacementMode.VALUES)
			shapeToModeBuilder.put(mode.shape, mode.isInverted, mode);

		shapeToMode = shapeToModeBuilder.build();
	}

	public static float getUses(NBTTagCompound tag) {
		NBTBase value = tag.getTag(TAG_USES);
		if (value == null) return 0;
		if (value instanceof NBTPrimitive) return ((NBTPrimitive)value).getFloat();

		throw new IllegalStateException("Invalid tag type: " + value);
	}

	public static float getUses(ItemStack stack) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		return getUses(tag);
	}

	public static PlacementMode getMode(NBTTagCompound tag) {
		int value = tag.getByte(TAG_MODE);
		return PlacementMode.VALUES[value];
	}

	public static PlacementMode getMode(ItemStack stack) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		return getMode(tag);
	}

	public static Integer getColor(NBTTagCompound tag) {
		if (!tag.hasKey(TAG_COLOR, Constants.NBT.TAG_ANY_NUMERIC)) return null;
		return tag.getInteger(TAG_COLOR);
	}

	public static Integer getColor(ItemStack stack) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		return getColor(tag);
	}

	public static boolean isCrayon(ItemStack stack) {
		return stack.getItemDamage() == DAMAGE_CRAYON;
	}

	public ItemImaginary(Block block) {
		super(block);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setMaxDamage(0);

		addPropertyOverride(new ResourceLocation("mode"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				NBTTagCompound tag = ItemUtils.getItemTag(stack);
				return tag.getByte(TAG_MODE);
			}
		});
	}

	public static ItemStack setupValues(ItemStack result, Integer color, BlockImaginary.Shape shape, boolean isInverted) {
		return setupValues(result, color, shape, isInverted, Config.imaginaryItemUseCount);
	}

	public static ItemStack setupValues(ItemStack result, Integer color, BlockImaginary.Shape shape, boolean isInverted, float uses) {
		return setupValues(result, color, Objects.firstNonNull(shapeToMode.get(shape, isInverted), PlacementMode.BLOCK), uses);
	}

	public static ItemStack setupValues(ItemStack result, Integer color) {
		return setupValues(result, color, PlacementMode.BLOCK);
	}

	public static ItemStack setupValues(ItemStack result, Integer color, PlacementMode mode) {
		return setupValues(result, color, mode, Config.imaginaryItemUseCount);
	}

	public static ItemStack setupValues(ItemStack result, Integer color, PlacementMode mode, float uses) {
		NBTTagCompound tag = ItemUtils.getItemTag(result);

		if (color != null) {
			tag.setInteger(TAG_COLOR, color);
			result.setItemDamage(DAMAGE_CRAYON);
		}

		tag.setInteger(TAG_MODE, mode.ordinal());
		tag.setFloat(TAG_USES, uses);
		return result;
	}

	@Override
	protected void afterBlockPlaced(ItemStack stack, EntityPlayer player, World world, BlockPos pos) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);

		NBTTagInt color = (NBTTagInt)tag.getTag(TAG_COLOR);
		PlacementMode mode = getMode(tag);
		world.setTileEntity(pos, new TileEntityImaginary(color == null? null : color.getInt(), mode.isInverted, mode.shape));

		if (!player.capabilities.isCreativeMode) {
			float uses = Math.max(getUses(tag) - mode.cost, 0);
			tag.setFloat(TAG_USES, uses);

			if (uses <= 0) stack.stackSize = 0;
		}
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		float uses = getUses(tag);
		if (uses <= 0) {
			stack.stackSize = 0;
			return EnumActionResult.FAIL;
		}

		if (uses < getMode(tag).cost) return EnumActionResult.FAIL;

		return super.onItemUse(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		return tag.hasKey(TAG_COLOR)? "item.openblocks.crayon" : "item.openblocks.pencil";
	}

	@Override
	public String getUnlocalizedName() {
		return "item.openblocks.imaginary";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> result, boolean extended) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);

		result.add(TranslationUtils.translateToLocalFormatted("openblocks.misc.uses", getUses(tag)));

		NBTTagInt color = (NBTTagInt)tag.getTag(TAG_COLOR);
		if (color != null) result.add(TranslationUtils.translateToLocalFormatted("openblocks.misc.color", color.getInt()));

		PlacementMode mode = getMode(tag);
		String translatedMode = TranslationUtils.translateToLocal(mode.name);
		result.add(TranslationUtils.translateToLocalFormatted("openblocks.misc.mode", translatedMode));
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> result) {
		result.add(setupValues(new ItemStack(this, 1, DAMAGE_PENCIL), null, PlacementMode.BLOCK));
		for (ColorMeta color : ColorMeta.getAllColors())
			result.add(setupValues(new ItemStack(this, 1, DAMAGE_CRAYON), color.rgb, PlacementMode.BLOCK));
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		float uses = getUses(tag) - CRAFTING_COST;
		if (uses <= 0) return null;

		ItemStack copy = stack.copy();
		NBTTagCompound copyTag = ItemUtils.getItemTag(copy);
		copyTag.setFloat(TAG_USES, uses);
		return copy;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		if (getUses(tag) <= 0) {
			stack.stackSize = 0;
		} else if (player.isSneaking()) {
			byte modeId = tag.getByte(TAG_MODE);
			modeId = (byte)((modeId + 1) % PlacementMode.VALUES.length);
			tag.setByte(TAG_MODE, modeId);

			if (world.isRemote) {
				PlacementMode mode = PlacementMode.VALUES[modeId];
				TextComponentTranslation modeName = new TextComponentTranslation(mode.name);
				player.addChatComponentMessage(new TextComponentTranslation("openblocks.misc.mode", modeName));
			}
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
}
