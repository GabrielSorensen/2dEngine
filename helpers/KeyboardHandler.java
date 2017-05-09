package helpers;

import java.util.Stack;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class KeyboardHandler extends GLFWKeyCallback {

	public static boolean[] keys = new boolean[65536];
	private static Stack<Integer> keyStack = new Stack<Integer>();
	private static long windowCallback = 0l;

	private static Integer bufferKey(int keycode) {
		return keyStack.push(keycode);
	}

	public static Stack<Integer> getInputs() {
		return keyStack;
	}

	public static boolean hasNext() {
		return !keyStack.isEmpty();
	}
	
	// boolean method that returns true if a given key
	// is pressed.
	public static boolean isKeyDown(int keycode) {
		int state = GLFW.glfwGetKey(windowCallback, keycode);
		if (state == GLFW.GLFW_PRESS) {
			return true;
		} else {
			return false;
		}
	}
	
	public static int next() {
		return keyStack.pop();
	}
	
	public KeyboardHandler(long windowCallback) {
		KeyboardHandler.windowCallback  = windowCallback;
		KeyboardHandler.keyStack = new Stack<Integer>();
	}
	
	// The GLFWKeyCallback class is an abstract method that
	// can't be instantiated by itself and must instead be extended
	// 
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		keys[key] = action == GLFW.GLFW_PRESS;
		if (action == GLFW.GLFW_RELEASE) {
			if (keyStack.isEmpty()) {
				bufferKey(key);
			} else {
				if (keyStack.peek() == key) {
					return;
				} else {
					bufferKey(key);
				}
			} 
		}
	}

}