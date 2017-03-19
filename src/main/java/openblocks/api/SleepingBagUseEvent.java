package openblocks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * Event triggered before player uses sleeping bag.
 * Use {@link Event#setResult(Result)} to override: {@link Event.Result.ALLOW} to allow even if original algorithm forbids it, {@link Event.Result.DENY} to block normally allowed action.
 */
@HasResult
@Deprecated // TODO switch to forge
public class SleepingBagUseEvent extends PlayerEvent {

	/**
	 * Text displayed to player if action was denied
	 */
	public ITextComponent playerChat;

	/**
	 * Status determined by original algorithm
	 */
	public final EntityPlayer.SleepResult status;

	public SleepingBagUseEvent(EntityPlayer player, EntityPlayer.SleepResult cause) {
		super(player);
		this.status = cause;
	}

	public boolean defaultCanSleep() {
		return status == EntityPlayer.SleepResult.OK;
	}
}
