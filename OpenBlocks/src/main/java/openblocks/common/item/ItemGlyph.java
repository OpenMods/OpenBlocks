package openblocks.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.common.entity.EntityGlyph;
import openmods.geometry.BlockTextureTransform;
import openmods.geometry.BlockTextureTransform.TexCoords;
import openmods.utils.ItemUtils;
import openmods.utils.TranslationUtils;
import org.apache.commons.lang3.ArrayUtils;

public class ItemGlyph extends Item {

	private static final String TAG_CHAR_INDEX = "CharIndex";

	@SideOnly(Side.CLIENT)
	public static class ColorHandler implements IItemColor {
		@Override
		public int colorMultiplier(@Nonnull ItemStack stack, int tintIndex) {
			return 0; // black letters
		}
	}

	// list of chars in basic texture sheet - copied form FontRenderer
	public static final char[] ALMOST_ASCII =
			"\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"
					.toCharArray();

	private static final char[] DISPLAY_CHARS = " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

	private static final char DEFAULT_CHAR = '?';

	public static final int DEFAULT_CHAR_INDEX = getCharIndex(DEFAULT_CHAR);

	private static int getCharIndex(char ch) {
		final int result = ArrayUtils.indexOf(ALMOST_ASCII, ch);
		return result != ArrayUtils.INDEX_NOT_FOUND? result : DEFAULT_CHAR_INDEX;
	}

	public static boolean isHiddenCharacter(char ch) {
		return ch != ' ' && (ch == 0 || Character.isWhitespace(ch));
	}

	private static char getChar(int index) {
		if (index >= 0 && index < ALMOST_ASCII.length) {
			return ALMOST_ASCII[index];
		} else {
			return DEFAULT_CHAR;
		}
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		final char ch = getChar(getCharIndex(stack));
		tooltip.add(String.format("%04X (%s)", Integer.valueOf(ch), Character.getName(ch)));
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		final int index = getCharIndex(stack);
		if (index == DEFAULT_CHAR_INDEX) {
			return super.getItemStackDisplayName(stack);
		} else {
			final char ch = getChar(index);
			return TranslationUtils.translateToLocalFormatted("item.openblocks.glyph.with_char", ch);
		}
	}

	public static ItemStack createStack(Item item, int size, char ch) {
		return createStack(item, size, getCharIndex(ch));
	}

	public static ItemStack createStack(Item item, char ch) {
		return createStack(item, 1, ch);
	}

	public static ItemStack createStack(Item item, int size, int charIndex) {
		final ItemStack result = new ItemStack(item, size);
		ItemUtils.getItemTag(result).setInteger(TAG_CHAR_INDEX, charIndex);
		return result;
	}

	public static ItemStack createStack(Item item, int charIndex) {
		return createStack(item, 1, charIndex);
	}

	public static int getCharIndex(ItemStack item) {
		final CompoundNBT tag = item.getTagCompound();
		return tag != null? tag.getInteger(TAG_CHAR_INDEX) : 0;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (Config.showGlypsInSearch && tab == CreativeTabs.SEARCH) {
			for (int i = 0; i < ALMOST_ASCII.length; i++) {
				final char ch = ALMOST_ASCII[i];
				if (!isHiddenCharacter(ch))
					items.add(createStack(this, i));
			}
		} else if (ArrayUtils.contains(getCreativeTabs(), tab)) {
			for (char ch : DISPLAY_CHARS)
				items.add(createStack(this, ch));
		}
	}

	private final BlockTextureTransform transform = BlockTextureTransform.builder().build();

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World worldIn, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		BlockPos blockpos = pos.offset(facing);

		if (facing != Direction.DOWN && facing != Direction.UP && player.canPlayerEdit(blockpos, facing, stack)) {
			final TexCoords localHit = transform.worldVecToTextureCoords(facing, hitX, hitY, hitZ);
			final byte xOffset = (byte)(localHit.u * 16);
			final byte yOffset = (byte)(16 - localHit.v * 16);
			final HangingEntity entityhanging = new EntityGlyph(worldIn, blockpos, facing, getCharIndex(stack), xOffset, yOffset);

			if (entityhanging.onValidSurface()) {
				if (!worldIn.isRemote) {
					entityhanging.playPlaceSound();
					worldIn.spawnEntity(entityhanging);
				}

				stack.shrink(1);
			}

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.FAIL;
	}

}
