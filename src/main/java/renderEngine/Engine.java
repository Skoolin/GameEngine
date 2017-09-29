package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import animatedModels.AnimatedModel;
import audio.AudioMaster;
import audio.Source;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Movable;
import entities.Player;
import entities.PlayerCamera;
import fontRendering.TextMaster;
import gameObjects.GameObject;
import gameObjects.GameStarter;
import guis.GuiRenderer;
import guis.GuiTexture;
import loaders.AnimatedModelLoader;
import loaders.Loader;
import models.Model;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;

public class Engine {

	private static Map<String, TexturedModel> loadedTexturedModels = new HashMap<String, TexturedModel>();

	private static List<GameObject> allObjects = new ArrayList<>();

	public static Game manager;
	private static GuiRenderer guiRenderer;
	private static List<Light> lights = new ArrayList<Light>();
	private static List<Terrain> terrains = new ArrayList<Terrain>();
	private static List<Entity> staticEntities = new ArrayList<Entity>();
	private static List<Entity> animatedEntities = new ArrayList<Entity>();
	private static List<Entity> normalMappedEntities = new ArrayList<Entity>();
	private static List<ParticleSystem> particleSystems = new ArrayList<ParticleSystem>();
	private static List<GuiTexture> guis = new ArrayList<GuiTexture>();
	private static List<Source> audioSources = new ArrayList<Source>();
	private static Camera camera;
	private static Player player;
	private static MasterRenderer renderer;
	private static boolean hasPlayer = false;
	private static boolean hasPlayerCam = false;
	private static int playerType = 0;

	private static MousePicker picker;

	// readin constants for user:
	public static final int TYPE_STATIC = 0;
	public static final int TYPE_ANIMATED = 1;
	private static GameStarter gameStarter;

	private static boolean gameEnded = false;

	// TODO change to <String?> of what Project to load
	public static void run(GameStarter starter) {
		gameStarter = starter;
		setup();

		loadGame();

		while (!Display.isCloseRequested() && !gameEnded) {


			for (ParticleSystem system : particleSystems) {
				system.generateParticles();
			}
			
			updateAllObjects();

			render();
		}

		exit();
	}

	private static void setup() {
		Mouse.setGrabbed(true);
		DisplayManager.createDisplay();
		camera = new Camera();
		guiRenderer = new GuiRenderer();
		renderer = new MasterRenderer();
		picker = new MousePicker(camera, renderer.getProjectionMatrix());
		TextMaster.init();
		AudioMaster.init();
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
		ParticleMaster.init(renderer.getProjectionMatrix());
		manager = gameStarter.createGame();
		camera.move();
		picker.update();
	}

	private static void updateAllObjects() {
		List<GameObject> currentList = new ArrayList<>();
		for (GameObject object : allObjects) {
			currentList.add(object);
		}
		Iterator<GameObject> it = currentList.iterator();
		while (it.hasNext()) {
			GameObject o = it.next();
			if (allObjects.contains(o)) {
				o.update();
			}
		}
	}

	private static void render() {
		float delta = DisplayManager.getFrameTime();
		manager.update();
		camera.move();
		picker.update();
		ParticleMaster.update(camera);
		if (hasPlayer) {
			player.move(delta);
			player.update();
			switch (playerType) {
			case 0:
				renderer.processStaticEntity(player);
				break;
			case 1:
				renderer.processAnimatedEntity(player);
				break;
			}
		}
		for (Terrain terrain : terrains) {
			renderer.processTerrain(terrain);
		}
		for (Entity entity : staticEntities) {
			entity.move(delta);
			renderer.processStaticEntity(entity);
		}
		for (Entity entity : animatedEntities) {
			entity.update();
			entity.move(delta);
			renderer.processAnimatedEntity(entity);
		}
		for (Entity entity : normalMappedEntities) {
			entity.move(delta);
			renderer.processNormalMapEntity(entity);
		}
		for (Source source : audioSources) {
			source.move(delta);
		}
		renderer.render(lights, camera);
		for (ParticleSystem system : particleSystems) {
			system.move(delta);
		}
		ParticleMaster.renderParticles(camera, lights);
		guiRenderer.render(guis);
		DisplayManager.updateDisplay();
	}

	private static void loadGame() {
		// TODO load project file, then load specified starting scene.
	}

	private static void exit() {

		manager.exit();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		Loader.cleanUp();
		ParticleMaster.cleanUp();
		AudioMaster.cleanUp();
		DisplayManager.closeDisplay();
	}

