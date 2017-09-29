package statics;

import java.io.File;

public class Const {

	public static final int SCREEN_WIDTH = 1200;
	public static final int SCREEN_HEIGHT = 660;
	public static final int FPS_CAP = 80;

	public static final String TITLE = new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath()
			+ "/GameEngine/magGame";
	public static final String RESSOURCES_FOLDER = new File(System.getProperty("user.dir")).getParentFile()
			.getAbsolutePath() + "/Client/res/";

	public static final int MAX_JOINTS = 50;
	public static final int MAX_WEIGHTS = 3;

	public static final int MAX_LIGHTS = 4;
	public static final boolean ANTIALIASING = true;
	public static final boolean ANISOTROPIC_FILTERING = true;
	public static final float ANISOTROPIC_FILTERING_AMOUNT = 4f;

	public static final float GRAVITY = -50;
}
