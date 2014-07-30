package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.ElevatorBlockRules;
import openblocks.events.ElevatorActionEvent;
import openmods.events.network.TileEntityMessageEventPacket;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.EnchantmentUtils;

import com.google.common.base.Preconditions;

public class TileEntityElevator extends OpenTileEntity {

	private ForgeDirection direction = ForgeDirection.UNKNOWN;
	
	public ForgeDirection getDirection() {
		return this.direction;
	}
	
	public void setNextDirection() {
		switch(direction) {
			case UNKNOWN:
				direction = ForgeDirection.NORTH;
				break;
			case NORTH:
				direction = ForgeDirection.EAST;
				break;
			case EAST:
				direction = ForgeDirection.SOUTH;
				break;
			case SOUTH:
				direction = ForgeDirection.WEST;
				break;
			case WEST:
				direction = ForgeDirection.UNKNOWN;
				break;
			default: 
				direction = ForgeDirection.UNKNOWN;
				break;
		}
	}
	
	private ForgeDirection getRecievingElevator(int x, int y, int z) {
		TileEntity te = worldObj.getTileEntity(x, y, z);
		if (te instanceof TileEntityElevator && te != null) {
			return ((TileEntityElevator) te).getDirection();
		} else {
			return ForgeDirection.UNKNOWN;
		}
	}
	
	private boolean canTeleportPlayer(int x, int y, int z) {
		Block block = worldObj.getBlock(x, y, z);
		if (block == null || block.isAir(worldObj, x, y, z)) return true;

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

			Block block = worldObj.getBlock(xCoord, y, zCoord);

			if (block == OpenBlocks.Blocks.elevator) {
				TileEntity otherBlock = worldObj.getTileEntity(xCoord, y, zCoord);
				if (otherBlock instanceof TileEntityElevator) {
					final int otherColor = otherBlock.getBlockMetadata();
					if (otherColor == thisColor &&
							canTeleportPlayer(xCoord, y + 1, zCoord) &&
							canTeleportPlayer(xCoord, y + 2, zCoord)) return y;
				}
			}

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
					EnchantmentUtils.addPlayerXP(player, -distance);
					doTeleport = true;
				}
			} else {
				doTeleport = true;
			}
			if (doTeleport) {
				switch(getRecievingElevator(xCoord, level, zCoord)) {
					case NORTH:
						player.rotationYaw = 180;
						break;
					case EAST:
						player.rotationYaw = 270;
						break;
					case SOUTH:
						player.rotationYaw = 0;
						break;
					case WEST:
						player.rotationYaw = 90;
						break;
					default:
						break;
				}
				player.setPositionAndUpdate(xCoord + 0.5, level + 1.1, zCoord + 0.5);
				worldObj.playSoundAtEntity(player, "openblocks:elevator.activate", 1F, 1F);
			}
		}
	}

	@Override
	public void onEvent(TileEntityMessageEventPacket event) {
		if (event instanceof ElevatorActionEvent) {
			final ElevatorActionEvent elevatorEvent = (ElevatorActionEvent)event;
			if (elevatorEvent.sender != null) switch (elevatorEvent.type) {
				case JUMP:
					activate(event.sender, ForgeDirection.UP);
					break;
				case SNEAK:
					activate(event.sender, ForgeDirection.DOWN);
					break;
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.direction = ForgeDirection.valueOf(nbt.getString("direction"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setString("direction", direction.name());
	}

}
