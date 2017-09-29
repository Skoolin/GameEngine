package animation;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;

import animatedModels.AnimatedModel;
import animatedModels.Joint;
import renderEngine.DisplayManager;

public class Animator {

	private final AnimatedModel animatedModel;

	private float animationTime = 0;
	private Animation currentAnimation;
	private KeyFrame lastAnimKeyFrame = null;
	private float interpolationTime = 0;

	public Animator(AnimatedModel animatedModel) {
		this.animatedModel = animatedModel;
	}

	public void doAnimation(Animation animation) {
		this.currentAnimation = animation;
	}

	public void doAnimation(Animation animation, float interpolationTime) {
		this.interpolationTime = interpolationTime;
		lastAnimKeyFrame = getKeyFrameFromPose(calculateCurrentAnimationPose());
		this.animationTime = -interpolationTime;
		this.currentAnimation = animation;
	}

	private KeyFrame getKeyFrameFromPose(Map<String, Matrix4f> pose) {
		Map<String, JointTransform> animationPose = new HashMap<String, JointTransform>();
		for(Map.Entry<String, Matrix4f> entry: pose.entrySet()) {
			animationPose.put(entry.getKey(), new JointTransform(entry.getValue()));
		}
		return new KeyFrame(interpolationTime, animationPose);
	}

	public void update() {
		if (currentAnimation == null) {
			return;
		}
		increaseAnimationTime();
		Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
		applyPoseToJoints(currentPose, animatedModel.getRootJoint(), new Matrix4f());
	}

	public void increaseAnimationTime() {
		animationTime += DisplayManager.getFrameTime();
		if (animationTime > currentAnimation.getLength()) {
			this.animationTime %= currentAnimation.getLength();
		}
	}

	private Map<String, Matrix4f> calculateCurrentAnimationPose() {
		KeyFrame[] frames;
		float progression;
		if(animationTime < 0.0f) {
			frames = new KeyFrame[] { lastAnimKeyFrame, currentAnimation.getKeyFrames()[0] };
			progression = (animationTime  / interpolationTime) + 1.0f;
		} else {
			frames = getPreviousAndNextFrames();
			progression = calculateProgression(frames[0], frames[1]);
		}
		return interpolatePoses(frames[0], frames[1], progression);
	}

	private void applyPoseToJoints(Map<String, Matrix4f> currentPose, Joint joint, Matrix4f parentTransform) {
		Matrix4f currentLocalTransform = currentPose.get(joint.name);
		Matrix4f currentTransform = Matrix4f.mul(parentTransform, currentLocalTransform, null);
		for (Joint childJoint : joint.children) {
			applyPoseToJoints(currentPose, childJoint, currentTransform);
		}
		Matrix4f.mul(currentTransform, joint.getInverseBindTransform(), currentTransform);
		joint.setAnimationTransform(currentTransform);
	}

	private KeyFrame[] getPreviousAndNextFrames() {
		KeyFrame previousFrame = null;
		KeyFrame nextFrame = null;
		for (KeyFrame frame : currentAnimation.getKeyFrames()) {
			if (frame.getTimeStamp() > animationTime) {
				nextFrame = frame;
				break;
			}
			previousFrame = frame;
		}
		previousFrame = previousFrame == null ? nextFrame : previousFrame;
		nextFrame = nextFrame == null ? previousFrame : nextFrame;
		return new KeyFrame[] { previousFrame, nextFrame };
	}

	private float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame) {
		float timeDifference = nextFrame.getTimeStamp() - previousFrame.getTimeStamp();
		return (animationTime - previousFrame.getTimeStamp()) / timeDifference;
	}

	private Map<String, Matrix4f> interpolatePoses(KeyFrame previousFrame, KeyFrame nextFrame, float progression) {
		Map<String, Matrix4f> currentPose = new HashMap<String, Matrix4f>();
		for (String jointName : previousFrame.getJointKeyFrames().keySet()) {
			JointTransform previousTransform = previousFrame.getJointKeyFrames().get(jointName);
			JointTransform nextTransform = nextFrame.getJointKeyFrames().get(jointName);
			JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform, progression);
			currentPose.put(jointName, currentTransform.getLocalTransform());
		}
		return currentPose;
	}
}
