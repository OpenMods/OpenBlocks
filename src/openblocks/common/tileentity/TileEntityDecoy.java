package openblocks.common.tileentity;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.EntityCreature;

public class TileEntityDecoy extends OpenTileEntity {

	public static Set<TileEntityDecoy> activeDecoys = Collections.newSetFromMap(new WeakHashMap<TileEntityDecoy, Boolean>());

	public TileEntityDecoy() {}

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

	public static TileEntityDecoy findNearestDecoyWithinRangeOfEntity(EntityCreature entity, double maxRange) {
		TileEntityDecoy nearest = null;
		double nearestDistance = Double.MAX_VALUE;
		for (TileEntityDecoy decoy : activeDecoys) {
			double distance = entity.getDistance(decoy.xCoord, decoy.yCoord, decoy.zCoord);
			if (distance <= maxRange && distance < nearestDistance) {
				nearestDistance = distance;
				nearest = decoy;
			}
		}
		return nearest;
	}

}
