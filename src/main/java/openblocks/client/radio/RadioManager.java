package openblocks.client.radio;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemTunedCrystal;
import openmods.Log;
import openmods.config.ConfigurationChange;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.*;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;

public class RadioManager implements IVillageTradeHandler {

	public static class RadioException extends RuntimeException {
		private static final long serialVersionUID = 1026197667827191392L;

		public RadioException(String message) {
			super(message);
		}
	}

	public static class RadioStation {
		public final String url;
		public final String name;
		public final Iterable<String> attributes;
		private ItemStack stack;

		private RadioStation(String url, String name, Iterable<String> attributes) {
			this.url = instance.updateURL(url);
			this.name = name;
			this.attributes = attributes;
		}

		public ItemStack getStack() {
			final ItemTunedCrystal tunedCrystal = OpenBlocks.Items.tunedCrystal;
			if (stack == null && tunedCrystal != null) {
				stack = tunedCrystal.createStack(url, name, attributes);
			}
			return stack;
		}
	}

	private class RadioResourcePack implements IResourcePack {

		public static final String RESOURCE_PACK_ID = "OpenBlocks-Radio";

		@Override
		public InputStream getInputStream(ResourceLocation location) throws IOException {
			String url = locationToUrl.get(location);

			if (url == null) return null;

			try {
				return streamManager.getStream(location, url);
			} catch (IOException e) {
				Log.warn("Failed to open url %s (location %s)", location, url);
				return null;
			}
		}

		@Override
		public boolean resourceExists(ResourceLocation location) {
			return locationToUrl.containsKey(location);
		}

		@Override
		@SuppressWarnings("rawtypes")
		public Set getResourceDomains() {
			return ImmutableSet.of(RESOURCE_PACK_ID);
		}

		@Override
		public IMetadataSection getPackMetadata(IMetadataSerializer var1, String var2) throws IOException {
			return null;
		}

		@Override
		public BufferedImage getPackImage() throws IOException {
			return null;
		}

		@Override
		public String getPackName() {
			return RESOURCE_PACK_ID;
		}

	}

	public static final String OGG_EXT = "ogg";

	private static final Map<String, String> PROTOCOLS = Maps.newHashMap();

	private static final Map<String, String> REPLACEMENTS = Maps.newHashMap();

	private final RadioStreamManager streamManager = new RadioStreamManager();

	static {
		PROTOCOLS.put("audio/ogg", OGG_EXT);
		PROTOCOLS.put("application/ogg", OGG_EXT);
		PROTOCOLS.put("audio/vorbis", OGG_EXT);
	}

	public static RadioException error(String userMsg, String logMsg, Object... args) {
		Log.warn(logMsg, args);
		throw new RadioException(userMsg);
	}

	private final Map<String, UrlMeta> urlMeta = Maps.newHashMap();

	private final BiMap<ResourceLocation, String> locationToUrl = HashBiMap.create();

	private int locationCounter = 0;

	private RadioManager() {}

	public static final RadioManager instance = new RadioManager();

	public void readConfigs() {
		for (String entry : Config.derpList) {
			String[] parts = entry.split("\\s+");
			Preconditions.checkArgument(parts.length == 2, "Invalid format in replacementList");
			REPLACEMENTS.put(parts[0], parts[1]);
		}
	}

	public void init() {
		reloadStations();

		// TODO: add SRG name once we verify it actually works
		List<IResourcePack> packs = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks");
		packs.add(new RadioResourcePack());
	}

	public String updateURL(String url) {
		for (Map.Entry<String, String> e : REPLACEMENTS.entrySet())
			if (url.equals(e.getKey())) return e.getValue();

		return url;
	}

	private List<RadioStation> stations;

	public static void addCodecsInfo(NBTTagCompound codecs) {
		NBTTagList data = codecs.getTagList("data", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < data.tagCount(); i++) {
			NBTTagCompound tag = data.getCompoundTagAt(i);
			String ext = tag.getString("ext");
			String mime = tag.getString("mime");
			PROTOCOLS.put(mime, ext);
		}
	}

	@SubscribeEvent
	public void onReconfiguration(ConfigurationChange.Post evt) {
		if (evt.check("radio", "radioStations")) stations = null;
	}

	public List<RadioStation> getRadioStations() {
		if (stations == null) reloadStations();
		return stations;
	}

