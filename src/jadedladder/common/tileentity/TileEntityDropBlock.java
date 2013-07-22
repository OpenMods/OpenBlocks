package jadedladder.common.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import jadedladder.JadedLadder;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityDropBlock extends TileEntity {
	
	private int lowerLevel = 0;
	private int upperLevel = 0;
	
	private HashMap<String, Integer> cooldown = new HashMap<String, Integer>();
	
	public int colorIndex = 0;
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if (!worldObj.isRemote) {

			Iterator<Entry<String, Integer>> cooldownIter = cooldown.entrySet().iterator();
			while (cooldownIter.hasNext()) {
				Entry<String, Integer> entry = cooldownIter.next();
				int less = entry.getValue() - 1;
				entry.setValue(less);
				if (less == 0) {
					cooldownIter.remove();
				}
			}

			List<EntityPlayer> playersInRange = (List<EntityPlayer>)worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 3, zCoord + 1));
			
			if (playersInRange.size() > 0) {

				try {
					upperLevel = findLevel(ForgeDirection.UP);
					lowerLevel = findLevel(ForgeDirection.DOWN);
				}catch(Exception e) {
					upperLevel = 0;
					lowerLevel = 0;
					return;
				}
				
				boolean doTeleport = false;
				int teleportTo = 0;

				for (EntityPlayer player : playersInRange) {
					if (cooldown.containsKey(player.username)) {
						continue;
					}
					if (lowerLevel != 0 && player.isSneaking()) {
						doTeleport = true;
						teleportTo = lowerLevel;
					}else if (upperLevel != 0 && player.posY > yCoord + 1.2) {
						doTeleport = true;
						teleportTo = upperLevel;
					}
					if (doTeleport) {
						player.setPositionAndUpdate(player.posX, teleportTo + 1.1, player.posZ);
						worldObj.playSoundAtEntity(player, "jadedladder.teleport", 1F, 1F);
						TileEntity targetTile = worldObj.getBlockTileEntity(xCoord, teleportTo, zCoord);
						if (targetTile instanceof TileEntityDropBlock) {
							((TileEntityDropBlock)targetTile).addPlayerCooldown(player);
						}
					}
					
				}
			}
			
			lowerLevel = 0;
			upperLevel = 0;
		}
		
	}
	
	private void addPlayerCooldown(EntityPlayer player) {
		cooldown.put(player.username, 10);
	}

	private int findLevel(ForgeDirection direction) throws Exception {
		if (direction != ForgeDirection.UP && direction != ForgeDirection.DOWN) {
			throw new Exception("Must be either up or down... for now");
		}
		
		for (int y = 2; y < 30; y++) {
			int yPos = yCoord + (y * direction.offsetY);
			if (worldObj.blockExists(xCoord, yPos, zCoord)) {
				int blockId = worldObj.getBlockId(xCoord, yPos, zCoord);
				if (blockId == JadedLadder.Config.blockDropId) {
					if (worldObj.isAirBlock(xCoord, yPos+1, zCoord) && worldObj.isAirBlock(xCoord, yPos+2, zCoord)) {
						return yPos;
					}
					return 0;
				}else if (blockId != 0) {
					return 0;
				}
			}else {
				return 0;
			}
		}
		return 0;
	}

	public void onActivated(EntityPlayer player) {
		ItemStack stack = player.getHeldItem();
		if (stack != null) {
			Item item = stack.getItem();
			if (item instanceof ItemDye) {
				System.out.println(stack.getItemDamage());
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, stack.getItemDamage(), 3);
			}
			
		}
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

}
