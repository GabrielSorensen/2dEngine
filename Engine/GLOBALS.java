package Engine;

import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;

import entities.Entity.RENDERCODES;
import helpers.CerealDispenser;
import helpers.KeyboardHandler;
import helpers.Logging;
import helpers.Timer;

public class GLOBALS implements Serializable {

	public enum VSYNC_SETTING {

		OFF(0), ON_DOUBLE_BUFFER(2), ON_SINGLE_BUFFER(1);
		private int value;

		private VSYNC_SETTING(int value) {
			this.value = value;
		}

		public int getValue(){
			return value;
		}
	}


	protected double SCALE= 1;
	//TODO:[BUG]Width and Height by graphics capabilities, crasshing on glfwGetVideoModes
	public boolean DEBUG = true;

	private static final long serialVersionUID = 86753091L;
	protected final int HEIGHT= (int)(480*SCALE);
	protected final boolean VULKAN_SUPPORTED = glfwVulkanSupported();
	protected final int WIDTH= (int)(640*SCALE);
	public transient long PRIMARY_MONITOR = GLFW.glfwGetPrimaryMonitor();
	public transient GLFWVidMode.Buffer availableVideoModes;

	public transient AudioEngine audioEngine;
	protected transient double delta = 0;
	protected transient GLFWErrorCallback errorCallback;
	protected transient RENDERCODES graphicsContext = RENDERCODES.INTERMEDIATE;
	protected transient GLFWKeyCallback keyboardCallback;
	public transient KeyboardHandler keyHandler;
	protected transient Timer timer = new Timer();

	protected AtomicInteger fps = new AtomicInteger(0);
	protected physicsConstants physicsVars;
	protected boolean SAVE_LOGS = false;
	protected boolean useGLIntermediateMode = false;

	protected String TITLE = "2D Engine Test!";

	protected VSYNC_SETTING use_vsync = VSYNC_SETTING.OFF;

	protected int TICK_LIMIT = 60;

	//protected String logString = ""; 
	// if we really need to capture this as a global its here, sorta.
	// really, just pretend that this isnt here...
	protected long WINDOW_HANDLE;

	public GLOBALS() {
		this(true);
	}

	public GLOBALS(boolean createFromFile) {
		GLOBALSfromFile(true);
	}

	public GLOBALS GLOBALSfromFile(boolean tryToDeserializeFromFile) {
		File oldGlobals = new File("./globals.gbl");
		return (GLOBALS) CerealDispenser.fromFile(oldGlobals);
	}

	public void printVars() {
		System.out.println("DEBUG: "+DEBUG);
		System.out.println("FPS_LIMIT_"+TICK_LIMIT);
		System.out.println("Timer: "+timer.toString());
		System.out.println("SCALE: "+SCALE);
		System.out.println("TITLE: "+TITLE);
		System.out.println("WIDTH: "+WIDTH);
		System.out.println("HEIGHT: "+HEIGHT);
		System.out.println("PRIMARY MONITOR: "+PRIMARY_MONITOR);
		System.out.println("AVAILABLE VIDEO MODES: ");
		//		GLFWVidMode mode;
		//		while ((mode = availableVideoModes.get())!=null) {
		//			System.out.println("\t"+mode.toString());
		//		}
		//System.out.println("ErrorCallBack: "+errorCallback.toString());
		//System.out.println("KeyCallBack: "+keyboardCallback.toString());
		System.out.println("Window Handle: "+WINDOW_HANDLE);
		System.out.println("Vulkan? "+VULKAN_SUPPORTED);
		System.out.println("Intermediate_Mode? "+useGLIntermediateMode);
		System.out.println("Save_Logs? "+SAVE_LOGS);
		System.out.println("Vsync? "+use_vsync);
		System.out.println("Graphics Context: "+graphicsContext);
		System.out.println("FPS: "+fps.get());
		physicsConstants.printVars();
		System.out.println("");
	}

	public void saveGlobals() {
		File oldGlobals = new File("./globals.gbl");
		boolean success = CerealDispenser.toFile(oldGlobals, this);
		if (!success) {
			//just let the user know that file creation was unsuccessful.
			Logging.log("File was unsuccesfully created!", DEBUG);
		}
	}

	/*
	 *Notes on exit codes!
	 * returning an int of -20 means that one of the sub threads that was running failed to stop with the program.
	 *
	 *
	 *
	 */

}
