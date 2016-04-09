package openblocks.shapes;

import java.nio.ByteBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import openblocks.client.renderer.tileentity.guide.BufferHelper;
import openmods.utils.Coord;

public class CoordShape {
	
	@SideOnly(Side.CLIENT)
	int vbo;
	List<Coord> coords;
	
	public CoordShape(List<Coord> coords) {
		this.coords = coords;
		vbo = 0;
	}
	
	@SideOnly(Side.CLIENT)
	public int bindVBO()
	{
		if (vbo == 0)
		{
			ByteBuffer data = BufferUtils.createByteBuffer(coords.size() * 3 * 4);
			for (Coord c : coords)
				data.putInt(c.x).putInt(c.y).putInt(c.z);
			data.flip();
			
			vbo = BufferHelper.methods().glGenBuffers();
			BufferHelper.methods().glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			BufferHelper.methods().glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
		}else
			BufferHelper.methods().glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		
		return vbo;
	}
	
	public List<Coord> getCoords() {
		return coords;
	}
	
	public int size() {
		return coords.size();
	}
	
	@SideOnly(Side.CLIENT)
	public void destroy() {
		if (vbo != 0)
			BufferHelper.methods().glDeleteBuffers(vbo);
		vbo = 0;
	}
}
