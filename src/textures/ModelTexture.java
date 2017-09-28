package textures;
 
public class ModelTexture {
     
    private int textureID;
    private int normalMap;
    
    private float shineDamper = 1;
    private float reflectivity = 0;
    
    private boolean useFakeLightning = false;
    private boolean hasTransparency = false;
    
    private int numberOfRows = 1;
     
    public ModelTexture(int texture){
        this.textureID = texture;
        this.normalMap = -1;
    }
     
    public int getID(){
        return textureID;
    }

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public boolean isHasTransparency() {
		return hasTransparency;
	}

	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}

	public boolean isUseFakeLightning() {
		return useFakeLightning;
	}

	public void setUseFakeLightning(boolean useFakeLightning) {
		this.useFakeLightning = useFakeLightning;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public int getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}
 
}
