package openblocks.utils;

import java.util.Collection;
import java.util.Random;

public class CollectionUtils {

	public static Random rnd = new Random();

	public static <T> T getRandom(Collection<T> collection) {
		if (collection.size() == 0) { return null; }
		int randomIndex = rnd.nextInt(collection.size());
		int i = 0;
		for (T obj : collection)
		{
			if (i == randomIndex) return obj;
			i = i + 1;
		}
		return null;
	}
}
