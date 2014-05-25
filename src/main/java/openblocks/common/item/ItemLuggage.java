package openblocks.common.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.entity.EntityLuggage;

public class ItemLuggage extends Item {

	public ItemLuggage() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setMaxStackSize(1);
	}

	@Override
	public void registerIcons(IconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:luggage");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if (!world.isRemote) {

			Vec3 vec3 = world.getWorldVec3Pool().getVecFromPool(player.posX, player.posY, player.posZ);
			Vec3 vec31 = player.getLook(1.0f);
			Vec3 vec32 = vec3.addVector(vec31.xCoord * 2.0f, vec31.yCoord * 2.0f, vec31.zCoord * 2.0f);
			EntityLuggage luggage = new EntityLuggage(world);
			luggage.setPositionAndRotation(0.5 + vec32.xCoord, vec3.yCoord, 0.5 + vec32.zCoord, 0, 0);
			luggage.setOwner(player.username);
			if (itemStack.hasTagCompound()) {
				luggage.getInventory().readFromNBT(itemStack.getTagCompound());
				if (luggage.getInventory().getSizeInventory() > 27) {
					luggage.setSpecial();
				}
			}

			if (itemStack.hasDisplayName()) luggage.setCustomNameTag(itemStack.getDisplayName());

			world.spawnEntityInWorld(luggage);
			itemStack.stackSize--;

		}
		return itemStack;
	}
}
