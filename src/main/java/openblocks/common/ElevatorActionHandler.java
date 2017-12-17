package openblocks.common;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.api.ElevatorCheckEvent;
import openblocks.api.IElevatorBlock;
import openblocks.api.IElevatorBlock.PlayerRotation;
import openblocks.events.ElevatorActionEvent;
import openmods.movement.PlayerMovementEvent;
import openmods.utils.EnchantmentUtils;

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
		final AxisAlignedBB aabb = blockState.getCollisionBoundingBox(world, pos);
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

	private static ElevatorCheckEvent checkIsElevator(EntityPlayer player, World world, BlockPos pos, IBlockState state) {
		final ElevatorCheckEvent evt = new ElevatorCheckEvent(world, pos, state, player);

		final Block block = state.getBlock();
		if (block instanceof IElevatorBlock) {
			final IElevatorBlock elevatorBlock = (IElevatorBlock)block;
			evt.setColor(elevatorBlock.getColor(world, pos, state));
			evt.setRotation(elevatorBlock.getRotation(world, pos, state));
		}

		ElevatorBlockRules.instance.configureEvent(evt);

		MinecraftForge.EVENT_BUS.post(evt);

		return evt;
	}

	private static SearchResult findLevel(EntityPlayer player, World world, EnumDyeColor thisColor, BlockPos pos, EnumFacing searchDirection) {
		Preconditions.checkArgument(searchDirection == EnumFacing.UP
				|| searchDirection == EnumFacing.DOWN, "Must be either up or down... for now");

		int blocksInTheWay = 0;
		BlockPos searchPos = pos;
		for (int i = 0; i < Config.elevatorTravelDistance; i++) {
			searchPos = searchPos.offset(searchDirection);
			if (!world.isBlockLoaded(searchPos)) break;
			if (world.isAirBlock(searchPos)) continue;

			final IBlockState blockState = world.getBlockState(searchPos);
			final ElevatorCheckEvent elevatorCheckResult = checkIsElevator(player, world, searchPos, blockState);

			if (elevatorCheckResult.isElevator()) {
				final EnumDyeColor otherColor = elevatorCheckResult.getColor();
				if (otherColor == thisColor && canTeleportPlayer(player, world, searchPos.up())) {
					final PlayerRotation rotation = elevatorCheckResult.getRotation();
					return new SearchResult(searchPos, rotation);
				}
			}

			if (!Config.elevatorIgnoreBlocks) {
				ElevatorBlockRules.Action action = ElevatorBlockRules.instance.getActionForBlock(blockState);
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

	private static void activate(EntityPlayer player, World world, EnumDyeColor color, BlockPos pos, EnumFacing dir) {
		SearchResult result = findLevel(player, world, color, pos, dir);
		if (result != null) {
			boolean doTeleport = checkXpCost(player, result);

			if (doTeleport) {
				if (result.rotation != PlayerRotation.NONE) player.rotationYaw = getYaw(result.rotation);
				if (Config.elevatorCenter) player.setPositionAndUpdate(result.getX() + 0.5, result.getY() + 1.1, result.getZ() + 0.5);
				else player.setPositionAndUpdate(player.posX, result.getY() + 1.1, player.posZ);
				world.playSound(null, player.getPosition(), OpenBlocks.Sounds.BLOCK_ELEVATOR_ACTIVATE, SoundCategory.BLOCKS, 1, 1);
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
		final EntityPlayer player = evt.sender;
		if (player == null) return;

		final World world = player.worldObj;
		if (world == null) return;

		final int x = MathHelper.floor_double(player.posX);
		final int y = MathHelper.floor_double(player.getEntityBoundingBox().minY) - 1;
		final int z = MathHelper.floor_double(player.posZ);
		final BlockPos blockPos = new BlockPos(x, y, z);

		if (evt.sender != null) {
			if (evt.sender.isRiding()) return;

			final IBlockState blockState = world.getBlockState(blockPos);
			final ElevatorCheckEvent elevatorCheckResult = checkIsElevator(evt.sender, world, blockPos, blockState);

			if (elevatorCheckResult.isElevator()) {
				switch (evt.type) {
					case JUMP:
						activate(evt.sender, world, elevatorCheckResult.getColor(), blockPos, EnumFacing.UP);
						break;
					case SNEAK:
						activate(evt.sender, world, elevatorCheckResult.getColor(), blockPos, EnumFacing.DOWN);
						break;
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerMovement(PlayerMovementEvent evt) {
		new ElevatorActionEvent(evt.type).sendToServer();
	}
}
