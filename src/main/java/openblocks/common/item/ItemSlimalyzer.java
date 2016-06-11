package openblocks.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import openblocks.OpenBlocks;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class ItemSlimalyzer extends Item {

	public IIcon on;
	public IIcon off;

	public ItemSlimalyzer() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		super.registerIcons(register);

		off = register.registerIcon("openblocks:slimeoff");
		on = register.registerIcon("openblocks:slimeon");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int dmg) {
		return dmg == 0? off : on;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
		if (!world.isRemote) {
			if (entity != null) {
				Chunk chunk = world.getChunkFromBlockCoords(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posZ));
				int previousDamage = stack.getItemDamage();
				stack.setItemDamage(chunk.getRandomWithSeed(987234911L).nextInt(10) == 0? 1 : 0);
				if (previousDamage != stack.getItemDamage()
						&& previousDamage == 0) {
					world.playSoundAtEntity(entity, "openblocks:slimalyzer.signal", 1F, 1F);
				}
			} else {
				stack.setItemDamage(0);
			}
		}
	}

}
