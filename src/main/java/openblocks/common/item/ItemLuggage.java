package openblocks.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import openblocks.common.entity.EntityLuggage;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class ItemLuggage extends Item {

	public ItemLuggage() {
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			Vec3d vec3 = new Vec3d(player.posX, player.posY, player.posZ);
			Vec3d vec31 = player.getLook(1.0f);
			Vec3d vec32 = vec3.addVector(vec31.xCoord * 2.0f, vec31.yCoord * 2.0f, vec31.zCoord * 2.0f);
			EntityLuggage luggage = new EntityLuggage(world);
			luggage.setPositionAndRotation(0.5 + vec32.xCoord, vec3.yCoord, 0.5 + vec32.zCoord, 0, 0);
			luggage.setOwnerId(player.getGameProfile().getId());
			luggage.restoreFromStack(itemStack);

			world.spawnEntityInWorld(luggage);
			itemStack.stackSize--;

		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
	}
}
