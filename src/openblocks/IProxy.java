package openblocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.Player;

public interface IProxy {
	public void init();

	public void postInit();

	public void registerRenderInformation();

	public boolean isServerOnly();

	public boolean isServerThread();

	public World getClientWorld();

	public World getServerWorld(int id);

	public void spawnLiquidSpray(World worldObj, FluidStack water, double x, double y, double z, ForgeDirection sprayDirection, float angleRadians, float spread);

	public EntityPlayer getThePlayer();

	public IGuiHandler createGuiHandler();

	public long getTicks(World worldObj);

	public void sendPacketToPlayer(Player player, Packet packet);

	public void sendPacketToServer(Packet packet);
}
