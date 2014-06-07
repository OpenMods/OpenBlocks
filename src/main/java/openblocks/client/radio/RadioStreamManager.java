package openblocks.client.radio;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import net.minecraft.util.ResourceLocation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class RadioStreamManager {

	private final Multimap<ResourceLocation, WeakReference<HttpURLConnection>> connections = HashMultimap.create();

	public void disconnect(ResourceLocation id) {
		synchronized (connections) {
			for (WeakReference<HttpURLConnection> c : connections.removeAll(id)) {
				HttpURLConnection connection = c.get();
				if (connection != null) connection.disconnect();
			}
		}
	}

	public void disconnectAll() {
		for (WeakReference<HttpURLConnection> c : connections.values()) {
			HttpURLConnection connection = c.get();
			if (connection != null) connection.disconnect();
		}

		connections.clear();
	}

	public InputStream getStream(ResourceLocation location, String addr) throws IOException {
		URL url = new URL(addr);
		HttpURLConnection connection = new IcyURLConnection(url);

		connection.setInstanceFollowRedirects(true);
		connection.connect();
		int responseCode = connection.getResponseCode();
		if (responseCode != 200) {
			connection.disconnect();
			throw new IOException(String.format("Response code %d != 200, aborting", responseCode));
		}

		synchronized (connections) {
			connections.put(location, new WeakReference<HttpURLConnection>(connection));
		}

		return new SafeInputStream(connection.getInputStream());
	}
}
