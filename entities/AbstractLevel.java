package entities;

import java.util.ArrayList;
import java.util.Stack;

import entities.Entity.RENDERCODES;

public abstract class AbstractLevel {
	
	protected ArrayList<ArrayList<Entity>> allLayers = new ArrayList<>();

	protected float height;
	protected ArrayList<Entity> layer1 = new ArrayList<>();
	
	protected ArrayList<Entity> layer2 = new ArrayList<>();
	
	protected ArrayList<Entity> layer3 = new ArrayList<>();
	protected ArrayList<Entity> layer4 = new ArrayList<>();
	protected ArrayList<Entity> layer5 = new ArrayList<>();
	
	protected ArrayList<Entity> layer6 = new ArrayList<>();
	protected ArrayList<Entity> layer7 = new ArrayList<>();
	protected ArrayList<Entity> layer8 = new ArrayList<>();
	
	protected ArrayList<Entity> layer9 = new ArrayList<>();
	protected String levelName = "default Name";
	protected float width;
	
	protected AbstractWorld world;
	
	public AbstractLevel(float width, float height, AbstractWorld levelLocation, String name) {
		this.world = levelLocation;
		this.width = width;
		this.height = height;
		this.levelName = name;
		
		getAllLayers().add(layer1);
		getAllLayers().add(layer2);
		getAllLayers().add(layer3);
		getAllLayers().add(layer4);
		getAllLayers().add(layer5);
		getAllLayers().add(layer6);
		getAllLayers().add(layer7);
		getAllLayers().add(layer8);
		getAllLayers().add(layer9);
		
		this.setupLevel();
	
	}
	
	public void createVBOs() {
		for (int i = 0; i < getAllLayers().size(); i++) {
			ArrayList<Entity> Layer = getAllLayers().get(i);
			for (int j = 0; j < Layer.size(); j++) {
				Layer.get(j).createGL();
			}
		}
	}
	
	public ArrayList<ArrayList<Entity>> getAllLayers() {
		return allLayers;
	}
	
	public ArrayList<Entity> getLayer1() {
		return layer1;
	}

	public ArrayList<Entity> getLayer2() {
		return layer2;
	}

	public ArrayList<Entity> getLayer3() {
		return layer3;
	}

	public ArrayList<Entity> getLayer4() {
		return layer4;
	}

	public ArrayList<Entity> getLayer5() {
		return layer5;
	}

	public ArrayList<Entity> getLayer6() {
		return layer6;
	}

	public ArrayList<Entity> getLayer7() {
		return layer7;
	}

	public ArrayList<Entity> getLayer8() {
		return layer8;
	}

	public ArrayList<Entity> getLayer9() {
		return layer9;
	}

	public String getName() {
		return this.levelName;
	}

	public AbstractWorld getWorldspace() {
		return world;
	}

	public abstract void handleInput(Stack<Integer> stack);
	
	public void printAllEntities() {
		for (int i = 0; i < getAllLayers().size(); i++) {
			ArrayList<Entity> Layer = getAllLayers().get(i);
			for (int j = 0; j < Layer.size(); j++) {
				System.out.println(Layer.get(j).toString());
			}
			if (Layer.isEmpty()) {
				System.out.println("Layer" +i+" is empty.");
			}
		}
		
	}
	
	public void render(RENDERCODES graphicsContext) {
		for (int i = 0; i < getAllLayers().size(); i++) {
			ArrayList<Entity> Layer = getAllLayers().get(i);
			for (int j = 0; j < Layer.size(); j++) {
				Layer.get(j).draw(graphicsContext);
			}
		}
	}
	
	protected abstract boolean restoreLevelState(); //TODO: Have the world restore the state of the level

	protected abstract boolean saveLevelState(); //TODO: Have the world save the state of the Level
	public void setName(String name) {
		if (!name.isEmpty()) {
			this.levelName = name;
			return;
		} 
		this.levelName = "default Name";
	}
	protected abstract boolean setupLevel();
	public void update(double delta){
		for (int i = 0; i < getAllLayers().size(); i++) {
			ArrayList<Entity> Layer = getAllLayers().get(i);
			if (Layer.isEmpty()) {
				continue;
			}
			for (int j = 0; j < Layer.size(); j++) {
				Layer.get(j).update(delta);
			}
		}
	}
	
}
