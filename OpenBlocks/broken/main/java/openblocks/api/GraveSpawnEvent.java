package openblocks.api;

import java.util.List;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class GraveSpawnEvent extends PlayerEvent {

	public final List<ItemEntity> loot;

	public final ITextComponent clickText;

	public final String gravestoneText;

	public final BlockPos location;

	public GraveSpawnEvent(PlayerEntity player, BlockPos pos, List<ItemEntity> loot, String gravestoneText, ITextComponent clickText) {
		super(player);
		this.loot = loot;
		this.gravestoneText = gravestoneText;
		this.clickText = clickText;
		this.location = pos;
	}
}
