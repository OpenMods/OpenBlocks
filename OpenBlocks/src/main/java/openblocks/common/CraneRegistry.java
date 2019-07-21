package openblocks.common;

import com.google.common.collect.MapMaker;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import openblocks.common.entity.EntityMagnet;

public class CraneRegistry {
	private static final double MIN_LENGTH = 0.25;
	private static final double MAX_LENGTH = 10;
	private static final double LENGTH_DELTA = 0.1;

	public static class Data {
		public boolean isExtending;
		public double length = MIN_LENGTH;

		public float prevYaw;
		public double prevPosX;
		public double prevPosY;
		public double prevPosZ;

		private Data(LivingEntity player) {
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
	private final Map<LivingEntity, Data> itemData = new MapMaker().weakKeys().makeMap();
	private final Map<LivingEntity, EntityMagnet> magnetOwners = new MapMaker().weakKeys().weakValues().makeMap();

	public void ensureMagnetExists(LivingEntity player) {
		EntityMagnet magnet = magnetOwners.get(player);

		if (magnet == null || magnet.isDead) {
			createMagnetForPlayer(player);
		} else if (!magnet.isValid()) {
			magnet.setDead();
			createMagnetForPlayer(player);
		}
	}

	private static EntityMagnet createMagnetForPlayer(LivingEntity player) {
		EntityMagnet result = new EntityMagnet.PlayerBound(player.world, player);
		player.world.spawnEntity(result);
		return result;
	}

	public EntityMagnet getMagnetForPlayer(LivingEntity player) {
		return magnetOwners.get(player);
	}

	public void bindMagnetToPlayer(Entity owner, EntityMagnet magnet) {
		if (owner instanceof PlayerEntity) magnetOwners.put((PlayerEntity)owner, magnet);
	}

	public static final double ARM_RADIUS = 2.0;

	public final static CraneRegistry instance = new CraneRegistry();

	private CraneRegistry() {}

	public Data getData(LivingEntity player, boolean canCreate) {
		Data result = itemData.get(player);

		if (result == null && canCreate) {
			result = new Data(player);
			itemData.put(player, result);
		}

		return result;
	}

	public double getCraneMagnetDistance(LivingEntity player) {
		Data data = getData(player, false);
		return data != null? data.length : MIN_LENGTH;
	}
}
