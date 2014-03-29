package openperipheral.api;

import java.lang.annotation.*;

/**
 * OpenPeripheral will skip peripheral generation for classes marked with this annotations.
 * We will be very sad if you ever use that. We work so hard to make you happy.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Ignore {

}
