package openblocks.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class WallpaperData extends WorldSavedData {
	
	public int[] colorData;
	
	public WallpaperData(int id) {
		super(getWallpaperName(id));
	}

	public static String getWallpaperName(int id) {
		return "wallpaper_" + id;
	}
	
	public void setColorData(int[] data) {
		this.colorData = data;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		colorData = tag.getIntArray("colors");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setIntArray("colors", colorData);
	}

}
