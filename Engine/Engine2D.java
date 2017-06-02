/*
 * NOTES:
 * 
 * 
 * LINKS:\
 * http://www.wildbunny.co.uk/blog/2011/04/06/physics-engines-for-dummies/
 * https://renderdoc.org/vulkan-in-30-minutes.html
 * http://gamedevelopment.tutsplus.com/tutorials/how-to-create-a-custom-2d-physics-engine-the-basics-and-impulse-resolution--gamedev-6331
 * 
 */

/**
 * TODO:(LIST)Main Engine suff that needs to be done
 * 1. OpenAL threaded responder
 * 2. Better Imputs?  //DONE: Inputs are handed to levels unsing a stack
 * 3. Fullscreen / video modes
 * 4. ???
 * 5. Profit.
 */
package Engine;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.Objects;
import java.util.TimerTask;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import entities.AbstractWorld;
import entities.Entity.RENDERCODES;
import helpers.KeyboardHandler;
import helpers.Logging;
import worlds.blankWorld;

public class Engine2D {

	protected AbstractWorld currentWorld = null;
	protected GLOBALS globals = new GLOBALS(true);
	private boolean running = true; // this is a manual stop to the main loop. Use only when necessary.
	java.util.Timer timer;

	private void loop() {
		// start when the thread is created and print the fps every second from this thread.
		while ( !glfwWindowShouldClose(globals.WINDOW_HANDLE)) {
			if (!running) {
				break;
			}
			if (globals.DEBUG) {
				//System.out.println("Delta Current Frame: "+globals.delta);
			}
			globals.fps.incrementAndGet();
			globals.delta = globals.timer.getDelta();
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			glfwPollEvents();
			// this should wait our thread enough to be nice to the processor
			currentWorld.getCurrentLevel().handleInput(KeyboardHandler.getInputs());
			globals.timer.sync(globals.TICK_LIMIT, Thread.currentThread(), true);
			globals.delta = globals.timer.getDelta();
			currentWorld.getCurrentLevel().update(globals.delta);
			currentWorld.getCurrentLevel().render(globals.graphicsContext);

			glfwSwapBuffers(globals.WINDOW_HANDLE); // swap the frame buffers

			//GLFW.glfwWaitEvents(); // sleeps the main and display threads until the input thread detects hardware interrupts.
		}
	}

	private void setupAudioEngine() {
		globals.audioEngine = new AudioEngine(globals);
	}

	private void setupDisplayInput(String windowTitle) {
		try {
			glfwSetErrorCallback(globals.errorCallback = GLFWErrorCallback.createPrint(System.err));

			if (!glfwInit()) {
				throw new IllegalStateException("Unable To initialize GLFW..");
			}

			glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
			glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
			//glfwWindowHint( ,  );
			//glfwWindowHint(GLFW_DOUBLE_BUFFER, GLFW_TRUE); //Refer to the glfw docs to know what window hints do what

			globals.WINDOW_HANDLE = glfwCreateWindow(globals.WIDTH, globals.HEIGHT, windowTitle, NULL, NULL);

			if (globals.WINDOW_HANDLE == NULL) {
				throw new RuntimeException("Failed to create the GLFW window");
			}

			glfwSetKeyCallback(globals.WINDOW_HANDLE, globals.keyboardCallback = globals.keyHandler = new KeyboardHandler(globals.WINDOW_HANDLE));	

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			// Center our window
			glfwSetWindowPos(
					globals.WINDOW_HANDLE,
					(vidmode.width() - globals.WIDTH) / 2,
					(vidmode.height() - globals.HEIGHT) / 2
					);
			// Make the OpenGL context current
			glfwMakeContextCurrent(globals.WINDOW_HANDLE);
			// Enable v-sync
			glfwSwapInterval(globals.use_vsync.getValue()); //VSYNC: 0=off, 1=singlebufferedMonitorLocked, 2=DoubleBuffered1/2singleBuffered
			// Make the window visible
			glfwShowWindow(globals.WINDOW_HANDLE);

		} catch (Exception e) {
			Logging.logError(e, globals.DEBUG);
			//Display.destroy();
			System.exit(1);
		}
	}

	private void setupOpenGL() {
		GL.createCapabilities();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, globals.WIDTH, globals.HEIGHT, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Logging.log("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION), globals.DEBUG);
	}

	private void setupTimer() {
		timer = new java.util.Timer(true);
		TimerTask fpsCounter = new TimerTask() {

			@Override
			public void run() {
				try {
					if (globals.DEBUG) {
						System.err.println("fps: " + globals.fps.get());
					}
					globals.TITLE = "2D Engine Test!\t    FPS: "+globals.fps.get();
					if (globals.WINDOW_HANDLE != 0 && !Objects.isNull(globals.WINDOW_HANDLE)) {
						GLFW.glfwSetWindowTitle(globals.WINDOW_HANDLE, globals.TITLE);
					}
					globals.fps.set(1);
					return;
				} catch (Exception e) {
					Logging.logError(e, globals.DEBUG);
					System.exit(-20);
				}
			};
		};

		timer.scheduleAtFixedRate(fpsCounter, 0l, 1000l);
	}

	public void startEngine(AbstractWorld world) {
		Logging.log(GLFW.glfwGetVersionString(), true);
		if (globals.VULKAN_SUPPORTED) {
			globals.graphicsContext = RENDERCODES.VULKAN;
			// Note that Vulkan is not implemented but allowed for in the code.
			globals.graphicsContext = RENDERCODES.INTERMEDIATE;
		} else if (globals.useGLIntermediateMode) {
			globals.graphicsContext = RENDERCODES.INTERMEDIATE;
		} else {
			globals.graphicsContext = RENDERCODES.OPENGL;
		}

		currentWorld = world;
		if (currentWorld == null) {
			currentWorld = new blankWorld();
			Logging.log("The Level selected could not be added. \nWorld: "+world , globals.DEBUG);
		}

		Logging.log("Starting New Thread.", globals.DEBUG);
		new Thread().start();
		Logging.log("Starting Display.", globals.DEBUG);
		setupDisplayInput(globals.TITLE);
		Logging.log("Starting OpenGL.", globals.DEBUG);
		setupOpenGL();
		Logging.log("Updating GL capabilities for all objects in all levels.", globals.DEBUG);
		currentWorld.updateGLcapabilities();
		Logging.log("Starting AudioEngine.", globals.DEBUG);
		setupAudioEngine();
		Logging.log("Starting Timer.", globals.DEBUG);
		setupTimer();
		Logging.log("Starting Deltas.", globals.DEBUG);
		globals.delta = globals.timer.getDelta();
		Logging.log("Ready, Entering Loop State.", globals.DEBUG);
		loop();
		Logging.log("Window Closed, Cleaning up .", globals.DEBUG);
		stopTimers();
		globals.audioEngine.cleanup();
		glfwDestroyWindow(globals.WINDOW_HANDLE);
		glfwTerminate();
		Logging.log("glfw realeased", globals.DEBUG);
		globals.keyboardCallback.free();
		globals.errorCallback.free();
		Logging.log("callbacks released. Cya L8r M80.", globals.DEBUG);
		globals.saveGlobals();
		Logging.log("Globals saved for next time.", globals.DEBUG);
		System.exit(0);
	}

	private void stopTimers() {
		timer.purge();
		timer.cancel();
	}

}
