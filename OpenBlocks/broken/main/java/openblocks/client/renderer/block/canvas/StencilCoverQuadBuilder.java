package openblocks.client.renderer.block.canvas;

import com.google.common.collect.Lists;
import java.util.List;
import javax.vecmath.Vector3f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

public class StencilCoverQuadBuilder {

	private final AxisAlignedBB bounds;

	private final VertexFormat format;

	private final int tint;

	private final List<BakedQuad> quads = Lists.newArrayList();

	public StencilCoverQuadBuilder(AxisAlignedBB bounds, VertexFormat format, int tint) {
		this.bounds = bounds;
		this.format = format;
		this.tint = tint;
	}

	private static class Vertex {
		public final float x;
		public final float y;
		public final float z;

		public final float u;
		public final float v;

		public Vertex(float x, float y, float z, float u, float v) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.u = u;
			this.v = v;
		}

		@Override
		public String toString() {
			return String.format("[%f,%f,%f][%f,%f]", x, y, z, u, v);
		}
	}

	private void add(TextureAtlasSprite texture, TextureOrientation rotation, Direction face, float x, float y, float z, Vector3f right, Vector3f down) {
		final Vertex[] vertices = new Vertex[] {
				new Vertex(x, y, z, 0, 0), // top left
				new Vertex(x + down.x, y + down.y, z + down.z, 0, 1), // bottom left
				new Vertex(x + right.x + down.x, y + right.y + down.y, z + right.z + down.z, 1, 1), // bottom right
				new Vertex(x + right.x, y + right.y, z + right.z, 1, 0), // top right
		};

		final UnpackedBakedQuad.Builder builderCcw = createQuadBuilder(texture, face);
		final UnpackedBakedQuad.Builder builderCw = createQuadBuilder(texture, face.getOpposite());
		for (int v = 0; v < 4; v++) {
			{
				final Vertex textureV = vertices[v];
				final Vertex positionV = vertices[rotation.shift(v)];
				addQuad(builderCcw, face, texture, positionV, textureV);
			}

			{
				final Vertex textureV = vertices[3 - v];
				final Vertex positionV = vertices[rotation.shift(3 - v)];
				addQuad(builderCw, face.getOpposite(), texture, positionV, textureV);
			}
		}

		quads.add(builderCcw.build());
		quads.add(builderCw.build());
	}

	private UnpackedBakedQuad.Builder createQuadBuilder(TextureAtlasSprite sprite, Direction face) {
		final UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
		builder.setApplyDiffuseLighting(true);
		builder.setQuadOrientation(face);
		builder.setQuadTint(tint);
		builder.setTexture(sprite);
		return builder;
	}

	private void addQuad(final UnpackedBakedQuad.Builder builder, Direction face, TextureAtlasSprite sprite, Vertex position, Vertex texture) {
		for (int e = 0; e < format.getElementCount(); e++) {
			final VertexFormatElement el = format.getElement(e);
			switch (el.getUsage()) {
				case POSITION:
					builder.put(e, position.x, position.y, position.z);
					break;
				case NORMAL:
					builder.put(e, face.getFrontOffsetX(), face.getFrontOffsetY(), face.getFrontOffsetZ());
					break;
				case COLOR:
					builder.put(e, 1, 1, 1, 1);
					break;
				case UV:
					if (el.getIndex() == 0) {
						builder.put(e, sprite.getInterpolatedU(16 * texture.u), sprite.getInterpolatedV(16 * texture.v));
					} else {
						builder.put(e, 0, 0);
					}
					break;
				default:
					builder.put(e);
			}
		}
	}

	private static final Vector3f NORMAL_NORTH = new Vector3f(0, 0, -1);
	private static final Vector3f NORMAL_SOUTH = new Vector3f(0, 0, +1);

	private static final Vector3f NORMAL_EAST = new Vector3f(+1, 0, 0);
	private static final Vector3f NORMAL_WEST = new Vector3f(-1, 0, 0);

	private static final Vector3f NORMAL_DOWN = new Vector3f(0, -1, 0);

	public void add(Direction side, TextureAtlasSprite texture, TextureOrientation rotation) {
		switch (side) {
			case NORTH:
				add(texture, rotation, Direction.NORTH, 1, 1, (float)bounds.minZ, NORMAL_WEST, NORMAL_DOWN);
				break;
			case SOUTH:
				add(texture, rotation, Direction.SOUTH, 0, 1, (float)bounds.maxZ, NORMAL_EAST, NORMAL_DOWN);
				break;
			case EAST:
				add(texture, rotation, Direction.EAST, (float)bounds.maxX, 1, 1, NORMAL_NORTH, NORMAL_DOWN);
				break;
			case WEST:
				add(texture, rotation, Direction.WEST, (float)bounds.minX, 1, 0, NORMAL_SOUTH, NORMAL_DOWN);
				break;
			case UP:
				add(texture, rotation, Direction.UP, 0, (float)bounds.maxY, 0, NORMAL_EAST, NORMAL_SOUTH);
				break;
			case DOWN:
				add(texture, rotation, Direction.DOWN, 0, (float)bounds.minY, 1, NORMAL_EAST, NORMAL_NORTH);
				break;
			default:
				throw new AssertionError(side);
		}

	}

	public List<BakedQuad> build() {
		return quads;
	}
}
