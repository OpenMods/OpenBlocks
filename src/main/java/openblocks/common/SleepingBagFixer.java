package openblocks.common;

import info.openmods.calc.utils.reflection.FieldWrapper;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEvent;
import openmods.network.event.NetworkEventMeta;
import openmods.reflection.MethodAccess;
import openmods.reflection.MethodAccess.Function2;

// workaround for Forge #4112
@EventBusSubscriber(Side.CLIENT)
public class SleepingBagFixer {

	@NetworkEventMeta(direction = EventDirection.S2C)
	public static class SleepingBagEvent extends NetworkEvent {

		private BlockPos blockPos;

		public SleepingBagEvent() {}

		public SleepingBagEvent(BlockPos blockPos) {
			this.blockPos = blockPos;
		}

		@Override
		protected void readFromStream(PacketBuffer input) {
			blockPos = input.readBlockPos();
		}

		@Override
		protected void writeToStream(PacketBuffer output) {
			output.writeBlockPos(blockPos);
		}

	}

	@SubscribeEvent
	public static void onEvent(SleepingBagEvent evt) {
		trySleep(evt.sender, evt.blockPos);
	}

	private static class PlayerSleepAdapter {

		private static final FieldWrapper<Boolean> PLAYER_SLEEPING = FieldWrapper.create(ReflectionHelper.findField(EntityPlayer.class, "sleeping", "field_71083_bS"));

		private static final FieldWrapper<Integer> PLAYER_SLEEP_TIMER = FieldWrapper.create(ReflectionHelper.findField(EntityPlayer.class, "sleepTimer", "field_71076_b"));

		private static final Function2<Void, Float, Float> PLAYER_SET_SIZE = MethodAccess.create(void.class, Entity.class, float.class, float.class, "setSize", "func_70105_a");

		private final EntityPlayer player;

		public PlayerSleepAdapter(EntityPlayer player) {
			this.player = player;
		}

		public EntityPlayer.SleepResult trySleep(BlockPos bedLocation) {
			EntityPlayer.SleepResult ret = net.minecraftforge.event.ForgeEventFactory.onPlayerSleepInBed(player, bedLocation);
			if (ret != null) return ret;

			if (!player.world.isRemote) {
				if (player.isPlayerSleeping() || !player.isEntityAlive()) { return EntityPlayer.SleepResult.OTHER_PROBLEM; }

				if (!player.world.provider.isSurfaceWorld()) { return EntityPlayer.SleepResult.NOT_POSSIBLE_HERE; }

				if (player.world.isDaytime()) { return EntityPlayer.SleepResult.NOT_POSSIBLE_NOW; }

				List<EntityMob> list = player.world.<EntityMob> getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(bedLocation.getX() - 8.0D, bedLocation.getY() - 5.0D, bedLocation.getZ() - 8.0D, bedLocation.getX() + 8.0D, bedLocation.getY() + 5.0D, bedLocation.getZ() + 8.0D));

				if (!list.isEmpty()) { return EntityPlayer.SleepResult.NOT_SAFE; }
			}

			if (player.isRiding()) {
				player.dismountRidingEntity();
			}

			PLAYER_SET_SIZE.call(player, 0.2F, 0.2F);

			player.setPosition(bedLocation.getX() + 0.5F, bedLocation.getY() + 0.6875F, bedLocation.getZ() + 0.5F);

			// player.sleeping = true;
			PLAYER_SLEEPING.set(player, true);
			// player.sleepTimer = 0;
			PLAYER_SLEEP_TIMER.set(player, 0);
			player.bedLocation = bedLocation;
			player.motionX = 0.0D;
			player.motionY = 0.0D;
			player.motionZ = 0.0D;

			if (!player.world.isRemote) {
				player.world.updateAllPlayersSleepingFlag();
			}

			return EntityPlayer.SleepResult.OK;
		}
	}

	private static class PlayerSleepAdapterMP extends PlayerSleepAdapter {
		private final EntityPlayerMP player;

		public PlayerSleepAdapterMP(EntityPlayerMP player) {
			super(player);
			this.player = player;
		}

		@Override
		public EntityPlayer.SleepResult trySleep(BlockPos bedLocation) {
			final SleepResult result = super.trySleep(bedLocation);

			if (result == EntityPlayer.SleepResult.OK) {
				player.addStat(StatList.SLEEP_IN_BED);
				final SleepingBagEvent sleepingBagEvent = new SleepingBagEvent(bedLocation);
				sleepingBagEvent.sendToEntity(player);
				player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
				sleepingBagEvent.sendToPlayer(player);
			}

			return result;
		}
	}

	private static PlayerSleepAdapter createAdapter(EntityPlayer player) {
		return player instanceof EntityPlayerMP? new PlayerSleepAdapterMP((EntityPlayerMP)player) : new PlayerSleepAdapter(player);
	}

	public static EntityPlayer.SleepResult trySleep(EntityPlayer player, BlockPos bedLocation) {
		return createAdapter(player).trySleep(bedLocation);
	}

}
