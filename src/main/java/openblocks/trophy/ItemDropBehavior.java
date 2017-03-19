package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import openblocks.common.tileentity.TileEntityTrophy;

public class ItemDropBehavior implements ITrophyBehavior {

	private final int minTicks;
	private final SoundEvent sound;
	private final ItemStack drop;

	public ItemDropBehavior(int minTicks, ItemStack drop) {
		this(minTicks, drop, null);
	}

	public ItemDropBehavior(int minTicks, ItemStack drop, SoundEvent sound) {
		this.minTicks = minTicks;
		this.sound = sound;
		this.drop = drop.copy();
	}

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		if (sound != null) {
			player.playSound(sound, 1.0F, (tile.getWorld().rand.nextFloat() - tile.getWorld().rand.nextFloat()) * 0.2F + 1.0F);
		}

		player.entityDropItem(drop.copy(), 0);
		return minTicks;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
