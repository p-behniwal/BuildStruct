//This class will handle the different types of tiles as well as collisions and other interactions with players

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Tile {
	private int type;
	private Texture tileImg;
	private Sprite area;
	
	private static int SIDELEN = 64;
	public static final int PLAIN = 0;
	public static final int FOREST = 1;
	public static final int WATER = 2;
	
	public Tile(int t, int x, int y) {
		type = t;
		if(type == PLAIN) {
			tileImg = new Texture("plainTile.png");
		} else if(type == FOREST) {
			tileImg = new Texture("forestTile.png");
		} else if(type == WATER) {
			tileImg = new Texture("waterTile.png");
		}
		area = new Sprite(tileImg);
		area.setSize(SIDELEN, SIDELEN);
		area.setPosition(x * SIDELEN, y * SIDELEN);
	}
	
	public int type() {
		return type;
	}
	
	public Sprite sprite() {
		return area;
	}
	
}
