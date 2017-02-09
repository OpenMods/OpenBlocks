package openblocks.common;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;

public class BeepGenerator {

	private static final int SAMPLE_RATE = 128 * 1024;
	private static final int MIN_BUFFER_AVAILABLE = SAMPLE_RATE * 64 / 1000;
	private static final int TIMEOUT_SECONDS = 1;

	private static byte volume = 8;

	public static byte getVolume() {
		return volume;
	}

	public static void setVolume(byte volume) {
		BeepGenerator.volume = volume;
	}

	private byte[] buffer;
	private int bufferSize;
	private boolean currentlyBeeping;
	private boolean running;

	private double toneFrequency;
	private double lastToneFrequency;
	private double beepFrequency;
	private int samplesSinceLastBeepChange;
	private int timeout;

	private SourceDataLine line;

	public BeepGenerator() {
		running = false;
	}

	public void start() {
		running = true;
		timeout = TIMEOUT_SECONDS * 10;

		AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
		try {
			this.line = AudioSystem.getSourceDataLine(af);
			this.line.open(af, SAMPLE_RATE);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		bufferSize = this.line.getBufferSize();

		startWriter();
		startTimeout();
	}

	public void stop() {
		timeout = 0;
		setToneFrequency(0d);
		setBeepFrequency(0d);
	}

	public void keepAlive() {
		timeout = TIMEOUT_SECONDS * 10;
	}

	public boolean isRunning() {
		return running;
	}

	private void startWriter() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				writeSample();
				writeSample();

				BeepGenerator.this.line.start();

				int kill = 5;

				while (BeepGenerator.this.running) {

					writeSample();

					// Calculate sleep in ms from buffer-surplus
					int bufferAvailable = bufferSize - BeepGenerator.this.line.available();
					int ms = (int)((bufferAvailable - MIN_BUFFER_AVAILABLE) / ((SAMPLE_RATE) / 1000d));

					if (ms <= 8)
						continue;

					// Fixes a weird bug (BeepGenerator.this.line.available() returning 0)
					if (bufferAvailable == bufferSize) {
						BeepGenerator.this.line.stop();
						while (bufferAvailable == bufferSize || bufferAvailable <= MIN_BUFFER_AVAILABLE) {
							writeSample();
							bufferAvailable = bufferSize - BeepGenerator.this.line.available();
						}
						BeepGenerator.this.line.start();
						continue;
					}

					if (ms < 100)
						kill = 5;

					if (kill == 0) {
						stop();
					} else {
						kill--;
					}

					try {
						Thread.sleep(ms);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
				BeepGenerator.this.line.close();
			}
		}).start();
	}

	private void startTimeout() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (BeepGenerator.this.running) {
					BeepGenerator.this.timeout--;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (BeepGenerator.this.timeout <= 0)
						BeepGenerator.this.running = false;
				}
			}
		}).start();
	}

	private void writeSample() {
		if (this.lastToneFrequency == 0d || getToneFrequency() == 0d)
			this.lastToneFrequency = getToneFrequency();
		else if (this.lastToneFrequency < getToneFrequency())
			this.lastToneFrequency += Math.min(5d, getToneFrequency() - this.lastToneFrequency);
		else if (this.lastToneFrequency > getToneFrequency())
			this.lastToneFrequency -= Math.min(5d, this.lastToneFrequency - getToneFrequency());

		BeepGenerator.this.generateSample(this.lastToneFrequency);
		line.write(BeepGenerator.this.buffer, 0, this.buffer.length);
	}

	private void generateSample(double frequency) {

		final int samples = SAMPLE_RATE * 16 / 1000;
		final float soundLevel = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER);

		if (frequency == 0.0) {
			this.buffer = new byte[samples];
			return;
		}

		double sinLength = (SAMPLE_RATE / frequency);

		int sinSmooth = (int)(sinLength - (int)(samples % sinLength));

		this.buffer = new byte[samples + sinSmooth];

		final int samplesPerBeep;
		float vol;
		if (getBeepFrequency() > 0) {
			samplesPerBeep = (int)(SAMPLE_RATE / getBeepFrequency());
			vol = this.currentlyBeeping? BeepGenerator.volume : 0;
		} else {
			samplesPerBeep = 0;
			vol = BeepGenerator.volume;
			this.currentlyBeeping = true;
		}

		vol = (soundLevel != 0? Math.max((vol * soundLevel), 2) : 0);

		for (int i = 0; i < this.buffer.length; i++) {
			this.buffer[i] = (byte)(Math.sin((2.0 * Math.PI * i) / sinLength) * vol);
			samplesSinceLastBeepChange++;
			if (samplesPerBeep > 0 && samplesSinceLastBeepChange >= samplesPerBeep) {
				this.currentlyBeeping = !this.currentlyBeeping;
				if (soundLevel == 0) this.currentlyBeeping = false;
				vol = (this.currentlyBeeping? Math.max((BeepGenerator.volume * soundLevel), 2) : 0);
				samplesSinceLastBeepChange = 0;
			}
		}
	}

	public double getToneFrequency() {
		return toneFrequency;
	}

	public void setToneFrequency(double frequency) {
		this.toneFrequency = frequency;
	}

	public double getBeepFrequency() {
		return beepFrequency;
	}

	public void setBeepFrequency(double beepFrequency) {
		this.beepFrequency = beepFrequency;
	}

}
