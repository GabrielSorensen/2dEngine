package entities;


public abstract class AbstractMoveableEntity extends AbstractEntity implements MoveableEntity {

    protected double [] velocity = new double [2];

    public AbstractMoveableEntity(double x, double y, double width, double height, String name) {
        super(x, y, width, height, name);
        this.velocity[0] = 0;
        this.velocity[1] = 0;
    }

	public void applyImpulse(double dx, double dy) {
		this.setDX(this.getDX() + dx);
		this.setDY(this.getDY() + dy);
	}
	
    @Override
	public double getDX() {
        return velocity[0];
    }

	@Override
	public double getDY() {
        return velocity[1];
    }

    @Override
	public double[] getVelocity() {
    	return velocity;
    }

    @Override
	public void setDX(double dx) {
    	this.velocity[0] = dx;
    }
    
    @Override
	public void setDY(double dy) {
        this.velocity[1] = dy;
    }
    
    @Override
	public void setMass(double mass) {
		if (mass == 0) {
			this.invMass = 0;
		} else 	{
			this.invMass = 1/mass;
		}
		this.mass = mass;
	}
    
    @Override
	public void setVelocity(double [] velocity) {
    	setDX(velocity[0]);
    	setDY(velocity[1]);
    }

    @Override
    public void update(double delta) {
        double x = this.position[0] + velocity[0];
        double y = this.position[1] + velocity[1];
        this.setLocation(x, y);
    }

}