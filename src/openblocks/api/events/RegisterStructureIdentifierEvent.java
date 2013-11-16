package openblocks.api.events;

import openblocks.api.IStructureNamer;
import net.minecraftforge.event.Event;

public class RegisterStructureIdentifierEvent extends Event {
	public IStructureNamer namer;

	public RegisterStructureIdentifierEvent() {}

	public RegisterStructureIdentifierEvent(IStructureNamer namer) {
		this.namer = namer;
	}

}
