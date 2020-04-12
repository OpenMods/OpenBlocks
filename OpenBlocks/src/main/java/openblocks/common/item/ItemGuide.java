package openblocks.common.item;

import static openblocks.shapes.GuideShape.VALUES;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import openblocks.shapes.GuideShape;

public class ItemGuide extends BlockItem {

	public static final String TAG_POS_X = "PosX";
	public static final String TAG_NEG_X = "NegX";

	public static final String TAG_POS_Y = "PosY";
	public static final String TAG_NEG_Y = "NegY";

	public static final String TAG_POS_Z = "PosZ";
	public static final String TAG_NEG_Z = "NegZ";

	public static final String TAG_COLOR = "Color";
	public static final String TAG_SHAPE = "Mode";

	public ItemGuide(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> result, ITooltipFlag flag) {
		CompoundNBT tag = stack.getTag();
		if (tag != null) {
			final int posX = tag.getInt(TAG_POS_X);
			final int posY = tag.getInt(TAG_POS_Y);
			final int posZ = tag.getInt(TAG_POS_Z);

			final int negX = -tag.getInt(TAG_NEG_X);
			final int negY = -tag.getInt(TAG_NEG_Y);
			final int negZ = -tag.getInt(TAG_NEG_Z);

			result.add(new TranslationTextComponent("openblocks.misc.box", negX, negY, negZ, posX, posY, posZ));
			if (tag.contains(TAG_COLOR)) {
				result.add(new TranslationTextComponent("openblocks.misc.color", new StringTextComponent(String.format("%06X", tag.getInt(TAG_COLOR)))));
			}

			if (tag.contains(TAG_SHAPE)) {
				int mode = tag.getInt(TAG_SHAPE);
				try {
					GuideShape shape = VALUES[mode];
					result.add(new TranslationTextComponent("openblocks.misc.shape", shape.getLocalizedName()));
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
		}
	}

}
