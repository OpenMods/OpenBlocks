package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import openblocks.OpenBlocks;
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
	
	/**
	 * The distance that findBlock will look for another teleport block
	 */
	private static final int BLOCK_SEARCH_DISTANCE = 30;
	/**
	 * Indicates if the player must be looking in the direction they want to teleport
	 */
	private static final boolean MUST_FACE_DIRECTION = true;
	/**
	 * How far a player must be looking in a direction to be teleported 
	 */
	private static final float DIRECTION_MAGNITUDE = 0.95f;
	
	private HashMap<String, Integer> cooldown = new HashMap<String, Integer>();
	
	public int colorIndex = 0; // What is the point in colorIndex if we use Metadata?
	
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
					/* Don't activate when a player is flying around in creative, it's annoying */
					
					if(player.capabilities.isCreativeMode && player.capabilities.isFlying) continue;
					if (lowerLevel != 0 && player.isSneaking() && (!MUST_FACE_DIRECTION || player.getLookVec().yCoord < -DIRECTION_MAGNITUDE)) {
						doTeleport = true;
						teleportTo = lowerLevel;
					/* player.isJumping doesn't seem to work server side ? */
					}else if (upperLevel != 0 && player.posY > yCoord + 1.2 && (!MUST_FACE_DIRECTION || player.getLookVec().yCoord > DIRECTION_MAGNITUDE)) {
						doTeleport = true;
						teleportTo = upperLevel;
					}
					if (doTeleport) {
						player.setPositionAndUpdate(player.posX, teleportTo + 1.1, player.posZ);
						worldObj.playSoundAtEntity(player, "openblocks.teleport", 1F, 1F);
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
		
		for (int y = 2; y < BLOCK_SEARCH_DISTANCE; y++) {
			int yPos = yCoord + (y * direction.offsetY);
			if (worldObj.blockExists(xCoord, yPos, zCoord)) {
				int blockId = worldObj.getBlockId(xCoord, yPos, zCoord);
				if (blockId == OpenBlocks.Config.blockDropId) {
					TileEntity otherBlock = worldObj.getBlockTileEntity(xCoord, yPos, zCoord);					
					// Check that it is a drop block and that it has the same color index.
					if(!(otherBlock instanceof TileEntityDropBlock)) continue;
					if(((TileEntityDropBlock)otherBlock).getBlockMetadata() != this.getBlockMetadata()) continue; 
					
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

	public boolean onActivated(EntityPlayer player) {
		ItemStack stack = player.getHeldItem();
		if (stack != null) {
			Item item = stack.getItem();
			if (item instanceof ItemDye) {
				System.out.println(stack.getItemDamage());
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, stack.getItemDamage(), 3);
		        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		        return true;
			}			
		}
		return false; // Don't update the block and don't block placement if it's not dye we're using
	}

}
