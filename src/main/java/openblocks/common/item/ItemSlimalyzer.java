package openblocks.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import openblocks.OpenBlocks;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class ItemSlimalyzer extends Item {

	public ItemSlimalyzer() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	// TODO 1.8.9 If possible, move to rendering logic?
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
		if (!world.isRemote) {
			if (entity != null) {
				Chunk chunk = world.getChunkFromBlockCoords(entity.getPosition());
				int previousDamage = stack.getItemDamage();
				stack.setItemDamage(chunk.getRandomWithSeed(987234911L).nextInt(10) == 0? 1 : 0);
				if (previousDamage != stack.getItemDamage() && previousDamage == 0) {
					world.playSoundAtEntity(entity, "openblocks:slimalyzer.signal", 1F, 1F);
				}
			} else {
				stack.setItemDamage(0);
			}
		}
	}

}
