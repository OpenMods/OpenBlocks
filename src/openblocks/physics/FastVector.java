package openblocks.physics;

public class FastVector {

	public double x,y;
	public FastVector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public FastVector add(double n) {
		return new FastVector(x + n, y + n);
	}
	
	public FastVector add(FastVector b) {
		return new FastVector(x + b.x, y + b.y);
	}
	
	public FastVector _add(double n) {
		this.x += n;
		this.y += n;
		return this;
	}
	
	public FastVector _add(FastVector b) {
		this.x += b.x;
		this.y += b.y;
		return this;
	}
	
	public double dot(FastVector b) {
		return ((this.x*b.x)+ (this.y*b.y));
	}
	
	public double length() {
		return Math.sqrt((this.x*this.x) + (this.y*this.y));
	}
	
	public FastVector multiply(double n) {
		return new FastVector(this.x*n, this.y*n);
	}
	
	public FastVector multiply(FastVector b) {
		return new FastVector(this.x*b.x, this.y*b.y);
	}
	
	public FastVector _multiply(double n) {
		this.x *= n;
		this.y *= n;
		return this;
	}
	
	public FastVector _multiply(FastVector b) {
		this.x *= b.x;
		this.y *= b.y;
		return this;
	}
	
	public double squaredLength() {
		return (this.x*this.x)+(this.y*this.y);
	}
	
	public double sum() {
		return this.x+this.y;
	}
	
	public FastVector subtract(double n) {
		return new FastVector(this.x - n, this.y - n);
	}
	
	public FastVector subtract(FastVector b) {
		return new FastVector(this.x - b.x, this.y - b.y);
	}
	
	public FastVector _subtract(double n) {
		this.x -= n;
		this.y -= n;
		return this;
	}
	
	public FastVector _subtract(FastVector b) {
		this.x -= b.x;
		this.y -= b.y;
		return this;
	}
	
	@Override
	public String toString() {
		return "[" + this.x + "," + this.y + "]";
	}
}
