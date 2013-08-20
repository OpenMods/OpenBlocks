package openblocks.common.item;

import openblocks.OpenBlocks;
import openblocks.common.entity.EntityLuggage;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemLuggage extends Item {

	public ItemLuggage() {
		super(OpenBlocks.Config.itemLuggageId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return "item.openblocks.luggage.name";
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
	        Vec3 vec32 = vec3.addVector(vec31.xCoord * 10.0f, vec31.yCoord * 10.0f, vec31.zCoord * 10.0f);
	        MovingObjectPosition mop = world.rayTraceBlocks(vec3, vec32);
	        if (mop.typeOfHit == EnumMovingObjectType.TILE) {
				EntityLuggage luggage = new EntityLuggage(world);
				luggage.setPositionAndRotation(0.5 + mop.blockX, 1 + mop.blockY, 0.5+mop.blockZ, 0, 0);
				luggage.setOwner(player.username);
				if (itemStack.hasTagCompound()) {
					luggage.getInventory().readFromNBT(itemStack.getTagCompound());
				}
				world.spawnEntityInWorld(luggage);
				itemStack.stackSize--;
	        }
	        
		}
		return itemStack;
	}
}
