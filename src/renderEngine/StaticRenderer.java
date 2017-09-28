package renderEngine;
 
import models.RawModel;
import models.TexturedModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import shaders.StaticShader;
import statics.Const;
import textures.ModelTexture;
import toolbox.Maths;
 
import entities.Entity;
import entities.Light;
 
public class StaticRenderer {
     
     
    protected StaticShader shader;
    
    protected List<Light> useLights;
     
    public StaticRenderer(StaticShader shader, Matrix4f projectionMatrix){
    	this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
        useLights = new ArrayList<Light>();
    }
    
    public void render(Map<TexturedModel, List<Entity>> entities, List<Light> lights) {
    	for(TexturedModel model: entities.keySet()) {
    		prepareTexturedModel(model);
    		List<Entity> batch = entities.get(model);
    		for(Entity entity: batch) {
    			prepareInstance(entity, lights);
    	        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
    	        useLights.clear();
    		}
    		unbindTexturedModel();
    	}
    }
    
    private void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        ModelTexture texture = model.getTexture();
        shader.loadNumberOfRows(texture.getNumberOfRows());
        if(texture.isHasTransparency()) {
        	MasterRenderer.disableCulling();
        }
        shader.loadFakeLightningVariable(texture.isUseFakeLightning());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
    }
    
    private void unbindTexturedModel() {
    	MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }
    
    private void prepareInstance(Entity entity, List<Light> lights) {
    	selectClosestLights(lights);
    	shader.loadLights(useLights);
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }
    
    protected void selectClosestLights(List<Light> lights) {
    	List<Light> trueLights = new ArrayList<Light>();
    	for(Light light: lights) {
    		trueLights.add(light);
    	}
    	if(lights.isEmpty()) {
    		return;
    	}
    	useLights.add(trueLights.get(0));
    	trueLights.remove(0);
    	for(int i = 1; i < Const.MAX_LIGHTS; i++) {
    		int index = 0;
    		float minDist = 1000000000;
    		for(Light light: trueLights) {
    			float newDist = light.distanceSqr(trueLights.get(index));
    			if(newDist  < minDist) {
    				minDist = newDist;
    				index = trueLights.indexOf(light);
    			}
    		}
    		if(minDist < 900000000) {
        		useLights.add(trueLights.get(index));
        		trueLights.remove(index);
    		}
    	}
    }
 
}
