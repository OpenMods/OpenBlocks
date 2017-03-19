package openblocks.shapes;

import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.renderer.shaders.BufferHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

public class CoordShape {

	@SideOnly(Side.CLIENT)
	private int vbo;

	private final List<BlockPos> coords;

	public CoordShape(List<BlockPos> coords) {
		this.coords = coords;
	}

	@SideOnly(Side.CLIENT)
	public int bindVBO() {
		if (vbo == 0) {
			ByteBuffer data = BufferUtils.createByteBuffer(coords.size() * 3 * 4);
			for (BlockPos c : coords)
				data.putInt(c.getX()).putInt(c.getY()).putInt(c.getZ());
			data.flip();

			vbo = BufferHelper.methods().glGenBuffers();
			BufferHelper.methods().glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			BufferHelper.methods().glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
		} else BufferHelper.methods().glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

		return vbo;
	}

	public List<BlockPos> getCoords() {
		return coords;
	}

	public int size() {
		return coords.size();
	}

	@SideOnly(Side.CLIENT)
	public void destroy() {
		if (vbo != 0) BufferHelper.methods().glDeleteBuffers(vbo);
		vbo = 0;
	}
}
