package openblocks.client.radio;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class IcyConnectionHandler extends URLStreamHandler {

	@Override
	protected URLConnection openConnection(URL url) {
		return new IcyURLConnection(url);
	}

	@Override
	protected int getDefaultPort() {
		return 80;
	}
}
