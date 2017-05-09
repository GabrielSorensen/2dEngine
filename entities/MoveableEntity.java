package entities;


public interface MoveableEntity extends Entity {

    public double getDX();

    public double getDY();

    public double[] getVelocity();

    public void setDX(double dx);

	public void setDY(double dy);
	
	public void setMass(double mass);

	public void setVelocity(double [] velocity);

	@Override
	public void update(double delta);

}

