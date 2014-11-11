package openperipheral.api;

import net.minecraft.util.Vec3;

public interface IEntityMetaProvider<C> extends IMetaProvider<C> {

	public Object getMeta(C target, Vec3 relativePos);

}
