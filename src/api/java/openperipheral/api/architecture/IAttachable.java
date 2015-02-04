package openperipheral.api.architecture;

/**
 * Tile Entities marked with this annotation will be informed when computer is attached.
 */
public interface IAttachable {

	public void addComputer(IArchitectureAccess machine);

	public void removeComputer(IArchitectureAccess machine);

}
