package openblocks.common.item;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.utils.io.TypeRW;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Property;

public class ItemCursor extends Item {
	
	// temporary
	public static Map<Class<?>, Integer> damageMap = ImmutableMap.<Class<?>, Integer> builder()
			.put(IInventory.class, 32)
			.build();
	
	public ItemCursor() {
		super(Config.itemCursorId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setMaxStackSize(1);
		setMaxDamage(Config.cursorMaxDamage);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:cursor");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int par7, float par8, float par9, float par10) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("dimension", world.provider.dimensionId);
		tag.setInteger("x", x);
		tag.setInteger("y", y);
		tag.setInteger("z", z);
		stack.setTagCompound(tag);
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		
		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag != null) {
			if (tag.hasKey("x") && tag.hasKey("y") && tag.hasKey("z") && tag.hasKey("dimension")) {
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				int dimension = tag.getInteger("dimension");
				if (world.provider.dimensionId == dimension && world.blockExists(x, y, z)) {
					int blockId = world.getBlockId(x, y, z);
					Block block = Block.blocksList[blockId];
					if (block != null) {
						int cost = 1;
						TileEntity te = world.getBlockTileEntity(x, y, z);
						if (te != null) {
							for (Entry<Class<?>, Integer> entry : damageMap.entrySet()) {
								if (entry.getKey().isAssignableFrom(te.getClass())) {
									cost = entry.getValue();
									break;
								}
							}
						}
						if (itemStack.getItemDamage() + cost >= Config.cursorMaxDamage) return itemStack;
						block.onBlockActivated(world, x, y, z, player, 0, 0, 0, 0);
						// this can cause a crash. havent really looked into why, but
						// I think it's to do with the nethandler trying to find the stack
						// that was used, but because we're damaging the item, the item is 'different'
						// to what it's looking for.
						//itemStack.damageItem(cost, player);
					}
				}
			}
		}
		return itemStack;
	}
}
