package openblocks.common;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.api.ElevatorCheckEvent;
import openblocks.api.IElevatorBlock;
import openblocks.api.IElevatorBlock.PlayerRotation;
import openblocks.events.ElevatorActionEvent;
import openmods.movement.PlayerMovementEvent;
import openmods.utils.EnchantmentUtils;

public class ElevatorActionHandler {
	private static class SearchResult {
		private final BlockPos pos;
		public final PlayerRotation rotation;

		public SearchResult(BlockPos pos, PlayerRotation rotation) {
			this.pos = pos.toImmutable();
			this.rotation = rotation;
		}
	}

	private static boolean canTeleportPlayer(World world, BlockPos pos) {
		if (world.isAirBlock(pos)) {
			return true;
		}

		if (!Config.irregularBlocksArePassable) {
			return false;
		}
		final BlockState blockState = world.getBlockState(pos);
		final VoxelShape shape = blockState.getCollisionShape(world, pos);
		return shape.getBoundingBox().getAverageEdgeLength() < 0.7;
	}

	private static boolean canTeleportPlayer(PlayerEntity entity, World world, BlockPos pos) {
		final AxisAlignedBB aabb = entity.getBoundingBox();
		double height = Math.abs(aabb.maxY - aabb.minY);
		int blockHeight = Math.max(1, MathHelper.ceil(height));

		for (int dy = 0; dy < blockHeight; dy++) {
			if (!canTeleportPlayer(world, pos.up(dy))) {
				return false;
			}
		}

		return true;
	}

	private static ElevatorCheckEvent checkIsElevator(PlayerEntity player, World world, BlockPos pos, BlockState state) {
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

	@Nullable
	private static SearchResult findLevel(PlayerEntity player, World world, DyeColor thisColor, BlockPos pos, Direction.AxisDirection searchDirection) {
		int blocksInTheWay = 0;
		final BlockPos.Mutable searchPos = pos.toMutable();
		for (int i = 0; i < Config.elevatorTravelDistance; i++) {
			searchPos.move(0, searchDirection.getOffset(), 0);
			if (!world.isBlockLoaded(searchPos)) {
				break;
			}
			if (world.isAirBlock(searchPos)) {
				continue;
			}

			final BlockState blockState = world.getBlockState(searchPos);
			final ElevatorCheckEvent elevatorCheckResult = checkIsElevator(player, world, searchPos, blockState);

			if (elevatorCheckResult.isElevator()) {
				final DyeColor otherColor = elevatorCheckResult.getColor();
				if (otherColor == thisColor && canTeleportPlayer(player, world, searchPos.up())) {
					final PlayerRotation rotation = elevatorCheckResult.getRotation();
					return new SearchResult(searchPos, rotation);
				}
			}

			if (!Config.elevatorIgnoreBlocks) {
				ElevatorBlockRules.Action action = ElevatorBlockRules.instance.getActionForBlock(world, searchPos, blockState);
				switch (action) {
					case ABORT:
						return null;
					case IGNORE:
						continue;
					case INCREMENT:
					default:
						break;
				}

				if (++blocksInTheWay > Config.elevatorMaxBlockPassCount) {
					break;
				}
			}
		}

		return null;
	}

	private static void activate(PlayerEntity player, World world, DyeColor color, BlockPos pos, Direction.AxisDirection dir) {
		SearchResult result = findLevel(player, world, color, pos, dir);
		if (result != null) {
			boolean doTeleport = checkXpCost(player, result);

			if (doTeleport) {
				if (result.rotation != PlayerRotation.NONE) {
					player.rotationYaw = getYaw(result.rotation);
				}
				if (Config.elevatorCenter) {
					player.setPositionAndUpdate(result.pos.getX() + 0.5, result.pos.getY() + 1.1, result.pos.getZ() + 0.5);
				} else {
					player.setPositionAndUpdate(player.getPosX(), result.pos.getY() + 1.1, player.getPosZ());
				}
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

	protected static boolean checkXpCost(PlayerEntity player, SearchResult result) {
		int distance = (int)Math.abs(player.getPosY() - result.pos.getY());
		if (Config.elevatorXpDrainRatio == 0 || player.abilities.isCreativeMode) {
			return true;
		}

		int playerXP = EnchantmentUtils.getPlayerXP(player);
		int neededXP = MathHelper.ceil(Config.elevatorXpDrainRatio * distance);
		if (playerXP >= neededXP) {
			EnchantmentUtils.addPlayerXP(player, -neededXP);
			return true;
		}

		return false;
	}

	public static void onElevatorEvent(ElevatorActionEvent evt) {
		final PlayerEntity player = evt.sender;
		if (player == null) {
			return;
		}

		final World world = player.world;
		if (world == null) {
			return;
		}

		final BlockPos blockPos = player.getPosition().down();

		if (evt.sender != null) {
			if (evt.sender.isPassenger()) {
				return;
			}

			final BlockState blockState = world.getBlockState(blockPos);
			final ElevatorCheckEvent elevatorCheckResult = checkIsElevator(evt.sender, world, blockPos, blockState);

			if (elevatorCheckResult.isElevator()) {
				DyeColor color = elevatorCheckResult.getColor();
				switch (evt.type) {
					case JUMP:
						activate(evt.sender, world, color, blockPos, Direction.AxisDirection.POSITIVE);
						break;
					case SNEAK:
						activate(evt.sender, world, color, blockPos, Direction.AxisDirection.NEGATIVE);
						break;
				}
			}
		}
	}

	public static void onPlayerMovement(PlayerMovementEvent evt) {
		new ElevatorActionEvent(evt.type).sendToServer();
	}
}
