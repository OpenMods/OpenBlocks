package openblocks.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.OpenBlocks;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSqueegee extends Item {

	public ItemSqueegee() {
		super(Config.itemSqueegeeId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setHasSubtypes(true);
		setMaxStackSize(1);
		setUnlocalizedName("openblocks.squeegee");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:squeegee");
	}
	
	@Override
	public boolean shouldPassSneakingClickToBlock(World par2World, int par4, int par5, int par6) {
		return true;
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		int id = world.getBlockId(x, y, z);
		if(id == OpenBlocks.Blocks.canvas.blockID ||
				id == OpenBlocks.Blocks.canvasGlass.blockID) {
			return true;
		}
		return false;
	}
}
