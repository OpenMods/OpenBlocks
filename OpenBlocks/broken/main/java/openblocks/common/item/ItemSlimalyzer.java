package openblocks.common.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import openblocks.OpenBlocks;
import openmods.infobook.BookDocumentation;
import openmods.utils.ItemUtils;

@BookDocumentation
public class ItemSlimalyzer extends Item {

	// NOTE for future: world seed is not available on client side

	private static final String TAG_ACTIVE = "Active";

	public ItemSlimalyzer() {
		addPropertyOverride(new ResourceLocation("active"), (ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) -> isActive(stack)? 2 : 0);
	}

	private static boolean isActive(ItemStack stack) {
		final CompoundNBT itemTag = stack.getTagCompound();
		return itemTag != null && itemTag.getBoolean(TAG_ACTIVE);
	}

	private static boolean isInSlimeChunk(World world, Entity entity) {
		if (world == null || entity == null) return false;

		final Chunk chunk = world.getChunkFromBlockCoords(entity.getPosition());
		return chunk.getRandomWithSeed(987234911L).nextInt(10) == 0;
	}

	private static boolean update(@Nonnull ItemStack stack, World world, Entity entity) {
		final boolean isActive = isActive(stack);
		final boolean isInSlimeChunk = isInSlimeChunk(world, entity);
		if (isActive != isInSlimeChunk) {
			if (isInSlimeChunk)
				world.playSound(null, entity.getPosition(), OpenBlocks.Sounds.ITEM_SLIMALYZER_PING, SoundCategory.PLAYERS, 1F, 1F);
			ItemUtils.getItemTag(stack).setBoolean(TAG_ACTIVE, isInSlimeChunk);
			return true;
		}
		return false;
	}

	@Override
	public void onUpdate(@Nonnull ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (!world.isRemote)
			update(stack, world, entity);
	}

	@Override
	public boolean onEntityItemUpdate(ItemEntity entityItem) {
		final World world = entityItem.world;

		if (!world.isRemote) {
			final ItemStack stack = entityItem.getItem();
			if (update(stack, world, entityItem))
				entityItem.setItem(stack);
		}

		return false;
	}
}
