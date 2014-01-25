package openblocks.client.radio;

import java.net.URL;
import java.net.URLConnection;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import openmods.Log;

public class StreamPlayer extends PlaybackListener implements Runnable {

	private final String streamURL;
	private final Thread thread;
	private AdvancedPlayer player;

	public StreamPlayer(String mp3url) {
		streamURL = mp3url;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		try {
			URLConnection connection = new URL(streamURL).openConnection();
			connection.connect();
			player = new AdvancedPlayer(connection.getInputStream());
			player.setPlayBackListener(this);
			player.play();
		} catch (Throwable t) {
			Log.warn(t, "Failed to play from radio stream for url %s", streamURL);
		}
	}

	public void stop() {
		if (player != null) player.stop();
	}

	@Override
	public void playbackStarted(PlaybackEvent evt) {}

	@Override
	public void playbackFinished(PlaybackEvent evt) {}

	public void setVolume(float f) {
		if (player != null) {
			player.setVolume(f);
		}
	}

	public float getVolume() {
		return player.getVolume();
	}
}