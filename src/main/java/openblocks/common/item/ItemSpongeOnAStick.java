package openblocks.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.infobook.BookDocumentation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation
public class ItemSpongeOnAStick extends Item {

	public ItemSpongeOnAStick() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setMaxStackSize(1);
		setMaxDamage(Config.spongeMaxDamage);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:spongeonastick");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		soakUp(world, x, y, z, player, stack);
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		soakUp(world, (int)player.posX, (int)player.posY, (int)player.posZ, player, stack);
		return stack;
	}

	private void soakUp(World world, int xCoord, int yCoord, int zCoord, EntityPlayer player, ItemStack stack) {
		if (world.isRemote) return;
		boolean hitLava = false;
		int damage = stack.getItemDamage();

		for (int x = -Config.spongeStickRange; x <= Config.spongeStickRange; x++) {
			for (int y = -Config.spongeStickRange; y <= Config.spongeStickRange; y++) {
				for (int z = -Config.spongeStickRange; z <= Config.spongeStickRange; z++) {
					Block block = world.getBlock(xCoord + x, yCoord + y, zCoord + z);
					if (block != null) {
						Material material = block.getMaterial();
						if (material.isLiquid()) {
							hitLava |= material == Material.lava;
							world.setBlockToAir(xCoord + x, yCoord + y, zCoord + z);
							if (++damage >= getMaxDamage()) break;
						}
					}
				}
			}
		}

		if (hitLava) {
			stack.stackSize = 0;
			player.setFire(6);
		}

		if (damage >= getMaxDamage()) stack.stackSize = 0;
		else stack.setItemDamage(damage);
	}

}
