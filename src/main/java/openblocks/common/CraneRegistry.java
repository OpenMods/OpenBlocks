package openblocks.common;

import com.google.common.collect.MapMaker;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import openblocks.common.entity.EntityMagnet;

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

		private Data(EntityLivingBase player) {
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

	// TODO does it need two separate collections?
	private Map<EntityLivingBase, Data> itemData = new MapMaker().weakKeys().makeMap();
	private Map<EntityLivingBase, EntityMagnet> magnetOwners = new MapMaker().weakKeys().weakValues().makeMap();

	public void ensureMagnetExists(EntityLivingBase player) {
		EntityMagnet magnet = magnetOwners.get(player);

		if (magnet == null || magnet.isDead) {
			createMagnetForPlayer(player);
		} else if (!magnet.isValid()) {
			magnet.setDead();
			createMagnetForPlayer(player);
		}
	}

	private static EntityMagnet createMagnetForPlayer(EntityLivingBase player) {
		EntityMagnet result = new EntityMagnet.PlayerBound(player.worldObj, player);
		player.worldObj.spawnEntityInWorld(result);
		return result;
	}

	public EntityMagnet getMagnetForPlayer(EntityLivingBase player) {
		return magnetOwners.get(player);
	}

	public void bindMagnetToPlayer(Entity owner, EntityMagnet magnet) {
		if (owner instanceof EntityPlayer) magnetOwners.put((EntityPlayer)owner, magnet);
	}

	public static final double ARM_RADIUS = 2.0;

	public final static CraneRegistry instance = new CraneRegistry();

	private CraneRegistry() {}

	public Data getData(EntityLivingBase player, boolean canCreate) {
		Data result = itemData.get(player);

		if (result == null && canCreate) {
			result = new Data(player);
			itemData.put(player, result);
		}

		return result;
	}

	public double getCraneMagnetDistance(EntityLivingBase player) {
		Data data = getData(player, false);
		return data != null? data.length : MIN_LENGTH;
	}
}
