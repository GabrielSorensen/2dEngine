package helpers;

import java.util.ArrayList;

import entities.AbstractEntity;
import entities.AbstractMoveableEntity;
import topLevelEntities.Point;

public class Maths {
	
	public static double[] addVectors(double [] a, double [] b) {
		double [] res = b;
		if (a.length != b.length) {
			return new double [] {};
		}
		for (int i=0; i<res.length; i++) {
			res[i] = res[i] + a[i];
		}
		return res;
	}
	
	public static double[] applyScalar(double scalar, double [] vector) {
		double [] res = vector;
		for (int i=0; i<res.length; i++) {
			res[i] = res[i]*scalar;
		}
		return res;
	}

	public static double [] crossProduct(double c, double [] a) {
		return new double [] {a[0]*-c, a[1]*c};
	}

	public static double [] crossProduct(double [] a, double c) {
		return new double [] {a[0]*c, a[1]*-c};
	}
	
	public static double crossProduct(double [] a, double [] b) {
		if (a.length != b.length) {
			return 0;
		}
		return ((a[0] * b[0]) - (a[1] * b[1]));
	}
	
	public static double distanceBetweenTwoObjects(AbstractEntity e1, AbstractEntity e2) {
		return Math.sqrt(Math.pow((e2.getX() - e1.getX()),2) - Math.pow((e2.getY()-e1.getY()), 2));
	}
	
	public static double distanceTriangle(AbstractEntity A, AbstractEntity B, AbstractEntity C) {
		double ABx = B.getX()-A.getX();
		double ABy = B.getY()-A.getY();
		double num = ABx*(A.getY()-C.getY())-ABy*(A.getX()-C.getX());
		if (num < 0) num = -num;
		return num;
	}
	
	public static double dotProduct(double [] a, double [] b) {
		double res = 0;
		if (a.length != b.length) {
			return 0;
		}
		for (int i=0; i<a.length; i++) {
			res += a[i]*b[i];
		}
		return res;
	}
	
	public static int Location(AbstractEntity A, AbstractEntity B, AbstractEntity P) {
		double cp1 = (B.getX()-A.getX())*(P.getY()-A.getY()) - (B.getY()-A.getY())*(P.getX()-A.getX());
		return (cp1>0)?1:-1;
	}
	
	public static void resolveColisions(AbstractMoveableEntity A, AbstractMoveableEntity B) {
		double [] relativeVelocity = {B.getX() - A.getX(), B.getY() - A.getY()};
		double [] normal = {(B.getDX() - A.getDX()), (B.getDY() - A.getDY())};
		double normalVelocity = dotProduct(relativeVelocity, normal);
		
		if (normalVelocity > 0) {
			return; //Objects are seperating themselves
		}
		
		double rest = Math.min(A.getRestitution(), B.getRestitution());
		
		double impulseScalar = -1*(1 + rest) * normalVelocity;
		impulseScalar = impulseScalar / (A.getInvMass() + B.getInvMass());
		
		double [] impulse = applyScalar(impulseScalar, normal);
		A.setVelocity(subtractVectors(A.getVelocity(), applyScalar(A.getInvMass(), impulse)));
		B.setVelocity(addVectors(B.getVelocity(), applyScalar(B.getInvMass(), impulse)));
	}
	
	public static void resolveColisionsPorportions(AbstractMoveableEntity A, AbstractMoveableEntity B) {
		double [] relativeVelocity = {B.getX() - A.getX(), B.getY() - A.getY()};
		double [] normal = {(B.getDX() - A.getDX()), (B.getDY() - A.getDY())};
		double normalVelocity = dotProduct(relativeVelocity, normal);
		
		if (normalVelocity > 0) {
			return; //Objects are seperating themselves
		}
		
		double rest = Math.min(A.getRestitution(), B.getRestitution());
		
		double impulseScalar = -1*(1 + rest) * normalVelocity;
		impulseScalar = impulseScalar / (A.getInvMass() + B.getInvMass());
		
		double [] impulse = applyScalar(impulseScalar, normal);
		double mass_sum = A.getMass() + B.getMass();
		double ratio = A.getMass() / mass_sum;
		A.setVelocity(subtractVectors(A.getVelocity(), applyScalar(ratio, impulse)));
		B.setVelocity(addVectors(B.getVelocity(), applyScalar(ratio, impulse)));
	}
	
	public static double[] subtractVectors(double [] a, double [] b) {
		double [] res = b;
		if (a.length != b.length) {
			return new double [] {};
		}
		for (int i=0; i<res.length; i++) {
			res[i] = res[i] - a[i];
		}
		return res;
	}
	
	public  ArrayList<Point> getCircleOfPoints(int x0, int y0, int radius){ //midpoint circle algorithm
		ArrayList<Point> points = new ArrayList<>();

		int x = radius;
		int y = 0;
		int decisionOver2 = 1 - x;   // Decision criterion divided by 2 evaluated at x=r, y=0

		while( y <= x ) {
			points.add(new Point( x + x0,  y + y0 )); // Octant 1
			points.add(new Point( y + x0,  x + y0 )); // Octant 2
			points.add(new Point(-x + x0,  y + y0 )); // Octant 4
			points.add(new Point(-y + x0,  x + y0 )); // Octant 3
			points.add(new Point(-x + x0, -y + y0 )); // Octant 5
			points.add(new Point(-y + x0, -x + y0 )); // Octant 6
			points.add(new Point( x + x0, -y + y0 )); // Octant 7
			points.add(new Point( y + x0, -x + y0 )); // Octant 8
			y++;
			if (decisionOver2<=0) {
				decisionOver2 += 2 * y + 1;   // Change in decision criterion for y -> y+1
			}
			else {
				x--;
				decisionOver2 += 2 * (y - x) + 1;   // Change for y -> y+1, x -> x-1
			}
		}

		return points;
	}

	
}
