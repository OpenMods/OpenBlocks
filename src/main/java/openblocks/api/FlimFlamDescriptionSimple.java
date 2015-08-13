package openblocks.api;

public class FlimFlamDescriptionSimple implements IFlimFlamDescription {
	private boolean isSilent;
	private boolean isSafe;
	private final String name;
	private final int cost;
	private final int weight;
	private int lowerLuck;
	private int upperLuck;
	private final IFlimFlamAction effect;

	public FlimFlamDescriptionSimple(String name, int cost, int weight, IFlimFlamAction effect) {
		this.name = name;
		this.cost = cost;
		this.weight = weight;
		this.effect = effect;
		if (cost < 0) setRange(Integer.MIN_VALUE, cost);
		else setRange(cost, Integer.MAX_VALUE);
	}

	public FlimFlamDescriptionSimple markSafe() {
		isSafe = true;
		return this;
	}

	public FlimFlamDescriptionSimple markSilent() {
		isSilent = true;
		return this;
	}

	public FlimFlamDescriptionSimple setRange(int a, int b) {
		if (a < b) {
			lowerLuck = a;
			upperLuck = b;
		} else {
			lowerLuck = b;
			upperLuck = a;
		}
		return this;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public int weight() {
		return weight;
	}

	@Override
	public int cost() {
		return cost;
	}

	@Override
	public boolean isSafe() {
		return isSafe;
	}

	@Override
	public boolean isSilent() {
		return isSilent;
	}

	@Override
	public IFlimFlamAction action() {
		return effect;
	}

	@Override
	public boolean canApply(int luck) {
		return lowerLuck <= luck && luck <= upperLuck;
	}
}