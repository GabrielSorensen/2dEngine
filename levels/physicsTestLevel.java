package levels;

import java.util.Stack;

import org.lwjgl.glfw.GLFW;

import entities.AbstractLevel;
import entities.AbstractWorld;
import topLevelEntities.Player;
import topLevelEntities.Point;

public class physicsTestLevel extends AbstractLevel {

	private Player player = new Player(50, 50, 15, 15, "player");

	public physicsTestLevel(float width, float height, AbstractWorld levelLocation, String name) {
		super(width, height, levelLocation, name);
	}

	@Override
	public void handleInput(Stack<Integer> stack) {
		while (!stack.empty()) {			
			Integer key = stack.pop();
			switch(key) {
			
				case GLFW.GLFW_KEY_Q:
					layer9.add(player);
					break;
					
				case GLFW.GLFW_KEY_A:
					player.setVelocity(player.getVelocity());
					break;
				
				case GLFW.GLFW_KEY_D:
					player.setLocation(player.getX() + 1, player.getY());
					break;	
				
				case GLFW.GLFW_KEY_C:
					this.getWorldspace().getGlobals().printVars();
					break;
					
				case GLFW.GLFW_KEY_V:
					this.printAllEntities();
					break;
					
			}
		}
		return;


	}

	@Override
	protected boolean restoreLevelState() {
		return false;
	}

	@Override
	protected boolean saveLevelState() {
		return false;
	}

	@Override
	protected boolean setupLevel() {
		layer1.add(new Point(100, 100, 0.4f, 0.17f, 0.18f, 70, 70));
		layer2.add(new Point(200, 100, 1.0f, 1.0f, 0.0f, 10, 10));
		layer3.add(new Point(300, 100, 1.0f, 1.0f, 0.0f, 10, 10));
		layer4.add(new Point(400, 100, 1.0f, 1.0f, 0.0f, 10, 10));
		layer5.add(new Point(100, 200, 1.0f, 1.0f, 0.0f, 10, 10));
		layer6.add(new Point(100, 300, 1.0f, 1.0f, 0.0f, 10, 10));
		layer7.add(new Point(100, 400, 1.0f, 1.0f, 0.0f, 10, 10));
		layer8.add(new Point(300, 300, 1.0f, 1.0f, 0.0f, 10, 10));
		layer9.add(new Point(400, 400, 1.0f, 1.0f, 0.0f, 10, 10));
		return true;
	}
}
