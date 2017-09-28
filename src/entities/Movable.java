package entities;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public abstract class Movable {
	
	protected Vector3f position;
	protected float radius;
	protected Vector3f velocity = new Vector3f(0f, 0f, 0f);

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public Vector3f getVelocity() {
		return velocity;
	}
	
	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}

	public float distance(Movable movable) {
		return (float) Math.sqrt(distanceSqr(movable));
	}
	
	public float distanceSqr(Movable movable) {
		Vector3f vector = new Vector3f(position.x - movable.position.x, position.y - movable.position.y, position.z - movable.position.z);
		return vector.x * vector.x + vector.y * vector.y + vector.z * vector.z;
	}
	
	public float distance(Vector3f vector) {
		return (float) Math.sqrt(distanceSqr(vector));
	}
	
	public float distanceSqr(Vector3f vector) {
		Vector3f distVec = new Vector3f(position.x - vector.x, position.y - vector.y, position.z - vector.z);
		return distVec.x * distVec.x + distVec.y * distVec.y + distVec.z * distVec.z;
	}
	
	public void move(float delta) {
		position.x += delta * velocity.x;
		position.y += delta * velocity.y;
		position.z += delta * velocity.z;
	}
	
	public abstract Preset getPreset();

	public float flatDistance(Movable movable) {
		return (float) Math.sqrt(flatDistanceSqr(movable));
	}
	
	public float flatDistanceSqr(Movable movable) {
		Vector2f distVec = new Vector2f(position.x - movable.position.x, position.z - movable.position.z);
		return distVec.x * distVec.x + distVec.y * distVec.y;
	}

	public float flatDistanceSqr(Vector2f target) {
		Vector2f distVec = new Vector2f(position.x - target.x, position.z - target.y);
		return distVec.x * distVec.x + distVec.y * distVec.y;
	}
}
