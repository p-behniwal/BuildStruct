//This class handles enemy creation, movement, and collisions
package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Sprite{
	private int health; //The amount of health an enemy has
	private int attackPower; //Determines how much damage an enemy deals to a player
	private int level; //The level f difficulty of this enemy, scales over time
	private Vector2 velocity; //The random direction the enemy goes in for a period of time
	//private float lastTimeChange; //The time in seconds that the enemy performed it's last change in direction
	
	private static final int DEFMOVESPEED = 10; //The default movement speed of an enemy
	private static final int RADIUS = 120; //The radius of proximity of the enemy to other players
	
	public Enemy(int time, float x, float y) {
		//Creates the enemy sprite and sets it's position and level
		super(new Sprite(new Texture("Enemy.png")));
		level = Math.min((time / 35) + 1, 13);
		health = 5 + level * level / 2;
		attackPower = 1 + level * 4;
		setSize(16, 16);
		setPosition(x, y);
		velocity = new Vector2();
	}
	
	/*public Vector2 moveDir(float totalTime) {
		//Determines the temporary randomized movement direction for this enemy over a certain time period
		if(lastTimeChange + 0.5 < totalTime || moveDir == null) {
			moveDir = new Vector2(randint(-2, 1), randint(-2, 1));
			lastTimeChange = totalTime;
		}
		return moveDir;
	}*/
	
	public void move(float delta, Field map) {
		//Moves the enemy in the appropriate direction in the same way players are moved
		float oldX = getX(), oldY = getY(), tileWidth = map.ground.getTileWidth(), tileHeight = map.ground.getTileHeight();
        boolean collisionX = false, collisionY = false; //flags for determining collisions
		if(getX() + getWidth() <= map.ground.getWidth() * map.ground.getTileWidth() && getX() >= 0) {
        	//Moves the enemy horizontally provided they are not at the edges of the map
        	translateX(velocity.x * delta * DEFMOVESPEED);
        }
        if(getX() < 0 || getX() + getWidth() > map.ground.getWidth() * map.ground.getTileWidth()) { 
        	//Applies collisions at edges of the screen
        	collisionX = true;
        }
        if(collisionX) {
            setX(oldX);
        } 
        if(getY() + getHeight() <= map.ground.getHeight() * map.ground.getTileHeight() && getY() >= 0) {
        	//Moves the enemy vertically provided they are not at the edges of the map
        	translateY(velocity.y * delta * DEFMOVESPEED);
        }
        if(getY() < 0 || getY() + getHeight() > map.ground.getHeight() * map.ground.getTileHeight()) {
        	//Applies collisions at edges of the screen
        	collisionY = true;
        }
        if(collisionY) {
            setY(oldY);
        }
        
        for(MapLayer l : map.layers) {
        	//Checking and reacting to collisions across all map layers
        	TiledMapTileLayer layer = (TiledMapTileLayer) l;
        	if(velocity.x < 0) { // going left
                // top left
                if(!collisionX) {
                	collisionX = (Boolean) layer.getCell((int) ((getX()) / tileWidth), (int) ((getY() + getHeight() - 1) / tileHeight)).getTile().getProperties().get("blocked");
                }

                // middle left
                if(!collisionX)
                    collisionX = (Boolean) layer.getCell((int) ((getX()) / tileWidth), (int) ((getY() + getHeight() / 2) / tileHeight)).getTile().getProperties().get("blocked");

                // bottom left
                if(!collisionX)
                    collisionX = (Boolean) layer.getCell((int) ((getX()) / tileWidth), (int) ((getY() + 1) / tileHeight)).getTile().getProperties().get("blocked");
            } else if(velocity.x > 0) { // going right
                // top right
                if(!collisionX) {
                	collisionX = (Boolean) layer.getCell((int) (((getX()) + getWidth()) / tileWidth), (int) ((getY() + getHeight() - 1) / tileHeight)).getTile().getProperties().get("blocked");
                }

                // middle right
                if(!collisionX)
                    collisionX = (Boolean) layer.getCell((int) (((getX()) + getWidth()) / tileWidth), (int) ((getY() + getHeight() / 2) / tileHeight)).getTile().getProperties().get("blocked");

                // bottom right
                if(!collisionX)
                    collisionX = (Boolean) layer.getCell((int) (((getX()) + getWidth()) / tileWidth), (int) ((getY() + 1) / tileHeight)).getTile().getProperties().get("blocked");
            }
            

            // react to x collision
            if(collisionX) {
                setX(oldX);
            } 

            if(velocity.y < 0) { // going down
                // bottom left
                if(!collisionY) {
                	collisionY = (Boolean) layer.getCell((int) ((getX()) / tileWidth), (int) (getY() / tileHeight)).getTile().getProperties().get("blocked");
                }

                // bottom middle
                if(!collisionY)
                    collisionY = (Boolean) layer.getCell((int) (((getX()) + getWidth() / 2) / tileWidth), (int) (getY() / tileHeight)).getTile().getProperties().get("blocked");

                // bottom right
                if(!collisionY)
                    collisionY = (Boolean) layer.getCell((int) (((getX()) + getWidth()) / tileWidth), (int) (getY() / tileHeight)).getTile().getProperties().get("blocked");

            } else if(velocity.y > 0) { // going up
                // top left
                if(!collisionY) {
                	collisionY = (Boolean) layer.getCell((int) ((getX()) / tileWidth), (int) ((getY() + getHeight()) / tileHeight)).getTile().getProperties().get("blocked");
                }

                // top middle
                if(!collisionY)
                    collisionY = (Boolean) layer.getCell((int) (((getX()) + getWidth() / 2) / tileWidth), (int) ((getY() + getHeight()) / tileHeight)).getTile().getProperties().get("blocked");

                // top right
                if(!collisionY)
                    collisionY = (Boolean) layer.getCell((int) (((getX()) + getWidth()) / tileWidth), (int) ((getY() + getHeight()) / tileHeight)).getTile().getProperties().get("blocked");
            }
            

            // react to y collision
            if(collisionY) {
                setY(oldY);
            }
        }
        
        //Stopping the enemy
        velocity.x = 0;
        velocity.y = 0;
	}
	
	public void moveTo(Player p, Field map) {
		//Sets movement toward a nearby player
		if(getX() > p.getX()) {
			velocity.x -= 3;
		} else if(getX() < p.getX()) {
			velocity.x += 3;
		}
		if(getY() > p.getY()) {
			velocity.y -= 3;
		} else if(getY() < p.getY()) {
			velocity.y += 3;
		}
	}
	
	public void collide(Player p) {
		//Attacks a player provided they're not under invincibility frames
		if(!p.isInvincible()) {
			if(getBoundingRectangle().overlaps(p.getBoundingRectangle())) {
				p.takeDamage(attackPower, 1.5f);
			}
		}
	}
	
	public boolean isNear(Player p) {
		//Check if a player is within a range of this enemy
		boolean isNear = false;
		float dx = getX() - p.getX();
		float dy = getY() - p.getY();
		if(dy * dy + dx * dx < RADIUS * RADIUS) {
			isNear = true;
		}
		return isNear;
	}
	
	public int takeDamage(int dmg) {
		//Makes the enemy take damage from an attack and kills it if it's health reaches 0 and subsequently rewards the player with an appropriate amount of gold as compensation
		int gold = 0;
		health -= dmg;
		if(health <= 0) {
			gold = 2 * (level + 4);
		}
		return gold;
	}
	
	public static int randint(int low, int high){
	    return (int)(Math.random()*(high-low+1) + low);
	}

	public int getHealth() {
		return health;
	}
	
	public int getLevel() {
		return level;
	}
}
