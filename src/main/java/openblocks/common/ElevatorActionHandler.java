package openblocks.common;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.api.IElevatorBlock;
import openblocks.api.IElevatorBlock.PlayerRotation;
import openblocks.events.ElevatorActionEvent;
import openmods.movement.PlayerMovementEvent;
import openmods.utils.EnchantmentUtils;

import com.google.common.base.Preconditions;

public class ElevatorActionHandler {

	private static class SearchResult extends BlockPos {
		public final PlayerRotation rotation;

		public SearchResult(Vec3i other, PlayerRotation rotation) {
			super(other);
			this.rotation = rotation;
		}
	}

	private static boolean canTeleportPlayer(World world, BlockPos pos) {
		if (world.isAirBlock(pos)) return true;

		if (!Config.irregularBlocksArePassable) return false;
		final IBlockState blockState = world.getBlockState(pos);
		final Block block = blockState.getBlock();

		final AxisAlignedBB aabb = block.getCollisionBoundingBox(world, pos, blockState);
		return aabb == null || aabb.getAverageEdgeLength() < 0.7;
	}

	private static boolean canTeleportPlayer(EntityPlayer entity, World world, BlockPos pos) {
		final AxisAlignedBB aabb = entity.getEntityBoundingBox();
		double height = Math.abs(aabb.maxY - aabb.minY);
		int blockHeight = Math.max(1, MathHelper.ceiling_double_int(height));

		for (int dy = 0; dy < blockHeight; dy++)
			if (!canTeleportPlayer(world, pos.up(dy))) return false;

		return true;
	}

	private static SearchResult findLevel(EntityPlayer player, World world, IBlockState thisBlockState, BlockPos pos, EnumFacing direction) {
		Preconditions.checkArgument(direction == EnumFacing.UP
				|| direction == EnumFacing.DOWN, "Must be either up or down... for now");

		final IElevatorBlock thisElevatorBlock = (IElevatorBlock)thisBlockState.getBlock();
		final EnumDyeColor thisColor = thisElevatorBlock.getColor(world, pos, thisBlockState);

		int blocksInTheWay = 0;
		BlockPos searchPos = pos;
		for (int i = 0; i < Config.elevatorTravelDistance; i++) {
			searchPos = searchPos.offset(direction);
			if (!world.isBlockLoaded(searchPos)) break;
			if (world.isAirBlock(searchPos)) continue;

			final IBlockState blockState = world.getBlockState(searchPos);
			final Block block = blockState.getBlock();

			if (block instanceof IElevatorBlock) {
				final IElevatorBlock otherElevatorBlock = (IElevatorBlock)block;
				final EnumDyeColor otherColor = otherElevatorBlock.getColor(world, searchPos, blockState);
				if (otherColor == thisColor && canTeleportPlayer(player, world, searchPos.up())) {
					final PlayerRotation rotation = otherElevatorBlock.getRotation(world, searchPos, blockState);
					return new SearchResult(searchPos, rotation);
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

	private static void activate(EntityPlayer player, World world, IBlockState state, BlockPos pos, EnumFacing dir) {
		SearchResult result = findLevel(player, world, state, pos, dir);
		if (result != null) {
			boolean doTeleport = checkXpCost(player, result);

			if (doTeleport) {
				if (result.rotation != PlayerRotation.NONE) player.rotationYaw = getYaw(result.rotation);
				if (Config.elevatorCenter) player.setPositionAndUpdate(result.getX() + 0.5, result.getY() + 1.1, result.getZ() + 0.5);
				else player.setPositionAndUpdate(player.posX, result.getY() + 1.1, player.posZ);
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
		int distance = (int)Math.abs(player.posY - result.getY());
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

		final IBlockState blockState = world.getBlockState(evt.blockPos);
		if (!(blockState.getBlock() instanceof IElevatorBlock)) return;

		if (evt.sender != null) {
			if (evt.sender.ridingEntity != null) return;

			switch (evt.type) {
				case JUMP:
					activate(evt.sender, world, blockState, evt.blockPos, EnumFacing.UP);
					break;
				case SNEAK:
					activate(evt.sender, world, blockState, evt.blockPos, EnumFacing.DOWN);
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
		final int y = MathHelper.floor_double(player.getEntityBoundingBox().minY) - 1;
		final int z = MathHelper.floor_double(player.posZ);
		final BlockPos pos = new BlockPos(x, y, z);
		final Block block = world.getBlockState(pos).getBlock();

		if (block instanceof IElevatorBlock) new ElevatorActionEvent(world.provider.getDimensionId(), pos, evt.type).sendToServer();

	}
}
