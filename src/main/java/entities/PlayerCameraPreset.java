package entities;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.Engine;

public class PlayerCameraPreset extends CameraPreset {
	
	@SuppressWarnings("unused")
	private float distanceFromPlayer = 50;
	@SuppressWarnings("unused")
	private float angleAroundPlayer = 0;

	public Movable player;

	public PlayerCameraPreset(float distanceFromPlayer, float yaw, float angleAroundPlayer, Movable player2) {
		super(new Vector3f(0f, 0f, 0f), 0f, yaw, 0f);
		this.player = player2;
		this.distanceFromPlayer = distanceFromPlayer;
		this.angleAroundPlayer = angleAroundPlayer;
	}

	@Override
	public Movable makeCopy() {
		return Engine.setPlayerCamera(player);
	}

}
