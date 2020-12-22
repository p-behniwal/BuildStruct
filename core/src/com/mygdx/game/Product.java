//These are purchasable products from the shop that include items and upgrades
package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Product extends Sprite{
	public String name; //The name of the prodct
	public String description; //The desrciption for the player's benefit
	public boolean selected; //Determines if this item is currently selected by the player and whether or not it should be bold
	public float requirement; //Either a gold amount or a waiting period
	public float relativeX; //The relative x position to the shop
	public float relativeY; //the relative y position to the shop
	
	public Product(String name, String d, float x, float y, float t) {
		//Constructs a product
		super(new Sprite(new Texture("ProductTemplate.png")));
		setSize(430 * 0.3f, 100 * 0.3f);
		setAlpha(0.7f);
		setPosition(x, y);
		relativeX = x;
		relativeY = y;
		this.name = name;
		description = d;
		requirement = t;
	}
	
	public void setTime(float t) {
		//Sets the time requirement for this product
		requirement = t;
	}
	
	public void passTime(float delta) {
		//Passes the time for this product
		requirement -= delta;
	}
	
	public boolean isAvailable() {
		//Retrns whether or not this upgrade can be gotten
		return requirement <= 0;
	}
	
	public void select() {
		//Selects this item and enboldens it
		selected = true;
		setAlpha(0.9f);
	}
	
	public void deselect() {
		//Deselects this item and unboldens it
		selected = false;
		setAlpha(0.7f);
	}
}
