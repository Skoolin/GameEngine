package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import audio.AudioMaster;
import toolbox.Maths;

public class PlayerCamera extends Camera {

	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;

	public Movable player;

	public PlayerCamera(Movable player) {
		this.player = player;
		position = new Vector3f(0f, 0f, 0f);
	}

	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroudPlayer();
		float hDist = calculateHorizontalDistance();
		float vDist = calculateVerticalDistance();
		calculateCameraPosition(hDist, vDist);
		this.yaw = 180 - (angleAroundPlayer);
		ByteBuffer bb = ByteBuffer.allocateDirect( 6 * 4);
	    bb.order( ByteOrder.nativeOrder() );
	    FloatBuffer listenerOrientation = bb.asFloatBuffer();
	    Matrix4f viewMatrix = Maths.createViewMatrix(this);
	    listenerOrientation.put( 0, viewMatrix.m00 );
	    listenerOrientation.put( 1, viewMatrix.m01 );
	    listenerOrientation.put( 2, viewMatrix.m02 );
	    listenerOrientation.put( 3, viewMatrix.m10 );
	    listenerOrientation.put( 4, viewMatrix.m11 );
	    listenerOrientation.put( 5, viewMatrix.m12 );
		AudioMaster.setListenerData(position.x, position.y, position.z, listenerOrientation);
	}
	
	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
	}
	
	private void calculatePitch() {
	}
	
	private void calculateCameraPosition(float hDist, float vDist) {
		float theta = angleAroundPlayer;
		float offsetX = (float) (hDist * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (hDist * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + vDist + 8;
	}
	
	private void calculateAngleAroudPlayer() {
	}
	@Override
	public Preset getPreset() {
		return new PlayerCameraPreset(distanceFromPlayer, yaw, angleAroundPlayer, player);
	}
}
