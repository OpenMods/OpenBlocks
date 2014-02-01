package openblocks.client.radio;

import java.net.URL;
import java.net.URLConnection;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackListener;
import openmods.Log;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.spoledge.aacdecoder.IcyURLStreamHandler;

public class StreamPlayer extends PlaybackListener implements Runnable {

	private String streamURL;
	private Thread thread;
	private AdvancedPlayer player;

	public StreamPlayer() {}

	@Override
	public void run() {
		try {
			while (!Strings.isNullOrEmpty(streamURL)) {
				URLConnection connection = new URL(null, streamURL, new IcyURLStreamHandler()).openConnection();
				connection.connect();
				player = new AdvancedPlayer(connection.getInputStream());
				player.setPlayBackListener(this);
				player.play(); // blocks until stop()
			}
		} catch (Throwable t) {
			Log.warn(t, "Failed to play from radio stream for url %s", streamURL);
		}
		player = null;
		thread = null;
	}

	private void ensureThreadLives() {
		if (thread == null || !thread.isAlive()) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop() {
		streamURL = null;
		if (player != null) player.stop();
	}

	public void startOrReplace(String newStreamURL) {
		Preconditions.checkNotNull(newStreamURL);
		if (newStreamURL.equals(streamURL)) return;
		streamURL = newStreamURL;
		if (player != null) player.stop();
		ensureThreadLives();
	}

	public void setVolume(float f) {
		if (player != null) player.setVolume(f);
	}

	public float getVolume() {
		return player.getVolume();
	}
}