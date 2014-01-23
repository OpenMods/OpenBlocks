package openblocks.client;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import openblocks.client.radio.StreamPlayer;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class RadioRegistry implements IConnectionHandler {

	public static ArrayList<WeakReference<StreamPlayer>> players = new ArrayList<WeakReference<StreamPlayer>>();

	public static void registerPlayer(StreamPlayer player) {
		players.add(new WeakReference<StreamPlayer>(player));
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionClosed(INetworkManager manager) {
		for (WeakReference<StreamPlayer> playerRef : players) {
			StreamPlayer player = playerRef.get();
			if (player != null) {
				try {
					player.stop();
				} catch (Exception e) {
					// meh
				}
			}
		}
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
		// TODO Auto-generated method stub

	}

}
