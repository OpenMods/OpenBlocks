package openblocks.api.events;

import net.minecraftforge.event.Event;
import openblocks.api.IStructureGenProvider;

public class RegisterStructureGenProvider extends Event {
	public IStructureGenProvider provider;

	public RegisterStructureGenProvider() {}

	public RegisterStructureGenProvider(IStructureGenProvider provider) {
		this.provider = provider;
	}

}
