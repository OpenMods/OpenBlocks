package openblocks.client.radio;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Scanner;

import openmods.Log;

import com.google.common.base.Splitter;

public class UrlMeta {
	private static final String MIME_M3U_APP = "application/x-mpegurl";
	private static final String MIME_M3U_AUDIO = "audio/x-mpegurl";

	private static final String MIME_PLS = "audio/x-scpls";

	public enum Status {
		NOT_YET_RESOLVED(false, "not_ready"),
		UNKOWN_ERROR(false, "unknown_error"),
		INVALID_URL(false, "invalid_url"),
		NOT_FOUND(false, "url_not_found"),
		MALFORMED_DATA(false, "malformed_data"),
		OK(true, "");

		public final boolean valid;
		public final String message;

		private Status(boolean valid, String message) {
			this.valid = valid;
			this.message = "openblocks.misc.radio." + message;
		}
	}

	private Status status = Status.NOT_YET_RESOLVED;
	private String url;
	private String contentType;

	public UrlMeta(String url) {
		this.url = url;
	}

	public Status getStatus() {
		return status;
	}

	public String getUrl() {
		return url;
	}

	public String getContentType() {
		return contentType;
	}

	public synchronized void markAsFailed() {
		status = Status.UNKOWN_ERROR;
	}

	public synchronized void resolve() {
		if (status == Status.NOT_YET_RESOLVED) status = resolveImpl();
	}

	private Status resolveImpl() {
		Log.info("Resolving URL %s", url);
		HttpURLConnection connection = null;
		try {
			while (true) {
				URL url;
				try {
					url = new URL(null, this.url, new IcyConnectionHandler());
				} catch (MalformedURLException e) {
					return Status.INVALID_URL;
				}
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
					case 404:
						return Status.NOT_FOUND;
					default:
						Log.warn("Invalid status code from url %s: %d", url, responseCode);
						return Status.UNKOWN_ERROR;
				}
			}
		} catch (Throwable t) {
			Log.warn(t, "Exception during opening url %s", url);
		} finally {
			if (connection != null) connection.disconnect();
		}
		return Status.UNKOWN_ERROR;
	}

	private Status processStream(HttpURLConnection connection) {
		String contentType = connection.getContentType();
		Log.fine("URL %s has content type %s", url, contentType);
		if (contentType.equals(MIME_PLS)) return parsePLS(connection);
		else if (contentType.equals(MIME_M3U_APP) || contentType.equals(MIME_M3U_AUDIO)) return parseM3U(connection);

		this.contentType = contentType;
		return Status.OK;
	}

	private Status parsePLS(HttpURLConnection connection) {
		Log.fine("Parsing PLS file at URL %s", url);
		Scanner scanner = null;
		try {
			Reader reader = new InputStreamReader(connection.getInputStream());
			scanner = new Scanner(reader);
			String header = scanner.nextLine();
			if (!header.equals("[playlist]")) {
				Log.warn("Invalid header '%s' in pls file %s", header, url);
				return Status.MALFORMED_DATA;
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
					Status result = resolveImpl();
					if (result.valid) return result;
				}
			}
		} catch (Throwable t) {
			Log.warn(t, "Can't parse playlist file %s", url);
		} finally {
			if (scanner != null) scanner.close();
		}
		return Status.UNKOWN_ERROR;
	}

	private Status parseM3U(HttpURLConnection connection) {
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
				Status result = resolveImpl();
				if (result.valid) return result;
			}
		} catch (Throwable t) {
			Log.warn(t, "Can't parse playlist file %s", url);
		} finally {
			if (scanner != null) scanner.close();
		}
		return Status.UNKOWN_ERROR;
	}
}