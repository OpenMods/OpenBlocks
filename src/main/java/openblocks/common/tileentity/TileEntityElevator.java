package openblocks.common.tileentity;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.sync.SyncableEnum;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityElevator extends SyncedTileEntity {

	private SyncableEnum<ForgeDirection> direction;
	
	@Override
	protected void createSyncedFields() {
		direction = new SyncableEnum<ForgeDirection>(ForgeDirection.UNKNOWN);
	}
	
	public ForgeDirection getDirection() {
		return direction.getValue();
	}
	
	public void nextDirection() {
		switch(direction.getValue()) {
			case UNKNOWN:
				direction.set(ForgeDirection.NORTH);
				break;
			case NORTH:
				direction.set(ForgeDirection.EAST);
				break;
			case EAST:
				direction.set(ForgeDirection.SOUTH);
				break;
			case SOUTH:
				direction.set(ForgeDirection.WEST);
				break;
			case WEST:
				direction.set(ForgeDirection.UNKNOWN);
				break;
			default:
				direction.set(ForgeDirection.UNKNOWN);
				break;
			}
	}
}