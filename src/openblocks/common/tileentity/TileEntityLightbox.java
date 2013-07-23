package openblocks.common.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityLightbox extends TileEntity {

	private ForgeDirection surface = ForgeDirection.DOWN;
	private ForgeDirection rotation = ForgeDirection.EAST;
	
	public void setSurface(ForgeDirection surface) {
		this.surface = surface;
	}

	public void setRotation(ForgeDirection rotation) {
		this.rotation = rotation;
	}

	public ForgeDirection getRotation() {
		return rotation;
	}
	
	public ForgeDirection getSurface() {
		return surface;
	}
	
}
