package openblocks.client.radio;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import openblocks.Config;
import openmods.Log;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

import com.google.common.base.Throwables;
import com.google.common.collect.*;

public class RadioManager {

	private static final String OGG_EXT = "ogg";
	private static final String MP3_EXT = "mp3";
	private static final String ERROR_EXT = "error!";

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

	private final Map<String, String> streamFormats = Maps.newHashMap();

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
		if (soundId == null) {
			Log.warn("No resources to play %s, aborting", url);
		} else {
			if (reallyStartPlaying(soundId, url, x, y, z)) Log.info("Started playing %s (id: %s)", url, soundId);
			else {
				releaseName(soundId);
				return null;
			}
		}

		return soundId;
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

	private boolean reallyStartPlaying(String soundId, String url, float x, float y, float z) {
		final Minecraft mc = Minecraft.getMinecraft();
		final SoundManager sndManager = mc.sndManager;
		final SoundSystem sndSystem = sndManager.sndSystem;

		if (sndSystem == null || mc.gameSettings.soundVolume == 0.0F) return false;

		if (sndSystem.playing(soundId)) {
			sndSystem.stop(soundId);
			sndSystem.removeSource(soundId);
		}

		String ext = identifyStream(url);

		if (ext.equals(ERROR_EXT)) {
			Log.info("Invalid data in stream %s (soundId : %s), aborting", url, soundId);
			return false;
		}

		try {
			URL realUrl = new URL(null, url, AutoConnectingStreamHandler.createManaged(soundId));
			String dummyFilename = "radio_dummy." + ext;
			sndSystem.newStreamingSource(false, soundId, realUrl, dummyFilename, false, x, y, z, SoundSystemConfig.ATTENUATION_LINEAR, 32);
			sndSystem.play(soundId);
		} catch (Throwable t) {
			Log.warn(t, "Exception during opening url %s (soundId: %s)", url, soundId);
			return false;
		}

		return true;
	}

	private String identifyStream(String url) {
		String result = streamFormats.get(url);
		if (result == null) {
			result = getStreamExt(url);
			streamFormats.put(url, result);
		}
		return result;
	}

	private static String getStreamExt(String url) {
		final HttpURLConnection connection;
		try {
			URL realUrl = new URL(null, url, new AutoConnectingStreamHandler());
			connection = (HttpURLConnection)realUrl.openConnection();
		} catch (Throwable t) {
			Log.warn(t, "Exception during opening url %s", url);
			return ERROR_EXT;
		}

		try {
			String contentType = connection.getContentType();
			String ext = PROTOCOLS.get(contentType);

			if (ext != null) return ext;

			Log.warn("Unknown content type '%s' in url '%s', aborting", contentType, url);
			connection.disconnect();
		} catch (Throwable t) {
			Log.warn(t, "Exception during opening url %s", url);
		} finally {
			connection.disconnect();
		}

		return ERROR_EXT;
	}

}
