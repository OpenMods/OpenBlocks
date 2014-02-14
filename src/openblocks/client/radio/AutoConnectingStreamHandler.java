package openblocks.client.radio;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class AutoConnectingStreamHandler extends URLStreamHandler {

	/*
	 * Rationale behind this class:
	 * 
	 * 1. SoundSystem does not properly open streams. Works for local files, but
	 * breaks for net streams.
	 * 
	 * 2. If they forgot about opening, why would they remember about closing?
	 * That's why we have to keep list of opened streams and have some shutdown
	 * code.
	 */

	private static final Map<String, AutoConnectingStreamHandler> managed = Maps.newConcurrentMap();
	private final Multimap<URL, WeakReference<HttpURLConnection>> connections = HashMultimap.create();

	public static AutoConnectingStreamHandler createManaged(String id) {
		AutoConnectingStreamHandler result = managed.get(id);
		if (result == null) {
			result = new AutoConnectingStreamHandler();
			managed.put(id, result);
		}
		return result;
	}

	public static void disconnectManaged(String id) {
		AutoConnectingStreamHandler c = managed.remove(id);
		if (c != null) c.disconnect();
	}

	public static void disconnectAll() {
		for (AutoConnectingStreamHandler c : managed.values())
			c.disconnect();

		managed.clear();
	}

	public void disconnect() {
		synchronized (connections) {
			for (WeakReference<HttpURLConnection> r : connections.values()) {
				HttpURLConnection c = r.get();
				if (c != null) c.disconnect();
			}

			connections.clear();
		}
	}

	@Override
	protected HttpURLConnection openConnection(URL url) throws IOException {
		HttpURLConnection connection = new IcyURLConnection(url);

		connection.connect();
		int responseCode = connection.getResponseCode();
		if (responseCode != 200) {
			connection.disconnect();
			throw new IOException(String.format("Response code %d != 200, aborting", responseCode));
		}

		synchronized (connections) {
			connections.put(url, new WeakReference<HttpURLConnection>(connection));
		}

		return connection;
	}

	@Override
	protected int getDefaultPort() {
		return 80;
	}
}
