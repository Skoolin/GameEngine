package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import models.Model;
import renderEngine.DisplayManager;

public class Player extends Entity {
	
	private static final float RUN_SPEED = 20;
	private static final float TURN_SPEED = 160;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;

	public Player(boolean isAnimated, Model model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(isAnimated, model, position, rotX, rotY, rotZ, scale);
	}

	public void move() {
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTime(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTime();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
	}

	private void checkInputs() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			currentSpeed = RUN_SPEED;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			currentSpeed = -RUN_SPEED;
		} else {
			this.currentSpeed = 0;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			currentTurnSpeed = -TURN_SPEED;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			currentTurnSpeed = TURN_SPEED;
		} else {
			this.currentTurnSpeed = 0;
		}
	}
}
