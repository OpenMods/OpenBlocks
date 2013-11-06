package openblocks.common.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openblocks.Config;
import openblocks.OpenBlocks;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGeneric extends Item {

	protected HashMap<Integer, IMetaItem> metaitems = new HashMap<Integer, IMetaItem>();

	public enum Metas {
		gliderWing {
			@Override
			public IMetaItem createMetaItem() {
				ItemStack result = newItemStack();
				return new MetaGeneric("gliderwing", new ShapedOreRecipe(result, " sl", "sll", "lll", 's', "stickWood", 'l', Item.leather), new ShapedOreRecipe(result, "ls ", "lls", "lll", 's', "stickWood", 'l', Item.leather));
			}
		},
		beam {
			@Override
			public IMetaItem createMetaItem() {
				ItemStack result = newItemStack(2);
				return new MetaGeneric("beam", new ShapedOreRecipe(result, "iii", "b y", "iii", 'i', Item.ingotIron, 'b', "dyeBlack", 'y', "dyeYellow"), new ShapedOreRecipe(result, "iii", "y b", "iii", 'i', Item.ingotIron, 'b', "dyeBlack", 'y', "dyeYellow"));
			}
		},
		craneEngine {
			@Override
			public IMetaItem createMetaItem() {
				ItemStack result = newItemStack();
				return new MetaGeneric("crane_engine", new ShapedOreRecipe(result, "iii", "isi", "iri", 'i', Item.ingotIron, 's', "stickWood", 'r', Item.redstone));
			}
		},
		craneMagnet {
			@Override
			public IMetaItem createMetaItem() {
				ItemStack result = newItemStack();
				return new MetaGeneric("crane_magnet", new ShapedOreRecipe(result, "biy", "iri", 'i', Item.ingotIron, 'r', Item.redstone, 'b', "dyeBlack", 'y', "dyeYellow"), new ShapedOreRecipe(result, "yib", "iri", 'i', Item.ingotIron, 'r', Item.redstone, 'b', "dyeBlack", 'y', "dyeYellow"));
			}
		},
		miracleMagnet {
			@Override
			public IMetaItem createMetaItem() {
				ItemStack result = newItemStack();
				ItemStack magnet = craneMagnet.newItemStack();
				return new MetaMiracleMagnet("miracle_magnet", new ShapedOreRecipe(result, "rer", "eme", "rer", 'r', Item.redstone, 'e', Item.enderPearl, 'm', magnet), new ShapedOreRecipe(result, "ere", "rmr", "ere", 'r', Item.redstone, 'e', Item.enderPearl, 'm', magnet));
			}

			@Override
			public boolean isEnabled() {
				return Loader.isModLoaded(openblocks.Mods.COMPUTERCRAFT);
			}
		},
		line {
			@Override
			public IMetaItem createMetaItem() {
				ItemStack result = newItemStack(2);
				return new MetaGeneric("line", new ShapedOreRecipe(result, "sss", "bbb", "sss", 's', Item.silk, 'b', Item.slimeBall));
			}
		},
		mapController {
			@Override
			public IMetaItem createMetaItem() {
				return new MetaGeneric("map_controller").hideFromCreative();
			}
		},
		mapMemory {
			@Override
			public IMetaItem createMetaItem() {
				return new MetaGeneric("map_memory").hideFromCreative();
			}
		};

		public ItemStack newItemStack(int amount) {
			return OpenBlocks.Items.generic.newItemStack(this, amount);
		}

		public ItemStack newItemStack() {
			return OpenBlocks.Items.generic.newItemStack(this);
		}

		public abstract IMetaItem createMetaItem();

		public boolean isEnabled() {
			return true;
		}
	}

	public ItemGeneric() {
		this(Config.itemGenericId);
	}

	public ItemGeneric(int id) {
		super(id);
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(64);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	public void registerItems() {
		for (Metas m : Metas.values())
			if (m.isEnabled()) metaitems.put(m.ordinal(), m.createMetaItem());
	}

	public void initRecipes() {
		for (IMetaItem item : metaitems.values()) {
			item.addRecipe();
		}
	}

	@Override
	public void registerIcons(IconRegister register) {
		for (IMetaItem item : metaitems.values()) {
			item.registerIcons(register);
		}
	}

	@Override
	public Icon getIconFromDamage(int i) {
		IMetaItem meta = getMeta(i);
		if (meta != null) { return meta.getIcon(); }
		return null;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		IMetaItem meta = getMeta(stack.getItemDamage());
		if (meta != null) { return "item." + meta.getUnlocalizedName(stack); }
		return "";
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		IMetaItem meta = getMeta(itemStack.getItemDamage());
		if (meta != null) { return meta.onItemUse(itemStack, player, world, x, y, z, side, par8, par9, par10); }
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		IMetaItem meta = getMeta(itemStack.getItemDamage());
		if (meta != null) { return meta.onItemRightClick(itemStack, player, world); }
		return itemStack;
	}

	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase player) {
		IMetaItem meta = getMeta(itemStack.getItemDamage());
		if (meta != null) { return meta.hitEntity(itemStack, target, player); }
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack itemStack, int pass) {
		IMetaItem meta = getMeta(itemStack.getItemDamage());
		return meta != null? meta.hasEffect(pass) : false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubItems(int id, CreativeTabs tab, List subItems) {
		for (Entry<Integer, IMetaItem> entry : metaitems.entrySet())
			entry.getValue().addToCreativeList(id, entry.getKey(), subItems);
	}

	public IMetaItem getMeta(int id) {
		return metaitems.get(id);
	}

	public IMetaItem getMeta(ItemStack itemStack) {
		return getMeta(itemStack.getItemDamage());
	}

	public ItemStack newItemStack(int id) {
		return newItemStack(id, 1);
	}

	public ItemStack newItemStack(int id, int number) {
		return new ItemStack(this, number, id);
	}

	public ItemStack newItemStack(IMetaItem meta, int size) {
		for (Entry<Integer, IMetaItem> o : metaitems.entrySet()) {
			if (o.getValue().equals(meta)) { return newItemStack(o.getKey(), size); }
		}
		return null;
	}

	public ItemStack newItemStack(Metas metaenum, int number) {
		return new ItemStack(this, number, metaenum.ordinal());
	}

	public ItemStack newItemStack(Metas metaenum) {
		return new ItemStack(this, 1, metaenum.ordinal());
	}

	public static boolean isA(ItemStack stack, Metas meta) {
		return (stack.getItem() instanceof ItemGeneric) && (stack.getItemDamage() == meta.ordinal());
	}

}