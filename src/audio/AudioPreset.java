package audio;

import org.lwjgl.util.vector.Vector3f;

import entities.Movable;
import entities.Preset;

public class AudioPreset extends Preset {

	private int rolloff;
	private int referenceDist;
	private int maxDist;
	private boolean looping;
	private float volume;
	private float pitch;

	public AudioPreset(Vector3f position, int rolloff, int referenceDist, int maxDist, boolean looping, float volume,
			float pitch) {
		this.position = position;
		this.rolloff = rolloff;
		this.referenceDist = referenceDist;
		this.maxDist = maxDist;
		this.looping = looping;
		this.volume = volume;
		this.pitch = pitch;
	}
	
	@Override
	public Movable makeCopy() {
		Source source = new Source(position, rolloff, referenceDist, maxDist);
		source.setLooping(looping);
		source.setPitch(pitch);
		source.setVolume(volume);
		return source;
	}

}
