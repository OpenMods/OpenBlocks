package openperipheral.api;

import java.lang.reflect.Field;

/**
 *
 * Interface used to receive callback from generated Lua accessors
 *
 * @see CallbackProperty
 */
public interface IPropertyCallback {
	public void setField(Field field, Object value);

	public Object getField(Field field);
}
