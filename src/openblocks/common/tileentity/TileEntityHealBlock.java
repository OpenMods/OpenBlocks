package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import openblocks.OpenBlocks;
import openblocks.api.IAwareTile;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableDouble;
import openblocks.sync.SyncableInt;
import openblocks.utils.Coord;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityHealBlock extends OpenTileEntity {
	
	int value = 0;
	
	@Override
	public void updateEntity() {
		super.updateEntity();

		if (worldObj.isRemote) return;

		List<EntityPlayer> playersOnTop = (List<EntityPlayer>)worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1));
		if (worldObj.getTotalWorldTime() % 20 == 0) {
			for (EntityPlayer player : playersOnTop) {
				if (!player.capabilities.isCreativeMode) {
					if (player.getHealth() < player.maxHealth) player.heal(1);
					if (player.getFoodStats().needFood()) player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel() + 1);
				}
			}
		}
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		
	}

}
