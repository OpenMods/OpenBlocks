package openblocks.physics;

import java.util.List;

public class Cloth {
	
	private List<Constraint> constraints;
	private Point[][] points;
	private double width, height, max_dim, min_dim, spacing;
	private int num_x_points, num_y_points, n_constraints; 
	
	private static final int PHYSICS_ITERATIONS = 2;
	
	public Cloth(int density, double width, double height) {
		this.width = width;
		this.height = height;
		this.max_dim = Math.max(width, height);
		this.min_dim = Math.min(width, height);
		this.spacing = max_dim / density;
		this.num_x_points = (int)((density * (width / max_dim)) + 0.5);
		this.num_y_points = (int)((density * (height / max_dim)) + 0.5);
		
		Constraint constraint;
		int i,j;
		double x,y;
		points = new Point[num_y_points][];
		for(i = 0, y = 0; i < num_y_points; i++, y += spacing) {
			points[i] = new Point[num_x_points];
			for(j = 0, x = 0; j < num_x_points; j++, x += spacing) {
				points[i][j] = new Point(x / width, y / height);
				
				if(i > 0) {
					this.constraints.add(new Constraint(this.points[i-1][j], this.points[i][j], Double.NaN));
				}
				if(j > 0) {
					this.constraints.add(new Constraint(this.points[i][j-1], this.points[i][j], Double.NaN));
				}
			}
		}
		
		this.points[0][0].inv_mass = 0;
		this.points[0][((int)(num_x_points / 2))].inv_mass = 0;
		this.points[0][num_x_points -1].inv_mass = 0;
		this.n_constraints = this.constraints.size();
	}
	
	public void update() {
		int num_x = this.num_x_points,
				num_y = this.num_y_points,
				num_c = this.n_constraints,
				num_i = PHYSICS_ITERATIONS,
				i,j;
		
		// Apply force ( Gravity ) 
		for(i = 0; i < num_y; i++) {
			for(j = 0; j < num_x; j++) {
				this.points[i][j].update();
			}
		}
		
		// Update constraints
		for(j = 0; j < num_i; j++) {
			for(i = 0; i < num_c; i++) {
				this.constraints.get(i).update();
			}
		}
	}
	
	public Point getClosestPoint(FastVector pos) {
		int	num_x = this.num_x_points,
				num_y = this.num_y_points,
				i,j;
		double dist, min_dist = 1;
		Point min_point = null;
		
		for(i = 0; i < num_y; i++) {
			for(j = 0; j < num_x; j++) {
				dist = pos.subtract(this.points[i][j].getCurrent()).length();
				
				if(dist < min_dist) {
					min_dist = dist;
					min_point = this.points[i][j];
				}
			}
		}
		return min_point;
	}
}
