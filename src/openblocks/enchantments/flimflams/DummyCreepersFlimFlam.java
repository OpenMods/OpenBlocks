package openblocks.enchantments.flimflams;

import java.util.Random;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayerMP;
import openblocks.api.IFlimFlamAction;
import openmods.utils.ReflectionHelper;

import com.google.common.base.Throwables;

public class DummyCreepersFlimFlam implements IFlimFlamAction {

	private static final Random random = new Random();

	@Override
	public boolean execute(EntityPlayerMP target) {

		for (int i = 0; i < 15; i++) {
			EntityCreeper creeper = new EntityCreeper(target.worldObj);
			try {
				ReflectionHelper.setProperty(creeper, 0, "explosionRadius", "field_82226_g");
				DataWatcher watcher = ReflectionHelper.getProperty(creeper, "dataWatcher", "field_70180_af");
				watcher.updateObject(17, (byte)1); // Powered
			} catch (Throwable t) {
				throw Throwables.propagate(t);
			}

			creeper.setPosition(target.posX + 20 * (random.nextFloat() - 0.5),
					target.posY + 5 * (1 + random.nextFloat()),
					target.posZ + 20 * (random.nextFloat() - 0.5));
			target.worldObj.spawnEntityInWorld(creeper);
		}
		return true;
	}

}
