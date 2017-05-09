package helpers;

import org.lwjgl.glfw.GLFW;

/* NOTES:
 *http://www.java-gaming.org/topics/game-loops/24220/view.html
 *http://gamedev.stackexchange.com/questions/40683/random-spike-in-delta-time
 *http://forum.lwjgl.org/index.php?topic=4452.10;wap2
 *https://sourceforge.net/p/java-game-lib/mailman/message/29029484/
 */

//TODO: toString? or print func
//TODO: FIX TIMER!
public class Timer {
	
	public static final float NANOS = 100000000;
	
	private double lastFrame = 0;
	private double lastTime;
	private double overSleep;
	private double sleepTime;
	private double t;
	private double timerFreq = GLFW.glfwGetTimerFrequency() / 1; //decrease by 1000 becasuse threads dont measure that fast.
	private double variableYieldTime;
	private double yieldTime;
	private double delta;
	
	public Timer() {
		setupTimer();
	}

	public void setDelta() {
		double currentTime = getTime();
		this.delta = (float) (currentTime - lastFrame) * 100000;
		lastFrame = getTime();
	}
	
	public double getDelta() {
		return this.delta;
	}
	
	public double getTime() {
		//System.err.println("Got Time: "+ GLFW.glfwGetTime() * NANOS / timerFreq);
		return (GLFW.glfwGetTime() / timerFreq); // get time in ticks per second
		//return (Sys.getTime() * 1000) / Sys.getTimerResolution();
		//glfwGetTimerFrequency();
	}
	
	public void setupTimer() {
		this.variableYieldTime = 1l;
		this.lastTime = 0l;
		GLFW.glfwSetTime(1);
		this.variableYieldTime = 0l;
		lastFrame = getTime();
		String freq = ("Timer res:" + timerFreq);
		//as of when this was working the timer resolution was: 2016/09/03 12:00:40, Timer res:2727.0
		Logging.log(freq, true, Logging.LOG_LEVEL.ERROR);
//		java.util.Timer timer = new java.util.Timer(true);
//		
//		if (false) { // testing code: note that this doesnt compile in
//			timer.scheduleAtFixedRate(new TimerTask() {
//				public void run() {
//					try {
//						System.err.println("Last Frame: " + lastFrame + ". Last Time: " + lastTime + ". sleep time: "
//								+ sleepTime + ". \nYieldTime: " + yieldTime + ". VariableYield Time: "
//								+ variableYieldTime + ". Oversleep: " + overSleep + ".");
//						System.err.println("T: " + t + ". sleepTime - yieldTime: " + (sleepTime - yieldTime));
//						double currentTime = getTime();
//						float delta = (float) (currentTime - lastFrame) * 100000;
//						System.err.println("Delta: " + delta); //getting delta here ruins the loop delta
//						return;
//					} catch (Exception e) {
//						Logging.logError(e, true);
//						System.exit(-20);
//					}
//				};
//			}, 0l, 1000l); // start when the thread is created and print the fps every second from this thread.
//		}

	}

	/**
	 * 
	 * So, after many hours and expert opinions, I have found a solution
	 * This basically works.
	 * HOWEVER!
	 * it syncs to about 10-20% more ticks than selected for.
	 * This is still useful though. and it works so I will leave it.
	 * 
	 * Some useful Numbers:
	 * For actual ticks per second use the modified number
	 * Target ticks/sec = actual selected rate
	 *  60 = 45
	 *  75 = 56
	 * 100 = 75
	 * 120 = 90
	 * 144 = 108
	 * 
	 * If anyone finds a relation between these, or just my math error,
	 * I would be so happy.
	 * @param ticksPerSecond
	 * @param thread
	 * @param DEBUG
	 */

	public void sync(int ticksPerSecond, Thread thread, boolean DEBUG) {
		setDelta();
		if (ticksPerSecond <= 0) return;
		sleepTime = (timerFreq / ticksPerSecond) / 10000000;
		yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % 1000); 
		overSleep = 0; 
		if (sleepTime == 0) return;
		try {
			while (true) {
				
				t = getTime() - lastTime;

				if (t < sleepTime - yieldTime) {
					Thread.sleep(1);
				}
				else if (t < sleepTime) {
					Thread.yield();
				}
				else {
					overSleep = t - sleepTime; 
					break; 
				}
			}
		} catch (InterruptedException e) {
			Logging.logError(e, true);
		}
		
		lastTime = getTime() - Math.min(overSleep, sleepTime);

		if (overSleep > variableYieldTime) {
			variableYieldTime = Math.min(variableYieldTime + 20, sleepTime);
		}
		else if (overSleep < variableYieldTime - 20) {
			variableYieldTime = Math.max(variableYieldTime - 20, 0); 
		}
	}
	
	/**
	 * Timer base that ended up not working well
	 *
	 * @deprecated use {@link #sync()} instead.  
	 */
	@Deprecated
	public void syncLegacy(int ticksPerSecond, Thread thread, boolean debug) {
		
		if (ticksPerSecond <= 0) return;

		sleepTime = (NANOS/10000000) / ticksPerSecond; // nanoseconds to sleep this frame
		// yieldTime + remainder micro & nano seconds if smaller than sleepTime
		yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000000));
		overSleep = 0; // time the sync goes over by
		try {
			while (true) {
				t = getTime() - lastTime;
//				if (debug) {
//					System.out.println("Last Frame: " + lastFrame + ". Last Time: " + lastTime + ". sleep time: "
//							+ sleepTime + ". YieldTime: " + yieldTime + ". VariableYield Time: " + variableYieldTime
//							+ ". Oversleep: " + overSleep + ".\nOn Thread: " + thread + ". For " + fps + "  fps.");
//					System.out.println("T: " + t + ". sleepTime - yieldTime: " + (sleepTime - yieldTime));
//				}
				if (t < sleepTime - yieldTime) {
					Thread.sleep(1);
					//break;
				}
				else if (t < sleepTime) {
					// burn the last few CPU cycles to ensure accuracy
					Thread.yield();
					//break;
				}
				else {
					overSleep = t - sleepTime;
					break; // exit while loop
				}
			}
		} catch (InterruptedException e) {
			Logging.logError(e, true);
		}

		lastTime = getTime() - Math.min(overSleep, sleepTime);

		// auto tune the time sync should yield
		if (overSleep > variableYieldTime) {
			// increase by 200 microseconds (1/5 a ms)
			variableYieldTime = Math.min(variableYieldTime + 2000000, sleepTime);//we are taking the min of the two to ensure that we don't yield too long
		}
		else if (overSleep < variableYieldTime - 200000) {
			// decrease by 2 microseconds
			variableYieldTime = Math.max(variableYieldTime - 2000, 0);//we are taking the max of the two to ensure that we don't yield too long
		}
	}

}
