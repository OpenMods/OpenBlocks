package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.ElevatorBlockRules;
import openblocks.events.ElevatorActionEvent;
import openmods.network.events.TileEntityMessageEventPacket;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.EnchantmentUtils;

import com.google.common.base.Preconditions;

public class TileEntityElevator extends OpenTileEntity {

	private boolean canTeleportPlayer(int x, int y, int z) {
		int blockId = worldObj.getBlockId(x, y, z);
		Block block = Block.blocksList[blockId];
		if (block == null || block.isAirBlock(worldObj, x, y, z)) return true;

		if (!Config.irregularBlocksArePassable) return false;

		final AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(worldObj, x, y, z);
		return aabb == null || aabb.getAverageEdgeLength() < 0.7;
	}

	private int findLevel(ForgeDirection direction) {
		Preconditions.checkArgument(direction == ForgeDirection.UP
				|| direction == ForgeDirection.DOWN, "Must be either up or down... for now");

		final int thisColor = getMetadata();
		int blocksInTheWay = 0;
		final int delta = direction.offsetY;
		for (int i = 0, y = yCoord; i < Config.elevatorTravelDistance; i++) {
			y += delta;
			if (!worldObj.blockExists(xCoord, y, zCoord)) break;
			if (worldObj.isAirBlock(xCoord, y, zCoord)) continue;

			int blockId = worldObj.getBlockId(xCoord, y, zCoord);

			if (blockId == Config.blockElevatorId) {
				TileEntity otherBlock = worldObj.getBlockTileEntity(xCoord, y, zCoord);
				if (otherBlock instanceof TileEntityElevator) {
					final int otherColor = otherBlock.getBlockMetadata();
					if (otherColor == thisColor &&
							canTeleportPlayer(xCoord, y + 1, zCoord) &&
							canTeleportPlayer(xCoord, y + 2, zCoord)) return y;
				}
			}

			Block block = Block.blocksList[blockId];
			ElevatorBlockRules.Action action = ElevatorBlockRules.instance.getActionForBlock(block);
			switch (action) {
				case ABORT:
					return -1;
				case IGNORE:
					continue;
				case INCREMENT:
				default:
					break;
			}

			if (++blocksInTheWay > Config.elevatorMaxBlockPassCount) break;
		}

		return -1;
	}

	@Override
	protected void initialize() {}

	private void activate(EntityPlayer player, ForgeDirection dir) {
		int level = findLevel(dir);
		if (level >= 0) {
			int distance = (int)Math.abs(player.posY - level);
			boolean drainXP = Config.elevatorDrainsXP && !player.capabilities.isCreativeMode;
			boolean doTeleport = false;
			if (drainXP) {
				int playerXP = EnchantmentUtils.getPlayerXP(player);
				if (playerXP >= distance) {
					EnchantmentUtils.drainPlayerXP(player, distance);
					doTeleport = true;
				}
			} else {
				doTeleport = true;
			}
			if (doTeleport) {
				player.setPositionAndUpdate(xCoord + 0.5, level + 1.1, zCoord + 0.5);
				worldObj.playSoundAtEntity(player, "openblocks:teleport", 1F, 1F);
			}
		}
	}

	@Override
	public void onEvent(TileEntityMessageEventPacket event) {
		if (event instanceof ElevatorActionEvent) {
			switch (((ElevatorActionEvent)event).type) {
				case JUMP:
					activate((EntityPlayer)event.player, ForgeDirection.UP);
					break;
				case SNEAK:
					activate((EntityPlayer)event.player, ForgeDirection.DOWN);
					break;
			}
		}
	}
}
