package openblocks.api;

import java.util.List;

public interface IFlimFlamRegistry extends IApiInterface {

	List<String> getAllFlimFlamsNames();

	IFlimFlamDescription getFlimFlamByName(String name);

	List<IFlimFlamDescription> getFlimFlams();

	void registerFlimFlam(String name, IFlimFlamDescription meta);

	FlimFlamDescriptionSimple registerFlimFlam(String name, int cost, int weight, IFlimFlamAction effect);

}
