package openperipheral.api;

public interface ICallableWithArgs {
	public <T> T call(Object... args);
}
