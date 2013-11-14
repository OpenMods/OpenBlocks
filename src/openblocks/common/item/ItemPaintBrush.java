package openblocks.common.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Blocks;
import openblocks.common.block.BlockCanvas;
import openblocks.utils.ColorUtils;
import openblocks.utils.PaintUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemPaintBrush extends Item {

	public Icon paintIcon;
	
	public static final int MAX_USES = 24;
	
	public ItemPaintBrush() {
		super(Config.itemPaintBrushId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setHasSubtypes(true);
		setUnlocalizedName("openblocks.paintbrush");
		setMaxDamage(MAX_USES + 1); // Damage dealt in Canvas block
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		int color = getColorFromStack(itemStack);
		if (color < 0) color = 0;
		list.add("#" + Integer.toHexString(color).toUpperCase());
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
    	for(int color : ColorUtils.COLORS.values()) {
    		list.add(createStackWithColor(color));
    	}
    }
    
    public static ItemStack createStackWithColor(int color) {
    	ItemStack stack = new ItemStack(OpenBlocks.Items.paintBrush);
    	NBTTagCompound tag = new NBTTagCompound();
    	tag.setInteger("color", color);
    	tag.setInteger("damage", MAX_USES);
    	stack.setTagCompound(tag);
    	return stack;
    }
    
        
    @Override
    public boolean isDamaged(ItemStack stack) {
    	return getDamage(stack) < MAX_USES;
    }
    
    @Override
    public int getDisplayDamage(ItemStack stack) {
    	return getMaxDamage(stack) == 0 ? 0 : getDamage(stack);
    }
    
    @Override
    public int getDamage(ItemStack stack) {
    	NBTTagCompound tag = stack.getTagCompound();
    	return tag.hasKey("damage") ? tag.getInteger("damage") : 0;
    }
    
    @Override
    public void setDamage(ItemStack stack, int damage) {
    	NBTTagCompound tag = stack.getTagCompound();
    	if(damage > MAX_USES) {
    		damage = MAX_USES;
    	}
    	tag.setInteger("damage", damage);
    }
    
    @Override
    public int getMaxDamage(ItemStack stack) {
    	NBTTagCompound tag = stack.getTagCompound();
    	int damage = tag.getInteger("damage");
    	if(damage == super.getMaxDamage()) return 0;
    	return super.getMaxDamage();
    }
        
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		/* Check that we can paint this */
		if(stack.getItemDamage() >= MAX_USES) return true;
		/* Check with our manager upstairs about white lists */
		if(!PaintUtils.isAllowedToPaint(world, x, y, z)) return true;
		/* Do some logic to make sure it's okay to paint */
		int id = world.getBlockId(x, y, z);
		if(id == 0 || id == OpenBlocks.Blocks.canvas.blockID) return true; /* Ignore */
		Block b = Block.blocksList[id];
		if(b == null) return true;
		if(!b.canProvidePower() && !b.isAirBlock(world, x, y, z)) {
			BlockCanvas.replaceBlock(world, x, y, z);
			OpenBlocks.Blocks.canvas.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
		}
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
