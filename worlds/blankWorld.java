package worlds;

import Engine.GLOBALS;
import entities.AbstractWorld;
import levels.blankLevel;

public class blankWorld extends AbstractWorld {
	
	public blankWorld() {
		this(null);
	}

	public blankWorld(GLOBALS globals) {
		super(globals);
	}

	@Override
	protected boolean setupWorld() {
		this.addLevel(new blankLevel(this));
		this.switchLevel(getLevelByName("Blank Level"));
		return true;
	}

	@Override
	public boolean startWorld() {
		return false;
	}

}
