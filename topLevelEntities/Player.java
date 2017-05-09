package topLevelEntities;

import entities.AbstractMoveableEntity;

public class Player extends AbstractMoveableEntity{

	//	private int [] stats = {10, 10, 10, 10, 10, 10}; //figure this out later.
	

	
	
	public Player(double x, double y, double width, double height, String name) {
		super(x, y, width, height, name);
	}
	
	private void logic() {
//		if(this.intersects(Trigger t)) {
//			t.onTrigger();
//		}
		//Change to handle collisions with any trigger currently in the world
		//Probably need to bind the current world(level) context to the player,
		//To iterate through and check if the player is on any triggers.
		
	}

	@Override
	public void update(double delta) {
//		setX( delta * getDX() );
//        setY( delta * getDY() );
        double [] newVertices = new double [] {
				getX(), getY()+height, 0f, 1f, 	// Left top			ID: 0
				getX(), getY(), 0f, 1f,	// Left bottom		ID: 1
				getX()+width, getY(), 0f, 1f,	// Right bottom		ID: 2
				getX()+width, getY()+height, 0f, 1f		// Right left		ID: 3
		};
        setVerticies(newVertices);
        logic();
	}

	

}
