package openblocks.common.item;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.infobook.ICustomBookEntryProvider;
import openmods.item.IMetaItem;
import openmods.item.IMetaItemFactory;

public enum MetasGeneric implements IMetaItemFactory {

	gliderWing {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaGeneric("glider_wing");
		}
	},
	beam {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaGeneric("beam");
		}
	},
	craneEngine {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaGeneric("crane_engine");
		}
	},
	craneMagnet {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaGeneric("crane_magnet");
		}
	},
	miracleMagnet {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaMiracleMagnet("miracle_magnet");
		}

		@Override
		public boolean isEnabled() {
			return Loader.isModLoaded(openmods.Mods.OPENPERIPHERALCORE) && Config.enableCraneTurtles;
		}
	},
	line {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaGeneric("line");
		}
	},
	mapController {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaGeneric("map_controller");
		}
	},
	mapMemory {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaGeneric("map_memory");
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
			return new MetaGeneric("assistant_base");
		}
	},
	unpreparedStencil {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaGeneric("unprepared_stencil");
		}
	},
	sketchingPencil {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaGeneric("sketching_pencil");
		}
	};
	@Nonnull
	public ItemStack newItemStack(int size) {
		return new ItemStack(OpenBlocks.Items.generic, size, ordinal());
	}

	@Nonnull
	public ItemStack newItemStack() {
		return newItemStack(1);
	}

	public boolean isA(@Nonnull ItemStack stack) {
		return (stack.getItem() instanceof ItemOBGeneric) && (stack.getItemDamage() == ordinal());
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public boolean isAvailable() {
		return OpenBlocks.Items.generic != null && isEnabled();
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