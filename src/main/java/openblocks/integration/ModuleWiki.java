package openblocks.integration;

import static openmods.integration.Conditions.all;
import static openmods.integration.Conditions.modLoaded;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import openmods.Log;
import openmods.Mods;
import openmods.conditions.ICondition;
import openmods.integration.IntegrationModule;

public final class ModuleWiki extends IntegrationModule {

	public ModuleWiki() {

		super(all(
				modLoaded(Mods.IGW),
				new IsClient()));
	}

	@Override
	public String name() {

		return "OpenBlocks integration for IGW Mod";
	}

	@Override
	public void load() {

		try {

			final Class<?> WikiRegistry = Class.forName("igwmod.api.WikiRegistry");
			final Class<?> IWikiTab = Class.forName("igwmod.gui.tabs.IWikiTab");
			final Method registerWikiTab = WikiRegistry.getDeclaredMethod("registerWikiTab",
					IWikiTab);

			final OpenBlocksWikiTab tab = new OpenBlocksWikiTab();

			registerWikiTab.invoke(WikiRegistry.newInstance(), tab);
		}
		catch (ClassNotFoundException e) {

			Log.log(Level.WARN, e, "Attempted to access invalid IGW Mod class");
			Log.log(Level.WARN, "Probably the previous checks failed");
		}
		catch (NoSuchMethodException e) {

			Log.log(Level.WARN, e, "Attempted to access invalid IGW Mod method");
			Log.log(Level.WARN, "This should never happen");
		}
		catch (SecurityException e) {

			Log.log(Level.WARN, e, "Attempted to access IGW Mod class without permission");
			Log.log(Level.WARN, "This should never happen");
		}
		catch (IllegalAccessException e) {

			Log.log(Level.WARN, e, "Attempted to access IGW Mod method not declared as"
					+ "public");
			Log.log(Level.WARN, "This should never happen");
		}
		catch (IllegalArgumentException e) {

			Log.log(Level.WARN, e, "Attempted to pass to IGW Mod method an invalid"
					+ "parameter");
			Log.log(Level.WARN, "This should never happen");
		}
		catch (InvocationTargetException e) {

			Log.log(Level.WARN, e, "Invoked method has thrown an exception");
		}
		catch (InstantiationException e) {

			Log.log(Level.WARN, e, "IGW Mod class couldn't be instantiated");
			Log.log(Level.WARN, "This should never happen");
		}
	}

	private static class IsClient implements ICondition {

		@Override
		public boolean check() {

			return FMLCommonHandler.instance().getEffectiveSide().isClient();
		}
	}
}
