package openblocks.client.renderer.tileentity.guide;

import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import openmods.utils.Coord;

public class CoordShape {
	
	int vbo;
	List<Coord> coords;
	
	public CoordShape(List<Coord> coords) {
		this.coords = coords;
		vbo = 0;
	}
	
	public int bindVBO()
	{
		if (vbo == 0)
		{
			IntBuffer ib = BufferUtils.createIntBuffer(coords.size() * 3);
			for (Coord c : coords)
				ib.put(c.x).put(c.y).put(c.z);
			ib.flip();
			
			vbo = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, ib, GL15.GL_STATIC_DRAW);
		}else
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		
		return vbo;
	}
	
	public List<Coord> getCoords() {
		return coords;
	}
	
	public int size() {
		return coords.size();
	}
	
	public void destroy() {
		if (vbo != 0)
			GL15.glDeleteBuffers(vbo);
		vbo = 0;
	}
}
