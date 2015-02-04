package openperipheral.api.adapter;

import java.lang.annotation.*;

/**
 * Used for setting custom names for source id of inline adapters.
 * For external adapter see {@link IAdapter#getSourceId()}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdapterSourceName {
	public String value();
}
