package openblocks.api;

import java.util.HashMap;

import com.google.common.collect.Maps;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class MutantRegistry {

	private static HashMap<Class<? extends EntityLivingBase>, IMutantDefinition> definitions = Maps.newHashMap();

	public static void registerMutant(Class<? extends EntityLivingBase> entityClass, IMutantDefinition definition) {
		definitions.put(entityClass, definition);
	}

	public static IMutantDefinition getDefinition(Class<? extends EntityLivingBase> klazz) {
		return definitions.get(klazz);
	}
}
