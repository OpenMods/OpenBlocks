package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import openblocks.common.tileentity.TileEntityTrophy;

import com.google.common.base.Strings;

public class ItemDropBehavior implements ITrophyBehavior {

	private final int minTicks;
	private final String sound;
	private final ItemStack drop;

	public ItemDropBehavior(int minTicks, ItemStack drop) {
		this(minTicks, drop, "");
	}

	public ItemDropBehavior(int minTicks, ItemStack drop, String sound) {
		this.minTicks = minTicks;
		this.sound = sound;
		this.drop = drop.copy();
	}

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		if (!Strings.isNullOrEmpty(sound)) {
			player.playSound(sound, 1.0F, (tile.worldObj.rand.nextFloat() - tile.worldObj.rand.nextFloat()) * 0.2F + 1.0F);
		}

		player.entityDropItem(drop.copy(), 0);
		return minTicks;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
