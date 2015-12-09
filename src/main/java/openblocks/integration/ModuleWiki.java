package openblocks.integration;

import static openmods.integration.Conditions.all;
import static openmods.integration.Conditions.modLoaded;
import cpw.mods.fml.common.FMLCommonHandler;
import igwmod.api.WikiRegistry;
import openmods.Mods;
import openmods.conditions.ICondition;
import openmods.integration.IntegrationModule;

/**
 * Adds integration for IGW Mod.
 * 
 * <p>Practically, this integration module registers a custom
 * wiki tab which allows for blocks and items to be rendered
 * and for a brief list of pages to be shown. For more
 * information, refer to {@link OpenBlocksWikiTab}.</p>
 * 
 * @author TheSilkMiner
 * 
 * @since 1.4.5
 *
 */
public final class ModuleWiki extends IntegrationModule {

	/**
	 * A simple constructor.
	 * 
	 * @since 1.4.5
	 */
	public ModuleWiki() {
		
		super(all(
				modLoaded(Mods.IGW),
				new IsClient()));
		// Need to add IGW to openmods.Mods
		// public static final String IGW = "IGWMod";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.4.5 in OpenBlocks
	 */
	@Override
	public String name() {
		
		return "OpenBlocks integration for IGW Mod";
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.4.5 in OpenBlocks
	 */
	@Override
	public void load() {
		
		LoadHack.load();
	}
	
	/**
	 * A class which triggers the loading method avoiding non-existent
	 * classes to mess up the loading process.
	 * 
	 * @author TheSilkMiner
	 * 
	 * @since 1.4.5
	 *
	 */
	private static class LoadHack {
		
		/**
		 * The loading method.
		 * 
		 * @since 1.4.5
		 */
		private static final void load() {
			
			WikiRegistry.registerWikiTab(new OpenBlocksWikiTab());
		}
	}
	
	/**
	 * A check which controls if we are running on a client environment.
	 * 
	 * @author TheSilkMiner
	 * 
	 * @since 1.4.5
	 *
	 */
	private static class IsClient implements ICondition {

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.4.5 in OpenBlocks
		 */
		@Override
		public boolean check() {
			
			return FMLCommonHandler.instance().getEffectiveSide().isClient();
		}
	}
}
