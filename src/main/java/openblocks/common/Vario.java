package openblocks.common;

public class Vario {

	private static final int WATCHDOG_PERIOD = 1000 / 20; // nominal game tick

	private static final int WATCHDOG_TIMEOUT_TICKS = 10;

	private static final int VOL_MIN = 2;
	private static final int VOL_MAX = 20;

	public static final Vario instance = new Vario();

	private int varioVolume = 8;

	private boolean isEnabled;

	private IVarioController activeController = IVarioController.NULL;

	private WatchdogThread watchdogThread;

	private final BeepGenerator beeper = new BeepGenerator();

	{
		setVolume(varioVolume);
	}

	private class Controller implements IVarioController {

		private boolean isValid = true;

		@Override
		public void setFrequencies(double toneFrequency, double beepFrequency) {
			if (isValid) {
				beeper.setTargetToneFrequency(toneFrequency);
				beeper.setBeepFrequency(beepFrequency);
			}
		}

		@Override
		public synchronized void keepAlive() {
			if (isValid)
				resetWatchdog();
		}

		@Override
		public synchronized void kill() {
			if (isValid)
				if (watchdogThread != null) watchdogThread.shutdown();
		}

		@Override
		public boolean isValid() {
			return isValid;
		}

		@Override
		public void release() {
			isValid = false;
		}
	}

	private class WatchdogThread extends Thread {

		private boolean isAlive = true;
		private int watchdogMissedTicks;

		public WatchdogThread() {
			setName("Vario watchdog");
			setDaemon(true);
		}

		@Override
		public void run() {
			try {
				while (isAlive && isEnabled) {
					if (watchdogMissedTicks++ > WATCHDOG_TIMEOUT_TICKS) {
						isAlive = false;
						break;
					}

					if (!beeper.isRunning()) beeper.start();

					try {
						Thread.sleep(WATCHDOG_PERIOD);
					} catch (InterruptedException e) {
						isAlive = false;
						break;
					}
				}
			} finally {
				beeper.stop();
				isAlive = false;
			}
		}

		public boolean isShuttingDown() {
			return !isAlive || !isEnabled;
		}

		public void shutdown() {
			isAlive = false;
		}

		public void ping() {
			watchdogMissedTicks = 0;
		}
	}

	public void incVolume() {
		varioVolume = Math.min((varioVolume + 2), VOL_MAX);
		setVolume(varioVolume);
	}

	public void decVolume() {
		varioVolume = Math.max((varioVolume - 2), VOL_MIN);
		setVolume(varioVolume);
	}

	private void setVolume(int volume) {
		beeper.setVolume((short)(varioVolume << 8));
	}

	public void enable() {
		isEnabled = true;
	}

	public void disable() {
		isEnabled = false;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void toggle() {
		isEnabled = !isEnabled;
	}

	public IVarioController acquire() {
		if (activeController.isValid())
			activeController.release();

		return (activeController = new Controller());
	}

	private void resetWatchdog() {
		if (isEnabled) {
			if (watchdogThread == null || !watchdogThread.isAlive() || watchdogThread.isShuttingDown()) {
				watchdogThread = new WatchdogThread();
				watchdogThread.start();
			} else if (watchdogThread != null) {
				watchdogThread.ping();
			}
		}
	}
}
