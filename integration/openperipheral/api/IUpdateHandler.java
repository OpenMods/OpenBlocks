package openperipheral.api;

/**
 * If wrapped object implements this interface, OpenPeripheral will pass HostedPeripheral update calls to it.
 * This interface is only works in CC versions earlier than 1.6. In later version it is possible to achieve similar effect by marking target class with {@link ProxyInterfaces} and calling method when possible (like in ITurtleUpgrade.update)
 */
public interface IUpdateHandler {
	public void onPeripheralUpdate();
}
