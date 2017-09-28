package particles;

import java.util.Random;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Movable;
import entities.Preset;
import loaders.Loader;
import models.RawModel;
import renderEngine.DisplayManager;

public class ParticleSystem extends Movable {

	public void setParticlesPerSecond(float particlesPerSecond) {
		this.particlesPerSecond = particlesPerSecond;
	}

	public void setAverageSpeed(float averageSpeed) {
		this.averageSpeed = averageSpeed;
	}

	public void setGravityComplient(float gravityComplient) {
		this.gravityComplient = gravityComplient;
	}

	public void setAverageLifeLength(float averageLifeLength) {
		this.averageLifeLength = averageLifeLength;
	}

	public void setAverageScale(float averageScale) {
		this.averageScale = averageScale;
	}

	public void setRandomRotation(boolean randomRotation) {
		this.randomRotation = randomRotation;
	}

	public void setXRot(float xrot) {
		this.rotation3d.x = xrot;
	}

	public void setYRot(float yrot) {
		this.rotation3d.y = yrot;
	}

	public void setZRot(float zrot) {
		this.rotation3d.z = zrot;
	}

	public void setRotationError(Vector3f vector) {
		this.rotationIncrease = vector;
	}
	
	public void setScaleIncrease(float inc) {
		this.scaleIncrease = inc;
	}
	
	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

	public void setDirectionDeviation(float directionDeviation) {
		this.directionDeviation = directionDeviation;
	}

	public void setStartingDeviation(float startingDeviation) {
		this.startingDeviation = startingDeviation;
	}

	public void setTexture(ParticleTexture texture) {
		this.texture = texture;
	}

	public void setModel(String modelName) {
		if (modelName != null) {
			model = Loader.loadObjModel(modelName);
		} else {
			model = null;
		}
	}

	private float particlesPerSecond, averageSpeed, gravityComplient, averageLifeLength, averageScale;

	private float speedError, lifeError, scaleError = 0;
	private boolean randomRotation = false;
	private Vector3f direction;
	private float directionDeviation = 0;

	private float startingDeviation = 0;

	private ParticleTexture texture;

	private Random random = new Random();

	private float cameraDistSqr = 0;
	private RawModel model;

	private Vector3f rotation3d;

	private Vector3f rotationIncrease;

	private float scaleIncrease;

	public ParticleSystem(ParticleTexture texture, Vector3f position, float particlesPerSecond, float speed,
			float gravityComplient, float lifeLength, float scale) {
		this.position = position;
		this.texture = texture;
		this.particlesPerSecond = particlesPerSecond;
		this.averageSpeed = speed;
		this.gravityComplient = gravityComplient;
		this.averageLifeLength = lifeLength;
		this.averageScale = scale;
		this.rotation3d = new Vector3f(0f, 0f, 0f);
		this.model = null;
	}

	public ParticleSystem(ParticleTexture texture, RawModel model, ParticleSystem system, Vector3f position,
			Vector3f velocity, float gravityEffect, float lifeLength, Vector3f rotation, float scale,
			Vector3f rotationIncrease, float scaleIncrease) {
		this.texture = texture;
		this.position = position;
		this.velocity = velocity;
		this.gravityComplient = gravityEffect;
		this.averageLifeLength = lifeLength;
		this.rotation3d = rotation;
		this.averageScale = scale;
		this.model = model;
		this.rotationIncrease = rotationIncrease;
		this.scaleIncrease = scaleIncrease;
	}

	/**
	 * @param direction
	 *            - The average direction in which particles are emitted.
	 * @param deviation
	 *            - A value between 0 and 1 indicating how far from the chosen
	 *            direction particles can deviate.
	 */
	public void setDirection(Vector3f direction, float deviation) {
		if (direction != null) {
			this.direction = new Vector3f(direction);
			this.directionDeviation = (float) (deviation * Math.PI);
		}
	}

	public void randomizeRotation() {
		randomRotation = true;
	}

	public ParticleTexture getTexture() {
		return this.texture;
	}

	public void setStartingSpread(float value) {
		this.startingDeviation = value;
	}

	/**
	 * @param error
	 *            - A number between 0 and 1, where 0 means no error margin.
	 */
	public void setSpeedError(float error) {
		this.speedError = error * averageSpeed;
	}

	public void setRealSpeedError(float error) {
		this.speedError = error;
	}

