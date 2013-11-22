package openblocks.mutant;

import net.minecraft.util.Vec3;
import openblocks.api.IMutantDefinition;
import openblocks.api.IMutantRenderer;
import openblocks.client.renderer.mutant.MutantRendererCreeper;

public class DefinitionCreeper implements IMutantDefinition {

	private Vec3[] legAttachmentPoints2 = new Vec3[] {
			Vec3.createVectorHelper(2.0F, -12.0F, 0.0F),
			Vec3.createVectorHelper(-2.0F, -12.0F, 0.0F),
	};

	private Vec3[] legAttachmentPoints4 = new Vec3[] {
			Vec3.createVectorHelper(2.0F, -12.0F, 4.0F),
			Vec3.createVectorHelper(-2.0F, -12.0F, 4.0F),
			Vec3.createVectorHelper(2.0F, -12.0F, -4.0F),
			Vec3.createVectorHelper(-2.0F, -12.0F, -4.0F),
	};

	private Vec3[] legAttachmentPoints8 = new Vec3[] {
			Vec3.createVectorHelper(0.0, -9.0F, 0.0),
			Vec3.createVectorHelper(0.0, -9.0F, 0.0),
			Vec3.createVectorHelper(0.0, -9.0F, 0.0),
			Vec3.createVectorHelper(0.0, -9.0F, 0.0),
			Vec3.createVectorHelper(0.0, -9.0F, 0.0),
			Vec3.createVectorHelper(0.0, -9.0F, 0.0),
			Vec3.createVectorHelper(0.0, -9.0F, 0.0),
			Vec3.createVectorHelper(0.0, -9.0F, 0.0),
	};
	
	private Vec3 headAttachmentPoint = Vec3.createVectorHelper(0, 0, 0);

	@Override
	public Vec3[] getLegAttachmentPoints(int numLegs) {
		switch(numLegs) {
			case 2:
				return legAttachmentPoints2;
			case 4:
				return legAttachmentPoints4;
			case 8:
				return legAttachmentPoints8;
		}
		return null;
	}
	
	private Vec3 tailAttachmentPoint = Vec3.createVectorHelper(0, -9, 3);
	
	private Vec3[] wingAttachmentPoints = new Vec3[] {
            Vec3.createVectorHelper(-4.0F, 0F, 0.0F),
            Vec3.createVectorHelper(4.0F, 0F, 0.0F),
	};

	private Vec3[] armAttachmentPoints = new Vec3[] {
            Vec3.createVectorHelper(-6.0F, 0F, 0.0F),
            Vec3.createVectorHelper(6.0F, 0F, 0.0F),
	};
	
	@Override
	public Vec3 getHeadAttachmentPoint() {
		return headAttachmentPoint;
	}

	@Override
	public Vec3 getTailAttachmentPoint() {
		return tailAttachmentPoint;
	}

	@Override
	public Vec3[] getWingAttachmentPoints() {
		return wingAttachmentPoints;
	}

	@Override
	public Vec3[] getArmAttachmentPoints() {
		return armAttachmentPoints;
	}

	@Override
	public int getLegHeight() {
		return 6;
	}

	@Override
	public int getBodyHeight() {
		return 12;
	}

	@Override
	public int getNumberOfLegs() {
		return 4;
	}

	@Override
	public IMutantRenderer createRenderer() {
		return new MutantRendererCreeper();
	}

}
