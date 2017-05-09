package worlds;

import Engine.GLOBALS;
import entities.AbstractWorld;
import levels.physicsTestLevel;

public class physicsWorld extends AbstractWorld {

	public physicsWorld(GLOBALS globals) {
		super(globals);
	}

	@Override
	protected boolean setupWorld() {
		this.addLevel(new physicsTestLevel(800, 600, this, "Physics Level"));
		this.switchLevel(this.getLevelByName("Physics Level"));
		return true;
	}

	@Override
	public boolean startWorld() {
		return false;
	}
	
	


}
