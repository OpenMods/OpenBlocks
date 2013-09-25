package openblocks.common.tileentity;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.api.IAwareTile;
import openblocks.sync.ISyncableObject;

public class TileEntityDecoy extends NetworkedTileEntity implements IAwareTile {

	public static Set<TileEntityDecoy> activeDecoys = Collections
			.newSetFromMap(new WeakHashMap<TileEntityDecoy, Boolean>());

	public TileEntityDecoy() {
	}

	@Override
	public void invalidate() {
		super.invalidate();
		activeDecoys.remove(this);
	}

	@Override
	public void onChunkUnload() {
		activeDecoys.remove(this);
	}

	@Override
	public void initialize() {
		activeDecoys.add(this);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
	}

	@Override
	public void onBlockBroken() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockAdded() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX,
			float hitY, float hitZ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side,
			ItemStack stack, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		// TODO Auto-generated method stub

	}

	public static TileEntityDecoy findNearestDecoyWithinRangeOfEntity(
			EntityCreature entity, double maxRange) {
		TileEntityDecoy nearest = null;
		double nearestDistance = Double.MAX_VALUE;
		for (TileEntityDecoy decoy : activeDecoys) {
			double distance = entity.getDistance(decoy.xCoord, decoy.yCoord,
					decoy.zCoord);
			if (distance <= maxRange && distance < nearestDistance) {
				nearestDistance = distance;
				nearest = decoy;
			}
		}
		return nearest;
	}

}
