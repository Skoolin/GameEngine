package entities;
 
import models.Model;
 
import org.lwjgl.util.vector.Vector3f;

import animatedModels.AnimatedModel;
import loaders.AnimationLoader;
 
public class Entity extends Movable {
 
    private Model model;
    private float rotX, rotY, rotZ;
    private float scale;
    private boolean isAnimated;
    
    private int textureIndex = 0;
    
    public void update() {
    	model.update();
    }
 
    public Entity(boolean animated, Model model, Vector3f position, float rotX, float rotY, float rotZ,
            float scale) {
    	this.isAnimated = animated;
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }
    
    public Entity(boolean animated, Model model, int index, Vector3f position, float rotX, float rotY, float rotZ,
            float scale) {
    	this.isAnimated = animated;
        this.model = model;
        this.textureIndex = index;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }
    
    public boolean getAnimated() {
    	return isAnimated;
    }
    
    public float getTextureXOffset() {
    	int column = textureIndex%model.getTexture().getNumberOfRows();
    	return (float) column / (float) model.getTexture().getNumberOfRows();
    }
    
    public float getTextureYOffset() {
    	int row = textureIndex/model.getTexture().getNumberOfRows();
    	return (float) row / (float) model.getTexture().getNumberOfRows();
    }
 
    public void increasePosition(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }
 
    public void increaseRotation(float dx, float dy, float dz) {
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }
 
    public Model getModel() {
        return model;
    }
 
    public void setModel(Model model) {
        this.model = model;
    }
 
    public Vector3f getPosition() {
        return position;
    }
 
    public void setPosition(Vector3f position) {
        this.position = position;
    }
 
    public float getRotX() {
        return rotX;
    }
 
    public void setRotX(float rotX) {
        this.rotX = rotX;
    }
 
    public float getRotY() {
        return rotY;
    }
 
    public void setRotY(float rotY) {
        this.rotY = rotY;
    }
 
    public float getRotZ() {
        return rotZ;
    }
 
    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }
 
    public float getScale() {
        return scale;
    }
 
    public void setScale(float scale) {
        this.scale = scale;
    }

	@Override
	public Preset getPreset() {
		return new EntityPreset(position, radius, model, rotX, rotY, rotZ, scale, isAnimated, (model.getTexture().getNormalMap() != 0));
	}
 
	public void doAnimation(String animation) {
		if(isAnimated) {
			AnimatedModel animModel = (AnimatedModel) model;
			animModel.doAnimation(AnimationLoader.loadNewAnimation(animation));
		}
	}
	public void doAnimation(String animation, float interpolationTime) {
		if(isAnimated) {
			AnimatedModel animModel = (AnimatedModel) model;
			animModel.doAnimation(AnimationLoader.loadNewAnimation(animation), interpolationTime);
		}
	}
}
