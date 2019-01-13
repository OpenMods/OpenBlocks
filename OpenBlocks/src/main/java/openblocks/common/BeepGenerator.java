package openblocks.common;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import openmods.Log;

public class BeepGenerator {

	private static final int SAMPLE_RATE = 44100;

	private static final int SAMPLES_PER_BUFFER = SAMPLE_RATE / 8;

	private static final int BYTES_PER_SAMPLE = 2;

	private static final int BYTES_PER_BUFFER = BYTES_PER_SAMPLE * SAMPLES_PER_BUFFER;

	private static final double BUFFER_DURATION = (double)SAMPLES_PER_BUFFER / SAMPLE_RATE;

	private static final double FREQUENCY_MAX_CHANGE_PER_BUFFER_DURATION = 50.0;

	private final byte[] scratchBuffer = new byte[BYTES_PER_BUFFER];

	private static final byte[] ZERO_BUFFER = new byte[BYTES_PER_BUFFER];

	private short volume = 2560;

	private double wavePhase;
	private int beepPhase;

	private double toneFrequency;
	private double targetToneFrequency;
	private double beepFrequency;
	private int samplesPerBeep;

	private class WriterThread extends Thread {

		private final SourceDataLine line;

		private boolean running = true;

		public WriterThread(SourceDataLine line) {
			this.line = line;
			setDaemon(true);
			setName("Beeper thread");
		}

		@Override
		public void run() {
			line.start();

			try {
				while (running) {
					final int available = line.available();

					if (available >= SAMPLES_PER_BUFFER)
						writeSample(line);

					try {
						Thread.sleep(100); // has to be lower than SAMPLES_PER_BUFFER / SAMPLE_RATE
					} catch (InterruptedException e) {
						running = false;
					}
				}
			} finally {
				running = false;
				line.close();
			}
		}

		public boolean isShuttingDown() {
			return !running;
		}

		public void shutdown() {
			running = false;
		}
	}

	private WriterThread writerThread;

	public synchronized void start() {
		wavePhase = 0;
		beepPhase = 0;

		if (!isRunning()) {
			final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8 * BYTES_PER_SAMPLE, 1, true, true);
			try {
				SourceDataLine line = AudioSystem.getSourceDataLine(af);
				line.open(af, SAMPLE_RATE);
				writerThread = new WriterThread(line);
				writerThread.start();
			} catch (LineUnavailableException e) {
				Log.warn(e, "Failed to initialize beeper");
				if (writerThread != null) writerThread.shutdown();
			}
		}
	}

	public synchronized void stop() {
		if (writerThread != null) writerThread.shutdown();
		setTargetToneFrequency(0d);
		setBeepFrequency(0d);
	}

	public synchronized boolean isRunning() {
		return writerThread != null && writerThread.isAlive() && !writerThread.isShuttingDown();
	}

	private void writeSample(SourceDataLine line) {
		final double lastToneFrequency;

		if (this.toneFrequency == 0d || this.targetToneFrequency == 0d) {
			lastToneFrequency = this.targetToneFrequency;
			this.toneFrequency = this.targetToneFrequency;
		} else {
			lastToneFrequency = this.toneFrequency;
			final double delta = this.targetToneFrequency - this.toneFrequency;
			this.toneFrequency += limit(delta, FREQUENCY_MAX_CHANGE_PER_BUFFER_DURATION);
		}

		byte[] buffer = generateSamplesWithSweep(lastToneFrequency, this.toneFrequency);
		line.write(buffer, 0, buffer.length);
	}

	private static double limit(double value, double limit) {
		if (value < 0)
			return Math.max(value, -limit);
		else
			return Math.min(value, limit);
	}

	private byte[] generateSamplesWithSweep(double f0, double f1) {
		if (f0 == 0.0 && f1 == 0.0)
			return ZERO_BUFFER;

		final float masterSoundLevel = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER);

		if (masterSoundLevel == 0)
			return ZERO_BUFFER;

		final float amplitude = Math.max(volume * masterSoundLevel, 2);

		// see 'chirp' on wiki for explanation of constants
		final double k = (f1 - f0) / BUFFER_DURATION;

		int sampleCount = 0;

		if (samplesPerBeep == 0) {
			for (int i = 0; i < BYTES_PER_BUFFER; i += BYTES_PER_SAMPLE, sampleCount++) {
				final short v = (short)calculateSample(amplitude, wavePhase, f0, k, sampleToRealTime(sampleCount));
				writeShortSample(scratchBuffer, i, v);
			}
		} else {
			int beepSample = beepPhase;
			for (int i = 0; i < BYTES_PER_BUFFER; i += BYTES_PER_SAMPLE, sampleCount++) {
				if (beepSample < samplesPerBeep) {
					final short v = (short)calculateSample(amplitude, wavePhase, f0, k, sampleToRealTime(sampleCount));
					writeShortSample(scratchBuffer, i, v);
				} else {
					scratchBuffer[i] = 0;
					scratchBuffer[i + 1] = 0;
				}

				if (beepSample++ >= 2 * samplesPerBeep)
					beepSample = 0;
			}
			beepPhase = beepSample;
		}

		wavePhase = phase(wavePhase, f0, k, sampleToRealTime(sampleCount)) % (2 * Math.PI);
		return scratchBuffer;

	}

	private static void writeShortSample(byte[] buf, int i, final short v) {
		buf[i] = (byte)(v >> 8);
		buf[i + 1] = (byte)(v);
	}

	private static double sampleToRealTime(int sampleCount) {
		return (double)sampleCount / SAMPLE_RATE;
	}

	private static double calculateSample(float amplitude, double phase0, double f0, double k, double t) {
		return (short)(amplitude * Math.sin(phase(phase0, f0, k, t)));
	}

	private static double phase(double phase0, double f0, double k, double t) {
		return phase0 + 2.0 * Math.PI * (f0 + k / 2.0 * t) * t;
	}

	public short getVolume() {
		return volume;
	}

	public void setVolume(short volume) {
		this.volume = volume;
	}

	public double getToneFrequency() {
		return toneFrequency;
	}

	public double getTargetToneFrequency() {
		return this.targetToneFrequency;
	}

	public void setTargetToneFrequency(double frequency) {
		this.targetToneFrequency = frequency;
	}

	public double getBeepFrequency() {
		return beepFrequency;
	}

	public void setBeepFrequency(double beepFrequency) {
		this.beepFrequency = beepFrequency;

		if (beepFrequency == 0) {
			samplesPerBeep = 0;
		} else {
			samplesPerBeep = (int)(SAMPLE_RATE / beepFrequency);
		}
	}

}
