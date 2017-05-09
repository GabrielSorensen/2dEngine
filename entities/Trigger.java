package entities;

import org.lwjgl.opengl.GL11;

public abstract class Trigger extends AbstractMoveableEntity {
	
	private AbstractLevel whereIexist;
	
	public Trigger(float x, float y, float width, float height, AbstractLevel abstractLevel) {
		this(x, y, width, height, abstractLevel, "trigger");
	}
	
	public Trigger(float x, float y, float width, float height, AbstractLevel abstractLevel, String name) {
		super(x, y, width, height, name);
		whereIexist = abstractLevel;
	}
	
	
	@Override
	public void draw(RENDERCODES graphicsContext) {
		if (whereIexist.getWorldspace().getGlobals().DEBUG) { //show a thing just for debugging
			GL11.glColor4f(1.0f, 0.0f, 0.83137254902f, 0.5f);
			//a nice pink transparent color
			//http://www.hexcolortool.com/#ff00d4,0.5
			GL11.glRectd(getX(), getY(), getX() + getWidth(), getY() + getHeight());
			GL11.glColor4f(0f, 0f, 0f, 1f);
		}
		return;
	}
	

	public abstract void onTrigger();

}
