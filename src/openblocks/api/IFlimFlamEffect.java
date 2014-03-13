package openblocks.api;

public interface IFlimFlamEffect {

	public String name();

	public int weight();

	public int cost();

	public boolean isSafe();

	public boolean isSilent();

	public IFlimFlamAction action();

}
