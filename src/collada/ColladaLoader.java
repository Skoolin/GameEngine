package collada;

import statics.Const;

public class ColladaLoader {
	
	public static AnimatedModelData loadColladaModel(String fileAdress, int maxWeights) {
		XmlNode node = XmlParser.loadXmlFile(fileAdress);
		
		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
		SkinningData skinningData = skinLoader.extractSkinData();
		
		JointsLoader jointsLoader = new JointsLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
		JointsData jointsData = jointsLoader.extractBoneData();
		
		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		MeshData meshData = g.extractModelData();
		
		return new AnimatedModelData(meshData, jointsData);
	}

	public static AnimationData loadColladaAnimation(String fileName) {
		XmlNode node = XmlParser.loadXmlFile(Const.RESSOURCES_FOLDER + fileName + ".dae");
		XmlNode animNode = node.getChild("library_animations");
		XmlNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}
}
