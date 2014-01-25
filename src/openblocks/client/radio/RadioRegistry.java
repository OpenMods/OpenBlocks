package openblocks.client.radio;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.List;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import openmods.Log;

import com.google.common.collect.Lists;
import com.spoledge.aacdecoder.IcyURLStreamHandler;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;

public class RadioRegistry implements IConnectionHandler {

	public static List<WeakReference<StreamPlayer>> players = Lists.newArrayList();

	public static void registerPlayer(StreamPlayer player) {
		players.add(new WeakReference<StreamPlayer>(player));
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {}

	@Override
	public void connectionClosed(INetworkManager manager) {
		for (WeakReference<StreamPlayer> playerRef : players) {
			StreamPlayer player = playerRef.get();
			if (player == null) continue;

			try {
				player.stop();
			} catch (Throwable e) {
				Log.severe(e, "Error during stream player stopping");
			}
		}
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {}

	public static void init() {
		NetworkRegistry.instance().registerConnectionHandler(new RadioRegistry());

		URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
			@Override
			public URLStreamHandler createURLStreamHandler(String protocol) {
				if ("icy".equals(protocol)) return new IcyURLStreamHandler();
				return null;
			}
		});
	}
}
