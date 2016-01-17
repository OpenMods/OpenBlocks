package openblocks.common.item;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.infobook.ICustomBookEntryProvider;
import openmods.item.IMetaItem;
import openmods.item.IMetaItemFactory;

import com.google.common.collect.ImmutableList;

public enum MetasGeneric implements IMetaItemFactory {
	gliderWing {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack();
			return new MetaGeneric("gliderwing",
					new ShapedOreRecipe(result, " sl", "sll", "lll", 's', "stickWood", 'l', Items.leather),
					new ShapedOreRecipe(result, "ls ", "lls", "lll", 's', "stickWood", 'l', Items.leather)
			);
		}
	},
	beam {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack(2);
			return new MetaGeneric("beam",
					new ShapedOreRecipe(result, "iii", "b y", "iii", 'i', "ingotIron", 'b', "dyeBlack", 'y', "dyeYellow"),
					new ShapedOreRecipe(result, "iii", "y b", "iii", 'i', "ingotIron", 'b', "dyeBlack", 'y', "dyeYellow")
			);
		}
	},
	craneEngine {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack();
			return new MetaGeneric("crane_engine",
					new ShapedOreRecipe(result, "iii", "isi", "iri", 'i', "ingotIron", 's', "stickWood", 'r', "dustRedstone")
			);
		}
	},
	craneMagnet {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack();
			return new MetaGeneric("crane_magnet",
					new ShapedOreRecipe(result, "biy", "iri", 'i', "ingotIron", 'r', "dustRedstone", 'b', "dyeBlack", 'y', "dyeYellow"),
					new ShapedOreRecipe(result, "yib", "iri", 'i', "ingotIron", 'r', "dustRedstone", 'b', "dyeBlack", 'y', "dyeYellow")
			);
		}
	},
	miracleMagnet {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack();
			ItemStack magnet = craneMagnet.newItemStack();
			return new MetaMiracleMagnet("miracle_magnet",
					new ShapedOreRecipe(result, "rer", "eme", "rer", 'r', "dustRedstone", 'e', Items.ender_pearl, 'm', magnet),
					new ShapedOreRecipe(result, "ere", "rmr", "ere", 'r', "dustRedstone", 'e', Items.ender_pearl, 'm', magnet)
			);
		}

		@Override
		public boolean isEnabled() {
			return Loader.isModLoaded(openmods.Mods.OPENPERIPHERALCORE) && Config.enableCraneTurtles;
		}
	},
	line {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack(2);
			return new MetaGeneric("line",
					new ShapedOreRecipe(result, "sss", "bbb", "sss", 's', Items.string, 'b', "slimeball")
			);
		}
	},
	mapController {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack(1);
			return new MetaGeneric("map_controller",
					new ShapedOreRecipe(result, " r ", "rgr", " r ", 'r', "dustRedstone", 'g', "ingotGold")
			);
		}
	},
	mapMemory {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack(1);
			return new MetaGeneric("map_memory",
					new ShapedOreRecipe(result, "rg", "rg", "rg", 'g', "nuggetGold", 'r', "dustRedstone")
			);
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
			return new MetaGeneric("assistant_base",
					new ShapedOreRecipe(result, "iei", "iri", 'i', "ingotIron", 'e', Items.ender_pearl, 'r', "dustRedstone")
			);
		}
	},
	unpreparedStencil {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack();
			return new MetaGeneric("unprepared_stencil",
					new ShapedOreRecipe(result, " p ", "pip", " p ", 'p', Items.paper, 'i', "ingotIron")
			);
		}
	},
	sketchingPencil {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack();
			return new MetaGeneric("sketching_pencil",
					new ShapedOreRecipe(result, "c  ", " s ", "  s", 'c', new ItemStack(Items.coal, 1, OreDictionary.WILDCARD_VALUE), 's', "stickWood")
			);
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

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public int getMeta() {
		return ordinal();
	}

	public static class DocProvider implements ICustomBookEntryProvider {

		@Override
		public Iterable<Entry> getBookEntries() {
			return ImmutableList.of(new ICustomBookEntryProvider.Entry("unprepared_stencil", MetasGeneric.unpreparedStencil.newItemStack()));
		}

	}
}