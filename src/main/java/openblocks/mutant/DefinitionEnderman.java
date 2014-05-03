package openblocks.mutant;

import net.minecraft.util.Vec3;
import openblocks.api.IMutantDefinition;
import openblocks.api.IMutantRenderer;
import openblocks.client.renderer.mutant.MutantRendererEnderman;

public class DefinitionEnderman implements IMutantDefinition {

	private Vec3[] legAttachmentPoints2 = new Vec3[] {
			Vec3.createVectorHelper(2.0, -12.0, 0.0),
			Vec3.createVectorHelper(-2.0, -12.0, 0.0),
	};

	private Vec3[] legAttachmentPoints4 = new Vec3[] {
			Vec3.createVectorHelper(1.0, -12.0, 1.0),
			Vec3.createVectorHelper(-1.0, -12.0, 1.0),
			Vec3.createVectorHelper(1.0, -12.0, -1.0),
			Vec3.createVectorHelper(-1.0, -12.0, -1.0),
	};

	private Vec3[] legAttachmentPoints8 = new Vec3[] {
			Vec3.createVectorHelper(0.0, -10.0F, 0.0),
			Vec3.createVectorHelper(0.0, -10.0F, 0.0),
			Vec3.createVectorHelper(0.0, -10.0F, 0.0),
			Vec3.createVectorHelper(0.0, -10.0F, 0.0),
			Vec3.createVectorHelper(0.0, -10.0F, 0.0),
			Vec3.createVectorHelper(0.0, -10.0F, 0.0),
			Vec3.createVectorHelper(0.0, -10.0F, 0.0),
			Vec3.createVectorHelper(0.0, -10.0F, 0.0),
	};

	private Vec3[] wingAttachmentPoints = new Vec3[] {
			Vec3.createVectorHelper(-4.0F, -1.0F, 0.0F),
			Vec3.createVectorHelper(4.0F, -1.0F, 0.0F),
	};

	private Vec3[] armAttachmentPoints = new Vec3[] {
			Vec3.createVectorHelper(-5.0F, -1.0F, 0.0F),
			Vec3.createVectorHelper(5.0F, -1.0F, 0.0F),
	};

	private Vec3 headAttachmentPoint = Vec3.createVectorHelper(0, 0, 0);

	private Vec3 tailAttachmentPoint = Vec3.createVectorHelper(0, -10, 2);

	@Override
	public IMutantRenderer createRenderer() {
		return new MutantRendererEnderman();
	}

	@Override
	public Vec3[] getLegAttachmentPoints(int numLegs) {
		switch (numLegs) {
			case 4:
				return legAttachmentPoints4;
			case 8:
				return legAttachmentPoints8;
			default:
				return legAttachmentPoints2;

		}
	}

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
		return 30;
	}

	@Override
	public int getBodyHeight() {
		return 12;
	}

	@Override
	public int getNumberOfLegs() {
		return 2;
	}

}
