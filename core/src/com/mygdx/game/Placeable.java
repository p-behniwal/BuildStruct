//Items that the player can place on the object layer after purchasing them for unique effects based on their upgrade path
package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Placeable extends Sprite{
	private int health; //How much damage the item can sustain before breaking
	private boolean isDestroyed; //flag for whether or not this item is destroyed
	private boolean isVisible; //flag for whether or not this item is visible to players
	private Player owner; //The player that placed this item
	private int attackRadius; //How far this item's attack range is (Applicable for totems and alchemy items)
	public int attack; //How much damage this item does upon activation (Not applicable for walls)
	private String type; //The type of item that this is based on player upgrades
	private static final float COOLDOWN = 1.5f;
	private float lastAttack;
	
	public Placeable(Player p, float x, float y, Field map, Sprite sprite) {
		//Constructs and places the placeable item
		super(sprite);
		setPosition(x, y);
		setSize(map.objects.getTileWidth(), map.objects.getTileHeight());
		isDestroyed = false;
		owner = p;
		determineType();
		determineProperties(map);
		lastAttack = 0;
	}
	
	public void takeDamage(int damage, Field map) {
		//Lowers health by a certain amount
		health -= damage;
		if(health <= 0) { //Handles the scenario where the damage taken is sufficient to destroy this item
			isDestroyed = true;
			map.setAir(tilePos(map)[0], tilePos(map)[1]); //Sets the tile on which this once was to air since it is now destroyed
		}
	}
	
	public void dealEffect(Player p, Field map, float totalTime) {
		//Deals Area of Effect (AoE) effects to a nearby player (only applicable for totems and Alchemy items)
		boolean isNear = false; //Determines whether the player is within this item's attack range
		float dx = (getX() + map.objects.getTileWidth() / 2) - p.getX();
		float dy = (getY() + map.objects.getTileHeight() / 2) - p.getY();
		if(dy * dy + dx * dx < attackRadius * attackRadius) {
			isNear = true;
		}
		if(isNear) {
			lastAttack = totalTime;
			if(owner.upgrades.contains("Natural Magic") && lastAttack + COOLDOWN <= totalTime) {
				p.takeDamage(attack, 0); //Totems deal damage to players
			} else if(owner.upgrades.contains("Alchemy")) {
				p.beaconEffect();
			}
		}
	}
	
	private void determineType() {
		//Determines the type of item this is based on the player's upgrades
		if(owner.upgrades.contains("Natural Magic")) {
			type = "Natural Magic";
			
		} else if(owner.upgrades.contains("Dark Arts")) {
			type = "Dark Arts";
			
		} else if(owner.upgrades.contains("Alchemy")) {
			type = "Alchemy";
			
		} else if(owner.upgrades.contains("Military")) {
			type = "Military";
			
		}
	}
	
	private void determineProperties(Field map){
		//Determines certain properties of the item such as damage, range, and visibility
		if((map.ground.getCell(tilePos(map)[0], tilePos(map)[1]).getTile().getId() == Field.FOREST) || type.equals("Dark Arts")) {
			isVisible = false;
		} else {
			isVisible = true;
		}
		if(type.equals("Natural Magic")) {
			attackRadius = 300;
			attack = 5;
			health = 30;
		} else if(type.equals("Alchemy")) {
			attackRadius = 130;
			attack = 5;
			health = 30;
		} else if(type.equals("Dark Arts")) {
			health = 15;
			attack = owner.getAttackPower() + owner.getAttackMod();
			attackRadius = 0;
		} else if(type.equals("Military")) {
			health = 75;
			attack = 0;
			attackRadius = 0;
		}
	}
	
	public int[] tilePos(Field map) {
    	//Returns the tile that the center of the placeable is on 
    	int[] tilePos = new int[2];
    	tilePos[0] = (int) ((getX() + getWidth() / 2) / map.ground.getTileWidth());
    	tilePos[1] = (int) ((getY() + getHeight() / 2) / map.ground.getTileHeight());
    	return tilePos;
    }
	
	public String getType() {
		return type;
	}
	
	public int getHP() {
		return health;
	}
	
	public void setHP(int hp) {
		health = hp;
	}

	public boolean isDestroyed() {
		return isDestroyed;
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public void setVisibility(boolean bool) {
		isVisible = bool;
	}
}
