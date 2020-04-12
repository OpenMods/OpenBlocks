package openblocks.api;

public interface IFlimFlamDescription {

	String name();

	int weight();

	int cost();

	boolean canApply(int luck);

	boolean isSafe();

	boolean isSilent();

	IFlimFlamAction action();

}
