package openblocks.sync;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class SyncMapEntity extends SyncMap {

	@Override
	protected void writeMapType(DataOutputStream dos) throws IOException {
		dos.writeByte(SyncableManager.TYPE_ENTITY);
	}
	
}
