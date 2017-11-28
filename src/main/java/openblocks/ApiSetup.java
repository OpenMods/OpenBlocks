package openblocks;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import openblocks.api.ApiHolder;
import openblocks.api.IApiInterface;
import openblocks.api.OpenBlocksApi;
import openblocks.enchantments.flimflams.FlimFlamRegistry;
import openmods.Log;
import openmods.access.ApiFactory;
import openmods.access.ApiProviderBase;
import openmods.access.ApiProviderRegistry;

public class ApiSetup {

	private static class ApiProviderAdapter extends ApiProviderBase<IApiInterface> implements openblocks.api.OpenBlocksApi.ApiProvider {
		public ApiProviderAdapter(ApiProviderRegistry<IApiInterface> apiRegistry) {
			super(apiRegistry);
		}
	}

	private final ApiProviderRegistry<IApiInterface> registry = new ApiProviderRegistry<>(IApiInterface.class);

	ApiSetup() {}

	public void setupApis() {
		registry.registerInstance(FlimFlamRegistry.instance);
		registry.freeze();
	}

	public void installHolderAccess(ASMDataTable table) {
		ApiFactory.instance.createApi(ApiHolder.class, IApiInterface.class, table, registry);
	}

	void injectProvider() {
		try {
			OpenBlocksApi.init(new ApiProviderAdapter(registry));
		} catch (Throwable t) {
			final String apiSource = getApiSource();
			throw new IllegalStateException(String.format("Failed to register OpenBlocks API provider (ApiAccess source: %s)", apiSource), t);
		}
	}

	private static String getApiSource() {
		try {
			return openblocks.api.OpenBlocksApi.ApiProvider.class.getProtectionDomain().getCodeSource().getLocation().toString();
		} catch (Throwable t) {
			Log.severe(t, "Failed to get OpenBlocks API source");
			return "<unknown, see logs>";
		}
	}

}
