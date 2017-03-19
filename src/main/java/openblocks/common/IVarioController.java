package openblocks.common;

public interface IVarioController {

	public static final IVarioController NULL = new IVarioController() {

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

	public void setFrequencies(double toneFrequency, double beepFrequency);

	public void keepAlive();

	public void kill();

	public boolean isValid();

	public void release();
}