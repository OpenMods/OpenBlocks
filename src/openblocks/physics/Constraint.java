package openblocks.physics;

public class Constraint {
	private Point p1, p2;
	private double rest_length, squared_rest_length;
	
	public Constraint(Point p1, Point p2, double r1) {
		this.p1 = p1;
		this.p2 = p2;
		this.rest_length = Double.isNaN(r1) ? 
					p1.getCurrent().subtract(p2.getCurrent()).length() :
					r1;
		this.squared_rest_length = this.rest_length * this.rest_length;
	}
	
	public Point getPoint1() {
		return this.p1;
	}
	
	public Point getPoint2() {
		return this.p2;
	}
	
	public double getRestLength() {
		return this.rest_length;
	}
	
	public double getSquaredRestLength() {
		return this.squared_rest_length;
	}
	
	public void update() {
		FastVector p1 = this.p1.getCurrent();
		FastVector p2 = this.p2.getCurrent();
		FastVector delta = p2.subtract(p1);
		
		double	p1_im = this.p1.inv_mass, 
				p2_im = this.p2.inv_mass;
		
		double d = delta.squaredLength();
		
		double diff = (d - this.squared_rest_length) / ((this.squared_rest_length + d) * (p1_im + p2_im));
		
		if(p1_im != 0) {
			this.p1.setCurrent(p1.add(delta.multiply(p1_im * diff)));
		}
		if(p2_im != 0) {
			this.p2.setCurrent(p2.subtract(delta.multiply(p2_im*diff)));
		}		
	}
}