	/**
	 * @param error
	 *            - A number between 0 and 1, where 0 means no error margin.
	 */
	public void setLifeError(float error) {
		this.lifeError = error * averageLifeLength;
	}

	public void setRealLifeError(float error) {
		this.lifeError = error;
	}

	/**
	 * @param error
	 *            - A number between 0 and 1, where 0 means no error margin.
	 */
	public void setScaleError(float error) {
		this.scaleError = error * averageScale;
	}

	public void setRealScaleError(float error) {
		this.scaleError = error;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void generateParticles() {
		float delta = DisplayManager.getFrameTime();
		float particlesToCreate = particlesPerSecond * delta;
		int count = (int) Math.floor(particlesToCreate);
		float partialParticle = particlesToCreate % 1;
		for (int i = 0; i < count; i++) {
			float xDev = position.x + (random.nextFloat() * 2 * startingDeviation) - startingDeviation;
			float yDev = position.y + (random.nextFloat() * 2 * startingDeviation) - startingDeviation;
			float zDev = position.z + (random.nextFloat() * 2 * startingDeviation) - startingDeviation;
			emitParticle(new Vector3f(xDev, yDev, zDev));
		}
		if (Math.random() < partialParticle) {
			emitParticle(position);
		}
	}

	private void emitParticle(Vector3f center) {
		Vector3f velocity = null;
		if (direction != null) {
			velocity = generateRandomUnitVectorWithinCone(direction, directionDeviation);
		} else {
			velocity = generateRandomUnitVector();
		}
		velocity.normalise();
		velocity.scale(generateValue(averageSpeed, speedError));
		float scale = generateValue(averageScale, scaleError);
		float lifeLength = generateValue(averageLifeLength, lifeError);
		new Particle(texture, model, this, new Vector3f(center), velocity, gravityComplient, lifeLength,
				generateRotation(), rotation3d, scale, rotationIncrease, scaleIncrease);
	}

	private float generateValue(float average, float errorMargin) {
		float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
		return average + offset;
	}

	private float generateRotation() {
		if (randomRotation) {
			return random.nextFloat() * 360f;
		} else {
			return 0;
		}
	}

	private static Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, float angle) {
		if (coneDirection.lengthSquared() > 0f) {
			float cosAngle = (float) Math.cos(angle);
			Random random = new Random();
			float theta = (float) (random.nextFloat() * 2f * Math.PI);
			float z = cosAngle + (random.nextFloat() * (1 - cosAngle));
			float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
			float x = (float) (rootOneMinusZSquared * Math.cos(theta));
			float y = (float) (rootOneMinusZSquared * Math.sin(theta));

			Vector4f direction = new Vector4f(x, y, z, 1);
			if (coneDirection.x != 0 || coneDirection.y != 0 || (coneDirection.z != 1 && coneDirection.z != -1)) {
				Vector3f rotateAxis = Vector3f.cross(coneDirection, new Vector3f(0, 0, 1), null);
				rotateAxis.normalise();
				float rotateAngle = (float) Math.acos(Vector3f.dot(coneDirection, new Vector3f(0, 0, 1)));
				Matrix4f rotationMatrix = new Matrix4f();
				rotationMatrix.rotate(-rotateAngle, rotateAxis);
				Matrix4f.transform(rotationMatrix, direction, direction);
			} else if (coneDirection.z == -1) {
				direction.z *= -1;
			}
			return new Vector3f(direction);
		} else {
			return generateRandomUnitVectorWithinCone(new Vector3f(1f, 1f, 1f), 360f);
		}
	}

	private Vector3f generateRandomUnitVector() {
		float theta = (float) (random.nextFloat() * 2f * Math.PI);
		float z = (random.nextFloat() * 2) - 1;
		float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float y = (float) (rootOneMinusZSquared * Math.sin(theta));
		return new Vector3f(x, y, z);
	}

	public float getCameraDistSqr() {
		return cameraDistSqr;
	}

	public void update(Camera camera) {
		this.cameraDistSqr = distanceSqr(camera);
	}

	@Override
	public Preset getPreset() {
		return new ParticlePreset(position, particlesPerSecond, averageSpeed, gravityComplient, averageLifeLength,
				averageScale, speedError, lifeError, scaleError, randomRotation, direction, directionDeviation,
				startingDeviation, texture);
	}

	public RawModel getModel() {
		return this.model;
	}
}
