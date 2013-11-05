package openblocks.common.api;

public interface IAwareTile extends IAwareTileLite, INeighbourAwareTile {
	public void onBlockBroken();

	public void onBlockAdded();
}
