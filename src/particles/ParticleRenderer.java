package particles;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import loaders.Loader;
import models.RawModel;
import renderEngine.MasterRenderer;
import statics.Const;
import toolbox.Maths;

public class ParticleRenderer {

	private static final float[] VERTICES = { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f };
	private static final int MAX_INSTANCES = 100000;
	private static final int INSTANCE_DATA_LENGTH = 21;

	private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);

	private RawModel quad;
	private ParticleShader shader;
	private StaticPShader staticShader;

	private int vbo;
	private int pointer = 0;
	private List<Light> useLights;

	protected ParticleRenderer(Matrix4f projectionMatrix) {
		this.vbo = Loader.createEmptyVbo(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
		quad = Loader.loadToVAO(VERTICES);
		Loader.addInstancedAttribute(quad.getVaoID(), vbo, 1, 4, INSTANCE_DATA_LENGTH, 0);
		Loader.addInstancedAttribute(quad.getVaoID(), vbo, 2, 4, INSTANCE_DATA_LENGTH, 4);
		Loader.addInstancedAttribute(quad.getVaoID(), vbo, 3, 4, INSTANCE_DATA_LENGTH, 8);
		Loader.addInstancedAttribute(quad.getVaoID(), vbo, 4, 4, INSTANCE_DATA_LENGTH, 12);
		Loader.addInstancedAttribute(quad.getVaoID(), vbo, 5, 4, INSTANCE_DATA_LENGTH, 16);
		Loader.addInstancedAttribute(quad.getVaoID(), vbo, 6, 1, INSTANCE_DATA_LENGTH, 20);
		shader = new ParticleShader();
		staticShader = new StaticPShader();
		staticShader.start();
		staticShader.loadProjectionMatrix(projectionMatrix);
		staticShader.stop();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	protected void render(Map<ParticleSystem, List<Particle>> particles, List<Light> lights, List<ParticleSystem> systems, Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		prepare();
		for (ParticleSystem system : systems) {
			ParticleTexture texture = system.getTexture();
			bindTexture(texture);
			List<Particle> particleList = particles.get(system);
			if (system.getModel() != null) {
				// render as normal Object, with Model
				finishRendering();
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDepthMask(false);
				staticShader.start();
				staticShader.loadViewMatrix(camera);
				GL30.glBindVertexArray(system.getModel().getVaoID());
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				GL20.glEnableVertexAttribArray(2);
		        staticShader.loadNumberOfRows(texture.getNumberOfRows());
	        	MasterRenderer.enableCulling();
				staticShader.loadFakeLightningVariable(true);
				staticShader.loadShineVariables(1f, 0f);
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
				pointer = 0;
				for (Particle particle : particleList) {
					staticShader.loadLights(lights);
					Matrix4f transformationMatrix = Maths.createTransformationMatrix(particle.getPosition(),
							particle.get3dRotation().x, particle.get3dRotation().y, particle.get3dRotation().z,
							particle.getScale());
					staticShader.loadTransformationMatrix(transformationMatrix);
					staticShader.loadOffset(particle.getTexOffset1(), particle.getTexOffset1());
					staticShader.loadBlend(particle.getBlend());
					GL11.glDrawElements(GL11.GL_TRIANGLES, system.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
				}
	        	MasterRenderer.enableCulling();
				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
				GL20.glDisableVertexAttribArray(2);
				GL30.glBindVertexArray(0);
				staticShader.stop();
				prepare();
			} else {
				pointer = 0;
				float[] vboData = new float[particleList.size() * INSTANCE_DATA_LENGTH];
				for (Particle particle : particleList) {
					updateModelViewMatrix(particle.getPosition(), particle.getRotation(), particle.getScale(),
							viewMatrix, vboData);
					updateTexCoordInfo(particle, vboData);
				}
				Loader.updateVbo(vbo, vboData, buffer);
				GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount(), particleList.size());
			}
		}
		finishRendering();
	}

	private void bindTexture(ParticleTexture texture) {
		if (texture.isAdditive()) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		} else {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
		shader.loadNumberOfRows(texture.getNumberOfRows());
	}

	protected void cleanUp() {
		shader.cleanUp();
	}

	private void updateTexCoordInfo(Particle particle, float[] data) {
		data[pointer++] = particle.getTexOffset1().x;
		data[pointer++] = particle.getTexOffset1().y;
		data[pointer++] = particle.getTexOffset2().x;
		data[pointer++] = particle.getTexOffset2().y;
		data[pointer++] = particle.getBlend();
	}

	public void updateModelViewMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix,
			float[] vboData) {
		Matrix4f modelMatrix = new Matrix4f();
		Matrix4f.translate(position, modelMatrix, modelMatrix);
		modelMatrix.m00 = viewMatrix.m00;
		modelMatrix.m01 = viewMatrix.m10;
		modelMatrix.m02 = viewMatrix.m20;
		modelMatrix.m10 = viewMatrix.m01;
		modelMatrix.m11 = viewMatrix.m11;
		modelMatrix.m12 = viewMatrix.m21;
		modelMatrix.m20 = viewMatrix.m02;
		modelMatrix.m21 = viewMatrix.m12;
		modelMatrix.m22 = viewMatrix.m22;
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 0, 1), modelMatrix, modelMatrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), modelMatrix, modelMatrix);
		Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, modelMatrix, null);
		storeMatrixData(modelViewMatrix, vboData);
	}

	private void storeMatrixData(Matrix4f matrix, float[] vboData) {
		vboData[pointer++] = matrix.m00;
		vboData[pointer++] = matrix.m01;
		vboData[pointer++] = matrix.m02;
		vboData[pointer++] = matrix.m03;
		vboData[pointer++] = matrix.m10;
		vboData[pointer++] = matrix.m11;
		vboData[pointer++] = matrix.m12;
		vboData[pointer++] = matrix.m13;
		vboData[pointer++] = matrix.m20;
		vboData[pointer++] = matrix.m21;
		vboData[pointer++] = matrix.m22;
		vboData[pointer++] = matrix.m23;
		vboData[pointer++] = matrix.m30;
		vboData[pointer++] = matrix.m31;
		vboData[pointer++] = matrix.m32;
		vboData[pointer++] = matrix.m33;
	}

	private void prepare() {
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		GL20.glEnableVertexAttribArray(5);
		GL20.glEnableVertexAttribArray(6);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
	}

	private void finishRendering() {
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL20.glDisableVertexAttribArray(5);
		GL20.glDisableVertexAttribArray(6);
		GL30.glBindVertexArray(0);
		shader.stop();
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
