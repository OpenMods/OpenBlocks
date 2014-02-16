package openblocks.client.radio;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemTunedCrystal;
import openmods.Log;
import openmods.config.ConfigurationChange;

import org.apache.commons.lang3.StringUtils;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.*;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
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

		public RadioStation(String url, String name, Iterable<String> attributes) {
			this.url = url;
			this.name = name;
			this.attributes = attributes;
		}

		public ItemStack getStack() {
			final ItemTunedCrystal tunedCrystal = OpenBlocks.Items.tunedCrystal;
			if (stack == null && tunedCrystal != null) {
				stack = tunedCrystal.createStack(this);
			}
			return stack;
		}
	}

	public static RadioException error(String userMsg, String logMsg, Object... args) {
		Log.warn(logMsg, args);
		throw new RadioException(userMsg);
	}

	private static final String OGG_EXT = "ogg";
	private static final String MP3_EXT = "mp3";
	private static final String ERROR_EXT = "!error!";
	private static final String RESOLVING_EXT = "!resolving!";

	private static final String SOUND_ID = "openblocks.radio.";

	private static final Map<String, String> PROTOCOLS = ImmutableMap.<String, String> builder()
			.put("audio/mpeg", MP3_EXT)
			.put("audio/x-mpeg", MP3_EXT)
			.put("audio/mpeg3", MP3_EXT)
			.put("audio/x-mpeg3", MP3_EXT)
			.put("audio/ogg", OGG_EXT)
			.put("application/ogg", OGG_EXT)
			.put("audio/vorbis", OGG_EXT)
			.build();

	private final Map<String, String> streamFormats = Maps.newConcurrentMap();

	private final Set<String> usedSounds = Sets.newHashSet();

	private final Set<String> freeSounds = Sets.newHashSet();

	private int soundCounter;

	private RadioManager() {}

	public static final RadioManager instance = new RadioManager();

	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		try {
			SoundSystemConfig.setCodec(MP3_EXT, CodecMp3.class);
		} catch (Throwable t) {
			throw Throwables.propagate(t);
		}

		getRadioStations(); // preload
	}

	private List<RadioStation> stations;

	@ForgeSubscribe
	public void onReconfiguration(ConfigurationChange.Post evt) {
		if (evt.check("radio", "radioStations")) stations = null;
	}

	public List<RadioStation> getRadioStations() {
		if (stations == null) {
			ImmutableList.Builder<RadioStation> stations = ImmutableList.builder();
			List<String> urls = Lists.newArrayList();
			for (String stationDesc : Config.radioStations) {
				if (stationDesc.startsWith("\"") && stationDesc.endsWith("\"")) stationDesc = stationDesc.substring(1, stationDesc.length() - 1);
				stationDesc = StringUtils.strip(stationDesc);

				List<String> fields = ImmutableList.copyOf(Splitter.on(';').split(stationDesc));
				Preconditions.checkState(fields.size() > 0 && fields.size() <= 3, "Invalid radio station descripion: %s", stationDesc);

				String url = fields.get(0);
				String name = (fields.size() > 1)? fields.get(1) : "";
				Iterable<String> attributes = (fields.size() > 2)? Splitter.on(",").split(fields.get(2)) : ImmutableList.<String> of();

				stations.add(new RadioStation(url, name, attributes));
				urls.add(url);
			}
			if (FMLCommonHandler.instance().getSide() == Side.CLIENT) RadioManager.instance.preloadStreams(urls);
			this.stations = stations.build();
		}
		return stations;
	}

	@ForgeSubscribe
	public void onWorldUnload(WorldEvent.Unload evt) {
		if (evt.world.isRemote) {
			for (String soundId : ImmutableList.copyOf(usedSounds))
				stopPlaying(soundId);

			AutoConnectingStreamHandler.disconnectAll();
		}
	}

	private synchronized String allocateNewName() {
		String name = null;

		if (freeSounds.isEmpty()) {
			if (usedSounds.size() < Config.maxRadioSources) name = SOUND_ID + soundCounter++;
		} else {
			Iterator<String> it = freeSounds.iterator();
			name = it.next();
			it.remove();
		}

		if (name != null) usedSounds.add(name);
		return name;
	}

	private synchronized void releaseName(String name) {
		if (usedSounds.remove(name)) freeSounds.add(name);
	}

	public String startPlaying(String soundId, String url, float x, float y, float z) {
		if (soundId == null) soundId = allocateNewName();
		if (soundId == null) throw error("openblocks.misc.radio.too_many", "No resources to play %s, aborting", url);

		try {
			final Minecraft mc = Minecraft.getMinecraft();
			final SoundManager sndManager = mc.sndManager;
			final SoundSystem sndSystem = sndManager.sndSystem;

			if (sndSystem == null || mc.gameSettings.soundVolume == 0.0F) throw new RadioException("openblocks.misc.radio.muted");

			if (sndSystem.playing(soundId)) {
				sndSystem.stop(soundId);
				sndSystem.removeSource(soundId);
			}

			final String ext = resolveStreamExt(url);

			if (ERROR_EXT.equals(ext)) throw error("openblocks.misc.radio.invalid_stream", "Invalid data in stream %s (soundId : %s), aborting", url, soundId);

			if (RESOLVING_EXT.equals(ext)) throw error("openblocks.misc.radio.not_ready", "Stream %s (soundId : %s) not yet resolved, aborting", url, soundId);

			try {
				URL realUrl = new URL(null, url, AutoConnectingStreamHandler.createManaged(soundId));
				String dummyFilename = "radio_dummy." + ext;
				sndSystem.newStreamingSource(false, soundId, realUrl, dummyFilename, false, x, y, z, SoundSystemConfig.ATTENUATION_LINEAR, 32);
				sndSystem.play(soundId);
			} catch (Throwable t) {
				Log.warn(t, "Exception during opening url %s (soundId: %s)", url, soundId);
				throw new RadioException("openblocks.misc.radio.unknown_error");
			}

			Log.info("Started playing %s (id: %s)", url, soundId);
			return soundId;
		} catch (RadioException e) {
			releaseName(soundId);
			throw e;
		}
	}

	public void stopPlaying(String soundId) {
		final Minecraft mc = Minecraft.getMinecraft();
		final SoundManager sndManager = mc.sndManager;
		final SoundSystem sndSystem = sndManager.sndSystem;

		if (sndSystem != null && sndSystem.playing(soundId)) {
			sndSystem.stop(soundId);
		}

		AutoConnectingStreamHandler.disconnectManaged(soundId);
		releaseName(soundId);
	}

	private void resolveStreamType(final String url) {
		streamFormats.put(url, RESOLVING_EXT);
		final HttpURLConnection connection;
		try {
			URL realUrl = new URL(null, url, new AutoConnectingStreamHandler());
			connection = (HttpURLConnection)realUrl.openConnection();
		} catch (Throwable t) {
			Log.warn(t, "Exception during opening url %s", url);
			streamFormats.put(url, ERROR_EXT);
			return;
		}

		try {
			final String contentType = connection.getContentType();
			String ext = PROTOCOLS.get(contentType);

			if (ext == null) {
				ext = ERROR_EXT;
				Log.warn("Unknown content type '%s' in url '%s', aborting", contentType, url);
			}

			streamFormats.put(url, ext);
		} catch (Throwable t) {
			Log.warn(t, "Exception during opening url %s", url);
		} finally {
			connection.disconnect();
		}
	}

	public void preloadStreams(final Collection<String> urls) {
		if (urls.isEmpty()) return;
		final Thread th = new Thread() {
			@Override
			public void run() {
				for (String url : urls) {
					Log.info("Preloading stream: %s", url);
					resolveStreamType(url);
					Log.info("Finished preloading stream: %s", url);
				}
			}
		};

		th.setDaemon(true);
		th.start();
	}

	private String resolveStreamExt(final String url) {
		final String ext = streamFormats.get(url);
		if (ext != null) return ext;

		final Thread th = new Thread() {
			@Override
			public void run() {
				resolveStreamType(url);
			}
		};

		th.setDaemon(true);
		th.start();
		try {
			th.join(750); // seems to be reasonable delay
			return streamFormats.get(url);
		} catch (InterruptedException e) {
			Log.warn(e, "Thread interrupted!");
			streamFormats.put(url, ERROR_EXT);
			return ERROR_EXT;
		}
	}

	private static ItemStack randomItemAmount(Random random, Item item, int min, int max) {
		int amount = random.nextInt(max - min) + min;
		return new ItemStack(item, amount);
	}

	private static ItemStack randomEmeralds(Random random, int min, int max) {
		return randomItemAmount(random, Item.emerald, min, max);
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
							randomItemAmount(random, Item.redstone, 4, 20),
							st.getStack().copy()));
		}

		if (random.nextFloat() > 0.5) recipeList.addToListWithCheck(
				new MerchantRecipe(
						randomEmeralds(random, 1, 2),
						new ItemStack(Block.music)));

		if (random.nextFloat() > 0.25 || recipeList.isEmpty()) recipeList.addToListWithCheck(
				new MerchantRecipe(
						randomEmeralds(random, 3, 7),
						new ItemStack(Block.jukebox)));
	}
}
