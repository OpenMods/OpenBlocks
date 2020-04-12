package openblocks.common.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.entity.EntityLuggage;
import openmods.fixers.NestedItemInventoryWalker;
import openmods.infobook.BookDocumentation;
import openmods.inventory.GenericInventory;
import openmods.utils.ItemUtils;

@BookDocumentation(hasVideo = true)
public class ItemLuggage extends Item {

	public ItemLuggage() {
		setMaxStackSize(1);

		addPropertyOverride(new ResourceLocation("inventory"), (@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) -> getInventorySize(stack));
	}

	@Override
	public boolean hasEffect(@Nonnull ItemStack stack) {
		return getInventorySize(stack) > EntityLuggage.SIZE_NORMAL;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		final ItemStack stack = player.getHeldItem(hand);

		if (hand != Hand.MAIN_HAND) return ActionResult.newResult(ActionResultType.PASS, stack);

		if (!world.isRemote) {
			Vec3d vec3 = new Vec3d(player.posX, player.posY, player.posZ);
			Vec3d vec31 = player.getLook(1.0f);
			Vec3d vec32 = vec3.addVector(vec31.x * 2.0f, vec31.y * 2.0f, vec31.z * 2.0f);
			EntityLuggage luggage = new EntityLuggage(world);
			luggage.setPositionAndRotation(0.5 + vec32.x, vec3.y, 0.5 + vec32.z, 0, 0);
			luggage.setOwnerId(player.getGameProfile().getId());
			luggage.restoreFromStack(stack);

			world.spawnEntity(luggage);
			stack.shrink(1);

		}

		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	private static int getInventorySize(@Nonnull ItemStack stack) {
		return ItemUtils.getItemTag(stack).getInteger(GenericInventory.TAG_SIZE);
	}

	public static void registerFixes(DataFixer dataFixer) {
		if (OpenBlocks.Items.luggage != null)
			dataFixer.registerWalker(FixTypes.ITEM_INSTANCE, new NestedItemInventoryWalker(OpenBlocks.Items.luggage, GenericInventory.TAG_ITEMS));
	}
}
