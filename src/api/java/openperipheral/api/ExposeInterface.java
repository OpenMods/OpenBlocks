package openperipheral.api;

import java.lang.annotation.*;

/**
 * This annotations marks classes that want to expose some of interfaces when wrapped by proxy.
 * Resulting object will not only implement IPeripheral, but also all interfaces of wrapped class.
 * All calls to extra interfaces will be directly passed to wrapped object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExposeInterface {
	public Class<?>[] value();
}
