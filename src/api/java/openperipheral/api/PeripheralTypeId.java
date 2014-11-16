package openperipheral.api;

import java.lang.annotation.*;

import dan200.computercraft.api.peripheral.IPeripheral;

/**
 * Used for creating custom names for generated {@link IPeripheral} and source id of generated adapters
 *
 * @author boq
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PeripheralTypeId {
	public String value();
}
