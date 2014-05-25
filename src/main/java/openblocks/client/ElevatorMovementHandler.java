package openblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import openblocks.events.ElevatorActionEvent;
import openmods.movement.PlayerMovementEvent;
import openmods.tileentity.OpenTileEntity;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ElevatorMovementHandler {

	private static OpenTileEntity getTileUnderPlayer(EntityPlayer player) {
		World world = Minecraft.getMinecraft().theWorld;
		if (world != null && player != null) {
			int x = MathHelper.floor_double(player.posX);
			int y = MathHelper.floor_double(player.boundingBox.minY) - 1;
			int z = MathHelper.floor_double(player.posZ);
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if (te instanceof OpenTileEntity) return (OpenTileEntity)te;
		}
		return null;
	}

	@SubscribeEvent
	public void onPlayerMovement(PlayerMovementEvent evt) {
		OpenTileEntity te = getTileUnderPlayer(evt.entityPlayer);
		if (te == null) return;
		new ElevatorActionEvent(te, evt.type).sendToServer();
	}

}
