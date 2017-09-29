package animatedModels;

import org.lwjgl.util.vector.Matrix4f;

import animation.Animation;
import animation.Animator;
import models.Model;
import models.RawModel;
import textures.ModelTexture;

public class AnimatedModel extends Model {

	private final Joint rootJoint;
	private final int jointCount;
	
	private final Animator animator;
	
    public AnimatedModel(RawModel model, ModelTexture texture, Joint rootJoint, int jointCount){
        this.rawModel = model;
        this.texture = texture;
        this.rootJoint = rootJoint;
        this.jointCount = jointCount;
        this.animator = new Animator(this);
        rootJoint.calcInverseBindTransform(new Matrix4f());
    }
    
    public Joint getRootJoint() {
    	return rootJoint;
    }
    
    public void doAnimation(Animation animation) {
    	animator.doAnimation(animation);
    }
    
    public Matrix4f[] getJointTransforms() {
    	Matrix4f[] jointMatrices = new Matrix4f[jointCount];
    	addJointsToArray(rootJoint, jointMatrices);
    	return jointMatrices;
    }
    
    private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
    	jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
    	for(Joint childJoint: headJoint.children) {
    		addJointsToArray(childJoint, jointMatrices);
    	}
    }
    
    public void update() {
    	animator.update();
    }

	public void doAnimation(Animation animation, float interpolationTime) {
		animator.doAnimation(animation, interpolationTime);
	}

	public AnimatedModel copy() {
		ModelTexture tex = new ModelTexture(texture.getID());
		tex.setHasTransparency(texture.isHasTransparency());
		tex.setNormalMap(texture.getNormalMap());
		tex.setNumberOfRows(texture.getNumberOfRows());
		tex.setReflectivity(texture.getReflectivity());
		tex.setShineDamper(texture.getShineDamper());
		tex.setUseFakeLightning(texture.isUseFakeLightning());
		return new AnimatedModel(rawModel, tex, rootJoint, jointCount);
	}
}
