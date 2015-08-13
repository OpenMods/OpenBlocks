package openblocks;

import openblocks.api.IApiInterface;
import openblocks.api.OpenBlocksApi;
import openblocks.enchantments.flimflams.FlimFlamRegistry;
import openmods.Log;
import openmods.access.ApiProviderBase;
import openperipheral.api.ApiAccess;

public class ApiProvider extends ApiProviderBase<IApiInterface> implements openblocks.api.OpenBlocksApi.ApiProvider {

	public ApiProvider() {
		registerInstance(FlimFlamRegistry.instance);
	}

	static void setupApi() {
		try {
			OpenBlocksApi.init(new ApiProvider());
		} catch (Throwable t) {
			final String apiSource = getApiSource();
			throw new IllegalStateException(String.format("Failed to register OpenBlocks API provider (ApiAccess source: %s)", apiSource), t);
		}
	}

	private static String getApiSource() {
		try {
			return ApiAccess.class.getProtectionDomain().getCodeSource().getLocation().toString();
		} catch (Throwable t) {
			Log.severe(t, "Failed to get OpenBlocks API source");
			return "<unknown, see logs>";
		}
	}

}
