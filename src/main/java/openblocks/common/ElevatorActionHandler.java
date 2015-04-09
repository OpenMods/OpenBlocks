package openblocks.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.events.ElevatorActionEvent;
import openmods.movement.PlayerMovementEvent;
import openmods.utils.EnchantmentUtils;

import com.google.common.base.Preconditions;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ElevatorActionHandler {

	private static boolean canTeleportPlayer(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (block == null || block.isAir(world, x, y, z)) return true;

		if (!Config.irregularBlocksArePassable) return false;

		final AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(world, x, y, z);
		return aabb == null || aabb.getAverageEdgeLength() < 0.7;
	}

	private static boolean canTeleportPlayer(EntityPlayer entity, World world, int x, int y, int z) {
		final AxisAlignedBB aabb = entity.boundingBox;
		double height = Math.abs(aabb.maxY - aabb.minY);
		int blockHeight = Math.max(1, MathHelper.ceiling_double_int(height));

		for (int dy = 0; dy < blockHeight; dy++)
			if (!canTeleportPlayer(world, x, y + dy, z)) return false;

		return true;
	}

	private static int findLevel(EntityPlayer player, World world, int x, int y, int z, ForgeDirection direction) {
		Preconditions.checkArgument(direction == ForgeDirection.UP
				|| direction == ForgeDirection.DOWN, "Must be either up or down... for now");

		final int thisColor = world.getBlockMetadata(x, y, z);
		int blocksInTheWay = 0;
		final int delta = direction.offsetY;
		for (int i = 0; i < Config.elevatorTravelDistance; i++) {
			y += delta;
			if (!world.blockExists(x, y, z)) break;
			if (world.isAirBlock(x, y, z)) continue;

			Block block = world.getBlock(x, y, z);

			if (block == OpenBlocks.Blocks.elevator) {
				final int otherColor = world.getBlockMetadata(x, y, z);
				if (otherColor == thisColor && canTeleportPlayer(player, world, x, y + 1, z)) return y;
			}

			if (!Config.elevatorIgnoreBlocks) {
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
		}

		return -1;
	}

	private static void activate(EntityPlayer player, World world, int x, int y, int z, ForgeDirection dir) {
		int level = findLevel(player, world, x, y, z, dir);
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
				player.setPositionAndUpdate(x + 0.5, level + 1.1, z + 0.5);
				world.playSoundAtEntity(player, "openblocks:elevator.activate", 1, 1);
			}
		}
	}

	@SubscribeEvent
	public void onElevatorEvent(ElevatorActionEvent evt) {
		final World world = evt.getWorld();
		final int x = evt.xCoord;
		final int y = evt.yCoord;
		final int z = evt.zCoord;

		if (world.getBlock(x, y, z) != OpenBlocks.Blocks.elevator) return;

		if (evt.sender != null) {
			if (evt.sender.ridingEntity != null) return;

			switch (evt.type) {
				case JUMP:
					activate(evt.sender, world, x, y, z, ForgeDirection.UP);
					break;
				case SNEAK:
					activate(evt.sender, world, x, y, z, ForgeDirection.DOWN);
					break;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerMovement(PlayerMovementEvent evt) {
		final EntityPlayer player = evt.entityPlayer;
		if (player == null) return;

		final World world = player.worldObj;
		if (world == null) return;

		final int x = MathHelper.floor_double(player.posX);
		final int y = MathHelper.floor_double(player.boundingBox.minY) - 1;
		final int z = MathHelper.floor_double(player.posZ);
		Block block = world.getBlock(x, y, z);

		if (block == OpenBlocks.Blocks.elevator) new ElevatorActionEvent(world.provider.dimensionId, x, y, z, evt.type).sendToServer();

	}
}
