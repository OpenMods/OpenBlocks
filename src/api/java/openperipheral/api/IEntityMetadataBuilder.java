package openperipheral.api;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public interface IEntityMetadataBuilder extends IApiInterface {
	public Map<String, Object> getEntityMetadata(Entity entity, Vec3 relativePos);

	public void register(IEntityMetadataProvider<?> provider);
}
