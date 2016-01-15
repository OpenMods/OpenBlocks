package openblocks.common.tileentity;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

}
