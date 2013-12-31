package openblocks.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Items;
import openmods.item.IMetaItem;
import cpw.mods.fml.common.Loader;

public enum MetasGeneric {
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
			return Loader.isModLoaded(openmods.Mods.COMPUTERCRAFT);
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
			ItemStack result = newItemStack(1);
			return new MetaGeneric("map_controller", new ShapedOreRecipe(result, " r ", "rgr", " r ", 'r', Item.redstone, 'g', Item.ingotGold));
		}
	},
	mapMemory {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack(1);
			return new MetaGeneric("map_memory", new ShapedOreRecipe(result, "rg", "rg", "rg", 'g', Item.goldNugget, 'r', Item.redstone));
		}
	},
	/**
	 * Deprecated. Moved to ItemCursor
	 * Can't remove it from here because it'll re-index everyones items..
	 * I guess the next time we add a meta, replace this one.
	 */
	cursor {

		@Override
		public boolean isEnabled() {
			return false;
		}

		@Override
		public IMetaItem createMetaItem() {
			return null;
		}
	},
	assistantBase {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack();
			return new MetaGeneric("assistant_base", new ShapedOreRecipe(result, "iei", "iri", 'i', Item.ingotIron, 'e', Item.enderPearl, 'r', Item.redstone));
		}
	},
	unpreparedStencil {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack();
			return new MetaGeneric("unprepared_stencil", new ShapedOreRecipe(result, " p ", "pip", " p ", 'p', Item.paper, 'i', Item.ingotIron));
		}
	},
	sketchingPencil {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack();
			return new MetaGeneric("sketching_pencil", new ShapedOreRecipe(result, "c  ", " s ", "  s", 'c', Item.coal, 's', Item.stick),
					new ShapedOreRecipe(result, "c  ", " s ", "  s", 'c', new ItemStack(Item.coal, 1, 1), 's', Item.stick));
		}
	};

	public ItemStack newItemStack(int size) {
		return new ItemStack(OpenBlocks.Items.generic, size, ordinal());
	}

	public ItemStack newItemStack() {
		return new ItemStack(OpenBlocks.Items.generic, 1, ordinal());
	}

	public boolean isA(ItemStack stack) {
		return (stack.getItem() instanceof ItemOBGeneric) && (stack.getItemDamage() == ordinal());
	}

	protected abstract IMetaItem createMetaItem();

	protected boolean isEnabled() {
		return true;
	}

	public static void registerItems() {
		for (MetasGeneric m : values())
			if (m.isEnabled()) Items.generic.registerItem(m.ordinal(), m.createMetaItem());
	}
}