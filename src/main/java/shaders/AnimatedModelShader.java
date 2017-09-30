package shaders;

import java.io.File;

import org.lwjgl.util.vector.Matrix4f;

import statics.Const;

public class AnimatedModelShader extends StaticShader {
     
    private static final String VERTEX_FILE = Const.SHADER_SOURCES + "/animatedVertexShader.glsl";
    private static final String FRAGMENT_FILE = Const.SHADER_SOURCES + "/fragmentShader.glsl";
    
    protected int[] location_jointTransforms;
    
    public AnimatedModelShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "jointIndices");
        super.bindAttribute(4, "weights");
    }
 
    @Override
    protected void getAllUniformLocations() {
    	location_jointTransforms = new int[Const.MAX_JOINTS];
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_lightPosition = new int[MAX_LIGHTS];
        location_lightColour = new int[MAX_LIGHTS];
        location_attenuation = new int[MAX_LIGHTS];
        for(int i = 0; i < MAX_LIGHTS; i++) {
        	location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
        	location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
        	location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_useFakeLightning = super.getUniformLocation("useFakeLightning");
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");
        for(int i = 0; i < Const.MAX_JOINTS; i++) {
        	location_jointTransforms[i] = super.getUniformLocation("jointTransforms[" + i + "]");
        }
    }
    
    public void loadJointTransforms(Matrix4f[] transformations){
        super.loadMatrixArray(location_jointTransforms, transformations);
    }
}
