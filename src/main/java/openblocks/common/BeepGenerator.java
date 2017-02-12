package openblocks.common;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import openmods.Log;

public class BeepGenerator {

	private static final int SAMPLE_RATE = 44100;

	private static final int SAMPLES_PER_BUFFER = SAMPLE_RATE / 8;

	private static final int BYTES_PER_SAMPLE = 2;

	private static final int BYTES_PER_BUFFER = BYTES_PER_SAMPLE * SAMPLES_PER_BUFFER;

	private final byte[] scratchBuffer = new byte[BYTES_PER_BUFFER];

	private static final byte[] ZERO_BUFFER = new byte[BYTES_PER_BUFFER];

	private short volume = 2560;

	private boolean running;

	private double wavePhase;
	private int beepPhase;

	private double targetToneFrequency;
	private double toneFrequency;
	private double beepFrequency;
	private int samplesPerBeep;

	public BeepGenerator() {
		running = false;
	}

	public void start() {
		running = true;
		wavePhase = 0;
		beepPhase = 0;

		AudioFormat af = new AudioFormat(SAMPLE_RATE, 8 * BYTES_PER_SAMPLE, 1, true, true);
		try {
			SourceDataLine line = AudioSystem.getSourceDataLine(af);
			line.open(af, SAMPLE_RATE);
			startWriter(line);
		} catch (LineUnavailableException e) {
			Log.warn(e, "Failed to initialize beeper");
		}
	}

	public void stop() {
		running = false;
		setToneFrequency(0d);
		setBeepFrequency(0d);
	}

	public boolean isRunning() {
		return running;
	}

	private void startWriter(final SourceDataLine line) {
		final Thread writerThread = new Thread(new Runnable() {

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
					line.close();
				}
			}
		});
		writerThread.setDaemon(true);
		writerThread.setName("Beeper thread");
		writerThread.start();
	}

	private void writeSample(SourceDataLine line) {
		double lastToneFrequency = this.toneFrequency;
		if (this.toneFrequency == 0d || this.targetToneFrequency == 0d) {
			this.toneFrequency = targetToneFrequency;
			lastToneFrequency = targetToneFrequency;
		}
		else if (this.toneFrequency < targetToneFrequency)
			this.toneFrequency += Math.min(50d, targetToneFrequency - this.toneFrequency);
		else if (this.toneFrequency > targetToneFrequency)
			this.toneFrequency -= Math.min(50d, this.toneFrequency - targetToneFrequency);

		byte[] buffer = generateSamplesWithSweep(lastToneFrequency, this.toneFrequency);
		line.write(buffer, 0, buffer.length);
	}

	private byte[] generateSamplesWithSweep(double f0, double f1) {
		if (f0 == 0.0 && f1 == 0.0)
			return ZERO_BUFFER;

		final float masterSoundLevel = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER);

		if (masterSoundLevel == 0)
			return ZERO_BUFFER;

		final float amplitude = Math.max(volume * masterSoundLevel, 2);

		// trying to spread frequency sweep over whole sample
		final double sweepDuration = (double)SAMPLES_PER_BUFFER / SAMPLE_RATE;

		// see 'chirp' on wiki for explanation of constants
		final double k = (f1 - f0) / sweepDuration;

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

	public void setToneFrequency(double frequency) {
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
