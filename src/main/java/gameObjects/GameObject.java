package gameObjects;

import entities.Movable;
import renderEngine.Engine;

public abstract class GameObject {
	public Movable movable;
	public int id;
	
	private static int nextId;
	
	protected GameObject(Movable movable) {
		id = nextId++;
		this.movable = movable;
		Engine.create(this);
	}

	protected GameObject() {
		id = nextId++;
		Engine.create(this);
	}
	
	public boolean equals(GameObject object) {
		return id == object.id;
	}
	
	public abstract void update();
	
	public void destroy() {
		exit();
		Engine.destroy(this);
	}

	public abstract void exit();
}
