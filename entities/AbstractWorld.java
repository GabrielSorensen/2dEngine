package entities;

import java.util.ArrayList;

import Engine.GLOBALS;

public abstract class AbstractWorld {
	
	/*
	 * TODO:Worlds pass controll to the levels ( the active game state )
	 * Worlds can switch level context, and preserve state in current level before switch
	 * contexts for levels( positions of things, triggers/entities/etc...)
	 */
	
	private AbstractLevel currentLevel = null;
	
	private transient GLOBALS globals;
	
	ArrayList<AbstractLevel> Levels = new ArrayList<AbstractLevel>();

	public AbstractWorld() {
		this(null);
	}
	
	public AbstractWorld(GLOBALS globals) {
		this.globals = globals;
		this.setupWorld();
		//this.startWorld();// don't auto start world?
	}
	
	
	public void addLevel(AbstractLevel l) {
		if (l != null) {
			Levels.add(l);
		}
	}
	
	public AbstractLevel getCurrentLevel() {
		return currentLevel;
	}
	
	public GLOBALS getGlobals() {
		return globals;
	}

	public AbstractLevel getLevelByName(String name) {
		for (AbstractLevel level: Levels) {
			if (level.getName().equals(name)) {
				return level;
			}
		}
		return null;
	}

	public boolean restoreLevelState() {
		return false;
	}
	
	public boolean saveLevelState() {
		return false;
	}
	
	protected abstract boolean setupWorld();
	
	public abstract boolean startWorld();
	
	public boolean switchLevel(AbstractLevel destinationLevel){
		//TODO: save state of the level, check for state of destinationLevel and switch levels.
		currentLevel = destinationLevel;
		return true;
	}
	public void updateGLcapabilities() {
		for (AbstractLevel level: Levels) {
			level.createVBOs();
		}
	}
	
	
}
