package openblocks.common.item;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityImaginary.ICollisionData;
import openblocks.common.tileentity.TileEntityImaginary.PanelData;
import openblocks.common.tileentity.TileEntityImaginary.StairsData;
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

	private enum PlacementMode {
		BLOCK(1.0f, "block", "overlay_block", false) {
			@Override
			public ICollisionData createCollisionData(ItemStack stack, EntityPlayer player) {
				return TileEntityImaginary.DUMMY;
			}
		},
		PANEL(0.5f, "panel", "overlay_panel", false) {
			@Override
			public ICollisionData createCollisionData(ItemStack stack, EntityPlayer player) {
				return new PanelData(1.0f);
			}
		},
		HALF_PANEL(0.5f, "half_panel", "overlay_half", false) {
			@Override
			public ICollisionData createCollisionData(ItemStack stack, EntityPlayer player) {
				return new PanelData(0.5f);
			}
		},
		STAIRS(0.75f, "stairs", "overlay_stairs", false) {
			@Override
			public ICollisionData createCollisionData(ItemStack stack, EntityPlayer player) {
				EnumFacing dir = player.getHorizontalFacing();
				return new StairsData(0.5f, 1.0f, dir);
			}
		},

		INV_BLOCK(1.5f, "inverted_block", "overlay_inverted_block", true) {
			@Override
			public ICollisionData createCollisionData(ItemStack stack, EntityPlayer player) {
				return TileEntityImaginary.DUMMY;
			}
		},
		INV_PANEL(1.0f, "inverted_panel", "overlay_inverted_panel", true) {
			@Override
			public ICollisionData createCollisionData(ItemStack stack, EntityPlayer player) {
				return new PanelData(1.0f);
			}
		},
		INV_HALF_PANEL(1.0f, "inverted_half_panel", "overlay_inverted_half",
				true) {
			@Override
			public ICollisionData createCollisionData(ItemStack stack, EntityPlayer player) {
				return new PanelData(0.5f);
			}
		},
		INV_STAIRS(1.25f, "inverted_stairs", "overlay_inverted_stairs", true) {
			@Override
			public ICollisionData createCollisionData(ItemStack stack, EntityPlayer player) {
				EnumFacing dir = player.getHorizontalFacing();
				return new StairsData(0.5f, 1.0f, dir);
			}
		};

		public final float cost;
		public final String name;
		public final String overlayName;
		public final boolean isInverted;

		private PlacementMode(float cost, String name, String overlayName, boolean isInverted) {
			this.cost = cost;
			this.name = "openblocks.misc.mode." + name;
			this.overlayName = "openblocks:" + overlayName;
			this.isInverted = isInverted;
		}

		public abstract ICollisionData createCollisionData(ItemStack stack, EntityPlayer player);

		public static final PlacementMode[] VALUES = values();
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

	public static ItemStack setupValues(Integer color, ItemStack result) {
		return setupValues(color, result, Config.imaginaryItemUseCount);
	}

	public static ItemStack setupValues(Integer color, ItemStack result, float uses) {
		NBTTagCompound tag = ItemUtils.getItemTag(result);

		if (color != null) {
			tag.setInteger(TAG_COLOR, color);
			result.setItemDamage(DAMAGE_CRAYON);
		}

		tag.setFloat(TAG_USES, uses);
		return result;
	}

	@Override
	protected void afterBlockPlaced(ItemStack stack, EntityPlayer player, World world, BlockPos pos) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);

		NBTTagInt color = (NBTTagInt)tag.getTag(TAG_COLOR);
		PlacementMode mode = getMode(tag);
		ICollisionData collisions = mode.createCollisionData(stack, player);
		world.setTileEntity(pos, new TileEntityImaginary(color == null? null : color.getInt(), mode.isInverted, collisions));

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
		result.add(setupValues(null, new ItemStack(this, 1, DAMAGE_PENCIL)));
		for (ColorMeta color : ColorMeta.getAllColors())
			result.add(setupValues(color.rgb, new ItemStack(this, 1, DAMAGE_CRAYON)));
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
