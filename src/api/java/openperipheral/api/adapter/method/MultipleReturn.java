package openperipheral.api.adapter.method;

import java.lang.annotation.*;

/**
 *
 * Used to mark methods for adapters that return multiple types.
 * When used, method should return {@link IMultiReturn}, Java collection or array.
 *
 * @see IMultiReturn
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipleReturn {}
