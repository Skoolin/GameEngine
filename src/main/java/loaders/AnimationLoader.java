package loaders;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import animation.Animation;
import animation.JointTransform;
import animation.KeyFrame;
import animation.Quaternion;
import collada.AnimationData;
import collada.ColladaLoader;
import collada.JointTransformData;
import collada.KeyFrameData;

public class AnimationLoader {
	
	private static Map<String, Animation> loadedAnimations = new HashMap<String, Animation>();
	
	public static Animation loadNewAnimation(String fileName) {
		if(loadedAnimations.containsKey(fileName)) {
			return loadedAnimations.get(fileName);
		}
		AnimationData animationData = ColladaLoader.loadColladaAnimation(fileName);
		KeyFrame[] frames = new KeyFrame[animationData.keyFrames.length];
		for (int i = 0; i < frames.length; i++) {
			frames[i] = createKeyFrame(animationData.keyFrames[i]);
		}
		Animation anim = new Animation(animationData.lengthSeconds, frames);
		loadedAnimations.put(fileName, anim);
		return anim;
	}
	
	private static KeyFrame createKeyFrame(KeyFrameData data) {
		Map<String, JointTransform> map = new HashMap<String, JointTransform>();
		for (JointTransformData jointData: data.jointTransforms) {
			JointTransform jointTransform = createJointTransform(jointData);
			map.put(jointData.jointNameId, jointTransform);
		}
		return new KeyFrame(data.time, map);
	}

	private static JointTransform createJointTransform(JointTransformData data) {
		Matrix4f mat = data.jointLocalTransform;
		Vector3f translation = new Vector3f(mat.m30, mat.m31, mat.m32);
		Quaternion rotation = new Quaternion(mat);
		return new JointTransform(translation, rotation);
	}

}
