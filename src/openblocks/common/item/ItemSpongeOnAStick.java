package openblocks.common.item;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.utils.BlockNotifyFlags;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSpongeOnAStick extends Item {
	
	public static final int MAX_DAMAGE = 4096;
	
	public ItemSpongeOnAStick() {
		super(Config.itemSpongeOnAStickId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setHasSubtypes(true);
		setMaxStackSize(1);
		setMaxDamage(MAX_DAMAGE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:spongeonastick");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return soakUp(world, x, y, z, player, stack);
	}
	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		soakUp(world, (int)player.posX, (int)player.posY, (int)player.posZ, player, stack);
	    return stack;
	}

	public boolean soakUp(World world, int xCoord, int yCoord, int zCoord, EntityPlayer player, ItemStack stack) {
		if (world.isRemote) { return true; }
		boolean hitLava = false;
		int dmg = 0;
		int predmg = stack.getItemDamage();	
		
		for (int x = -3; x <= 3; x++) {
			for (int y = -3; y <= 3; y++) {
				for (int z = -3; z <= 3; z++) {
					Material material = world.getBlockMaterial(xCoord + x, yCoord
							+ y, zCoord + z);
					if (material.isLiquid()) {
						if (material == Material.lava) {
							hitLava = true; 
						}
						world.setBlock(xCoord + x, yCoord + y, zCoord + z, 0, 0, BlockNotifyFlags.SEND_TO_CLIENTS);
						dmg++;
						if (dmg + predmg >= MAX_DAMAGE) 
							break;
					}
				}
			}
		}
		if (hitLava) {
			// Set fire to the user?
			player.setFire(6);
		}
		
		if (dmg + predmg >= MAX_DAMAGE) {
			stack.stackSize = 0;
		}
		else {
			stack.setItemDamage(dmg + predmg);
		}
		return true;
	}
	

}
