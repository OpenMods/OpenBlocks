package openblocks.common;

public interface IStencilPattern {

	public int width();

	public int height();

	public int mix(int bitIndex, int src, int dst);
}
