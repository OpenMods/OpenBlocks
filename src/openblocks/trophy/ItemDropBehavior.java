package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import openblocks.common.tileentity.TileEntityTrophy;

public class ItemDropBehavior implements ITrophyBehavior {

	private int minTicks = 0;
	private String sound = "";
	private int itemId = 0;

	public ItemDropBehavior(int minTicks, int itemId) {
		this(minTicks, itemId, "");
	}

	public ItemDropBehavior(int minTicks, int itemId, String sound) {
		this.minTicks = minTicks;
		this.sound = sound;
		this.itemId = itemId;
	}

	@Override
	public void executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		if (!tile.worldObj.isRemote) {
			if (tile.sinceLastActivate() > minTicks) {
				if (!sound.isEmpty()) {
					player.playSound(sound, 1.0F, (tile.worldObj.rand.nextFloat() - tile.worldObj.rand.nextFloat()) * 0.2F + 1.0F);
				}

				player.dropItem(itemId, 1);
				tile.resetActivationTimer();
			}
		}
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {
		// TODO Auto-generated method stub

	}

}
