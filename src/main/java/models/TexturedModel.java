package models;
 
import textures.ModelTexture;
 
public class TexturedModel extends Model {
	
    public TexturedModel(RawModel model, ModelTexture texture){
        this.rawModel = model;
        this.texture = texture;
    }
    
    public void update() {
    }
    
    public TexturedModel copy() {
		ModelTexture tex = new ModelTexture(texture.getID());
		tex.setHasTransparency(texture.isHasTransparency());
		tex.setNormalMap(texture.getNormalMap());
		tex.setNumberOfRows(texture.getNumberOfRows());
		tex.setReflectivity(texture.getReflectivity());
		tex.setShineDamper(texture.getShineDamper());
		tex.setUseFakeLightning(texture.isUseFakeLightning());
		return new TexturedModel(rawModel, tex);
	}
}
