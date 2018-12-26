package openblocks.api;

import java.util.List;

public interface IFlimFlamRegistry extends IApiInterface {

	public List<String> getAllFlimFlamsNames();

	public IFlimFlamDescription getFlimFlamByName(String name);

	public List<IFlimFlamDescription> getFlimFlams();

	public void registerFlimFlam(String name, IFlimFlamDescription meta);

	public FlimFlamDescriptionSimple registerFlimFlam(String name, int cost, int weight, IFlimFlamAction effect);

}
