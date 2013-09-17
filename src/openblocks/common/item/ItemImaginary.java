package openblocks.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.utils.ColorUtils;
import openblocks.utils.ItemUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemImaginary extends ItemOpenBlock {

	private final static int DEFAULT_USE_COUNT = 10;
	public static final String TAG_COLOR = "Color";
	public static final String TAG_USES = "Uses";

	private static final int DAMAGE_PENCIL = 0;
	private static final int DAMAGE_CRAYON = 1;

	public static boolean hasUses(ItemStack stack) {
		return ItemUtils.getInt(stack, TAG_USES) > 0;
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
		return setupValues(color, result, DEFAULT_USE_COUNT);
	}

	public static ItemStack setupValues(Integer color, ItemStack result, int uses) {
		NBTTagCompound tag = ItemUtils.getItemTag(result);

		if (color != null) {
			tag.setInteger(TAG_COLOR, color);
			result.setItemDamage(DAMAGE_CRAYON);
		}

		tag.setInteger(TAG_USES, uses);
		return result;
	}

	@Override
	protected void afterBlockPlaced(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);

		NBTTagInt color = (NBTTagInt)tag.getTag(TAG_COLOR);
		world.setBlockTileEntity(x, y, z, new TileEntityImaginary(color == null? null : color.data));

		if (!player.capabilities.isCreativeMode) {
			int uses = Math.max(tag.getInteger(TAG_USES) - 1, 0);
			tag.setInteger(TAG_USES, uses);

			if (uses <= 0) stack.stackSize = 0;
		}
	}

	@Override
	protected boolean isStackValid(ItemStack stack, EntityPlayer player) {
		return hasUses(stack);
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

		result.add(StatCollector.translateToLocalFormatted("openblocks.misc.uses", tag.getInteger(TAG_USES)));

		NBTTagInt color = (NBTTagInt)tag.getTag(TAG_COLOR);
		if (color != null) result.add(StatCollector.translateToLocalFormatted("openblocks.misc.color", color.data));
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
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final Icon getIcon(ItemStack stack, int pass) {
		if (!isCrayon(stack)) return iconPencil;

		return pass == 1? iconCrayonColor : iconCrayonBackground;
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
		// just to force render to use getIcon(ItemStack stack, int pass)
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return metadata == DAMAGE_CRAYON? 2 : 1;
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
		int uses = Math.max(tag.getInteger(TAG_USES) - 1, 0);
		tag.setInteger(TAG_USES, uses);

		return copy;
	}
}
