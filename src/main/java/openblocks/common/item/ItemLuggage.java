package openblocks.common.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import openblocks.common.entity.EntityLuggage;
import openmods.infobook.BookDocumentation;
import openmods.inventory.GenericInventory;
import openmods.utils.ItemUtils;

@BookDocumentation(hasVideo = true)
public class ItemLuggage extends Item {

	public ItemLuggage() {
		setMaxStackSize(1);

		addPropertyOverride(new ResourceLocation("inventory"), new IItemPropertyGetter() {
			@Override
			public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return getInventorySize(stack);
			}
		});
	}

	@Override
	public boolean hasEffect(@Nonnull ItemStack stack) {
		return getInventorySize(stack) > EntityLuggage.SIZE_NORMAL;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		final ItemStack stack = player.getHeldItem(hand);
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

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	private static int getInventorySize(@Nonnull ItemStack stack) {
		return ItemUtils.getItemTag(stack).getInteger(GenericInventory.TAG_SIZE);
	}
}
