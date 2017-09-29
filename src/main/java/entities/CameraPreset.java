package entities;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.Engine;

public class CameraPreset extends Preset {
	protected float pitch = 20;
	protected float yaw = 0;
	protected float roll;

	public CameraPreset(Vector3f position, float pitch, float yaw, float roll) {
		this.position = position;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	@Override
	public Movable makeCopy() {
		return Engine.setStaticCamera();
	}

}
