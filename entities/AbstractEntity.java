package entities;


import static helpers.Logging.log;

import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import helpers.Logging;


public abstract class AbstractEntity implements Entity {

	private double alpha = 1;
	protected double angle;
	//AT is a variable for knowing where the interssections happen.
	protected double AT;
	private double blue = 0;
	private double green = 0;
	private boolean hasVBO = false;
	protected double height;
	//TODO: TEXTURES
	//protected Texture texture;
	private final Rectangle hitbox = new Rectangle();
	private int indicesCount = 0;
	protected double invMass = 0;
	protected double mass = 0;
	protected String name;
	protected double [] position = new double [2];
	protected double radius;
	private double red = 0;
	protected double restitution;
	private int vaoId = 0;
	private int vboId = 0;
	private int vboiId = 0;
	protected double[] vertices = new double [16];
	protected double width;
	


	public AbstractEntity(double x, double y, double width, double height, String name) {
		setLocation(x, y);
		this.width = width;
		this.height = height;
		this.name = name;
	}

	@Override
	public void createGL() {
		try {
			vaoId = GL30.glGenVertexArrays();
		} catch (IllegalStateException state) {
			Logging.log("GL capabilities not created yet. returning", true, Logging.LOG_LEVEL.ERROR);
			return;
		}
		CreateVBO();
	}

	private boolean CreateVBO() {
		try {
			vertices = new double [] {
					getX(), getY()+height, 0f, 1f, 	// Left top			ID: 0
					getX(), getY(), 0f, 1f,	// Left bottom		ID: 1
					getX()+width, getY(), 0f, 1f,	// Right bottom		ID: 2
					getX()+width, getY()+height, 0f, 1f		// Right left		ID: 3
			};
			DoubleBuffer verticesBuffer = BufferUtils.createDoubleBuffer(vertices.length);
			verticesBuffer.put(vertices);
			verticesBuffer.flip();
			byte[] indices = {
					// Left bottom triangle
					0, 3, 2,
					0, 2, 1
					// Right top triangle
					
			};
			indicesCount = indices.length;
			ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
			indicesBuffer.put(indices);
			indicesBuffer.flip();
			vaoId = GL30.glGenVertexArrays();
			GL30.glBindVertexArray(vaoId);
			vboId = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			GL30.glBindVertexArray(0);
			vboiId = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		} catch (Exception e) {
			e.printStackTrace();
			log("There was an exception creating the VBO for this object. \nError was: "+e, true);
			return false;
		}
		return true;
	}

	public void destroyMe() {
		GL20.glDisableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboId);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboiId);
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
		GL30.glDeleteVertexArrays(vaoId);
		log("Entity object cleaned up", true);
	}

	@Override
	public void draw(RENDERCODES graphicsContext) {
		if (!hasVBO() && graphicsContext == RENDERCODES.INTERMEDIATE) {
			graphicsContext = RENDERCODES.OPENGL;
		}
		switch (graphicsContext) {
			case OPENGL:
				GL11.glRectd(getX(), getY(), getX() + getWidth(), getY() + getHeight());
				return;
			
			case INTERMEDIATE:
				if (!hasVBO()) {
					graphicsContext = RENDERCODES.OPENGL;
					return;
				}
				//GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				GL30.glBindVertexArray(vaoId);
				GL20.glEnableVertexAttribArray(0);
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
				GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
				GL20.glDisableVertexAttribArray(0);
				GL30.glBindVertexArray(0);
				return;
				
			case VULKAN:
				
				return;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		destroyMe();
		log("The Garbage Collector had to manually clean up resources, try running destroyMe plz", true);
	}

	@Override
	public double getAlpha() {
		return alpha;
	}

	@Override
	public double getAngle() {
		return angle;
	}

	@Override
	public double getBlue() {
		return blue;
	}

	@Override
	public double getCenterX() {
		return position[0]+(width/2);
	}
	@Override
	public double getCenterY() {
		return position[1]+(height/2);
	}

	@Override
	public double getGreen() {
		return green;
	}
	
	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getIntersectAt(Entity other, Entity primary) {
		if (intersectsBoundingBox(other)) {
			AT= other.getY() - primary.getY();
			return AT;
		}
		return AT;
	}
	
	@Override
	public double getInvMass() {
		return this.invMass;
	}
	
	@Override
	public double getMass() {
		return this.mass;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override 
	public double getRadius() {
		return radius;
	}

	@Override
	public double getRed() {
		return red;
	}

	@Override
	public double getRestitution() {
		return restitution;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getX() {
		return position[0];
	}

	@Override
	public double getY() {
		return position[1];
	}

	@Override
	public boolean hasVBO() {
		return hasVBO;
	}

	@Override
	public boolean intersectsBoundingBox(Entity other) {
		return hitbox.intersects(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}

	public boolean intersectsCircle(Entity other) {
		double r = this.radius + other.getRadius();
		r *= r;
		return (r < (Math.pow((getX() + other.getX()), 2) + Math.pow((getY() + other.getY()), 2)));
	}
	
	@Override
	public void setAlpha(double alpha) {
		if (alpha > 1) {
			alpha = 1f;
		}
		this.alpha = alpha;
	}
	
	@Override
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	@Override
	public void setBlue(double colorBlue) {
		if (colorBlue > 1) {
			colorBlue = 1f;
		}
		this.blue = colorBlue;
	}

	@Override
	public void setGreen(double green) {
		if (green > 1) {
			green = 1f;
		}
		this.green = green;
	}

	@Override
	public void setHeight(double height) {
		this.height = height;
		updateCollision();
	}

	@Override
	public void setLocation(double x, double y) {
		this.position[0] = x;
		this.position[1] = y;
		updateCollision();
	}

	@Override
	public void setName(String name) {
		if (!name.isEmpty()) {
			this.name = name;
		}
	}
	
	@Override
	public void setRed(double red) {
		if (red > 1) {
			red = 1f;
		}
		this.red = red;
	}
	
	@Override
	public void setRestitution(double restitution) {
		this.restitution = restitution;
	}
	
	public void setVerticies(double [] verticies) {
		if (verticies.length != this.vertices.length) {
			return;
		}
		
	}

	@Override
	public void setWidth(double width) {
		this.width = width;
		updateCollision();
	}
	
	@Override
	public void setX(double x) {
		setLocation(x, getY());
	}
	
	@Override
	public void setY(double y) {
		setLocation(getX(), y);
	}
	
	@Override
	public String toString() {
		return "Entity [name: "+this.getName()+", X: "+this.getX()+", Y: "+this.getY()+", Red: "+this.getRed()+", Green: "+this.getGreen()+", Blue: "+this.getBlue()+ ", Alpha: " +this.alpha+ "]";
	}
	
	private void updateCollision() {
		updateHitbox();
		updateRadius();
	}
	
	private void updateHitbox() {
		hitbox.setBounds((int) position[0], (int) position[1], (int) width, (int) height);
	};

	private void updateRadius() {
		this.radius = (double) Math.sqrt(Math.pow((getWidth() - getCenterX()), 2) + Math.pow((getHeight() - getCenterY()), 2));
	}

}