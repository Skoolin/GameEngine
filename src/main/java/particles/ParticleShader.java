package particles;

import java.io.File;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;
import statics.Const;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = Const.SHADER_SOURCES + "particleVShader.glsl";
	private static final String FRAGMENT_FILE = Const.SHADER_SOURCES + "particleFShader.glsl";

	private int location_numberOfRows;
	private int location_projectionMatrix;

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "texOffsets");
		super.bindAttribute(6, "blendFactor");
	}
	
	protected void loadNumberOfRows(float numberOfRows) {
		super.loadFloat(location_numberOfRows, numberOfRows);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

}
