package entities;

import org.lwjgl.util.vector.Vector3f;

public abstract class Preset {

	protected Vector3f position;
	protected float radius;
	
	public Vector3f getPosition() {
		return position;
	}
	
	public float getRadius() {
		return radius;
	}

	public abstract Movable makeCopy();
}
