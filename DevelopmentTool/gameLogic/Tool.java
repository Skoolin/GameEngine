package gameLogic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import renderEngine.Game;

public class Tool implements Game{
	
    public Tool() {
    	//hier setup sachen machen einmal :)
    	
    	loadProject();
    }
    
    private void loadProject() {
    	try(BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    	    StringBuilder sb = new StringBuilder();
    	    String line = br.readLine();

    	    while (line != null) {
    	        sb.append(line);
    	        sb.append(System.lineSeparator());
    	        line = br.readLine();
    	        
    	        //TODO load that in ram
    	    }
    	} catch (FileNotFoundException e) {
			System.err.println("No Project found. Creating new Project");
			
			//TODO create new default Scene, load that up.
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	public void update() {
    	//hier sachen machen jeden Frame
	}
	
	public void exit() {
		//hier Sachen machen zum Schluss
		save();
	}
	
	private void save() {
		//TODO save everything
	}
}