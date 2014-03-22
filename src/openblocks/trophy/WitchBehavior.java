package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatMessageComponent;
import openblocks.common.tileentity.TileEntityTrophy;

public class WitchBehavior implements ITrophyBehavior {
static Random r = new Random();

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player){
		switch(rand.nextInt(5) + 1){
			case 1:
				player.addPotionEffect(new PotionEffect(Potion.blindness.id, 70, 1));
				break;
			case 2:
				player.addPotionEffect(new PotionEffect(Potion.hunger.id, 70, 1));
				break;
			case 3:
				player.addPotionEffect(new PotionEffect(Potion.weakness.id, 70, 1));
				break;
			case 4:
				player.addPotionEffect(new PotionEffect(Potion.poison.id, 70, 1));
				break;
			case 4:
				player.addPotionEffect(new PotionEffect(Potion.harm.id, 1, 1));
				break;
		}
		player.sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("openblocks.misc.get_witched"));
		return 0;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
