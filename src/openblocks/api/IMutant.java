package openblocks.api;

public interface IMutant {
	public int getLegHeight();
	public int getBodyHeight();
	public IMutantDefinition getHead();
	public IMutantDefinition getBody();
	public IMutantDefinition getArms();
	public IMutantDefinition getLegs();
	public IMutantDefinition getWings();
	public IMutantDefinition getTail();
	public float getArmSwingProgress(float scale);
}
