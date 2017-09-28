package entities;

import org.lwjgl.util.vector.Vector3f;

public class Point extends Movable {

	public Point(Vector3f position) {
		this.position = position;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	@Override
	public Preset getPreset() {
		return new PointPreset(position);
	}
}
