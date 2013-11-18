package openblocks.api;

import java.util.HashMap;

import com.google.common.collect.Maps;

import net.minecraft.entity.EntityLiving;

public class MutantRegistry {
	
	private static HashMap<Class< ? extends EntityLiving>, IMutantDefinition> definitions = Maps.newHashMap();
	
	public static void registerMutant(Class< ? extends EntityLiving> entityClass, IMutantDefinition definition) {
		definitions.put(entityClass, definition);
	}
	
	public static IMutantDefinition getDefinition(Class< ? extends EntityLiving> klazz) {
		return definitions.get(klazz);
	}
}
