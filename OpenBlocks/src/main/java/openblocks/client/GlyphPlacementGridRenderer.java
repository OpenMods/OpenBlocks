package openblocks.client;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.common.item.ItemGlyph;
import org.lwjgl.opengl.GL11;

public class GlyphPlacementGridRenderer {
	private static final double MARKER_SIZE_PX = 8.0;
	private static final double MARKER_SIZE = MARKER_SIZE_PX / 16.0;

	private static final double GRID_STEP_PX = 1.0;
	private static final double GRID_STEP = GRID_STEP_PX / 16.0;

	private static class Vertex {
		public static final Vertex ZERO = new Vertex(0, 0, 0);

		private final double x;
		private final double y;
		private final double z;

		public Vertex(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		void draw(BufferBuilder builder) {
			builder.pos(x, y, z).color(0.5f, 0.5f, 0.5f, 0.5f).endVertex();
		}
	}

	private static Vertex vertex(Vec3d v) {
		return new Vertex(v.x, v.y, v.z);
	}

	private static List<Vertex> generateGrid(Vec3d firstAxis, Vec3d secondAxis) {
		final ImmutableList.Builder<Vertex> builder = ImmutableList.builder();
		generateGridLines(builder, firstAxis, secondAxis);
		generateGridLines(builder, secondAxis, firstAxis);
		return builder.build();
	}

	private static void generateGridLines(ImmutableList.Builder<Vertex> builder, Vec3d firstAxis, Vec3d secondAxis) {
		Vec3d start = Vec3d.ZERO;
		Vec3d end = firstAxis;

		final Vec3d step = secondAxis.scale(GRID_STEP);

		for (int i = 0; i < 16; i++) {
			builder.add(vertex(start));
			builder.add(vertex(end));
			start = start.add(step);
			end = end.add(step);
		}

		builder.add(vertex(start));
		builder.add(vertex(end));
	}

	private static List<Vertex> generateMarker(Vec3d firstAxis, Vec3d secondAxis) {
		final Vec3d va = firstAxis.scale(MARKER_SIZE);
		final Vec3d vb = secondAxis.scale(MARKER_SIZE);

		final Vertex a = vertex(va);
		final Vertex b = vertex(vb);
		final Vertex ab = vertex(va.add(vb));

		return ImmutableList.of(
				Vertex.ZERO, a, ab, b,
				Vertex.ZERO, b, ab, a);
	}

	private static class AxisElements {
		private final Vec3d normal;
		private final Vec3d proj;
		private final List<Vertex> grid;
		private final List<Vertex> marker;

		public AxisElements(Vec3d normal, Vec3d pA, Vec3d pB) {
			this.normal = normal;
			this.proj = pA.add(pB);
			this.grid = generateGrid(pA, pB);
			this.marker = generateMarker(pA, pB);
		}

		public void drawGrid(BufferBuilder builder) {
			for (Vertex v : grid)
				v.draw(builder);
		}

		public void drawMarker(BufferBuilder builder) {
			for (Vertex v : marker)
				v.draw(builder);
		}
	}

	// I may unroll this if I'm sufficiently bored
	private final Map<Axis, AxisElements> elements = generateElements();

	private static Map<Axis, AxisElements> generateElements() {
		final Map<Axis, AxisElements> result = new EnumMap<>(Axis.class);

		final Vec3d axisX = new Vec3d(1, 0, 0);
		final Vec3d axisY = new Vec3d(0, 1, 0);
		final Vec3d axisZ = new Vec3d(0, 0, 1);

		result.put(Axis.X, new AxisElements(axisX, axisY, axisZ));
		result.put(Axis.Y, new AxisElements(axisY, axisX, axisZ));
		result.put(Axis.Z, new AxisElements(axisZ, axisX, axisY));

		return Collections.unmodifiableMap(result);
	}

	@SubscribeEvent
	public void onHighlightDraw(DrawBlockHighlightEvent evt) {
		final RayTraceResult target = evt.getTarget();
		if (target != null && target.typeOfHit == Type.BLOCK) {
			final PlayerEntity player = evt.getPlayer();
			if (player.getHeldItemMainhand().getItem() instanceof ItemGlyph)
				drawGrid(player, target, evt.getPartialTicks());
		}
	}

	private static double calculateGridCoordinate(double v, double selector) {
		// offset by half of marker size - center on cursor
		// round to 1/16 - align to grid
		// zero if it's parallel to normal - this dimension is already set
		return selector * Math.floor(v * 16 - MARKER_SIZE_PX / 2) / 16;
	}

	private void drawGrid(PlayerEntity player, RayTraceResult target, float partialTicks) {
		final Direction side = target.sideHit;
		if (side.getAxis() == Axis.Y) return; // TODO
		final BlockPos pos = target.getBlockPos();
		final Vec3d posD = new Vec3d(pos);
		final Vec3d hitRel = target.hitVec.subtract(posD);

		final double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		final double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		final double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

		final AxisElements e = elements.get(side.getAxis());

		final Vec3d normalDist = new Vec3d(
				hitRel.x * e.normal.x,
				hitRel.y * e.normal.y,
				hitRel.z * e.normal.z);

		final Vec3d gridOrigin = new Vec3d(
				calculateGridCoordinate(hitRel.x, e.proj.x),
				calculateGridCoordinate(hitRel.y, e.proj.y),
				calculateGridCoordinate(hitRel.z, e.proj.z));

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.glLineWidth(2.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		GlStateManager.doPolygonOffset(-3.0F, -3.0F);
		GlStateManager.enablePolygonOffset();

		// translate to block origin and offset to plane player is facing
		final double blockOriginX = posD.x - dx + normalDist.x;
		final double blockOriginY = posD.y - dy + normalDist.y;
		final double blockOriginZ = posD.z - dz + normalDist.z;

		final Tessellator t = Tessellator.getInstance();
		final BufferBuilder builder = t.getBuffer();

		builder.setTranslation(
				blockOriginX,
				blockOriginY,
				blockOriginZ);

		builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		e.drawGrid(builder);
		t.draw();

		builder.setTranslation(
				blockOriginX + gridOrigin.x,
				blockOriginY + gridOrigin.y,
				blockOriginZ + gridOrigin.z);

		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		e.drawMarker(builder);
		t.draw();

		builder.setTranslation(0, 0, 0);
		GlStateManager.disablePolygonOffset();
	}

}
