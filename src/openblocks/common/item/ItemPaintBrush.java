package openblocks.common.item;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import openblocks.Config;
import openblocks.OpenBlocks;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemPaintBrush extends Item {

	public Icon paintIcon;
	
	public ItemPaintBrush() {
		super(Config.itemPaintBrushId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setHasSubtypes(true);
		setUnlocalizedName("openblocks.paintbrush");
	}
	
	@SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses() {
		return true;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:paintbrush");
		paintIcon = registry.registerIcon("openblocks:paintbrush_paint");
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(int id, CreativeTabs par2CreativeTabs, List list) {
    	Random rnd = new Random();
    	for (int i = 0; i < 1000; i++) {
    		list.add(createStackWithColor(rnd.nextInt(0xFFFFFF)));
    	}
    }
    
    public static ItemStack createStackWithColor(int color) {
    	ItemStack stack = new ItemStack(OpenBlocks.Items.paintBrush);
    	NBTTagCompound tag = new NBTTagCompound();
    	tag.setInteger("color", color);
    	stack.setTagCompound(tag);
    	return stack;
    }
    
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return true;
	}
	
	@Override
    public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) {
        return true;
    }
	
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int pass) {
        return pass == 1 ? getColorFromStack(itemStack) : 0xFFFFFF;
    }
    
    public Icon getIconFromDamageForRenderPass(int dmg, int pass) {
        return pass == 1 ? paintIcon : getIconFromDamage(dmg);
    }

	public static int getColorFromStack(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey("color")) {
				return tag.getInteger("color");
			}
		}
		return -1;
	}
}
