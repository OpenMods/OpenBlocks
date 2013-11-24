package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import openmods.api.IAwareTile;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.BlockNotifyFlags;

public class TileEntitySponge extends OpenTileEntity implements IAwareTile {

	private void clearupLiquid() {
		if (worldObj.isRemote) { return; }
		boolean hitLava = false;
		for (int x = -3; x <= 3; x++) {
			for (int y = -3; y <= 3; y++) {
				for (int z = -3; z <= 3; z++) {
					Material material = worldObj.getBlockMaterial(xCoord + x, yCoord
							+ y, zCoord + z);
					if (material.isLiquid()) {
						if (material == Material.lava) {
							hitLava = true;
						}
						worldObj.setBlock(xCoord + x, yCoord + y, zCoord + z, 0, 0, BlockNotifyFlags.SEND_TO_CLIENTS);
					}
				}
			}
		}
		if (hitLava) {
			sendBlockEvent(0, 0);
		}
	}

	@Override
	public void onBlockAdded() {
		clearupLiquid();
	}

	@Override
	public void onBlockBroken() {}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
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
	public boolean receiveClientEvent(int eventId, int eventParam) {
		if (worldObj.isRemote) {
			for (int i = 0; i < 20; i++) {
				double f = xCoord + worldObj.rand.nextDouble() * 0.1;
				double f1 = yCoord + 1.0 + worldObj.rand.nextDouble();
				double f2 = zCoord + worldObj.rand.nextDouble();
				worldObj.spawnParticle("largesmoke", f, f1, f2, 0.0D, 0.0D, 0.0D);
			}
		} else {
			worldObj.setBlock(xCoord, yCoord, zCoord, Block.fire.blockID, 0, BlockNotifyFlags.ALL);
		}
		return true;
	}

}
