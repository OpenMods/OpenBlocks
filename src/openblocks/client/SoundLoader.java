package openblocks.client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundPool;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import openblocks.OpenBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SoundLoader {

	String[] soundFiles = new String[] { "teleport.ogg", "open.ogg", "close.ogg", "beartrapclose.ogg", "beartrapcloseb.ogg", "beartrapopen.ogg", "slowpokenom.ogg", "feet.ogg", "chomp.ogg", "mortar.ogg" };
	
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void loadingSounds(SoundLoadEvent event) {

		Minecraft mc = Minecraft.getMinecraft();

		for (String soundFile : soundFiles) {
			event.manager.soundPoolSounds.addSound("openblocks:" + soundFile);
		}
	}

}
