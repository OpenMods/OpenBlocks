package openblocks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

/**
 * Event triggered before player uses sleeping bag.
 * Use {@link Event#setResult(Result)} to override ({@link Event.Result.ALLOW} to allow even if original algorithm forbids it, {@link Event.Result.DENY} to block otherwise allowed action).
 */
@HasResult
public class SleepingBagUseEvent extends PlayerEvent {

	/**
	 * Text displayed to player is action was denied
	 */
	public IChatComponent playerChat;

	/**
	 * Status determined by original algorithm
	 */
	public final EntityPlayer.EnumStatus status;

	public SleepingBagUseEvent(EntityPlayer player, EntityPlayer.EnumStatus cause) {
		super(player);
		this.status = cause;
	}

	public boolean defaultCanSleep() {
		return status == EntityPlayer.EnumStatus.OK;
	}
}
