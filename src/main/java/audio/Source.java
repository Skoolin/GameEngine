package audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;

import entities.Movable;
import entities.Preset;

public class Source extends Movable {

	private int sourceID;
	private int rolloff;
	private int referenceDist;
	private int maxDist;
	private boolean looping;
	private float volume;
	private float pitch;

	public Source(Vector3f location, int rolloff, int referenceDist, int maxDist) {
		sourceID = AL10.alGenSources();
		this.position = location;
		setPosition(position);
		this.rolloff = rolloff;
		this.referenceDist = referenceDist;
		this.maxDist = maxDist;
		AL10.alSourcef(sourceID, AL10.AL_ROLLOFF_FACTOR, 1);
		AL10.alSourcef(sourceID, AL10.AL_REFERENCE_DISTANCE, 6);
		AL10.alSourcef(sourceID, AL10.AL_MAX_DISTANCE, 50);
	}

	public void play(String sound) {
		int buffer = AudioMaster.loadSound("audio/" + sound + ".wav");
		stop();
		AL10.alSourcei(sourceID, AL10.AL_BUFFER, buffer);
		AL10.alSourcePlay(sourceID);
	}

	public void delete() {
		stop();
		AL10.alDeleteSources(sourceID);
	}

	public void pause() {
		AL10.alSourcePause(sourceID);
	}

	public void continuePlaying() {
		AL10.alSourcePlay(sourceID);
	}

	public void stop() {
		AL10.alSourceStop(sourceID);
	}

	public void setVelocity(Vector3f direction) {
		AL10.alSource3f(sourceID, AL10.AL_VELOCITY, direction.x, direction.y, direction.z);
	}

	public void setLooping(boolean loop) {
		AL10.alSourcei(sourceID, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	public boolean isPlaying() {
		return AL10.alGetSourcei(sourceID, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	public void setVolume(float volume) {
		AL10.alSourcef(sourceID, AL10.AL_GAIN, volume);
	}

	public void setPitch(float pitch) {
		AL10.alSourcef(sourceID, AL10.AL_PITCH, pitch);
	}

	public void setPosition(Vector3f position) {
		AL10.alSource3f(sourceID, AL10.AL_POSITION, position.x, position.y, position.z);
	}
	
	public void move(float delta) {
		position.x += delta * velocity.x;
		position.y += delta * velocity.y;
		position.z += delta * velocity.z;
		setPosition(position);
	}

	@Override
	public Preset getPreset() {
		return new AudioPreset(position, rolloff, referenceDist, maxDist, looping, volume, pitch);
	}
}
