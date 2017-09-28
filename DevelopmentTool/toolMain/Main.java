package toolMain;

import gameLogic.Tool;
import gameObjects.GameStarter;
import renderEngine.Engine;
import renderEngine.Game;

public class Main implements GameStarter {

	public static void main(String[] args) {
		Engine.run(new Main());
	}

	public static void runGame() {
		Engine.run(new Main());
	}

	@Override
	public Game createGame() {
		return new Tool();
	}
}
