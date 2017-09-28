package particles;

import org.lwjgl.util.vector.Vector3f;

import entities.Movable;
import entities.Preset;
import renderEngine.Engine;

public class ParticlePreset extends Preset {

	private float particlesPerSecond, averageSpeed, gravityComplient, averageLifeLength, averageScale;

	private float speedError, lifeError, scaleError = 0;
	private boolean randomRotation = false;
	private Vector3f direction;
	private float directionDeviation = 0;

	private float startingDeviation = 0;

	private ParticleTexture texture;

	public ParticlePreset(Vector3f position, float particlesPerSecond, float averageSpeed, float gravityComplient,
			float averageLifeLength, float averageScale, float speedError, float lifeError, float scaleError,
			boolean randomRotation, Vector3f direction, float directionDeviation, float startingDeviation,
			ParticleTexture texture) {
		this.position = position;
		this.particlesPerSecond = particlesPerSecond;
		this.averageSpeed = averageSpeed;
		this.gravityComplient = gravityComplient;
		this.averageLifeLength = averageLifeLength;
		this.averageScale = averageScale;
		this.speedError = speedError;
		this.lifeError = lifeError;
		this.scaleError = scaleError;
		this.randomRotation = randomRotation;
		this.direction = direction;
		this.directionDeviation = directionDeviation;
		this.startingDeviation = startingDeviation;
		this.texture = texture;
	}

	@Override
	public Movable makeCopy() {
		ParticleSystem system = new ParticleSystem(texture, direction, particlesPerSecond, averageSpeed,
				gravityComplient, averageLifeLength, averageScale);
		system.setPosition(position);
		system.setStartingSpread(startingDeviation);
		system.setRealLifeError(lifeError);
		system.setRealScaleError(scaleError);
		system.setRealSpeedError(speedError);
		if(randomRotation) {
			system.randomizeRotation();
		}
		system.setDirection(direction, directionDeviation);
		return Engine.addParticleSystem(system);
	}

}
