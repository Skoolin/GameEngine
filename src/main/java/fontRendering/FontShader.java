package fontRendering;

import java.io.File;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;
import statics.Const;

public class FontShader extends ShaderProgram{

	private static final String VERTEX_FILE = Const.SHADER_SOURCES + "/fontVertex.glsl";
	private static final String FRAGMENT_FILE = Const.SHADER_SOURCES + "/fontFragment.glsl";
	
	private int location_colour;
	private int location_translation;
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colour = super.getUniformLocation("colour");
		location_translation = super.getUniformLocation("translation");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	protected void loadColour(Vector3f colour) {
		super.loadVector(location_colour, colour);
	}
	
	protected void loadTranslation(Vector2f translation) {
		super.loadVector(location_translation, translation);
	}
}
