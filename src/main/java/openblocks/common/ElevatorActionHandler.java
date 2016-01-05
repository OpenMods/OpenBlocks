package openblocks.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.api.IElevatorBlock;
import openblocks.api.IElevatorBlock.PlayerRotation;
import openblocks.events.ElevatorActionEvent;
import openmods.movement.PlayerMovementEvent;
import openmods.utils.EnchantmentUtils;

import com.google.common.base.Preconditions;

public class ElevatorActionHandler {

	private static class SearchResult {
		public final int level;

		public final PlayerRotation rotation;

		public SearchResult(int level, PlayerRotation rotation) {
			this.level = level;
			this.rotation = rotation;
		}
	}

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

	private static SearchResult findLevel(EntityPlayer player, World world, int x, int y, int z, ForgeDirection direction) {
		Preconditions.checkArgument(direction == ForgeDirection.UP
				|| direction == ForgeDirection.DOWN, "Must be either up or down... for now");

		final IElevatorBlock thisElevatorBlock = (IElevatorBlock)world.getBlock(x, y, z);
		final int thisColor = thisElevatorBlock.getColor(world, x, y, z);

		int blocksInTheWay = 0;
		final int delta = direction.offsetY;
		for (int i = 0; i < Config.elevatorTravelDistance; i++) {
			y += delta;
			if (!world.blockExists(x, y, z)) break;
			if (world.isAirBlock(x, y, z)) continue;

			Block block = world.getBlock(x, y, z);

			if (block instanceof IElevatorBlock) {
				final IElevatorBlock otherElevatorBlock = (IElevatorBlock)block;
				final int otherColor = otherElevatorBlock.getColor(world, x, y, z);
				if (otherColor == thisColor && canTeleportPlayer(player, world, x, y + 1, z)) {
					final PlayerRotation rotation = otherElevatorBlock.getRotation(world, x, y, z);
					return new SearchResult(y, rotation);
				}
			}

			if (!Config.elevatorIgnoreBlocks) {
				ElevatorBlockRules.Action action = ElevatorBlockRules.instance.getActionForBlock(block);
				switch (action) {
					case ABORT:
						return null;
					case IGNORE:
						continue;
					case INCREMENT:
					default:
						break;
				}

				if (++blocksInTheWay > Config.elevatorMaxBlockPassCount) break;
			}
		}

		return null;
	}

	private static void activate(EntityPlayer player, World world, int x, int y, int z, ForgeDirection dir) {
		SearchResult result = findLevel(player, world, x, y, z, dir);
		if (result != null) {
			boolean doTeleport = checkXpCost(player, result);

			if (doTeleport) {
				if (result.rotation != PlayerRotation.NONE) player.rotationYaw = getYaw(result.rotation);
				if (Config.elevatorCenter) player.setPositionAndUpdate(x + 0.5, result.level + 1.1, z + 0.5);
				else player.setPositionAndUpdate(player.posX, result.level + 1.1, player.posZ);
				world.playSoundAtEntity(player, "openblocks:elevator.activate", 1, 1);
			}
		}
	}

	private static float getYaw(PlayerRotation rotation) {
		switch (rotation) {
			case EAST:
				return 90;
			case NORTH:
				return 0;
			case SOUTH:
				return 180;
			case WEST:
				return -90;
			default:
				return 0;
		}
	}

	protected static boolean checkXpCost(EntityPlayer player, SearchResult result) {
		int distance = (int)Math.abs(player.posY - result.level);
		if (Config.elevatorXpDrainRatio == 0 || player.capabilities.isCreativeMode) return true;

		int playerXP = EnchantmentUtils.getPlayerXP(player);
		int neededXP = MathHelper.ceiling_double_int(Config.elevatorXpDrainRatio * distance);
		if (playerXP >= neededXP) {
			EnchantmentUtils.addPlayerXP(player, -neededXP);
			return true;
		}

		return false;
	}

	@SubscribeEvent
	public void onElevatorEvent(ElevatorActionEvent evt) {
		final World world = evt.getWorld();
		final int x = evt.xCoord;
		final int y = evt.yCoord;
		final int z = evt.zCoord;

		if (!(world.getBlock(x, y, z) instanceof IElevatorBlock)) return;

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

		if (block instanceof IElevatorBlock) new ElevatorActionEvent(world.provider.dimensionId, x, y, z, evt.type).sendToServer();

	}
}
