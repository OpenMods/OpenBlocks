package openblocks.physics;

public class Point {
	
	private FastVector current, previous, force;
	double mass, inv_mass;
	
	public Point(double d, double e) {
		this.current = this.previous = new FastVector(d,e);
		this.mass = this.inv_mass = 1;
		this.force = new FastVector(0.0, 0.5).multiply(0.05 * 0.05);
	}
	
	public void setCurrent(FastVector v) {
		this.current = v;
	}
	
	public void setPrevious(FastVector v) {
		this.previous = v;
	}
	
	public FastVector getCurrent() {
		return this.current;
	}
	
	public FastVector getPrevious() {
		return this.previous;
	}
	
	public void update() {
		if(this.inv_mass != 0) {
			FastVector new_pos = this.current.multiply(1.99).subtract(this.previous.multiply(0.99)).add(this.force);
			new_pos.x = (new_pos.x < 0) ? 0 : ((new_pos.x > 1) ? 1 : new_pos.x);
			new_pos.y = (new_pos.y < 0) ? 0 : ((new_pos.y > 1) ? 1 : new_pos.y);
			this.previous = this.current;
			this.current = new_pos;
		}
	}
}
