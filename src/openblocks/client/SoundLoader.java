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

	String[] soundFiles = new String[] { "teleport.ogg", "open.ogg", "close.ogg" };
	String[] streamingFiles = new String[] {};

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void loadingSounds(SoundLoadEvent event) {
		loadSounds(event, event.manager.soundPoolSounds, soundFiles);
		loadSounds(event, event.manager.soundPoolStreaming, streamingFiles);
	}

	public void loadSounds(SoundLoadEvent event, SoundPool pool, String[] fileNames) {
		Minecraft mc = Minecraft.getMinecraft();

		File resourcesDirectory = new File(mc.mcDataDir, "resources/openblocks/");

		if (!resourcesDirectory.exists()) {
			resourcesDirectory.mkdir();
		}

		for (String fileName : fileNames) {
			try {
				File soundFile = new File(resourcesDirectory, fileName);
				if (!soundFile.exists()) {

					InputStream streamIn = OpenBlocks.class.getResourceAsStream("/mods/openblocks/sounds/"
							+ fileName);
					BufferedOutputStream streamOut = new BufferedOutputStream(new FileOutputStream(soundFile));
					byte[] buffer = new byte[1024];
					for (int len = 0; (len = streamIn.read(buffer)) >= 0;) {
						streamOut.write(buffer, 0, len);
					}
					streamIn.close();
					streamOut.close();
				}
				event.manager.soundPoolSounds.addSound("openblocks/" + fileName, soundFile);
			} catch (Exception e) {
				//System.out.println("Couldnt load " + fileName);
			}
		}
	}
}
