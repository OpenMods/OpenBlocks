package openperipheral.api;

import java.lang.annotation.*;

/**
 * Marks first optional argument. It can be skipped or nulled in Lua call. Annotated argument and every one after it must have non-primitive Java type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface Optionals {

}
