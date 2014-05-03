package openperipheral.api;

import java.lang.annotation.*;

/**
 * HostedPeripherals created for types marked with this annotation will not be cached (i.e. one will be created on any call)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Volatile {

}
