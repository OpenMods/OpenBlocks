package openblocks.sync;

import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import openblocks.network.PacketHandler;

import com.google.common.collect.ImmutableSet;

public class SyncMapTile<H extends TileEntity & ISyncHandler> extends
		SyncMap<H> {

	public SyncMapTile(H handler) {
		super(handler);
	}

	@Override
	protected SyncMap.HandlerType getHandlerType() {
		return HandlerType.TILE_ENTITY;
	}

	@Override
	protected Set<EntityPlayer> getPlayersWatching() {
		if (handler.worldObj instanceof WorldServer) { return PacketHandler.getPlayersWatchingBlock((WorldServer)handler.worldObj, handler.xCoord, handler.zCoord); }
		return ImmutableSet.of();
	}

	@Override
	protected World getWorld() {
		return handler.worldObj;
	}

	public void writeToNBT(NBTTagCompound tag) {
		for (Entry<String, Integer> entry : nameMap.entrySet()) {
			int index = entry.getValue();
			String name = entry.getKey();
			if (objects[index] != null) {
				objects[index].writeToNBT(tag, name);
			}
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		for (Entry<String, Integer> entry : nameMap.entrySet()) {
			int index = entry.getValue();
			String name = entry.getKey();
			if (objects[index] != null) {
				objects[index].readFromNBT(tag, name);
			}
		}
	}
}
