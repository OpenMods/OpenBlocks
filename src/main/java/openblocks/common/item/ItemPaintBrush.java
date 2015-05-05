package openblocks.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.api.IPaintableBlock;
import openblocks.common.block.BlockCanvas;
import openmods.infobook.BookDocumentation;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;
import openmods.utils.ItemUtils;
import openmods.utils.render.PaintUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation(customName = "paintbrush")
public class ItemPaintBrush extends Item {

	private static final int SINGLE_COLOR_THRESHOLD = 16;

	private static final String TAG_COLOR = "color";

	public static final int MAX_USES = 24;

	public IIcon paintIcon;

	public ItemPaintBrush() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setMaxStackSize(1);
		setMaxDamage(MAX_USES); // Damage dealt in Canvas block
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		Integer color = getColorFromStack(itemStack);
		if (color != null) list.add(String.format("#%06X", color));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:paintbrush");
		paintIcon = registry.registerIcon("openblocks:paintbrush_paint");
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list) {
		list.add(new ItemStack(this));
		for (ColorMeta color : ColorUtils.getAllColors()) {
			list.add(createStackWithColor(color.rgb));
		}
	}

	public static ItemStack createStackWithColor(int color) {
		ItemStack stack = new ItemStack(OpenBlocks.Items.paintBrush);
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		tag.setInteger(TAG_COLOR, color);
		return stack;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		Integer color = getColorFromStack(stack);
		if (stack.getItemDamage() >= MAX_USES || color == null) return true;

		if (PaintUtils.instance.isAllowedToReplace(world, x, y, z)) {
			BlockCanvas.replaceBlock(world, x, y, z);
		}

		final boolean changed;

		if (player.isSneaking()) changed = tryRecolorBlock(world, x, y, z, color, ForgeDirection.VALID_DIRECTIONS);
		else changed = tryRecolorBlock(world, x, y, z, color, ForgeDirection.getOrientation(side));

		if (changed) {
			world.playSoundAtEntity(player, "mob.slime.small", 0.1F, 0.8F);
			stack.damageItem(1, player);
		}

		return true;
	}

	private static boolean tryRecolorBlock(World world, int x, int y, int z, int rgb, ForgeDirection... sides) {
		Block block = world.getBlock(x, y, z);

		// first try RGB color...
		if (block instanceof IPaintableBlock) {
			IPaintableBlock canvas = (IPaintableBlock)block;

			boolean result = false;
			for (ForgeDirection dir : sides)
				result |= canvas.recolourBlockRGB(world, x, y, z, dir, rgb);

			return result;
		}

		// ...then try finding nearest vanilla one
		final ColorMeta nearest = ColorUtils.findNearestColor(new ColorUtils.RGB(rgb), SINGLE_COLOR_THRESHOLD);
		if (nearest != null) {
			final int vanillaColorId = nearest.vanillaBlockId;

			boolean result = false;
			for (ForgeDirection dir : sides)
				result |= block.recolourBlock(world, x, y, z, dir, vanillaColorId);

			return result;
		}

		return false;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int pass) {
		if (pass == 1) {
			Integer color = getColorFromStack(itemStack);
			if (color != null) return color;
		}

		return 0xFFFFFF;
	}

	@Override
	public IIcon getIconFromDamageForRenderPass(int dmg, int pass) {
		return pass == 1? paintIcon : getIconFromDamage(dmg);
	}

	public static Integer getColorFromStack(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(TAG_COLOR)) { return tag.getInteger(TAG_COLOR); }
		}
		return null;
	}

	public static void setColor(ItemStack stack, int color) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		tag.setInteger(TAG_COLOR, color);
	}
}
