package openblocks.client.radio;

import java.net.URL;
import java.net.URLConnection;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class StreamPlayer extends PlaybackListener implements Runnable {

	private String streamURL;
	private AdvancedPlayer player;
	private Thread thread;

	public StreamPlayer(String mp3url) {
		try {
			streamURL = mp3url;
			thread = new Thread(this);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			URLConnection connection = new URL(streamURL).openConnection();
			connection.connect();
			player = new AdvancedPlayer(connection.getInputStream());
			player.setPlayBackListener(this);
			player.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		if (player != null) {
			player.stop();
		}
	}

	@Override
	public void playbackStarted(PlaybackEvent evt) {}

	@Override
	public void playbackFinished(PlaybackEvent evt) {}

	public boolean isPlaying() {
		return thread.isAlive();
	}

	public void setVolume(float f) {
		if (player != null) {
			player.setVolume(f);
		}
	}

	public float getVolume() {
		return player.getVolume();
	}
}