package openblocks.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openblocks.Config;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class ItemSpongeOnAStick extends Item {

	public ItemSpongeOnAStick() {
		setMaxStackSize(1);
		setMaxDamage(Config.spongeMaxDamage);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		soakUp(world, pos, player, stack);
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		soakUp(world, player.getPosition(), player, stack);
		return stack;
	}

	private void soakUp(World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
		if (world.isRemote) return;
		boolean hitLava = false;
		int damage = stack.getItemDamage();

		for (int x = -Config.spongeStickRange; x <= Config.spongeStickRange; x++) {
			for (int y = -Config.spongeStickRange; y <= Config.spongeStickRange; y++) {
				for (int z = -Config.spongeStickRange; z <= Config.spongeStickRange; z++) {
					final BlockPos targetPos = pos.add(x, y, z);
					Block block = world.getBlockState(targetPos).getBlock();
					if (block != null) {
						Material material = block.getMaterial();
						if (material.isLiquid()) {
							hitLava |= material == Material.lava;
							world.setBlockToAir(targetPos);
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
