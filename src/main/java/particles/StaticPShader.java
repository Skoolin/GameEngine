package particles;

import java.io.File;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import shaders.ShaderProgram;
import statics.Const;
import toolbox.Maths;

public class StaticPShader extends ShaderProgram {

	protected static final int MAX_LIGHTS = 4;

	private static final String VERTEX_FILE = Const.SHADER_SOURCES + "pVertexShader.glsl";
	private static final String FRAGMENT_FILE = Const.SHADER_SOURCES + "pFragmentShader.glsl";

	protected int location_transformationMatrix;
	protected int location_projectionMatrix;
	protected int location_viewMatrix;
	protected int location_lightPosition[];
	protected int location_lightColour[];
	protected int location_attenuation[];
	protected int location_shineDamper;
	protected int location_reflectivity;
	protected int location_useFakeLightning;
	protected int location_numberOfRows;
	protected int location_offset_1;
	protected int location_offset_2;
	protected int location_blend;

	public StaticPShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	public StaticPShader(String vertex, String fragment) {
		super(vertex, fragment);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLightning = super.getUniformLocation("useFakeLightning");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset_1 = super.getUniformLocation("offset_1");
		location_offset_2 = super.getUniformLocation("offset_2");
		location_blend = super.getUniformLocation("blend");
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColour = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		for (int i = 0; i < MAX_LIGHTS; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
	}

	public void loadNumberOfRows(int numberOfRows) {
		super.loadFloat(location_numberOfRows, numberOfRows);
	}

	public void loadOffset(Vector2f first,Vector2f second) {
		super.loadVector(location_offset_1, first);
		super.loadVector(location_offset_2, second);
	}
	
	public void loadBlend(float blend) {
		super.loadFloat(location_blend, blend);
	}

	public void loadFakeLightningVariable(boolean useFake) {
		super.loadBoolean(location_useFakeLightning, useFake);
	}

	public void loadShineVariables(float shineDamper, float reflectivity) {
		super.loadFloat(location_shineDamper, shineDamper);
		super.loadFloat(location_reflectivity, reflectivity);
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadLights(List<Light> lights) {
		for (int i = 0; i < MAX_LIGHTS; i++) {
			if (i < lights.size()) {
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColour[i], lights.get(i).getColour());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			} else {
				super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColour[i], new Vector3f(0, 0, 0));
				super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}

	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}

}
