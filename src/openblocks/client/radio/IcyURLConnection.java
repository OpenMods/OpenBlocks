/*
 ** AACDecoder - Freeware Advanced Audio (AAC) Decoder for Android
 ** Copyright (C) 2014 Spolecne s.r.o., http://www.spoledge.com
 **  
 ** This file is a part of AACDecoder.
 **
 ** AACDecoder is free software; you can redistribute it and/or modify
 ** it under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 3 of the License,
 ** or (at your option) any later version.
 ** 
 ** This program is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 ** GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package openblocks.client.radio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * This is a URLConnection allowing to accept http-like ICY (shoutcast)
 * responses. Normal Java HTTP handler has some problems with that
 */
public class IcyURLConnection extends HttpURLConnection {

	protected Socket socket;
	protected OutputStream outputStream;
	protected InputStream inputStream;
	protected Map<String, List<String>> requestProps;
	protected Map<String, List<String>> headers;
	protected String responseLine;

	public IcyURLConnection(URL url) {
		super(url);
	}

	@Override
	public synchronized void connect() throws IOException {

		// according to specification:
		if (connected) return;

		socket = createSocket();

		socket.connect(
				new InetSocketAddress(url.getHost(), url.getPort() != -1? url.getPort() : url.getDefaultPort()),
				getConnectTimeout());

		connected = true;

		headers = Maps.newHashMap();

		outputStream = socket.getOutputStream();

		// BB: Changed: wrapped result stream
		inputStream = new SafeInputStream(socket.getInputStream());

		writeLine("GET " + ("".equals(url.getPath())? "/" : url.getPath()) + " HTTP/1.1");
		writeLine("Host: " + url.getHost());

		if (requestProps != null) {
			for (Map.Entry<String, List<String>> entry : requestProps.entrySet()) {
				for (String val : entry.getValue()) {
					writeLine(entry.getKey() + ": " + val);
				}
			}
		}

		writeLine("");

		responseLine = readResponseLine();

		for (String line = readLine(); !Strings.isNullOrEmpty(line);) {
			parseHeaderLine(line);
			line = readLine();
		}
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public String getHeaderField(String name) {
		if (headers != null) {
			List<String> list = headers.get(name);
			if (list != null && !list.isEmpty()) return list.get(0);
		}

		return null;
	}

	@Override
	public String getHeaderField(int n) {
		return n == 0? responseLine : null;
	}

	@Override
	public Map<String, List<String>> getHeaderFields() {
		return headers;
	}

	@Override
	public synchronized void setRequestProperty(String key, String value) {
		if (requestProps == null) requestProps = Maps.newHashMap();
		requestProps.put(key, Lists.newArrayList(value));
	}

	@Override
	public synchronized void addRequestProperty(String key, String value) {
		if (requestProps == null) requestProps = Maps.newHashMap();

		List<String> list = requestProps.get(key);
		if (list == null) {
			list = Lists.newArrayList();
			requestProps.put(key, list);
		}

		list.add(value);
	}

	@Override
	public Map<String, List<String>> getRequestProperties() {
		return requestProps;
	}

	@Override
	public synchronized void disconnect() {
		if (!connected) return;

		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {}
			socket = null;
		}

		inputStream = null;
		outputStream = null;
		headers = null;
		responseLine = null;
	}

	@Override
	public boolean usingProxy() {
		return false;
	}

	/**
	 * Creates a new unconnected Socket instance.
	 * Subclasses may use this method to override the default socket
	 * implementation.
	 */
	protected Socket createSocket() {
		return new Socket();
	}

	/**
	 * Reads one response header line and adds it to the headers map.
	 */
	protected void parseHeaderLine(String line) {
		int len = 2;
		int n = line.indexOf(": ");

		if (n == -1) {
			len = 1;
			n = line.indexOf(':');
			if (n == -1) return;
		}

		String key = line.substring(0, n);
		String val = line.substring(n + len);

		List<String> list = headers.get(key);

		// BB: Changed: added toLowerCase
		if (list != null) list.add(val);
		else headers.put(key.toLowerCase(), Lists.newArrayList(val));
	}

	/**
	 * Reads the first response line.
	 */
	protected String readResponseLine() throws IOException {
		String line = readLine();

		if (line != null) {
			int n = line.indexOf(' ');

			if (n != -1) {
				line = "HTTP/1.0" + line.substring(n);
			}
		}

		return line;
	}

	/**
	 * Reads one response line.
	 * 
	 * @return the line without any new-line character.
	 */
	protected String readLine() throws IOException {
		StringBuilder sb = new StringBuilder();

		int c;
		while ((c = inputStream.read()) != -1) {
			if (c == '\r') continue;
			if (c == '\n') break;
			sb.append((char)c);
		}

		return sb.toString();
	}

	/**
	 * Writes one request line.
	 */
	protected void writeLine(String line) throws IOException {
		line += "\r\n";
		outputStream.write(line.getBytes("UTF-8"));
	}

}
