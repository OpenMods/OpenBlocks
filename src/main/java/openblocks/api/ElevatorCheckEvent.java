package openblocks.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import openblocks.api.IElevatorBlock.PlayerRotation;

public class ElevatorCheckEvent extends BlockEvent {

	public ElevatorCheckEvent(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		super(world, pos, state);
		this.player = player;
	}

	private final EntityPlayer player;

	private EnumDyeColor color;

	private IElevatorBlock.PlayerRotation rotation = PlayerRotation.NONE;

	public EntityPlayer getPlayer() {
		return player;
	}

	public EnumDyeColor getColor() {
		return color;
	}

	public boolean isElevator() {
		return color != null;
	}

	public void setColor(EnumDyeColor color) {
		this.color = color;
	}

	public IElevatorBlock.PlayerRotation getRotation() {
		return rotation;
	}

	public void setRotation(IElevatorBlock.PlayerRotation rotation) {
		this.rotation = rotation;
	}

}
