package openblocks.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityImaginary.ICollisionData;
import openblocks.common.tileentity.TileEntityImaginary.PanelData;
import openblocks.common.tileentity.TileEntityImaginary.StairsData;
import openblocks.utils.BlockUtils;
import openblocks.utils.ColorUtils;
import openblocks.utils.ItemUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemImaginary extends ItemOpenBlock {

	public static final float CRAFTING_COST = 1.0f;
	public static final String TAG_COLOR = "Color";
	public static final String TAG_USES = "Uses";
	public static final String TAG_MODE = "Mode";

	private static final int DAMAGE_PENCIL = 0;
	private static final int DAMAGE_CRAYON = 1;

	private enum PlacementMode {
		BLOCK(1.0f, "block", "overlay_block") {
			@Override
			public ICollisionData createCollisionData(ItemStack stack, EntityPlayer player) {
				return TileEntityImaginary.DUMMY;
			}
		},
		PANEL(0.5f, "panel", "overlay_panel") {
			@Override
			public ICollisionData createCollisionData(ItemStack stack, EntityPlayer player) {
				return new PanelData(1.0f);
			}
		},
		HALF_PANEL(0.5f, "half_panel", "overlay_half") {
			@Override
			public ICollisionData createCollisionData(ItemStack stack, EntityPlayer player) {
				return new PanelData(0.5f);
			}
		},
		STAIRS(0.75f, "stairs", "overlay_stairs") {
			@Override
			public ICollisionData createCollisionData(ItemStack stack, EntityPlayer player) {
				ForgeDirection dir = BlockUtils.get2dOrientation(player);
				return new StairsData(0.5f, 1.0f, dir);
			}
		};

		public final float cost;
		public final String name;
		public final String overlayName;
		public Icon overlay;

		private PlacementMode(float cost, String name, String overlayName) {
			this.cost = cost;
			this.name = "openblocks.misc.mode." + name;
			this.overlayName = "openblocks:" + overlayName;
		}

		public abstract ICollisionData createCollisionData(ItemStack stack, EntityPlayer player);

		public static final PlacementMode[] VALUES = values();
	}

	public static float getUses(NBTTagCompound tag) {
		NBTBase value = tag.getTag(TAG_USES);
		if (value instanceof NBTTagInt) return ((NBTTagInt)value).data;

		if (value instanceof NBTTagFloat) return ((NBTTagFloat)value).data;

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

	public ItemImaginary(int id) {
		super(id);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setMaxDamage(0);
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
	protected void afterBlockPlaced(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);

		NBTTagInt color = (NBTTagInt)tag.getTag(TAG_COLOR);
		PlacementMode mode = getMode(tag);
		ICollisionData collisions = mode.createCollisionData(stack, player);
		world.setBlockTileEntity(x, y, z, new TileEntityImaginary(color == null? null : color.data, collisions));

		if (!player.capabilities.isCreativeMode) {
			float uses = Math.max(getUses(tag) - mode.cost, 0);
			tag.setFloat(TAG_USES, uses);

			if (uses <= 0) stack.stackSize = 0;
		}
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (stack == null) return false;

		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		float uses = getUses(tag);
		if (uses <= 0) {
			stack.stackSize = 0;
			return true;
		}

		if (uses < getMode(tag).cost) return false;

		return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List result, boolean extended) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);

		result.add(StatCollector.translateToLocalFormatted("openblocks.misc.uses", getUses(tag)));

		NBTTagInt color = (NBTTagInt)tag.getTag(TAG_COLOR);
		if (color != null) result.add(StatCollector.translateToLocalFormatted("openblocks.misc.color", color.data));

		PlacementMode mode = getMode(tag);
		String translatedMode = StatCollector.translateToLocal(mode.name);
		result.add(StatCollector.translateToLocalFormatted("openblocks.misc.mode", translatedMode));
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(int id, CreativeTabs tab, List result) {
		result.add(setupValues(null, new ItemStack(this, 1, DAMAGE_PENCIL)));
		for (Integer color : ColorUtils.COLORS.values())
			result.add(setupValues(color, new ItemStack(this, 1, DAMAGE_CRAYON)));
	}

	@Override
	public int getSpriteNumber() {
		return 1; // render as item
	}

	private Icon iconCrayonBackground;
	private Icon iconCrayonColor;
	private Icon iconPencil;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		iconCrayonBackground = registry.registerIcon("openblocks:crayon_1");
		iconCrayonColor = registry.registerIcon("openblocks:crayon_2");
		iconPencil = registry.registerIcon("openblocks:pencil");

		for (PlacementMode mode : PlacementMode.VALUES)
			mode.overlay = registry.registerIcon(mode.overlayName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final Icon getIcon(ItemStack stack, int pass) {
		if (!isCrayon(stack)) return pass == 1? getMode(stack).overlay : iconPencil;

		switch (pass) {
			case 0:
				return iconCrayonBackground;
			case 1:
				return iconCrayonColor;
			case 2:
				return getMode(stack).overlay;
		}

		throw new IllegalArgumentException("Invalid pass: " + pass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if (isCrayon(stack) && pass == 1) return ItemUtils.getInt(stack, TAG_COLOR);

		return 0xFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return metadata == DAMAGE_CRAYON? 3 : 2;
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
		return false;
	}

	@Override
	public boolean hasContainerItem() {
		return true;
	}

	@Override
	public ItemStack getContainerItemStack(ItemStack stack) {
		ItemStack copy = stack.copy();

		NBTTagCompound tag = ItemUtils.getItemTag(copy);
		float uses = Math.max(getUses(tag) - CRAFTING_COST, 0);
		tag.setFloat(TAG_USES, uses);

		return copy;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		if (getUses(tag) <= 0) {
			stack.stackSize = 0;
		} else if (player.isSneaking()) {
			byte modeId = tag.getByte(TAG_MODE);
			modeId = (byte)((modeId + 1) % PlacementMode.VALUES.length);
			tag.setByte(TAG_MODE, modeId);

			if (world.isRemote) {
				PlacementMode mode = PlacementMode.VALUES[modeId];
				ChatMessageComponent modeName = ChatMessageComponent.createFromTranslationKey(mode.name);
				player.sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions("openblocks.misc.mode", modeName));
			}
		}

		return stack;
	}
}
