package openblocks.enchantments.flimflams;

import java.util.Random;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayerMP;
import openblocks.api.IFlimFlamAction;
import openmods.reflection.FieldAccess;

import com.google.common.base.Throwables;

public class DummyCreepersFlimFlam implements IFlimFlamAction {

	private static final Random random = new Random();

	private static final FieldAccess<Integer> EXPLOSION_RADIUS = FieldAccess.create(EntityCreeper.class, "explosionRadius", "field_82226_g");
	private static final FieldAccess<DataWatcher> DATA_WATCHER = FieldAccess.create(EntityCreeper.class, "dataWatcher", "field_70180_af");

	@Override
	public boolean execute(EntityPlayerMP target) {

		for (int i = 0; i < 15; i++) {
			EntityCreeper creeper = new EntityCreeper(target.worldObj);
			try {
				EXPLOSION_RADIUS.set(creeper, 0);
				DataWatcher watcher = DATA_WATCHER.get(creeper);
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
