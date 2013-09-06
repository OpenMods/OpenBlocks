package openblocks.common.item;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.entity.EntityHangGlider;

public class ItemHangGlider extends Item {

	public ItemHangGlider() {
		super(OpenBlocks.Config.itemHangGliderId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return "item.openblocks.hangglider";
	}

	@Override
	public void registerIcons(IconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:hangglider");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if (isGliderSpawned(world, player)) {
			despawnGlider(world, player);
		} else {
			spawnGlider(world, player);
		}
		return itemStack;
	}

	private void spawnGlider(World world, EntityPlayer player) {
		if (!world.isRemote) {
			ItemStack heldStack = player.getHeldItem();
			if (heldStack != null && heldStack.getItem() == this) {
				EntityHangGlider glider = new EntityHangGlider(world, player);
				glider.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationPitch, player.rotationYaw);
				world.spawnEntityInWorld(glider);
			}
		}
	}

	private static void despawnGlider(World world, EntityPlayer player) {
		if (isGliderSpawned(world, player)) {
			if (world.isRemote) {
				OpenBlocks.proxy.gliderClientMap.get(player).despawnGlider();
			} else {
				OpenBlocks.proxy.gliderMap.get(player).despawnGlider();
			}
		}
	}

	private static boolean isGliderSpawned(World world, EntityPlayer player) {
		if (world.isRemote) {
			return OpenBlocks.proxy.gliderClientMap.containsKey(player);
		} else {
			return OpenBlocks.proxy.gliderMap.containsKey(player);
		}
	}
}
