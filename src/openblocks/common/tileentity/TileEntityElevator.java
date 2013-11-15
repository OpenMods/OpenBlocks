package openblocks.common.tileentity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.network.TileEntityMessageEventPacket;

import com.google.common.base.Preconditions;

import cpw.mods.fml.common.network.Player;

public class TileEntityElevator extends OpenTileEntity {

	/**
	 * How far a player must be looking in a direction to be teleported
	 */
	private static final float DIRECTION_MAGNITUDE = 0.95f;

	private HashMap<String, Integer> cooldown = new HashMap<String, Integer>();

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {

		}

	}

	private void addPlayerCooldownToTargetAndNeighbours(EntityPlayer player, int xCoord, int level, int zCoord) {
		for (int x = xCoord - 1; x <= xCoord + 1; x++) {
			for (int z = zCoord - 1; z <= zCoord + 1; z++) {
				TileEntity targetTile = worldObj.getBlockTileEntity(x, level, z);
				if (targetTile instanceof TileEntityElevator) {
					((TileEntityElevator)targetTile).addPlayerCooldown(player);
				}
			}
		}
	}

	private void addPlayerCooldown(EntityPlayer player) {
		cooldown.put(player.username, 6);
	}

	private boolean isPassable(int x, int y, int z, boolean canStandHere) {
		int blockId = worldObj.getBlockId(x, y, z);
		Block block = Block.blocksList[blockId];
		if (canStandHere) { return worldObj.isAirBlock(x, y, z)
				|| block == null
				|| (Config.irregularBlocksArePassable && block.getCollisionBoundingBoxFromPool(worldObj, x, y, z) == null || block.getCollisionBoundingBoxFromPool(worldObj, x, y, z).getAverageEdgeLength() < 0.7); }
		/* Ugly logic makes NC sad :( */
		return !(worldObj.isAirBlock(x, y, z)
				|| Config.elevatorMaxBlockPassCount == -1 || Config.elevatorIgnoreHalfBlocks
				&& !Block.isNormalCube(blockId));
	}

	private int findLevel(ForgeDirection direction) {
		Preconditions.checkArgument(direction == ForgeDirection.UP
				|| direction == ForgeDirection.DOWN, "Must be either up or down... for now");
		
		int blocksInTheWay = 0;
		for (int y = 2; y <= Config.elevatorTravelDistance; y++) {
			int yPos = yCoord + (y * direction.offsetY);
			if (worldObj.blockExists(xCoord, yPos, zCoord)) {
				int blockId = worldObj.getBlockId(xCoord, yPos, zCoord);
				if (blockId == Config.blockElevatorId) {
					TileEntity otherBlock = worldObj.getBlockTileEntity(xCoord, yPos, zCoord);
					// Check that it is a drop block and that it has the same
					// color index.
					if (!(otherBlock instanceof TileEntityElevator)) continue;
					if (((TileEntityElevator)otherBlock).getBlockMetadata() != getBlockMetadata()) continue;
					if (isPassable(xCoord, yPos + 1, zCoord, true)
							&& isPassable(xCoord, yPos + 2, zCoord, true)) { return yPos; }
					return 0;
				} else if (isPassable(xCoord, yPos, zCoord, false)
						&& ++blocksInTheWay > Config.elevatorMaxBlockPassCount) { return 0; }
			} else {
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
				int dmg = stack.getItemDamage();
				// temp hack, dont tell anyone.
				if (dmg == 15) {
					dmg = 0;
				} else if (dmg == 0) {
					dmg = 15;
				}
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, dmg, 3);
				worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
				return true;
			}
		}
		return false; // Don't update the block and don't block placement if
						// it's not dye we're using
	}

	@Override
	protected void initialize() {}

	public void onClientJump() {
		sendEventToServer(new NBTTagByte("type", (byte)1));
	}
	
	public void onServerJump(EntityPlayer player) {
		int level = findLevel(ForgeDirection.UP);
		if (level != 0) {
			player.setPositionAndUpdate(xCoord + 0.5, level + 1.1, zCoord + 0.5);
			worldObj.playSoundAtEntity(player, "openblocks:teleport", 1F, 1F);
		}
	}
	
	public void onServerSneak(EntityPlayer player) {
		int level = findLevel(ForgeDirection.DOWN);
		if (level != 0) {
			player.setPositionAndUpdate(xCoord + 0.5, level + 1.1, zCoord + 0.5);
			worldObj.playSoundAtEntity(player, "openblocks:teleport", 1F, 1F);
		}
	}
	
	public void onClientSneak() {
		sendEventToServer(new NBTTagByte("type", (byte)0));
	}

	public void onEvent(TileEntityMessageEventPacket event) {
		NBTTagByte typeData = (NBTTagByte)event.data;
		if (typeData.data == 1) {
			onServerJump((EntityPlayer)event.player);
		}else {
			onServerSneak((EntityPlayer)event.player);
		}
	}
}
