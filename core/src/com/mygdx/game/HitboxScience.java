//Projectile hitbox used by science based upgrade paths
package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class HitboxScience extends Hitbox{
	private int framesPreX; //The number of frames before the projectile explodes
	private int totalXReps; //The total number of cycles of the explosion animation that the hitbox performs upon exploding
	private int curXReps; //The current number of cycles of the explosion animation
	private int projSpeed; //The speed at which the projectile travels
	private Vector2 dir; //The direction in which the projectile travels
	private int acidLinger; //used to see how long the hitbox lingers without growing, only applicable to the second alchemy upgrade
	private int growthMod; //The rate at which the explosion grows
	
	private int curFrame; //The current animation frame in which the hitbox is
	
	private final int LINGER = 4; //How long the hitbox stays in a certain animation frame
	private final int GROWTH = 2; //The default growth rate of an explosion
	
	public HitboxScience(Player p) {
		//Constructs a science hitbox
		super(new Sprite(), p);
		framesPreX = 20;
		curXReps = 0;
		projSpeed = 5;
		String aestheticUpgrade;
		acidLinger = 0;
		growthMod = 1;
		//Checks which upgrade effects the properties of the hitbox
		if(getOwner().upgrades.contains("Military")) {
			aestheticUpgrade = "Military";
			totalXReps = 1;
			if(getOwner().upgrades.contains("Bigger Artillery")) {
				growthMod = 5;
				setDamage(getDamage() * 10);
			}
		} else if(getOwner().upgrades.contains("Alchemy")) {
			aestheticUpgrade = "Alchemy";
			totalXReps = 3;
			if(getOwner().upgrades.contains("Lingering Acid Pools")) {
				acidLinger = 10;
			}
		} else {
			aestheticUpgrade = "Science";
			totalXReps = 3;
		}
		setTexture(new Texture(aestheticUpgrade + "Attack.png"));
		setRegion(0, 0, 32, 32);
		curFrame = 1;
		setSize(6, 6);
		determinePos();
		if(getDirection().equals("Left")) { //Modifying slightly the default positions of a hitbox
			translateY(getOwner().getHeight() / 2);
		} else if(getDirection().equals("Right")) {
			translateY(-getOwner().getHeight());
		}
		dir = new Vector2();
		determineDir();
	}

	public void animate() {
		//Animates, moves, and handles explosions of this hitbox
		if(!getThingsHit().isEmpty() && getActiveFrames() < framesPreX) {
			setActiveFrames(framesPreX); //Makes explosion occur immediately upon hitting an object
		}
		if(getActiveFrames() < framesPreX) { //Handles animation and movement pre explosion
			if(getActiveFrames() % LINGER == 0) {
				nextFrame();
			}
			translate(dir.x * projSpeed, dir.y * projSpeed);
		} else {
			if(curFrame < 5) { //Makes sure to move to explosion animation frames if it's not there yet
				curFrame = 5;
				setRegion(32, 32, 32, 32);
			}
			if(getActiveFrames() % LINGER == 0) { //Moves onto the next explosion frame
				nextFrame();
			}
			if(getActiveFrames() < framesPreX + totalXReps * LINGER) { //Handles explosion cycles
				if(getDirection().equals("Right")) { //Grows while maintaining center. Requires slight adjustments when facing right or left
					setBounds(getX() - GROWTH * growthMod, getY() + GROWTH * growthMod, getWidth() + GROWTH * growthMod * 2, getHeight() + GROWTH * growthMod * 2);
				} else if(getDirection().equals("Left")) {
					setBounds(getX() + GROWTH * growthMod, getY() - GROWTH * growthMod, getWidth() + GROWTH * growthMod * 2, getHeight() + GROWTH * growthMod * 2);
				} else if(getDirection().equals("Down")) {
					setBounds(getX() + GROWTH * growthMod, getY() + GROWTH * growthMod, getWidth() + GROWTH * growthMod * 2, getHeight() + GROWTH * growthMod * 2);
				} else {
					setBounds(getX() - GROWTH * growthMod, getY() - GROWTH * growthMod, getWidth() + GROWTH * growthMod * 2, getHeight() + GROWTH * growthMod * 2);
				}
			} else if(getActiveFrames() > framesPreX + totalXReps * LINGER + acidLinger) { //Destorys this hitbox after it's past its active frames
				getOwner().setAttack(null);
			}
		}
		countFrame();
	}

	private void nextFrame() {
		//Moves on to the next frame of animation
		if(getActiveFrames() < framesPreX) { //Handles frames before explosion happens
			if(curFrame < 4) {
				if(getRegionX() + getRegionWidth() < getTexture().getHeight()) {
					setRegion(getRegionX() + getRegionWidth(), getRegionY(), getRegionWidth(), getRegionHeight());
				} else {
					setRegion(0, getRegionY() + getRegionHeight(), getRegionWidth(), getRegionHeight());
				}
				curFrame++;
			} else { //Loops Around t the first pre explosion frame after running through all the rest
				setRegion(0, 0, getRegionWidth(), getRegionHeight());
				curFrame = 1;
			}
		} else { //Handles animation after the explosion
			if(curFrame < 7) {
				if(getRegionX() + getRegionWidth() < getTexture().getHeight()) {
					setRegion(getRegionX() + getRegionWidth(), getRegionY(), getRegionWidth(), getRegionHeight());
				} else {
					setRegion(0, getRegionY() + getRegionHeight(), getRegionWidth(), getRegionHeight());
				}
				curFrame++;
			} else { //Loops to the first explosion frame and increases the current repetitions performed
				if(curXReps < totalXReps) {
					setRegion(32, 32, 32, 32);
					curFrame = 5;
					curXReps++;
				}
			}
		}
	}
	
	private void determineDir() {
		//Determines a direction vector to use for movement based on the direction provided for it
		if(getDirection().equals("Up")) {
			dir.y = 1;
		} else if(getDirection().equals("Right")) {
			dir.x = 1;
		} else if(getDirection().equals("Down")) {
			dir.y = -1;
		} else if(getDirection().equals("Left")) {
			dir.x = -1;
		}
	}
}
