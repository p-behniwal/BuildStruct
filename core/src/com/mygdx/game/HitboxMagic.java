//close ranged Hitbox used by Magic based upgrade paths
package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class HitboxMagic extends Hitbox{
	
	private final static int LINGER = 3; //The number of frames before transitioning to the next animation frame
	private final static int TOTALACTIVEFRAMES = 12; //The total number of frames the move will be active for
	
	public HitboxMagic(Player p) {
		//Constructs a magic hitbox
		super(new Sprite(), p);
		String aestheticUpgrade; //Determines the upgrade that impacts the appearance of the hitbox
		if(getOwner().upgrades.contains("Dark Arts")) {
			aestheticUpgrade = "Dark Arts";
		} else if(getOwner().upgrades.contains("Natural Magic")) {
			aestheticUpgrade = "Natural Magic";
		} else {
			aestheticUpgrade = "Magic";
		}
		setTexture(new Texture(aestheticUpgrade + "Attack.png"));
		setRegion(0, 0, 32, 16);
		setSize(32, 16);
		determinePos();
	}
	
	public void animate() {
		//Animates the different frames of a magic hitbox
		if(getActiveFrames() <= TOTALACTIVEFRAMES) {
			if(getActiveFrames() % LINGER == 0) {
				setRegion(0, getRegionY() + 16, 32, 16); //Moving onto the next frame since magic hitboxes are on a 1 x 4 grid
			}
			countFrame();
		} else {
			getOwner().setAttack(null); //Destorys this attack once it has exceeded its active frames
		}
	}
	
}
