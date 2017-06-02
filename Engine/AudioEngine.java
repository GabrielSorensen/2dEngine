package Engine;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;


//	
/*	kcat.strangesoft.net/openal-tutorial.html
 * 	
 * 	note: if we want audio data to be played from either direction (left,right) we can do that by monaural(ing)
 * 		the data in the buffer and then applying the source position to either direction.
 * 
 *  TODO:(LIST)Finish audio engine,
 * 		1.Finish AudioData
 * 			add:	audio byte data, audio format, frequency
 * 			move: 	AUDIOENGINE buffers -> sources. the audioData classes will hold the right data
 * 			test: 	everything?
 */

public class AudioEngine {

	private class AudioData {

		protected IntBuffer buffer = BufferUtils.createIntBuffer(1);
		protected ByteBuffer data = null;

		public AudioData() {}

		public void deleteBuffer() {
			AL10.alDeleteBuffers(buffer);
		}



	}
	private long AUDIOCALLBACK = 0l;

	private long AUDIODEVICE = 0l;
	private GLOBALS globals;
	// Sound data
	ArrayList<AudioData> buffers = new ArrayList<>();

	//V first three floats are position, second three are the direction (in this case up or at)
	FloatBuffer listenerOrientation = BufferUtils.createFloatBuffer(6).put(new float[] {0f, 0f, -1f, 0f, 1f, 0f});
	FloatBuffer listenerPosition = BufferUtils.createFloatBuffer(3).put(new float[] {0f, 0f, 0f});
	FloatBuffer listenerVelocity = BufferUtils.createFloatBuffer(3).put(new float[] {0f, 0f, 0f});
	FloatBuffer sourcePosition = BufferUtils.createFloatBuffer(3).put(new float[] {0f, 0f, 0f});
	// points emmiting sound data
	ArrayList<IntBuffer> sources = new ArrayList<>();

	FloatBuffer sourceVelocity = BufferUtils.createFloatBuffer(3).put(new float[] {0f, 0f, 0f});

	public AudioEngine(GLOBALS globals) {
		this.globals = globals;
		setup();
	}

	public void setSourcePosition(float x, float y, float z) {
		sourcePosition.put(new float[] {x, y, z});
	}

	/*
	 * @params: path: the file to be loaded into the available buffers
	 * @return: returns the index of the placed buffer for callback.
	 */
	public int addBuffer(String path) {
		try {
			int index = -1;
			AudioData audio = getAudioData(path);
			if (!buffers.contains(audio)) {
				buffers.add(audio);
				index = buffers.indexOf(audio);
			}
			if (ALC10.alcGetError(AUDIOCALLBACK) == ALC10.ALC_NO_ERROR) {
				return index;
			}
			throw new IOException("Could not load "+path+", into a buffer.");
		} catch (IOException io) {
			helpers.Logging.logError(io, globals.DEBUG);
			return -1;
		}
	}

	public void pauseSource(int bufferNumber) {
		AL10.alSourcePause(bufferNumber);
	}

	public void playSource(int bufferNumber) {
		AL10.alSourcePlayv(sources.get(bufferNumber));
	}


	public void stopSource(int bufferNumber) {
		AL10.alSourceStop(bufferNumber);
	}

	void setListenerValues() {
		AL10.alListenerfv(AL10.AL_POSITION,    listenerPosition);
		AL10.alListenerfv(AL10.AL_VELOCITY,    listenerVelocity);
		AL10.alListenerfv(AL10.AL_ORIENTATION, listenerOrientation);
	}

