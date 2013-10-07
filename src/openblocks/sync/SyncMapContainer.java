package openblocks.sync;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import openblocks.common.container.ContainerInventory;

public class SyncMapContainer extends SyncMap {

	private ContainerInventory<?> container;

	public SyncMapContainer(ContainerInventory<?> container) {
		this.container = container;
	}

	@Override
	public Set<EntityPlayer> getListeningPlayers(World worldObj, double x, double z, int trackingRange) {
		return container.getPlayers();
	}

	@Override
	protected void writeMapType(DataOutputStream dos) throws IOException {
		dos.writeByte(SyncableManager.TYPE_CONTAINER);
	}

}
