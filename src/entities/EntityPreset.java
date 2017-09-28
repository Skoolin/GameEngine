package entities;

import org.lwjgl.util.vector.Vector3f;

import models.Model;
import renderEngine.Engine;

public class EntityPreset extends Preset {
	
    private Model model;
    private float rotX, rotY, rotZ;
    private float scale;
    private boolean isAnimated;
    private boolean isNormalMapped;

	public EntityPreset(Vector3f position, float radius, Model model, float rotX, float rotY, float rotZ, float scale,
			boolean isAnimated, boolean isNormalMapped) {
		this.position = position;
		this.radius = radius;
		this.model = model;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.isAnimated = isAnimated;
		this.isNormalMapped = isNormalMapped;
	}

	@Override
	public Movable makeCopy() {
		if (isAnimated) {
			return (Movable) Engine.addAnimatedEntity(position.x, position.y, position.z, rotX, rotY, rotZ, scale, model);
		} else if (isNormalMapped) {
			return(Movable) Engine.addNormalMapEntity(position.x, position.y, position.z, rotX, rotY, rotZ, scale, model);
		}
		return (Movable) Engine.addStaticEntity(position.x, position.y, position.z, rotX, rotY, rotZ, scale, model);
	}
}
