package openblocks.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.api.IAwareTile;
import openblocks.physics.Cloth;
import openblocks.physics.FastVector;
import openblocks.physics.Point;


public class TileEntityClothTest extends OpenTileEntity implements IAwareTile {

	public Cloth cloth;
	
	public TileEntityClothTest() {
		cloth = new Cloth(15, 1, 1);
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		cloth.update();
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side,
			ItemStack stack, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX,
			float hitY, float hitZ) {
		if(worldObj.isRemote) {
			ForgeDirection dir = ForgeDirection.getOrientation(side).getOpposite();
			Point center = cloth.getClosestPoint(new FastVector(0.5, 0.5, 0.5));
			if(center != null) {
				center.applyForce(new FastVector(dir.offsetX, dir.offsetY, dir.offsetZ));
			}
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBlockBroken() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBlockAdded() {
		// TODO Auto-generated method stub
		
	}
}
