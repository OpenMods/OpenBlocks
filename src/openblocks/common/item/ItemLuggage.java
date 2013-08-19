package openblocks.common.item;

import openblocks.OpenBlocks;
import openblocks.common.entity.EntityLuggage;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLuggage extends Item {

	public ItemLuggage() {
		super(OpenBlocks.Config.itemLuggageId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return "item.openblocks.luggage";
	}

	@Override
	public void registerIcons(IconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:luggage");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			EntityLuggage luggage = new EntityLuggage(world);
			luggage.setPositionAndRotation(player.posX, player.posY, player.posZ, 0, 0);
			luggage.setOwner(player.username);
			world.spawnEntityInWorld(luggage);
			itemStack.stackSize = 0;
		}
		return itemStack;
	}
}
