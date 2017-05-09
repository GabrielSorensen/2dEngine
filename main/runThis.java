package main;

import Engine.Engine2D;
import Engine.GLOBALS;
import worlds.physicsWorld;

public class runThis {

	public static void main(String[] args) {
		runEngine();
	}

	private static void runEngine() {
		Engine2D test = new Engine2D();
		GLOBALS g = new GLOBALS(true);
		test.startEngine(new physicsWorld(g));
	}

}
