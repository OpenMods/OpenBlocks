package openblocks.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import openmods.tileentity.OpenTileEntity;

public class TileEntitySky extends OpenTileEntity {

	public TileEntitySky() {}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1 || pass == 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 256 * 256;
	}

	@Override
	public boolean canUpdate() {
		return false;
	}
}
