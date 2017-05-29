package openblocks.common.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
		addPropertyOverride(new ResourceLocation("active"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return isActive(stack)? 2 : 0;
			}
		});
	}

	private static boolean isActive(ItemStack stack) {
		final NBTTagCompound itemTag = stack.getTagCompound();
		return itemTag != null && itemTag.getBoolean(TAG_ACTIVE);
	}

	private static boolean isInSlimeChunk(World world, Entity entity) {
		if (world == null || entity == null) return false;

		final Chunk chunk = world.getChunkFromBlockCoords(entity.getPosition());
		return chunk.getRandomWithSeed(987234911L).nextInt(10) == 0;
	}

	private static boolean update(ItemStack stack, World world, Entity entity) {
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
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		if (!world.isRemote)
			update(stack, world, entity);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		final World world = entityItem.worldObj;

		if (!world.isRemote) {
			final ItemStack stack = entityItem.getEntityItem();
			if (update(stack, world, entityItem))
				entityItem.setEntityItemStack(stack);
		}

		return false;
	}
}
