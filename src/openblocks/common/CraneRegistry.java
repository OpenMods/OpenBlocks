package openblocks.common;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import openblocks.common.entity.EntityMagnet;
import openblocks.common.entity.EntityMagnet.EntityPlayerTarget;
import openblocks.common.entity.EntityMagnet.IOwner;

import com.google.common.collect.MapMaker;

public class CraneRegistry {
	private static final double MIN_LENGTH = 0.25;
	private static final double MAX_LENGTH = 10;
	private static final double LENGTH_DELTA = 0.1;

	public static class Data {
		public boolean isDetected;
		public boolean isExtending;
		public double length = MIN_LENGTH;

		public float prevYaw;
		public double prevPosX;
		public double prevPosY;
		public double prevPosZ;

		private Data(EntityPlayer player) {
			prevYaw = player.rotationYaw;
			prevPosX = player.posX;
			prevPosY = player.posY;
			prevPosZ = player.posZ;
		}

		public void updateLength() {
			if (isExtending && length < MAX_LENGTH) length += LENGTH_DELTA;
			else if (!isExtending && length > MIN_LENGTH) length -= LENGTH_DELTA;
		}
	}

	private Map<EntityPlayer, Data> itemData = new MapMaker().weakKeys().makeMap();
	public Map<EntityPlayer, EntityMagnet> magnetData = new MapMaker().weakKeys().weakValues().makeMap();

	public EntityMagnet getOrCreateMagnet(EntityPlayer player) {
		EntityMagnet result = magnetData.get(player);

		if (result == null || result.isDead) {
			result = createMagnetForPlayer(player);
		} else if (!result.isValid()) {
			result.setDead();
			result = createMagnetForPlayer(player);
		}

		return result;
	}

	private static EntityMagnet createMagnetForPlayer(EntityPlayer player) {
		IOwner provider = new EntityPlayerTarget(player);
		EntityMagnet result = new EntityMagnet(player.worldObj, provider, false);
		player.worldObj.spawnEntityInWorld(result);
		return result;
	}

	public static final double ARM_RADIUS = 2.0;

	public final static CraneRegistry instance = new CraneRegistry();

	private CraneRegistry() {}

	public Data getData(EntityPlayer player, boolean canCreate) {
		Data result = itemData.get(player);

		if (result == null && canCreate) {
			result = new Data(player);
			itemData.put(player, result);
		}

		return result;
	}

	public double getCraneMagnetDistance(EntityPlayer player) {
		Data data = getData(player, false);
		return data != null? data.length : MIN_LENGTH;
	}
}
