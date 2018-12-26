package openblocks.api;

public interface IFlimFlamDescription {

	public String name();

	public int weight();

	public int cost();

	public boolean canApply(int luck);

	public boolean isSafe();

	public boolean isSilent();

	public IFlimFlamAction action();

}
