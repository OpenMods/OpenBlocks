package openblocks.physics;

public class Point {
	
	private FastVector current, previous, force, gravity;
	double mass, inv_mass;
	
	public Point(double x, double y, double z) {
		this.current = this.previous = new FastVector(x,y,z);
		this.mass = 5;
		this.inv_mass = this.mass > 0 ? 1/this.mass : 0;
		this.gravity = new FastVector(0.0, 0.5, 0).multiply(0.08 * 0.08);
		this.force = new FastVector(0, 0, 0);
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
	
	public Point applyForce(FastVector v) {
		this.force = this.force.add(v);
		return this;
	}
	
	public void update() {
		if(this.force.length() < 0.001) {
			this.force._multiply(0);
		}
		this.force._multiply(0.8);
		FastVector absForce = this.gravity.add(this.force);
		if(this.force.length() != 0) {
		//	System.out.println("Force " + this.force);
		}
		if(this.inv_mass != 0) {
			FastVector new_pos = this.current.multiply(1.99).subtract(this.previous.multiply(0.99)).add(absForce);
			new_pos.x = (new_pos.x < 0) ? 0 : ((new_pos.x > 1) ? 1 : new_pos.x);
			new_pos.y = (new_pos.y < 0) ? 0 : ((new_pos.y > 1) ? 1 : new_pos.y);
			new_pos.z = (new_pos.z < 0) ? 0 : ((new_pos.z > 1) ? 1 : new_pos.z);
			this.previous = this.current;
			this.current = new_pos;
		}
	}
}