	public AudioData getAudioData(String path) {
		int totalFramesRead = 0;
		File fileIn = new File(path);
		// somePathName is a pre-existing string whose value was
		// based on a user selection.
		try {
			AudioInputStream audioInputStream = 
					AudioSystem.getAudioInputStream(fileIn);
			int bytesPerFrame = 
					audioInputStream.getFormat().getFrameSize();
			if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
				// some audio formats may have unspecified frame size
				// in that case we may read any amount of bytes
				bytesPerFrame = 1;
			} 
			// Set an arbitrary buffer size of 1024 frames.
			int numBytes = 1024 * bytesPerFrame; 
			byte[] audioBytes = new byte[numBytes];
			try {
				int numBytesRead = 0;
				int numFramesRead = 0;
				// Try to read numBytes bytes from the file.
				while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
					// Calculate the number of frames actually read.
					numFramesRead = numBytesRead / bytesPerFrame;
					totalFramesRead += numFramesRead;
					// Here, do something useful with the audio data that's 
					// now in the audioBytes array...
				}
				AudioData audio = new AudioData();
				audio.data = ByteBuffer.allocate(1024 * audioBytes.length);
				audio.data.put(audioBytes);
				audio.data.flip();
				audio.data.rewind();
				//The audio data should now be in the byteBuffer inside the AudioData class
			} catch (Exception ex) { 
				// Handle the error...
			}
		} catch (Exception e) {
			// Handle the error...
		}
		return null;
	}

	private void setup() {
		try {
			new Thread().start();
			//I am not 100% sure why this throws an error,
			//hoever, this will not work without it.
			ALC.create();
			AUDIODEVICE = ALC10.alcOpenDevice((ByteBuffer)null);
			/**
			 *This is some meta shit.
			 *note that alcOpenDevice opens a device for audio rendering using hardware implementations
			 *the method is overloaded to accept either a bytebuffer for the device specifier
			 *OR, to accept a charsequence at the name (a char sequence is a null terminated string like C)
			 *passing NULL here tells the method to select whatever device is ready and willing
			 *!!!!BUT!!!! as of java 7/8? the method must be identifiable from the signature
			 *this includes the strong typing of the parameters, and since null is every type.
			 *We cast the null to be a specific object to call which method we want, which doesnt matter
			 */
			//AUDIODEVICE = ALC10.alcOpenDevice((CharSequence)null); // would also work. 

			AUDIOCALLBACK = ALC10.nalcCreateContext(AUDIODEVICE, 0l); //nalcCreateContext might be a spelling error...
			AUDIOCALLBACK = ALC10.alcGetCurrentContext();

			ALCCapabilities ca = ALC.createCapabilities(AUDIODEVICE);
			AL.createCapabilities(ca);

			ALC10.alcGetError(AUDIOCALLBACK);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private int loadALData() {
		// Load wav data into a buffer.

		//AL10.alGenBuffers(buffer);

		if(AL10.alGetError() != AL10.AL_NO_ERROR)
			return AL10.AL_FALSE;

		//Loads the wave file from your file system
		/*java.io.FileInputStream fin = null;
	    try {
	      fin = new java.io.FileInputStream("FancyPants.wav");
	    } catch (java.io.FileNotFoundException ex) {
	      ex.printStackTrace();
	      return AL10.AL_FALSE;
	    }
	    WaveData waveFile = WaveData.create(fin);
	    try {
	      fin.close();
	    } catch (java.io.IOException ex) {
	    }*/

		//Loads the wave file from this class's package in your classpath
		//WaveData waveFile = WaveData.create("FancyPants.wav");

		//AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
		//waveFile.dispose();

		// Bind the buffer with the source.
		//AL10.alGenSources(source);

		if (AL10.alGetError() != AL10.AL_NO_ERROR)
			return AL10.AL_FALSE;

		//		AL10.alSourcei(source.get(0), AL10.AL_BUFFER,   buffer.get(0) );
		//		AL10.alSourcef(source.get(0), AL10.AL_PITCH,    1.0f          );
		//		AL10.alSourcef(source.get(0), AL10.AL_GAIN,     1.0f          );
		//		AL10.alSource (source.get(0), AL10.AL_POSITION, sourcePos     );
		//		AL10.alSource (source.get(0), AL10.AL_VELOCITY, sourceVel     );

		// Do another error check and return.
		if (AL10.alGetError() == AL10.AL_NO_ERROR)
			return AL10.AL_TRUE;

		return AL10.AL_FALSE;
	}

	public void cleanup() {
		for (int i=0; i<sources.size(); i++) {
			AL10.alDeleteSources(sources.get(i));
		}
		for (int i=0; i<buffers.size(); i++) {
			//AL10.alDeleteBuffers(buffers.get(i));
			//but because this data is packed in the audioData wrapper we call the method to do this
			buffers.get(i).deleteBuffer();
		}
		ALC.destroy();
		AUDIOCALLBACK = 0l;
		buffers = null;
		sources = null;
		return;
	}
}
