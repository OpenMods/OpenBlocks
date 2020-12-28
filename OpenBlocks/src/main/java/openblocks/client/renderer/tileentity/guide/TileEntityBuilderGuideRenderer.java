package openblocks.client.renderer.tileentity.guide;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import openblocks.common.tileentity.TileEntityBuilderGuide;

public class TileEntityBuilderGuideRenderer extends TileEntityGuideRenderer<TileEntityBuilderGuide> {

	private static final float RADIUS = 0.4f;

	private static final double HALF_SIZE = 0.03;

	private static final float OMEGA_11 = 0.0413f;
	private static final float DELTA_11 = 0.5423f;

	private static final float OMEGA_12 = 0.0765f;
	private static final float DELTA_12 = 0.4241f;

	private static final float OMEGA_21 = 0.0543f;
	private static final float DELTA_21 = 0.1295f;

	private static final float OMEGA_22 = 0.0914f;
	private static final float DELTA_22 = 0.6532f;

	private static final float OMEGA_31 = 0.0624f;
	private static final float DELTA_31 = 0.6243f;

	private static final float OMEGA_32 = 0.0351f;
	private static final float DELTA_32 = 0.7635f;

	public TileEntityBuilderGuideRenderer(TileEntityRendererDispatcher dispatcher, GuideModelHolder holder) {
		super(dispatcher, holder);
	}

	@Override
	public void render(TileEntityBuilderGuide guide, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferGroup, int combinedLight, int combinedOverlay) {
		super.render(guide, partialTicks, matrixStack, bufferGroup, combinedLight, combinedOverlay);

		matrixStack.push();
		matrixStack.translate(0.5, 0.5, 0.5);
		IVertexBuilder buffer = bufferGroup.getBuffer(RenderType.getCutout());
		renderCubes(matrixStack, buffer, (guide.getTicks() + partialTicks) / 2);
		matrixStack.pop();
	}

	private void renderCubes(MatrixStack stack, IVertexBuilder buffer, float time) {
		stack.push();
		createTransformation(stack, MathHelper.sin(OMEGA_11 * time + DELTA_11), MathHelper.sin(OMEGA_12 * time + DELTA_12));
		drawCube(buffer, stack);
		stack.pop();

		stack.push();
		createTransformation(stack, MathHelper.sin(OMEGA_21 * time + DELTA_21), MathHelper.sin(OMEGA_22 * time + DELTA_22));
		drawCube(buffer, stack);
		stack.pop();

		stack.push();
		createTransformation(stack, MathHelper.sin(OMEGA_31 * time + DELTA_31), MathHelper.sin(OMEGA_32 * time + DELTA_32));
		drawCube(buffer, stack);
		stack.pop();

	}

	private void createTransformation(MatrixStack result, float theta, float phi) {
		result.rotate(new Quaternion(Vector3f.YP, theta, false));
		result.rotate(new Quaternion(Vector3f.ZP, phi, false));
		result.translate(RADIUS - 0.5f, -0.5f, -0.5f);
	}

	private void drawCube(IVertexBuilder buffer, MatrixStack trans) {
		for (BakedQuad quad : holder.getBitQuads()) {
			buffer.addQuad(trans.getLast(), quad, 1.0f, 1.0f, 1.0f, LightTexture.packLight(15, 15), OverlayTexture.NO_OVERLAY);
		}
	}
}
