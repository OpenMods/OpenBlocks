package openblocks.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class GraveDropsEvent extends Event {

	public enum Action {
		STORE, DELETE, DROP
	}

	public static class ItemAction {
		public final EntityItem item;
		public final Action action;

		public ItemAction(EntityItem item, Action action) {
			Preconditions.checkNotNull(action);
			Preconditions.checkNotNull(item);
			this.item = item;
			this.action = action;
		}
	}

	public final EntityPlayer player;

	public final List<ItemAction> drops = Lists.newArrayList();

	public GraveDropsEvent(EntityPlayer player) {
		this.player = player;
	}

	public void addItem(EntityItem stack) {
		drops.add(new ItemAction(stack, Action.STORE));
	}

	public void addItem(EntityItem stack, Action action) {
		drops.add(new ItemAction(stack, action));
	}
}
