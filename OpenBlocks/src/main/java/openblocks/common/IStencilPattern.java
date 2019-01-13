package openblocks.common;

public interface IStencilPattern {

	int width();

	int height();

	int mix(int bitIndex, int src, int dst);
}
