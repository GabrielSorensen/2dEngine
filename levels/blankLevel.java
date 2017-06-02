package levels;

import java.util.Stack;

import org.lwjgl.glfw.GLFW;

import entities.AbstractLevel;
import entities.AbstractWorld;
import topLevelEntities.Point;

public class blankLevel extends AbstractLevel {

	public blankLevel(AbstractWorld world) {
		this(1,1, world);
	}

	public blankLevel(float width, float height, AbstractWorld world) {
		super(width, height, world, "Blank Level");
	}

	@Override
	public void handleInput(Stack<Integer> stack) {
		while (!stack.empty()) {
			switch (stack.pop()) {

			case GLFW.GLFW_KEY_P:
				world.getGlobals().printVars();
				break;

			default:
				break;
			}
		}
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
