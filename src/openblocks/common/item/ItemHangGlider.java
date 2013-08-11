package openblocks.common.item;

import openblocks.OpenBlocks;
import openblocks.common.entity.EntityHangGlider;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemHangGlider extends Item {

	
	public ItemHangGlider() {
		super(OpenBlocks.Config.itemHangGliderId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	public void registerIcons(IconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:hangglider");
	}
	
	@Override
	public void onUpdate(ItemStack par1ItemStack, World world, Entity entity, int par4, boolean par5) {
		if (!world.isRemote && entity instanceof EntityPlayer && !EntityHangGlider.gliderMap.containsKey(entity)) {
			EntityPlayer player = (EntityPlayer) entity;
			ItemStack heldStack = player.getHeldItem();
			if (heldStack != null && heldStack.getItem() == this) {
				EntityHangGlider glider = new EntityHangGlider(world, (EntityPlayer)entity);
				glider.setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationPitch, entity.rotationYaw);
				world.spawnEntityInWorld(glider);
			}
		}
    }
}
