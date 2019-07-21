package openblocks.enchantments.flimflams;

import java.util.Random;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import openblocks.api.IFlimFlamAction;
import openmods.reflection.FieldAccess;

public class DummyCreepersFlimFlam implements IFlimFlamAction {

	private static final Random random = new Random();

	private static final FieldAccess<Integer> EXPLOSION_RADIUS = FieldAccess.create(CreeperEntity.class, "explosionRadius", "field_82226_g");
	private static final FieldAccess<DataParameter<Boolean>> POWERED_DATA_PARAMETER = FieldAccess.create(CreeperEntity.class, "POWERED", "field_184714_b");

	@Override
	public boolean execute(ServerPlayerEntity target) {

		for (int i = 0; i < 15; i++) {
			CreeperEntity creeper = new CreeperEntity(target.world);
			EXPLOSION_RADIUS.set(creeper, 0);
			EntityDataManager watcher = creeper.getDataManager();
			watcher.set(POWERED_DATA_PARAMETER.get(null), true);

			creeper.setPosition(target.posX + 20 * (random.nextFloat() - 0.5),
					target.posY + 5 * (1 + random.nextFloat()),
					target.posZ + 20 * (random.nextFloat() - 0.5));
			target.world.spawnEntity(creeper);
		}
		return true;
	}

}
