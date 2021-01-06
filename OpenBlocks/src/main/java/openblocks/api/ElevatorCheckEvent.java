package openblocks.api;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import openblocks.api.IElevatorBlock.PlayerRotation;

public class ElevatorCheckEvent extends BlockEvent {

	public ElevatorCheckEvent(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super(world, pos, state);
		this.player = player;
	}

	private final PlayerEntity player;

	@Nullable
	private DyeColor color;

	private IElevatorBlock.PlayerRotation rotation = PlayerRotation.NONE;

	public PlayerEntity getPlayer() {
		return player;
	}

	@Nullable
	public DyeColor getColor() {
		return color;
	}

	public boolean isElevator() {
		return color != null;
	}

	public void setColor(DyeColor color) {
		this.color = color;
	}

	public IElevatorBlock.PlayerRotation getRotation() {
		return rotation;
	}

	public void setRotation(IElevatorBlock.PlayerRotation rotation) {
		this.rotation = rotation;
	}

}
