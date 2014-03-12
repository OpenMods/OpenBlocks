package openblocks.client;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SoundLoader {

	private final static String[] soundFiles = new String[] {
			"teleport.ogg",
			"open.ogg",
			"close.ogg",
			"beartrapclose.ogg",
			"beartrapcloseb.ogg",
			"beartrapopen.ogg",
			"slowpokenom.ogg",
			"feet.ogg",
			"chomp.ogg",
			"mortar.ogg",
			"fill.ogg",
			"draw1.ogg", "draw2.ogg", "draw3.ogg", "draw4.ogg",
			"beep.ogg",
			"wipe.ogg",
			"cannon.ogg",
			"fart1.ogg", "fart2.ogg", "fart3.ogg", "fart4.ogg",
			"radio.ogg",
			"mosquito.ogg",
			"alarmclock.ogg",
			"vibrate.ogg"
	};

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void loadingSounds(SoundLoadEvent event) {
		for (String soundFile : soundFiles) {
			event.manager.addSound("openblocks:" + soundFile);
		}
	}

}
