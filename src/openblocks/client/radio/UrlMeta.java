package openblocks.client.radio;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import openmods.Log;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

public class UrlMeta {
	private static final String MIME_M3U_APP = "application/x-mpegurl";
	private static final String MIME_M3U_AUDIO = "audio/x-mpegurl";

	private static final String MIME_PLS = "audio/x-scpls";

	public static final String OGG_EXT = "ogg";
	public static final String MP3_EXT = "mp3";

	private static final Map<String, String> PROTOCOLS = ImmutableMap.<String, String> builder()
			.put("audio/mpeg", MP3_EXT)
			.put("audio/x-mpeg", MP3_EXT)
			.put("audio/mpeg3", MP3_EXT)
			.put("audio/x-mpeg3", MP3_EXT)
			.put("audio/ogg", OGG_EXT)
			.put("application/ogg", OGG_EXT)
			.put("audio/vorbis", OGG_EXT)
			.build();

	private boolean isResolved;
	private boolean isValid;
	private String url;
	private String extension;

	public UrlMeta(String url) {
		this.url = url;
	}

	public boolean isResolved() {
		return isResolved;
	}

	public boolean isValid() {
		return isResolved && isValid;
	}

	public String getUrl() {
		return url;
	}

	public String getExtension() {
		return extension;
	}

	public void markAsFailed() {
		isResolved = true;
		isValid = false;
	}

	public void resolve() {
		if (!isResolved) {
			try {
				isValid = resolveImpl();
			} finally {
				isResolved = true;
			}
		}
	}

	private boolean resolveImpl() {
		Log.info("Resolving URL %s", url);
		HttpURLConnection connection = null;
		try {
			while (true) {
				URL url = new URL(null, this.url, new IcyConnectionHandler());
				connection = (HttpURLConnection)url.openConnection();
				connection.setRequestProperty("User-Agent", "OpenMods/0.0 Minecraft/1.6.4");
				connection.connect();

				final int responseCode = connection.getResponseCode();
				switch (responseCode) {
					case 200:
						return processStream(connection);
					case 301:
					case 302:
					case 303:
					case 307: {
						final String redirectedUrl = connection.getHeaderField("location");
						Log.fine("Redirection to URL %s (code: %d)", redirectedUrl, responseCode);
						connection.disconnect();
						this.url = redirectedUrl;
						continue;
					}
					default:
						Log.warn("Invalid status code from url %s: %d", url, responseCode);
						return false;
				}
			}
		} catch (Throwable t) {
			Log.warn(t, "Exception during opening url %s", url);
		} finally {
			if (connection != null) connection.disconnect();
		}
		return false;
	}

	private boolean processStream(HttpURLConnection connection) {
		String contentType = connection.getContentType();
		Log.fine("URL %s has content type %s", url, contentType);
		if (contentType.equals(MIME_PLS)) return parsePLS(connection);
		else if (contentType.equals(MIME_M3U_APP) || contentType.equals(MIME_M3U_AUDIO)) return parseM3U(connection);

		String ext = PROTOCOLS.get(contentType);
		if (ext == null) {
			Log.warn("Unknown content type '%s' in url '%s', aborting", contentType, url);
			return false;
		} else {
			Log.fine("Resolved URL %s type: %s", url, ext);
			extension = ext;
			return true;
		}
	}

	private boolean parsePLS(HttpURLConnection connection) {
		Log.fine("Parsing PLS file at URL %s", url);
		Scanner scanner = null;
		try {
			Reader reader = new InputStreamReader(connection.getInputStream());
			scanner = new Scanner(reader);
			String header = scanner.nextLine();
			if (!header.equals("[playlist]")) {
				Log.warn("Invalid header '%s' in pls file %s", header, url);
				return false;
			}

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				Iterable<String> split = Splitter.on('=').split(line);
				Iterator<String> it = split.iterator();
				String name = it.next();
				if (name.startsWith("File")) {
					String value = it.next();
					Log.fine("Trying playlist %s entry %s = %s)", url, name, value);
					url = value;
					if (resolveImpl()) return true;
				}
			}
		} catch (Throwable t) {
			Log.warn(t, "Can't parse playlist file %s", url);
		} finally {
			if (scanner != null) scanner.close();
		}
		return false;
	}

	private boolean parseM3U(HttpURLConnection connection) {
		Log.info("Parsing M3U file at URL %s", url);
		Scanner scanner = null;
		try {
			Reader reader = new InputStreamReader(connection.getInputStream());
			scanner = new Scanner(reader);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.startsWith("#")) continue;
				Log.fine("Trying playlist %s entry %s)", url, line);
				url = line;
				if (resolveImpl()) return true;
			}
		} catch (Throwable t) {
			Log.warn(t, "Can't parse playlist file %s", url);
		} finally {
			if (scanner != null) scanner.close();
		}
		return false;
	}
}