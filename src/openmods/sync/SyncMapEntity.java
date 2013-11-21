package openmods.sync;

import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import openmods.network.PacketHandler;

import com.google.common.collect.ImmutableSet;

public class SyncMapEntity<H extends Entity & ISyncHandler> extends SyncMap<H> {

	public SyncMapEntity(H handler) {
		super(handler);
	}

	@Override
	protected SyncMap.HandlerType getHandlerType() {
		return HandlerType.ENTITY;
	}

	@Override
	protected Set<EntityPlayer> getPlayersWatching() {
		if (handler.worldObj instanceof WorldServer) { return PacketHandler.getPlayersWatchingEntity((WorldServer)handler.worldObj, handler.entityId); }
		return ImmutableSet.of();
	}

	@Override
	protected World getWorld() {
		return handler.worldObj;
	}
}