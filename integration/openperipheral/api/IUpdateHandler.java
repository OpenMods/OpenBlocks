package openperipheral.api;

/**
 * If wrapped object implements this interface, OpenPeripheral will pass HostedPeripheral update calls to it.
 * This interface is only works in CC versions earlier than 1.6
 */
public interface IUpdateHandler {
	public void onPeripheralUpdate();
}
