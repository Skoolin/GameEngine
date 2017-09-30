package shaders;
 
import java.io.File;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import statics.Const;
import toolbox.Maths;
import entities.Camera;
import entities.Light;
 
public class TerrainShader extends ShaderProgram {
     
	private static final int MAX_LIGHTS = 10;
	
    private static final String VERTEX_FILE = Const.SHADER_SOURCES + "/terrainVertexShader.glsl";
    private static final String FRAGMENT_FILE = Const.SHADER_SOURCES + "/terrainFragmentShader.glsl";
    
     
    protected int location_transformationMatrix;
    protected int location_projectionMatrix;
    protected int location_viewMatrix;
    protected int location_lightPosition[];
    protected int location_lightColour[];
    protected int location_shineDamper;
    protected int location_reflectivity;
    private int location_backgroundTexture;
    private int location_rTexture;
    private int location_gTexture;
    private int location_bTexture;
    private int location_blendMap;
 
    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
    
    public TerrainShader(String vertex, String fragment) {
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
        location_lightPosition = new int[MAX_LIGHTS];
        location_lightColour = new int[MAX_LIGHTS];
        for(int i = 0; i < MAX_LIGHTS; i++) {
            location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
        }
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_backgroundTexture = super.getUniformLocation("backgroundTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_blendMap = super.getUniformLocation("blendMap");
    }
    
    public void connectTextureUnits() {
    	super.loadInt(location_backgroundTexture, 0);
    	super.loadInt(location_rTexture, 1);
    	super.loadInt(location_gTexture, 2);
    	super.loadInt(location_bTexture, 3);
    	super.loadInt(location_blendMap, 4);
    }
    
    public void loadShineVariables(float shineDamper, float reflectivity) {
    	super.loadFloat(location_shineDamper, shineDamper);
    	super.loadFloat(location_reflectivity, reflectivity);
    }
     
    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }
    
    public void loadLights(List<Light> lights) {
    	for(int i = 0; i < MAX_LIGHTS; i++) {
    		if(lights.size() > i) {
    	    	super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
    	    	super.loadVector(location_lightColour[i], lights.get(i).getColour());
    		} else {
    	    	super.loadVector(location_lightPosition[i], new Vector3f(0f, 0f, 0f));
    	    	super.loadVector(location_lightColour[i], new Vector3f(0f, 0f, 0f));
    		}
    	}
    }
     
    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }
     
    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(location_projectionMatrix, projection);
    }
 
}
