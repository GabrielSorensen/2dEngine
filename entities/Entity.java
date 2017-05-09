package entities;

public interface Entity {
	
	public enum RENDERCODES{
	       INTERMEDIATE(2), OPENGL(1), VULKAN(3);
	       private int value;
	       private RENDERCODES(int value) {
	            this.value = value;
	       }
	       public int getValue(){
	        return value;
	       }
	   }

    public void createGL();

    public void draw(Entity.RENDERCODES graphicsContext);

    public double getAlpha();

    public double getAngle();

    public double getBlue();

    public double getCenterX();
    
    public double getCenterY();

    public double getGreen();
    
    public double getHeight();

    public double getIntersectAt(Entity other, Entity primary);
    
    public double getInvMass();

    public double getMass();

    public String getName();
    
    public double getRadius();
    
    public double getRed();
    
    public double getRestitution();

	public double getWidth();

	public double getX();

	public double getY();

	public boolean hasVBO();

	public boolean intersectsBoundingBox(Entity other);

	public void setAlpha(double alpha);

	public void setAngle(double angle);

	public void setBlue(double colorBlue);

	public void setGreen(double green);

	public void setHeight(double height);
	
    public void setLocation(double x, double y);

    public void setMass(double mass);
    
    public void setName(String name);

	public void setRed(double red);

	public void setRestitution(double restitution);
	
	public void setWidth(double width);
	
	public void setX(double x);
	
	public void setY(double y);
	
	public void update(double delta);
}