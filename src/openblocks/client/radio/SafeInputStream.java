package openblocks.client.radio;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SafeInputStream extends FilterInputStream {

	public SafeInputStream(InputStream in) {
		super(in);
	}

	@Override
	public int read(byte[] b, int off, int len) {
		/*
		 * This may be terrible solution, but SoundSystem handles exception very
		 * poorly (stream stays opened and client is bombared with events) while
		 * returning 0 quietly closes stream
		 */

		try {
			return super.read(b, off, len);
		} catch (IOException e) {
			return 0;
		}
	}

}
