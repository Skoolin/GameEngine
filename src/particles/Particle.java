package particles;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import models.RawModel;
import renderEngine.DisplayManager;
import statics.Const;

public class Particle {

	private Vector3f position;
	private Vector3f velocity;
	private float gravityEffect;
	private float lifeLength;
	private float rotation;
	private Vector3f rotation3d;
	private float scale;

	private float scaleIncrease;
	private Vector3f rotationIncrease;

	private ParticleTexture texture;

	private Vector2f texOffset1 = new Vector2f();
	private Vector2f texOffset2 = new Vector2f();
	private float blend;
	private boolean hasModel;

	public Vector2f getTexOffset1() {
		return texOffset1;
	}

	public Vector2f getTexOffset2() {
		return texOffset2;
	}

	public float getBlend() {
		return blend;
	}

	private float elapsedTime;
	private float distance;

	public Particle(ParticleTexture texture, RawModel model, ParticleSystem system, Vector3f position,
			Vector3f velocity, float gravityEffect, float lifeLength, float rotation, Vector3f rotation3d, float scale,
			Vector3f rotationIncrease, float scaleIncrease) {
		this.texture = texture;
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.rotation3d = new Vector3f(rotation3d);
		this.scale = scale;
		this.rotationIncrease = rotationIncrease;
		this.scaleIncrease = scaleIncrease;
		this.hasModel = system.getModel() != null;
		ParticleMaster.addParticle(this, system);
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public float getGravityEffect() {
		return gravityEffect;
	}

	public float getLifeLength() {
		return lifeLength;
	}

	public float getDistance() {
		return distance;
	}

	public float getRotation() {
		return rotation;
	}

	public Vector3f get3dRotation() {
		return this.rotation3d;
	}

	public float getScale() {
		return scale;
	}

	public float getElapsedTime() {
		return elapsedTime;
	}

	protected boolean update(Camera camera) {
		if(hasModel) {
			Vector3f.add(rotation3d, (Vector3f) new Vector3f(rotationIncrease).scale(DisplayManager.getFrameTime()), rotation3d);
		}
		scale += scaleIncrease * DisplayManager.getFrameTime();
		velocity.y += Const.GRAVITY * gravityEffect * DisplayManager.getFrameTime();
		Vector3f change = new Vector3f(velocity);
		change.scale(DisplayManager.getFrameTime());
		Vector3f.add(change, position, position);
		distance = Vector3f.sub(camera.getPosition(), position, null).lengthSquared();
		updateTextureCoordInfo();
		elapsedTime += DisplayManager.getFrameTime();
		return elapsedTime < lifeLength;
	}

	public ParticleTexture getTexture() {
		return texture;
	}

	private void updateTextureCoordInfo() {
		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
		float atlasProgression = lifeFactor * stageCount;
		int index1 = (int) Math.floor(atlasProgression);
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
		this.blend = atlasProgression % 1;
		setTextureOffset(texOffset1, index1);
		setTextureOffset(texOffset2, index2);
	}

	private void setTextureOffset(Vector2f offset, int index) {
		int column = index % texture.getNumberOfRows();
		int row = index / texture.getNumberOfRows();
		offset.x = (float) column / texture.getNumberOfRows();
		offset.y = (float) row / texture.getNumberOfRows();
	}
}
