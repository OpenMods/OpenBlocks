package openperipheral.api;

import java.lang.annotation.*;

/**
 * This annotation can be only applied to methods without arguments, returing type that can be wrapped to Lua object.
 * If this annotations is found, created peripheral will include methods from wrapped object.
 * Every call will be delegated to instance returned from this method
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Include {

}
