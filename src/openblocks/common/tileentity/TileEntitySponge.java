package openblocks.common.tileentity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.api.IAwareTile;

public class TileEntitySponge extends OpenTileEntity implements IAwareTile {

	@Override
	public void onBlockBroken() {
		// TODO Auto-generated method stub
		
	}
	
	private void clearupLiquid() {
		if (worldObj.isRemote) {
			return;
		}
		boolean hitLava = false;
		for (int x = -3; x <= 3; x++) {
			for (int y = -3; y <= 3; y++) {
				for (int z = -3; z <= 3; z++) {
					Material material = worldObj.getBlockMaterial(xCoord + x, yCoord + y, zCoord + z);
					if (material.isLiquid()) {
						if (material == Material.lava) {
							hitLava = true;
						}
						worldObj.setBlock(xCoord + x, yCoord + y, zCoord + z, 0, 0, 2);
					}
				}
			}
		}
		if (hitLava) {
			this.sendBlockEvent(0, 0);
		}
	}

	@Override
	public void onBlockAdded() {
		clearupLiquid();
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		clearupLiquid();
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		clearupLiquid();
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		if (worldObj.isRemote) { 
			for (int i = 0; i < 20; i++) {
	            double f = (double)xCoord + worldObj.rand.nextDouble() * 0.1F;
	            double f1 = (double)yCoord + 1.0 + worldObj.rand.nextDouble();
	            double f2 = (double)zCoord + worldObj.rand.nextDouble();
				worldObj.spawnParticle("largesmoke", f, f1, f2, 0.0D, 0.0D, 0.0D);
			}
		}else {
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		}
		return true;
	}

}
