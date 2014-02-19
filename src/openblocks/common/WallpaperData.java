package openblocks.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class WallpaperData extends WorldSavedData {
	
	public int[] colorData;
	
	public WallpaperData(String id) {
		super(id);
	}
	
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
	
	public void writeToStream(DataOutput output) throws IOException {
		output.writeInt(colorData.length);
		for (int i = 0; i < colorData.length; i++) {
			output.writeInt(colorData[i]);
		}
	}
	
	public void readFromStream(DataInput input) throws IOException {
		int length = input.readInt();
		colorData = new int[length];
		for (int i = 0; i < length; i++) {
			colorData[i] = input.readInt();
		}
	}

}
