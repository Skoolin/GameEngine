package audio;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.newdawn.slick.openal.WaveData;

public class AudioMaster {
	
	private static List<Integer> buffers = new ArrayList<Integer>();
	
	public static void init() {
		try {
			AL.create();
			AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public static void setListenerData(float x, float y, float z, FloatBuffer listenerOrientation) {
		AL10.alListener3f(AL10.AL_POSITION, x, y, z);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
		AL10.alListener(AL10.AL_ORIENTATION, listenerOrientation);
	}
	
	public static int loadSound(String file) {
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		WaveData waveFile = WaveData.create(file);
		AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
		return buffer;
	}

	public static void cleanUp() {
		for (int buffer: buffers) {
			AL10.alDeleteBuffers(buffer);
		}
		AL.destroy();
	}
}
