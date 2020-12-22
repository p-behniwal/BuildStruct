//Wrapper class used for all damaging hitboxes originating from a player
package com.mygdx.game;

import java.util.HashSet;

import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class Hitbox extends Sprite{
	private Player owner; //The player that initiated this hitbox
	private int damage; //The amount of damage this hitbox deals upon collision
	private String direction; //The direction in which the hitbox faces in order to move forward or position itself
	private HashSet<Sprite> thingsHit; //Keeps track of things already hit by this attack to avoid applying damage every frame
	
	private int activeFramesSoFar; //The frames that this hitbox has been out for so far
	
	public Hitbox(Sprite sprite, Player p) {
		//Constructs the hitbox
		super(sprite);
		setOwner(p);
		damage = getOwner().getAttackPower() + p.getAttackMod();
		direction = p.getLastDirectionFaced();
		activeFramesSoFar = 1;
		thingsHit = new HashSet<Sprite>();
	}
	
	public boolean collide(Player p, Field map) {
		//Checks for collision with other players bases and their items to make them take damage
		boolean collided = false;
		if(!p.equals(getOwner())) {
			if(p.baseDown) {
				if(getBoundingRectangle().overlaps(p.getBaseHitbox()) && !thingsHit.contains(p)) { //Hitting the base takes priority over hitting a player so that attacking the base leaves you far more likely to lose the combat if the opponent is close
					p.damageBase(damage);
					collided = true;
					thingsHit.add(p);
				}
			}
			if(getBoundingRectangle().overlaps(p.getBoundingRectangle()) && !thingsHit.contains(p)) {
				if(!p.isInvincible() && !thingsHit.contains(p)) {
					p.takeDamage(damage, 1.5f);
					collided = true;
					thingsHit.add(p);
				}
			} else {
				for(Placeable item : p.placeables) {
					if(getBoundingRectangle().overlaps(item.getBoundingRectangle()) && !thingsHit.contains(item)) {
						item.takeDamage(damage, map);
						collided = true;
						thingsHit.add(p);
					}
				}
			}
		}
		return collided;
	}
	
	public boolean collide(Enemy e) {
		//Checks for collision with enemies to make them take damage
		boolean collided = false;
		if(getBoundingRectangle().overlaps(e.getBoundingRectangle()) && !thingsHit.contains(e)) {
			getOwner().gainMoney(e.takeDamage(damage)); //Gives the owner of the hitbox mney for defeating the enemy if it is slain
			collided = true;
			thingsHit.add(e);
		}
		return collided;
	}
	
	public void determinePos() {
		//Determines the initial position and rotation of the hitbox based on direction the owner is facing
		if(direction.equals("Up")) {
			setPosition(getOwner().getX() - getWidth() / 2 + getOwner().getWidth() / 2, getOwner().getY() + getOwner().getHeight() + getHeight() / 2);
		} else if(direction.equals("Right")) {
			rotate(270);
			setPosition(getOwner().getX() + getOwner().getWidth() + getWidth() / 2, getOwner().getY() - getHeight() / 2 + getOwner().getHeight() * 2);
		} else if(direction.equals("Down")) {
			//flip(true, true);
			rotate(180);
			setPosition(getOwner().getX() + getWidth() / 2 + getOwner().getWidth() / 2, getOwner().getY() - (int) (getHeight() / 1.5));
		} else if(direction.equals("Left")) {
			rotate(90);
			setPosition(getOwner().getX() - getWidth() / 2, getOwner().getY() - getHeight() / 2);
		}
	}
	
	public abstract void animate(); //Goes through different animation frames of the hitbox and moves it if necessary

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}
	
	public String getDirection() {
		return direction;
	}

	public void setDirection(String dir) {
		direction = dir;
	}
	
	public int getDamage() {
		return damage;
	}

	public void setDamage(int n) {
		damage = n;
	}
	
	public HashSet<Sprite> getThingsHit() {
		return thingsHit;
	}
	
	public int getActiveFrames() {
		return activeFramesSoFar;
	}

	public void setActiveFrames(int n) {
		activeFramesSoFar = n;
	}
	
	public void countFrame() {
		activeFramesSoFar++;
	}
}
