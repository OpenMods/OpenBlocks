package openblocks.common.item;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.block.BlockImaginary;
import openmods.item.ItemOpenBlock;
import openmods.utils.ItemUtils;
import openmods.utils.TranslationUtils;

public abstract class ItemImaginary extends ItemOpenBlock {
	public static final float DEFAULT_USE_COUNT = 10.0f;
	public static final float CRAFTING_COST = 1.0f;
	protected static final String TAG_USES = "Uses";
	protected static final String TAG_MODE = "Mode";

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

		PlacementMode(float cost, String name, boolean isInverted, BlockImaginary.Shape shape) {
			this.cost = cost;
			this.name = "openblocks.misc.mode." + name;
			this.isInverted = isInverted;
			this.shape = shape;
		}

		public static final PlacementMode[] VALUES = values();
	}

	protected static final Table<BlockImaginary.Shape, Boolean, PlacementMode> SHAPE_TO_MODE;

	static {
		ImmutableTable.Builder<BlockImaginary.Shape, Boolean, PlacementMode> shapeToModeBuilder = ImmutableTable.builder();
		for (PlacementMode mode : PlacementMode.VALUES)
			shapeToModeBuilder.put(mode.shape, mode.isInverted, mode);

		SHAPE_TO_MODE = shapeToModeBuilder.build();
	}

	public static float getUses(CompoundNBT tag) {
		return tag.getFloat(TAG_USES);
	}

	public static float getUses(@Nonnull ItemStack stack) {
		CompoundNBT tag = ItemUtils.getItemTag(stack);
		return getUses(tag);
	}

	public static PlacementMode getMode(CompoundNBT tag) {
		int value = tag.getByte(TAG_MODE);
		return PlacementMode.VALUES[value];
	}

	public static PlacementMode getMode(@Nonnull ItemStack stack) {
		CompoundNBT tag = ItemUtils.getItemTag(stack);
		return getMode(tag);
	}

	public ItemImaginary(Block block) {
		super(block);
		setMaxStackSize(1);

		addPropertyOverride(new ResourceLocation("mode"), (@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) -> {
			CompoundNBT tag = ItemUtils.getItemTag(stack);
			return tag.getByte(TAG_MODE);
		});
	}

	@Override
	protected void afterBlockPlaced(@Nonnull ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
		CompoundNBT tag = ItemUtils.getItemTag(stack);

		PlacementMode mode = getMode(tag);
		final TileEntity tileEntity = world.getTileEntity(pos);
		configureBlockEntity(tileEntity, mode, tag);

		if (!player.capabilities.isCreativeMode) {
			float uses = Math.max(getUses(tag) - mode.cost, 0);
			tag.setFloat(TAG_USES, uses);

			if (uses <= 0) stack.setCount(0);
		}
	}

	protected abstract void configureBlockEntity(final TileEntity tileEntity, final PlacementMode mode, CompoundNBT tag);

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
		final ItemStack stack = player.getHeldItem(hand);
		CompoundNBT tag = ItemUtils.getItemTag(stack);
		float uses = getUses(tag);
		if (uses <= 0) {
			stack.setCount(0);
			return ActionResultType.FAIL;
		}

		if (uses < getMode(tag).cost) return ActionResultType.FAIL;

		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> result, ITooltipFlag flagIn) {
		CompoundNBT tag = ItemUtils.getItemTag(stack);

		result.add(TranslationUtils.translateToLocalFormatted("openblocks.misc.uses", getUses(tag)));

		PlacementMode mode = getMode(tag);
		String translatedMode = TranslationUtils.translateToLocal(mode.name);
		result.add(TranslationUtils.translateToLocalFormatted("openblocks.misc.mode", translatedMode));
	}

	@Override
	public boolean hasContainerItem(@Nonnull ItemStack stack) {
		return true;
	}

	@Override
	@Nonnull
	public ItemStack getContainerItem(@Nonnull ItemStack stack) {
		CompoundNBT tag = ItemUtils.getItemTag(stack);
		float uses = getUses(tag) - CRAFTING_COST;
		if (uses <= 0) return ItemStack.EMPTY;

		ItemStack copy = stack.copy();
		CompoundNBT copyTag = ItemUtils.getItemTag(copy);
		copyTag.setFloat(TAG_USES, uses);
		return copy;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		final ItemStack stack = player.getHeldItem(hand);

		if (hand != Hand.MAIN_HAND) return ActionResult.newResult(ActionResultType.PASS, stack);

		CompoundNBT tag = ItemUtils.getItemTag(stack);
		if (getUses(tag) <= 0) {
			stack.setCount(0);
		} else if (player.isSneaking()) {
			byte modeId = tag.getByte(TAG_MODE);
			modeId = (byte)((modeId + 1) % PlacementMode.VALUES.length);
			tag.setByte(TAG_MODE, modeId);

			if (world.isRemote) {
				PlacementMode mode = PlacementMode.VALUES[modeId];
				TranslationTextComponent modeName = new TranslationTextComponent(mode.name);
				player.sendMessage(new TranslationTextComponent("openblocks.misc.mode", modeName));
			}
		}

		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}
}
