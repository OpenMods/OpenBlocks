package openblocks.common;

public interface IVarioController {

	IVarioController NULL = new IVarioController() {

		@Override
		public void setFrequencies(double toneFrequency, double beepFrequency) {}

		@Override
		public void release() {}

		@Override
		public void kill() {}

		@Override
		public void keepAlive() {}

		@Override
		public boolean isValid() {
			return false;
		}
	};

	void setFrequencies(double toneFrequency, double beepFrequency);

	void keepAlive();

	void kill();

	boolean isValid();

	void release();
}