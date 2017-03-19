package openblocks.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.PedometerHandler;
import openblocks.common.PedometerHandler.PedometerData;
import openblocks.common.PedometerHandler.PedometerState;
import openmods.utils.Units.DistanceUnit;
import openmods.utils.Units.SpeedUnit;

public class ItemPedometer extends Item {

	public ItemPedometer() {
		setMaxStackSize(1);
	}

	private static void send(EntityPlayer player, String format, Object... args) {
		player.addChatComponentMessage(new TextComponentTranslation(format, args));
	}

	private SpeedUnit speedUnit = SpeedUnit.M_PER_TICK;
	private DistanceUnit distanceUnit = DistanceUnit.M;

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
			if (player.isSneaking()) {
				PedometerHandler.getProperty(player).reset();
				send(player, "openblocks.misc.pedometer.tracking_reset");
			} else {
				PedometerState state = PedometerHandler.getProperty(player);
				if (state.isRunning()) {
					showPedometerData(player, state);
				} else {
					state.init(player, world);
					send(player, "openblocks.misc.pedometer.tracking_started");
				}
			}
		} else {
			world.playSound(null, player.getPosition(), OpenBlocks.Sounds.ITEM_PEDOMETER_USE, SoundCategory.PLAYERS, 1F, 1F);
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	protected void showPedometerData(EntityPlayer player, PedometerState state) {
		PedometerData result = state.getData();
		if (result == null) return;
		player.addChatComponentMessage(new TextComponentString(""));
		send(player, "openblocks.misc.pedometer.start_point", String.format("%.1f %.1f %.1f", result.startingPoint.xCoord, result.startingPoint.yCoord, result.startingPoint.zCoord));

		send(player, "openblocks.misc.pedometer.speed", speedUnit.format(result.currentSpeed));
		send(player, "openblocks.misc.pedometer.avg_speed", speedUnit.format(result.averageSpeed()));
		send(player, "openblocks.misc.pedometer.total_distance", distanceUnit.format(result.totalDistance));

		send(player, "openblocks.misc.pedometer.straght_line_distance", distanceUnit.format(result.straightLineDistance));
		send(player, "openblocks.misc.pedometer.straigh_line_speed", speedUnit.format(result.straightLineSpeed()));

		send(player, "openblocks.misc.pedometer.last_check_speed", speedUnit.format(result.lastCheckSpeed()));
		send(player, "openblocks.misc.pedometer.last_check_distance", distanceUnit.format(result.lastCheckDistance));
		send(player, "openblocks.misc.pedometer.last_check_time", result.lastCheckTime);

		send(player, "openblocks.misc.pedometer.total_time", result.totalTime);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slotId, boolean isSelected) {
		if (world.isRemote && slotId < 9) {
			PedometerState state = PedometerHandler.getProperty(entity);
			if (state.isRunning()) state.update(entity);
		}
	}

	// TODO 1.8.9 actually change model based on this
	public static boolean isPlayerMoving(EntityPlayer player) {
		return player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ > 0.01;
	}

}
