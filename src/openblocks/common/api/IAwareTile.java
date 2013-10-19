package openblocks.common.api;

public interface IAwareTile extends IAwareTileLite {
	public void onBlockBroken();

	public void onBlockAdded();

	public void onNeighbourChanged(int blockId);

	public boolean onBlockEventReceived(int eventId, int eventParam);
}
