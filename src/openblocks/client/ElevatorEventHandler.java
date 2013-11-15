package openblocks.client;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import openblocks.OpenBlocks;
import openblocks.client.events.PlayerJumpEvent;
import openblocks.client.events.PlayerSneakEvent;
import openblocks.common.tileentity.TileEntityElevator;

public class ElevatorEventHandler {
	@ForgeSubscribe
	public void onSneak(PlayerSneakEvent event) {
		TileEntity te = getTileUnderPlayer(TileEntityElevator.class);
		if (te instanceof TileEntityElevator) {
			((TileEntityElevator)te).onClientSneak();
		}
	}
	
	@ForgeSubscribe
	public void onJump(PlayerJumpEvent event) {
		TileEntity te = getTileUnderPlayer(TileEntityElevator.class);
		if (te instanceof TileEntityElevator) {
			((TileEntityElevator)te).onClientJump();
		}
	}
	
	private TileEntity getTileUnderPlayer(Class<? extends TileEntity> klazz) {
		World world = Minecraft.getMinecraft().theWorld;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (world != null && player != null) {
			int x = (int)player.posX;
			int y = (int)(player.posY - player.yOffset - 0.7);
			int z = (int)player.posZ;
			System.out.println(x);
			return world.getBlockTileEntity(x, y, z);
		}
		return null;
	}
}
