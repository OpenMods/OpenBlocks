package openblocks.client.radio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.sound.sampled.AudioFormat;

import javazoom.jl.decoder.*;
import openmods.Log;
import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;

public class CodecMp3 implements ICodec {

	private boolean initalized;
	private boolean streamClosed;

	private Bitstream bitstream;
	private Decoder decoder;
	private AudioFormat audioFormat;
	private SampleBuffer buffer;

	@Override
	public void reverseByteOrder(boolean b) {}

	@Override
	public boolean initialize(URL url) {
		try {
			final URLConnection conn = url.openConnection();
			conn.connect();

			bitstream = new Bitstream(conn.getInputStream());
			decoder = new Decoder();
			initalized = true;

			updateBuffer(); // get single frame here, to receive stream params
			audioFormat = new AudioFormat(decoder.getOutputFrequency(), 16, decoder.getOutputChannels(), true, false);
			return true;
		} catch (Throwable t) {
			Log.warn("Failed to initalize codec for url '%s'", url);
		}

		return false;
	}

	private boolean updateBuffer() throws Exception {
		Header h = bitstream.readFrame();
		if (h == null) return false;
		buffer = (SampleBuffer)decoder.decodeFrame(h, bitstream);
		bitstream.closeFrame();
		return true;
	}

	@Override
	public boolean initialized() {
		return initalized;
	}

	@Override
	public SoundBuffer read() {
		if (!initalized || streamClosed) return null;

		final int limit = SoundSystemConfig.getStreamingBufferSize();
		ByteArrayOutputStream output = new ByteArrayOutputStream(limit);

		try {
			do {
				readBytes(output);
				if (!updateBuffer()) break;
			} while (!streamClosed && output.size() < limit);
		} catch (Throwable t) {
			Log.warn(t, "Error in stream decoding, aborting");
			streamClosed = true;
		}

		return new SoundBuffer(output.toByteArray(), audioFormat);
	}

	@Override
	public SoundBuffer readAll() {
		if (!initalized || streamClosed) return null;

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		try {
			do {
				readBytes(output);
				if (!updateBuffer()) break;
			} while (!streamClosed);
		} catch (Throwable t) {
			Log.warn(t, "Error in stream decoding, aborting");
			streamClosed = true;
		}

		return new SoundBuffer(output.toByteArray(), audioFormat);
	}

	private void readBytes(OutputStream output) throws IOException {
		final short[] samples = buffer.getBuffer();
		int len = buffer.getBufferLength();
		int idx = 0;
		while (len-- > 0) {
			final short s = samples[idx++];
			output.write(s);
			output.write(s >>> 8);
		}
	}

	@Override
	public boolean endOfStream() {
		return streamClosed;
	}

	@Override
	public void cleanup() {
		streamClosed = true;
		initalized = false;
		decoder = null;
		try {
			bitstream.close();
		} catch (BitstreamException e) {
			Log.warn(e, "Failed to close bitstream");
		}
		bitstream = null;
		buffer = null;
	}

	@Override
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

}