	public static void addTerrain(int xTile, int yTile, String blendMap, String backgroundTexture, String rTexture,
			String gTexture, String bTexture) {
		TerrainTexture bgt = new TerrainTexture(Loader.loadTexture(backgroundTexture));
		TerrainTexture rt = new TerrainTexture(Loader.loadTexture(rTexture));
		TerrainTexture gt = new TerrainTexture(Loader.loadTexture(gTexture));
		TerrainTexture bt = new TerrainTexture(Loader.loadTexture(bTexture));
		TerrainTexture map = new TerrainTexture(Loader.loadTexture(blendMap));

		TerrainTexturePack texturePack = new TerrainTexturePack(bgt, rt, gt, bt);

		terrains.add(new Terrain(xTile, yTile, texturePack, map));
	}

	public static void clearTerrain() {
		terrains.clear();
	}

	public static Entity addStaticEntity(Vector3f position, Vector3f rotation, float scale, String objFile,
			String textureFile) {
		RawModel model = Loader.loadObjModel(objFile);
		if (loadedTexturedModels.containsKey(objFile + ".+.+." + textureFile)) {
			Entity entity = new Entity(false, loadedTexturedModels.get(objFile + ".+.+." + textureFile), position,
					rotation.x, rotation.y, rotation.z, scale);
			staticEntities.add(entity);
			return entity;
		}
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(Loader.loadTexture(textureFile)));
		Entity entity = new Entity(false, staticModel, position, rotation.x, rotation.y, rotation.z, scale);
		staticEntities.add(entity);
		loadedTexturedModels.put(objFile + ".+.+." + textureFile, staticModel);
		return entity;
	}

	public static Entity addStaticEntity(float xPos, float yPos, float zPos, float xRot, float yRot, float zRot,
			float scale, String objFile, String textureFile, float shineDamper, float reflectivity) {
		RawModel model = Loader.loadObjModel(objFile);
		if (loadedTexturedModels.containsKey(objFile + ".+.+." + textureFile)) {
			Entity entity = new Entity(false, loadedTexturedModels.get(objFile + ".+.+." + textureFile),
					new Vector3f(xPos, yPos, zPos), xRot, yRot, zRot, scale);
			staticEntities.add(entity);
			entity.getModel().getTexture().setShineDamper(shineDamper);
			entity.getModel().getTexture().setReflectivity(reflectivity);
			return entity;
		}
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(Loader.loadTexture(textureFile)));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(shineDamper);
		texture.setReflectivity(reflectivity);
		Entity entity = new Entity(false, staticModel, new Vector3f(xPos, yPos, zPos), xRot, yRot, zRot, scale);
		staticEntities.add(entity);
		loadedTexturedModels.put(objFile + ".+.+." + textureFile, staticModel);
		return entity;
	}

	public static void setPlayerType(int type) {
		playerType = type;
	}

	public static void removeEntity(Entity entity) {
		if (entity.getAnimated()) {
			animatedEntities.remove(entity);
		} else if (entity.getModel().getTexture().getNormalMap() > -1) {
			normalMappedEntities.remove(entity);
		} else {
			staticEntities.remove(entity);
		}
	}

	public static Light addLight(float xPos, float yPos, float zPos, Vector3f colour, Vector3f attenuation) {
		Light light = new Light(new Vector3f(xPos, yPos, zPos), colour, attenuation);
		lights.add(light);
		return light;
	}

	public static void removeLight(Light light) {
		lights.remove(light);
	}

	public static Entity addAnimatedEntity(float xPos, float yPos, float zPos, float xRot, float yRot, float zRot,
			float scale, String colladaFile, String textureFile, String animationFile, float shineDamper,
			float reflectivity) {
		AnimatedModel guyModel = AnimatedModelLoader.loadAnimatedModel(colladaFile, textureFile);
		guyModel.getTexture().setShineDamper(shineDamper);
		guyModel.getTexture().setReflectivity(reflectivity);
		guyModel.doAnimation(Loader.loadAnimation(animationFile));
		Entity entity = new Entity(true, guyModel, new Vector3f(xPos, yPos, zPos), xRot, yRot, zRot, scale);
		animatedEntities.add(entity);
		return entity;
	}

	public static Player addPlayer(float xPos, float yPos, float zPos, float xRot, float yRot, float zRot, float scale,
			String meshFile, String textureFile, String animationFile, float shineDamper, float reflectivity) {

		switch (playerType) {
		case 0:
			RawModel model = Loader.loadObjModel(meshFile);
			if (loadedTexturedModels.containsKey(meshFile + ".+.+." + textureFile)) {
				Player entity = new Player(false, loadedTexturedModels.get(meshFile + ".+.+." + textureFile),
						new Vector3f(xPos, yPos, zPos), xRot, yRot, zRot, scale);
				entity.getModel().getTexture().setShineDamper(shineDamper);
				entity.getModel().getTexture().setReflectivity(reflectivity);
				return entity;
			}
			TexturedModel staticModel = new TexturedModel(model, new ModelTexture(Loader.loadTexture(textureFile)));
			ModelTexture texture = staticModel.getTexture();
			texture.setShineDamper(shineDamper);
			texture.setReflectivity(reflectivity);
			loadedTexturedModels.put(meshFile + ".+.+." + textureFile, staticModel);
			player = new Player(false, staticModel, new Vector3f(xPos, yPos, zPos), xRot, yRot, zRot, scale);
			return player;
		case 1:
			AnimatedModel guyModel = AnimatedModelLoader.loadAnimatedModel(meshFile, textureFile);
			guyModel.getTexture().setShineDamper(shineDamper);
			guyModel.getTexture().setReflectivity(reflectivity);
			guyModel.doAnimation(Loader.loadAnimation(animationFile));
			player = new Player(true, guyModel, new Vector3f(xPos, yPos, zPos), xRot, yRot, zRot, scale);
			hasPlayer = true;
			return player;
		}
		return null;

	}

	public static void removePlayer() {
		player = null;
		hasPlayer = false;
		if (hasPlayerCam) {
			camera = new Camera();
			picker = new MousePicker(camera, renderer.getProjectionMatrix());
			hasPlayerCam = false;
		}
	}

	public static Camera setPlayerCamera(Movable player) {
		camera = new PlayerCamera(player);
		picker = new MousePicker(camera, renderer.getProjectionMatrix());
		hasPlayerCam = true;
		return camera;
	}

	public static Camera setStaticCamera() {
		camera = new Camera();
		picker = new MousePicker(camera, renderer.getProjectionMatrix());
		hasPlayerCam = false;
		return camera;
	}

	public static GuiTexture addGui(String textureFile, float xPos, float zPos, float width, float height) {
		GuiTexture gui = new GuiTexture(Loader.loadTexture(textureFile), new Vector2f(xPos, zPos),
				new Vector2f(width, height));
		guis.add(gui);
		return gui;
	}

	public static int loadTexure(String textureFile) {
		return Loader.loadTexture(textureFile);
	}

	public static void removeGui(GuiTexture gui) {
		guis.remove(gui);
	}

	public static Entity addNormalMapEntity(float xPos, float yPos, float zPos, float xRot, float yRot, float zRot,
			float scale, String objFile, String textureFile, String normalFile, float shineDamper, float reflectivity) {
		if (loadedTexturedModels.containsKey(objFile + ".+.+." + textureFile)) {
			Entity entity = new Entity(false, loadedTexturedModels.get(objFile + ".+.+." + textureFile),
					new Vector3f(xPos, yPos, zPos), xRot, yRot, zRot, scale);
			normalMappedEntities.add(entity);
			entity.getModel().getTexture().setShineDamper(shineDamper);
			entity.getModel().getTexture().setReflectivity(reflectivity);
			entity.getModel().getTexture().setNormalMap(Loader.loadTexture(normalFile));
			return entity;
		}
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ(objFile),
				new ModelTexture(Loader.loadTexture(textureFile)));
		barrelModel.getTexture().setShineDamper(shineDamper);
		barrelModel.getTexture().setReflectivity(reflectivity);
		barrelModel.getTexture().setNormalMap(Loader.loadTexture(normalFile));
		Entity entity = new Entity(false, barrelModel, new Vector3f(xPos, yPos, zPos), xRot, yRot, zRot, scale);
		normalMappedEntities.add(entity);
		loadedTexturedModels.put(objFile + ".+.+." + textureFile, barrelModel);
		return entity;
	}

	public static ParticleSystem addParticleSystem(String textureFile, int numberOfRows, float xPos, float yPos,
			float zPos, float particlesPerSecond, float speed, float gravityCompliant, float lifeLength, float scale,
			float lifeError, float speedError, float scaleError, boolean randomizeRotation, float startingSpread) {
		ParticleTexture particleTexture = new ParticleTexture(Loader.loadTexture(textureFile), numberOfRows);
		ParticleSystem zergRush = new ParticleSystem(particleTexture, new Vector3f(xPos, yPos, zPos),
				particlesPerSecond, speed, gravityCompliant, lifeLength, scale);
		zergRush.setLifeError(lifeError);
		zergRush.setSpeedError(speedError);
		zergRush.setScaleError(scaleError);
		if (randomizeRotation) {
			zergRush.randomizeRotation();
		}
		zergRush.setStartingSpread(startingSpread);
		particleSystems.add(zergRush);
		return zergRush;
	}

	public static void removeParticleSystem(ParticleSystem system) {
		particleSystems.remove(system);
	}

	public static void setBackgroundColour(float red, float green, float blue) {
		renderer.red = red;
		renderer.green = green;
		renderer.blue = blue;
	}

	public static Movable addAnimatedEntity(float x, float y, float z, float rotX, float rotY, float rotZ, float scale,
			Model model) {
		AnimatedModel oldModel = (AnimatedModel) model;
		AnimatedModel newModel = oldModel.copy();
		Entity entity = new Entity(true, newModel, new Vector3f(x, y, z), rotX, rotY, rotZ, scale);
		animatedEntities.add(entity);
		return entity;
	}

	public static Movable addNormalMapEntity(float x, float y, float z, float rotX, float rotY, float rotZ, float scale,
			Model model) {
		TexturedModel newModel = (TexturedModel) model;
		newModel = newModel.copy();
		Entity entity = new Entity(false, newModel, new Vector3f(x, y, z), rotX, rotY, rotZ, scale);
		normalMappedEntities.add(entity);
		return entity;
	}

	public static Movable addStaticEntity(float x, float y, float z, float rotX, float rotY, float rotZ, float scale,
			Model model) {
		TexturedModel newModel = (TexturedModel) model;
		newModel = newModel.copy();
		Entity entity = new Entity(false, newModel, new Vector3f(x, y, z), rotX, rotY, rotZ, scale);
		normalMappedEntities.add(entity);
		return entity;
	}

	public static Movable addParticleSystem(ParticleSystem system) {
		particleSystems.add(system);
		return system;
	}

	public static Source addAudioSource(Vector3f position, int rolloff, int referenceDist, int maxDist, boolean looping,
			float pitch, float volume) {
		Source source = new Source(position, rolloff, referenceDist, maxDist);
		source.setLooping(looping);
		source.setPitch(pitch);
		source.setVolume(volume);
		audioSources.add(source);
		return source;
	}

	public static void removeAudioSource(Source source) {
		audioSources.remove(source);
		source.delete();
	}

	public static void destroy(GameObject object) {
		allObjects.remove(object);
	}

	public static GameObject create(GameObject object) {
		allObjects.add(object);
		return object;
	}

	public static boolean containsObject(GameObject target) {
		return allObjects.contains(target);
	}

	public static List<GameObject> detectCollisions(GameObject collider, Class className, float maxDist) {
		List<GameObject> result = new ArrayList<>();
		for (GameObject object : allObjects) {
			if (object != collider && object.getClass().isAssignableFrom(className) && Vector3f
					.sub(collider.movable.getPosition(), object.movable.getPosition(), null).length() < maxDist) {
				result.add(object);
			}
		}
		return result;
	}

	public static List<GameObject> detectCollisions(Vector3f collider, Class className, float maxDist) {
		List<GameObject> result = new ArrayList<>();
		for (GameObject object : allObjects) {
			if (className.isAssignableFrom(object.getClass()) && Vector3f
					.sub(collider, object.movable.getPosition(), null).length() < maxDist) {
				result.add(object);
			}
		}
		return result;
	}

	public static GameObject detectCollision(GameObject collider, Class className, float maxDist) {
		for (GameObject object : allObjects) {
			if (object != collider && className.isAssignableFrom(object.getClass()) && Vector3f
					.sub(collider.movable.getPosition(), object.movable.getPosition(), null).length() < maxDist) {
				return object;
			}
		}
		return null;
	}

	public static GameObject detectCollision(Vector3f collider, Class className, float maxDist) {
		for (GameObject object : allObjects) {
			if (className.isAssignableFrom(object.getClass())
					&& Vector3f.sub(collider, object.movable.getPosition(), null).length() < maxDist) {
				return object;
			}
		}
		return null;
	}

	public static Vector3f getMouseRay() {
		return picker.getCurrentRay();
	}

	public static Vector3f getMouseAtHeight(float height) {
		Vector3f pickerVec = picker.getCurrentRay();
		Vector3f camPos = camera.getPosition();

		float t = (0f - camPos.y + height) / pickerVec.y;
		return new Vector3f(camPos.x + t * pickerVec.x, camPos.y + t * pickerVec.y, camPos.z + t * pickerVec.z);
	}

	public static void end() {
		gameEnded = true;
	}

	public static List<GameObject> getObjectsOfType(Class objectClass) {
		List<GameObject> result = new ArrayList<>();
		for (GameObject object : allObjects) {
			if (objectClass.isAssignableFrom(object.getClass())) {
				result.add(object);
			}
		}
		return result;
	}
}
