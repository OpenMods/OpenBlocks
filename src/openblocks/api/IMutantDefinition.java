package openblocks.api;

import net.minecraft.util.Vec3;

public interface IMutantDefinition {
	
	public IMutantRenderer createRenderer();
	
	public Vec3[] getLegAttachmentPoints(int numLegs);

	public Vec3 getHeadAttachmentPoint();

	public Vec3 getTailAttachmentPoint();

	public Vec3[] getWingAttachmentPoints();

	public Vec3[] getArmAttachmentPoints();

	public int getLegHeight();

	public int getBodyHeight();

	public int getNumberOfLegs();
}
