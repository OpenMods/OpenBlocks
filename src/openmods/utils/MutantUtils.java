package openmods.utils;

import openblocks.api.IMutant;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.Vec3;

public class MutantUtils {
	public static void bindToAttachmentPoint(IMutant mutant, ModelRenderer renderer, Vec3 attachmentPoint) {
		renderer.setRotationPoint(
                (float) attachmentPoint.xCoord,
                (float) (24 - mutant.getLegHeight() - mutant.getBodyHeight() - attachmentPoint.yCoord),
                (float) attachmentPoint.zCoord
		);
	}
}
