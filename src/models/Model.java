package models;

import textures.ModelTexture;

public abstract class Model {
    
   protected RawModel rawModel;
   protected ModelTexture texture;

   public RawModel getRawModel() {
       return rawModel;
   }

   public ModelTexture getTexture() {
       return texture;
   }
   
   public abstract void update();
}
