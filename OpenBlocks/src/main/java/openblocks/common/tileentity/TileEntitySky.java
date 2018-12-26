package openblocks.common.tileentity;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.tileentity.OpenTileEntity;

public class TileEntitySky extends OpenTileEntity {

	public TileEntitySky() {}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 256 * 256;
	}

}
