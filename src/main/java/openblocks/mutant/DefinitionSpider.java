package openblocks.mutant;

import net.minecraft.util.Vec3;
import openblocks.api.IMutantDefinition;
import openblocks.api.IMutantRenderer;
import openblocks.client.renderer.mutant.MutantRendererSpider;

public class DefinitionSpider implements IMutantDefinition {
	private Vec3[] legAttachmentPoints2 = new Vec3[] {
			Vec3.createVectorHelper(-2.0F, -8.0F, 7.0F),
			Vec3.createVectorHelper(2.0F, -8.0F, 7.0F),
	};
	private Vec3[] legAttachmentPoints4 = new Vec3[] {
			Vec3.createVectorHelper(3.0, -8.0F, 9.0),
			Vec3.createVectorHelper(-3.0, -8.0F, 9.0),
			Vec3.createVectorHelper(3.0, -8.0F, 2.0),
			Vec3.createVectorHelper(-3.0, -8.0F, 2.0),
	};
	private Vec3[] legAttachmentPoints8 = new Vec3[] {
			Vec3.createVectorHelper(-4.0, -3.0F, -1.0),
			Vec3.createVectorHelper(4.0, -3.0F, -1.0),
			Vec3.createVectorHelper(-4.0, -3.0F, -1.0),
			Vec3.createVectorHelper(4.0, -3.0F, -1.0),
			Vec3.createVectorHelper(-4.0, -3.0F, -1.0),
			Vec3.createVectorHelper(4.0, -3.0F, -1.0),
			Vec3.createVectorHelper(-4.0, -3.0F, -1.0),
			Vec3.createVectorHelper(4.0, -3.0F, -1.0),
	};

	private Vec3[] wingAttachmentPoints = new Vec3[] {
			Vec3.createVectorHelper(-5.0F, -1.0F, 7.0F),
			Vec3.createVectorHelper(5.0F, -1.0F, 7.0F),
	};

	private Vec3[] armAttachmentPoints = new Vec3[] {
			Vec3.createVectorHelper(-5.0F, -2.0F, 7.0F),
			Vec3.createVectorHelper(5.0F, -2.0F, 7.0F),
	};

	private Vec3 tailAttachmentPoint = Vec3.createVectorHelper(0, -2, 10);

	private Vec3 headAttachmentPoint = Vec3.createVectorHelper(0.0F, -4.0F, -6.0F);

	@Override
	public IMutantRenderer createRenderer() {
		return new MutantRendererSpider();
	}

	@Override
	public Vec3[] getLegAttachmentPoints(int numLegs) {
		switch (numLegs) {
			case 8:
				return legAttachmentPoints8;
			case 4:
				return legAttachmentPoints4;
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
		return 6;
	}

	@Override
	public int getBodyHeight() {
		return 8;
	}

	@Override
	public int getNumberOfLegs() {
		return 8;
	}

}
