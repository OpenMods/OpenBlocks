package openperipheral.api;

import net.minecraft.util.Vec3;

public interface IEntityMetadataProvider<C> extends IMetaProvider<C> {

	public Object getMeta(C target, Vec3 relativePos);

}