	private void reloadStations() {
		locationToUrl.clear();
		ImmutableList.Builder<RadioStation> stations = ImmutableList.builder();
		List<String> urls = Lists.newArrayList();
		for (String stationDesc : Config.radioStations) {
			List<String> fields = ImmutableList.copyOf(Splitter.on(';').split(stationDesc));
			Preconditions.checkState(fields.size() > 0 && fields.size() <= 3, "Invalid radio station descripion: %s", stationDesc);

			String url = updateURL(fields.get(0));
			String name = (fields.size() > 1)? fields.get(1) : "";
			Iterable<String> attributes = (fields.size() > 2)? Splitter.on(",").split(fields.get(2)) : ImmutableList.<String> of();

			final RadioStation station = new RadioStation(url, name, attributes);
			stations.add(station);
			urls.add(url);
		}
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) preloadStreams(urls);
		this.stations = stations.build();
	}

	private ResourceLocation getResourceForUrl(String url) {
		ResourceLocation location = locationToUrl.inverse().get(url);

		if (location == null) {
			String locationName = "radio-" + locationCounter++;
			location = new ResourceLocation(RadioResourcePack.RESOURCE_PACK_ID, locationName);
			locationToUrl.put(location, url);
		}

		return location;
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload evt) {
		if (evt.world.isRemote) streamManager.disconnectAll();
	}

	public ISound startPlaying(String url, float x, float y, float z, float volume) {
		final UrlMeta ext = resolveStreamExt(url);

		UrlMeta.Status status = ext.getStatus();

		if (!status.valid) throw error(status.message, "Error in stream %s: %s", url, status);

		String mimeType = ext.getContentType();
		String fileExt = PROTOCOLS.get(mimeType);

		if (Strings.isNullOrEmpty(fileExt)) throw error("openblocks.misc.radio.unknown_stream_type", "Unknown MIME type %s in stream %s", mimeType, url);

		ResourceLocation dummyLocation = getResourceForUrl(url);
		ISound sound = new PositionedSoundRecord(dummyLocation, volume, 1.0f, x, y, z);

		try {
			Minecraft.getMinecraft().getSoundHandler().playSound(sound);
		} catch (Throwable t) {
			Log.warn(t, "Exception during opening url %s", url);
			throw new RadioException("openblocks.misc.radio.unknown_error");
		}

		Log.info("Started playing %s", url);
		return sound;
	}

	public void stopPlaying(ISound sound) {
		Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
		streamManager.disconnect(sound.getPositionedSoundLocation());
	}

	public void preloadStreams(final Collection<String> urls) {
		if (urls.isEmpty()) return;
		final Thread th = new Thread() {
			@Override
			public void run() {
				for (String url : urls) {
					UrlMeta data = null;
					synchronized (urlMeta) {
						if (!urlMeta.containsKey(url)) {
							data = new UrlMeta(url);
							urlMeta.put(url, data);
						}
					}

					if (data != null) {
						Log.info("Preloading stream: %s", url);
						data.resolve();
						Log.info("Finished preloading stream: %s, result %s, type %s", url, data.getStatus(), data.getContentType());
					}
				}
			}
		};

		th.setDaemon(true);
		th.start();
	}

	private UrlMeta resolveStreamExt(final String url) {
		UrlMeta data;
		synchronized (urlMeta) {
			data = urlMeta.get(url);
			if (data != null) return data;
			data = new UrlMeta(url);
			urlMeta.put(url, data);
		}

		final UrlMeta closure = data;

		final Thread th = new Thread() {
			@Override
			public void run() {
				closure.resolve();
			}
		};

		th.setDaemon(true);
		th.start();
		try {
			th.join(750); // seems to be reasonable delay
		} catch (InterruptedException e) {
			Log.warn(e, "Thread interrupted!");
			data.markAsFailed();
		}
		return data;
	}

	private static ItemStack randomItemAmount(Random random, Item item, int min, int max) {
		int amount = random.nextInt(max - min) + min;
		return new ItemStack(item, amount);
	}

	private static ItemStack randomEmeralds(Random random, int min, int max) {
		return randomItemAmount(random, Items.emerald, min, max);
	}

	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {

		if (Config.radioVillagerRecords) {
			for (ItemStack record : OreDictionary.getOres("record"))
				if (random.nextFloat() < 0.01) recipeList.addToListWithCheck(
						new MerchantRecipe(
								randomEmeralds(random, 7, 15),
								record));
		}

		for (RadioStation st : getRadioStations()) {
			if (random.nextFloat() < 0.2) recipeList.addToListWithCheck(
					new MerchantRecipe(
							randomEmeralds(random, 3, 7),
							randomItemAmount(random, Items.redstone, 4, 20),
							st.getStack().copy()));
		}

		if (random.nextFloat() > 0.5) recipeList.addToListWithCheck(
				new MerchantRecipe(
						randomEmeralds(random, 1, 2),
						new ItemStack(Blocks.noteblock)));

		if (random.nextFloat() > 0.25 || recipeList.isEmpty()) recipeList.addToListWithCheck(
				new MerchantRecipe(
						randomEmeralds(random, 3, 7),
						new ItemStack(Blocks.jukebox)));
	}
}
