package loaders;

import java.io.FileInputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.RawModel;
import statics.Const;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import animatedModels.AnimatedModel;
import animation.Animation;

public class Loader {

	private static List<Integer> vaos = new ArrayList<Integer>();
	public static List<Integer> vbos = new ArrayList<Integer>();
	private static List<Integer> textures = new ArrayList<Integer>();

	private static Map<String, Animation> animations = new HashMap<String, Animation>();
	private static Map<String, RawModel> models = new HashMap<String, RawModel>();
	private static Map<String, RawModel> colladaModels = new HashMap<String, RawModel>();
	private static Map<String, AnimatedModel> animatedModels = new HashMap<String, AnimatedModel>();
	private static Map<String, Integer> textureMap= new HashMap<String, Integer>();
	
	public static Animation loadAnimation(String fileName) {
		if(animations.containsKey(fileName)) {
			return animations.get(fileName);
		} else {
			Animation animation = AnimationLoader.loadNewAnimation(fileName);
			animations.put(fileName, animation);
			return animation;
		}
	}
	
	public static RawModel loadObjModel(String fileName) {
		if(models.containsKey(fileName)) {
			return models.get(fileName);
		} else {
			RawModel model = OBJLoader.loadObjModel(fileName);
			models.put(fileName, model);
			return model;
		}
	}
	
	public static RawModel loadColModel(String fileName) {
		if(colladaModels.containsKey(fileName)) {
			return colladaModels.get(fileName);
		} else {
			RawModel model = AnimatedModelLoader.loadStaticColladaModel(fileName);
			colladaModels.put(fileName, model);
			return model;
		}
	}
	
	public static AnimatedModel loadAnimatedModel(String fileName, String texture) {
		if(animatedModels.containsKey(fileName)) {
			return animatedModels.get(fileName);
		} else {
			AnimatedModel model = AnimatedModelLoader.loadAnimatedModel(fileName, texture);
			animatedModels.put(fileName, model);
			return model;
		}
	}

	public static RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	public static int loadToVAO(float[] positions, float[] textureCoords) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, 2, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		unbindVAO();
		return vaoID;
	}

	public static RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents,
			int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		storeDataInAttributeList(5, 3, tangents);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}

	public static RawModel loadToVAO(float[] positions) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, 2, positions);
		unbindVAO();
		return new RawModel(vaoID, positions.length / 2);
	}

	public static void loadAnimToVAO(int vao, int[] jointsData, float[] weights) {
		int vaoID = vao;
		GL30.glBindVertexArray(vaoID);
		storeIntDataInAttributeList(3, 3, jointsData);
		storeDataInAttributeList(4, 3, weights);
		unbindVAO();
	}
	
	public static int loadTexture(String fileName) {
		if (textureMap.containsKey(fileName)) {
			return textureMap.get(fileName);
		} else {
			int texture = loadNewTexture(fileName);
			textureMap.put(fileName, texture);
			return texture;
		}
	}

	private static int loadNewTexture(String fileName) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream(Const.RESSOURCES_FOLDER + fileName + ".png"));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
			if (Const.ANISOTROPIC_FILTERING) {
				if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
					float amount = Math.min(Const.ANISOTROPIC_FILTERING_AMOUNT,
							GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
					GL11.glTexParameterf(GL11.GL_TEXTURE_2D,
							EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, amount);
					GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
				} else {
					System.out.println("warning: anisotropic filtering not supported...");
					System.out.println("-> using mipmapping instead.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ".png , didn't work");
			System.exit(-1);
		}
		textures.add(texture.getTextureID());
		return texture.getTextureID();
	}

	public static void cleanUp() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (int texture : textures) {
			GL11.glDeleteTextures(texture);
		}
	}

	private static int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}

	public static int createEmptyVbo(int floatCount) {
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}
	
	public static void updateVbo(int vbo, float[] data, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity(), GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public static void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength,
			int offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	private static void storeIntDataInAttributeList(int attributeNumber, int coordinateSize, int[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribIPointer(attributeNumber, coordinateSize, GL11.GL_INT, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private static void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private static void unbindVAO() {
		GL30.glBindVertexArray(0);
	}

	private static void bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	private static IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private static FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

}
