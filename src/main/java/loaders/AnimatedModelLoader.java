package loaders;

import animatedModels.AnimatedModel;
import animatedModels.Joint;
import collada.AnimatedModelData;
import collada.ColladaLoader;
import collada.JointData;
import collada.JointsData;
import models.RawModel;
import statics.Const;
import textures.ModelTexture;

public class AnimatedModelLoader {

	public static AnimatedModel loadAnimatedModel(String fileName, String textureFileName) {
		AnimatedModelData modelData = ColladaLoader.loadColladaModel(Const.RESSOURCES_FOLDER + fileName + ".dae",
				Const.MAX_WEIGHTS);
		RawModel model = Loader.loadToVAO(modelData.getMeshData().getVertices(),
				modelData.getMeshData().getTextureCoords(), modelData.getMeshData().getNormals(),
				modelData.getMeshData().getIndices());
		Loader.loadAnimToVAO(model.getVaoID(), modelData.getMeshData().getJointIds(), modelData.getMeshData().getVertexWeights());
		ModelTexture texture = new ModelTexture(Loader.loadTexture(textureFileName));
		JointsData skeletonData = modelData.getJointsData();
		Joint rootJoint = createJoints(skeletonData.headJoint);
		int jointCount = skeletonData.jointCount;
		return new AnimatedModel(model, texture, rootJoint, jointCount);
	}
	
	public static RawModel loadStaticColladaModel(String fileName) {
		AnimatedModelData modelData = ColladaLoader.loadColladaModel(Const.RESSOURCES_FOLDER + fileName + ".dae",
				Const.MAX_WEIGHTS);
		return Loader.loadToVAO(modelData.getMeshData().getVertices(),
				modelData.getMeshData().getTextureCoords(), modelData.getMeshData().getNormals(),
				modelData.getMeshData().getIndices());
	}

	private static Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return joint;
	}
}
