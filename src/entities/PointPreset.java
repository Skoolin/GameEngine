package entities;

import org.lwjgl.util.vector.Vector3f;

public class PointPreset extends Preset {

	public PointPreset(Vector3f position) {
		this.position = position;
		this.radius = 0;
	}

	@Override
	public Movable makeCopy() {
		return new Point(position);
	}
}
