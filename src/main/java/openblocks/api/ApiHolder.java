package openblocks.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Static variables marked with this annotation will be filled with instance
 * of requested API (defined by type of variable).
 *
 * Static methods marked with this annotation will be called with instance of requested API.
 * Methods must have single argument, which will be used to select API.
 *
 * All used types must implements {@link IApiInterface}.
 * If requested type is not provided by OpenPeripheralAddons, variable will not be set.
 *
 * All variables should be filled in 'init'. Value in 'preInit' is undefined.
 *
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiHolder {}
