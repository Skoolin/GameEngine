package renderEngine;
 
import models.RawModel;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import animatedModels.AnimatedModel;
import shaders.AnimatedModelShader;
import textures.ModelTexture;
import toolbox.Maths;
 
import entities.Entity;
import entities.Light;
 
public class AnimatedModelRenderer extends StaticRenderer {
     
    public AnimatedModelRenderer(AnimatedModelShader shader, Matrix4f projectionMatrix){
        super(shader, projectionMatrix);
    }
    
    public void renderAnim(Map<AnimatedModel, List<Entity>> entities, List<Light> lights) {
    	for(AnimatedModel model: entities.keySet()) {
    		prepareAnimatedModel(model);
    		List<Entity> batch = entities.get(model);
    		for(Entity entity: batch) {
    			prepareInstance(entity, lights);
    	        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
    	        useLights.clear();
    		}
    		unbindTexturedModel();
    	}
    }
    
    private void prepareAnimatedModel(AnimatedModel model) {
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        GL20.glEnableVertexAttribArray(4);
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
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);
        GL30.glBindVertexArray(0);
    }
    
    private void prepareInstance(Entity entity, List<Light> lights) {
    	selectClosestLights(lights);
    	shader.loadLights(useLights);
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
      	AnimatedModel anim = (AnimatedModel) entity.getModel();
      	AnimatedModelShader animShader = (AnimatedModelShader) shader;
        animShader.loadJointTransforms(anim.getJointTransforms());
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }
}
