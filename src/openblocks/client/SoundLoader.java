package openblocks.client;

import openblocks.OpenBlocks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SoundLoader {
	
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void loadingSounds(SoundLoadEvent event) {
		Minecraft mc = Minecraft.getMinecraft();

		String[] soundFiles = {
				"teleport.ogg"
		};

		File resourcesDirectory = new File(mc.mcDataDir, "resources/openblocks/");

		if (!resourcesDirectory.exists()) {
			resourcesDirectory.mkdir();
		}

		for (String fileName : soundFiles) {
			try {
				File soundFile = new File(resourcesDirectory, fileName);
				if (!soundFile.exists()) {

					InputStream streamIn = OpenBlocks.class.getResourceAsStream("/mods/openblocks/sounds/" + fileName);
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
				System.out.println("Couldnt load "+ fileName);
			}
		}
	}
}
