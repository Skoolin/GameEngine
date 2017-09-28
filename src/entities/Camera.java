package entities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import audio.AudioMaster;
import renderEngine.DisplayManager;
import statics.Const;
import toolbox.Maths;

public class Camera extends Movable {
	protected float pitch = 40;
	protected float yaw = 0;
	protected float roll = 0;
	protected float scrollSpeed = 100f;
	protected float zoomSpeed = 0.03f;

	public Camera() {
		position = new Vector3f(0f, 20f, -5f);
	}

	public void move() {
		calculateZoom();
		calculateMovement();
		
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

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * zoomSpeed;
		position.y -= zoomLevel;
		position.z -= zoomLevel;
	}

	private void calculateMovement() {
		if(Mouse.getX() > Const.SCREEN_WIDTH - 2) {
			position.x += DisplayManager.getFrameTime() * scrollSpeed;
		} else if (Mouse.getX() < 3) {
			position.x -= DisplayManager.getFrameTime() * scrollSpeed;
		}
		
		if(Mouse.getY() > Const.SCREEN_HEIGHT - 2) {
			position.z -= DisplayManager.getFrameTime() * scrollSpeed;
		} else if (Mouse.getY() < 3) {
			position.z += DisplayManager.getFrameTime() * scrollSpeed;
		}
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	@Override
	public Preset getPreset() {
		return new CameraPreset(position,pitch, yaw, roll);
	}
}
