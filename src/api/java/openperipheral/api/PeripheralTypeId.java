package openperipheral.api;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PeripheralTypeId {
	public String value();
}
