package renderEngine;
 
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

import statics.Const;
 
public class DisplayManager {
	
	private static long lastFrameTime;
	private static float delta;
     
    public static void createDisplay(){     
        ContextAttribs attribs = new ContextAttribs(3,3)
        .withForwardCompatible(true)
        .withProfileCore(true);
         
        try {
            Display.setDisplayMode(new DisplayMode(Const.SCREEN_WIDTH, Const.SCREEN_HEIGHT));
            PixelFormat format = new PixelFormat();
            if(Const.ANTIALIASING) {
            	format = new PixelFormat().withSamples(4).withDepthBits(24);
            }
            Display.create(format, attribs);
            Display.setTitle(Const.TITLE);
            GL11.glEnable(GL13.GL_MULTISAMPLE);
            Display.setInitialBackground(1, 1, 1);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
         
        GL11.glViewport(0,0, Const.SCREEN_WIDTH, Const.SCREEN_HEIGHT);
        lastFrameTime = getCurrentTime();
    }
     
    public static void updateDisplay(){
         
        Display.sync(Const.FPS_CAP);
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }
    
    public static void closeDisplay(){
        Display.destroy();
    }

	public static float getFrameTime() {
		return delta;
	}
 
	public static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
}
