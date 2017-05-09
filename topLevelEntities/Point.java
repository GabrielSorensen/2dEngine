package topLevelEntities;

import org.lwjgl.opengl.GL11;

import entities.AbstractMoveableEntity;

public class Point extends AbstractMoveableEntity{

	

	public Point() {
		this(1, 1, 1, 1, 1, 1, 1);
	}
	
	public Point(int x, int y) {
		this(x, y, 1, 1, 1, 1, 1);
	}
	
	public Point(int x, int y, double colorRed, double colorGreen, double colorBlue) {
		this(x, y, colorRed, colorGreen, colorBlue, 1, 1);
	}

	public Point(int x, int y, double colorRed, double colorGreen, double colorBlue, double alpha, int width, int height) {
		this(x, y, colorRed, colorGreen, colorBlue, 1f, width, height, "point");
	}
	
	public Point(int x, int y, double colorRed, double colorGreen, double colorBlue, double alpha, int width, int height, String name) {
		super(x, y, width, height, name);
		setRed(colorRed);
		setGreen(colorGreen);
		setBlue(colorBlue);
		setAlpha(alpha);
	}
	
	public Point(int x, int y, double colorRed, double colorGreen, double colorBlue, int width, int height) {
		this(x, y, colorRed, colorGreen, colorBlue, 1f, width, height);
	}
	
	public Point(int x, int y, int width, int height) {
		this(x, y, 1, 1, 1, 1, width, height);
	}

	

//	@Override
//	public void draw(String graphicsContext) {
//		GL11.glRectd(x, y, x + getWidth(), y + getHeight());
//	}

	@Override
	public void draw(RENDERCODES graphicsContext) {
		switch (graphicsContext) {
			case OPENGL:
				GL11.glColor4d(this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
				GL11.glRectd(getX(), getY(), getX() + getWidth(), getY() + getHeight());
				return;
			
			case INTERMEDIATE:
				GL11.glColor4d(this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
				GL11.glRectd(getX(), getY(), getX() + getWidth(), getY() + getHeight());
				return;
				
			case VULKAN:
				
				return;
		}
	}

}
