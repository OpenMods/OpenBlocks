package openblocks.api.events;

import net.minecraftforge.event.Event;
import openblocks.api.IStructureNamer;

public class RegisterStructureIdentifierEvent extends Event {
	public IStructureNamer namer;

	public RegisterStructureIdentifierEvent() {}

	public RegisterStructureIdentifierEvent(IStructureNamer namer) {
		this.namer = namer;
	}

}
