package openblocks.common.tileentity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Log;
import openblocks.sync.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class SyncedTileEntity extends OpenTileEntity implements
		ISyncHandler {

	
	protected SyncMapTile<SyncedTileEntity> syncMap;
	
	private static final Map<Class<? extends SyncedTileEntity>, List<Field>> syncedFields = Maps.newIdentityHashMap();
	
	private static final Comparator<Field> FIELD_NAME_COMPARATOR = new Comparator<Field>() {
		@Override
		public int compare(Field o1, Field o2) {
			// No need to worry about nulls
			return o1.getName().compareTo(o2.getName());
		}
	};
	
	public SyncedTileEntity() {
		syncMap = new SyncMapTile<SyncedTileEntity>(this);
		createSyncedFields();
		registerFields();
	}
	
	protected abstract void createSyncedFields();
	
	private List<Field> getSyncedFields() {
		List<Field> result = syncedFields.get(getClass());
		
		if (result == null) {
			Set<Field> fields = Sets.newTreeSet(FIELD_NAME_COMPARATOR);
			for (Field field : getClass().getDeclaredFields()) {
				if (ISyncableObject.class.isAssignableFrom(field.getType())) {
					fields.add(field);
					field.setAccessible(true);
				}
			}
			result = ImmutableList.copyOf(fields);
			syncedFields.put(getClass(), result);
		}
		
		return result;
	}
	
	private void registerFields() {
		for (Field field : getSyncedFields()) {
			try {
				addSyncedObject(field.getName(), (ISyncableObject)field.get(this));
			} catch (Exception e) {
				Log.severe(e, "Exception while registering synce field '%s'", field);
			}
		}
	}
	
	public void addSyncedObject(String name, ISyncableObject obj) {
		syncMap.put(name, obj);
	}

	public void sync() {
		if (syncMap.sync()) {
			onSync();
		}
	}
	
	public void onSync() {
		
	}

	@Override
	public SyncMap<SyncedTileEntity> getSyncMap() {
		return syncMap;
	}

	@Override
	public Packet getDescriptionPacket() {
		try {
			return syncMap.createPacket(true, false);
		} catch (IOException e) {
			Log.severe(e, "Error during description packet creation");
			return null;
		}
	}

	public ForgeDirection getSecondaryRotation() {
		ISyncableObject rot = syncMap.get("_rotation2");
		if (rot != null) {
			return ((SyncableDirection) rot).getValue();
		}
		return null;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		syncMap.writeToNBT(tag);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		syncMap.readFromNBT(tag);
	}
}
