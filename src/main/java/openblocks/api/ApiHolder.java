package openblocks.api;

import java.lang.annotation.*;

/**
 * Static variables marked with this annotation will be filled with instance
 * of requested API (defined by type of variable).
 *
 * All variables must have type that implements {@link IApiInterface}.
 * If requested type is not provided by OpenPeripheralAddons, variable will not be set.
 *
 * All variables should be filled in 'init'. Value in 'preInit' is undefined.
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiHolder {}
