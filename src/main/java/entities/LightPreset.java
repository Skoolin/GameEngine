package entities;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.Engine;


public class LightPreset extends Preset {
	
	private Vector3f colour;
	private Vector3f attenuation;

	public LightPreset(Vector3f position, Vector3f colour, Vector3f attenuation) {
		this.position = position;
		this.colour = colour;
		this.attenuation = attenuation;
	}
	
	public Vector3f getColour() {
		return colour;
	}
	
	public Vector3f getAttenuation() {
		return attenuation;
	}

	@Override
	public Movable makeCopy() {
		// TODO Auto-generated method stub
		return Engine.addLight(position.x, position.y, position.z, colour, attenuation);
	}
}
