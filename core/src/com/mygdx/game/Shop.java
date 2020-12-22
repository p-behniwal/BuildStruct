//The shop from which the player purchases products from
package com.mygdx.game;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Shop extends Sprite{
	
	public static final float STARTINGHEIGHT = 700; //The starting height for products
	public static final float YSPACING = 120; //The vertical space between products
	public static final float ITEMSX = 20; //The relative x position for items
	public static final float UPGRADESX = 500; //The relative x position for upgrades
	
	private Product selected; //The product that is selected to be potentially purchased by the player
	public LinkedList<Product> sales; //All products available for sale
	public LinkedList<Product> upgrades; //Exclusively upgrades available
	public LinkedList<Product> items; //Exclusively items available
	
	LinkedList<Product> naturalUpgrades;
	LinkedList<Product> darkUpgrades;
	LinkedList<Product> militaryUpgrades;
	LinkedList<Product> alchemyUpgrades;
	LinkedList<LinkedList<Product>> upgradePaths;
	
	public Shop() {
		//Creates the shop
		super(new Sprite(new Texture("BaseShop.png")));
		setSize(960 * 0.3f, 960 * 0.3f);
		setAlpha(0.9f); //Allows player to still see what is going on in their screen while in the shop
		
		sales = new LinkedList<Product>();
		upgrades = new LinkedList<Product>();
		items = new LinkedList<Product>();
		
		createProducts();
	}
	
	public void createProducts() {
		//Fills the appropriate linked lists full of the Product paths for future reference
		naturalUpgrades = new LinkedList<Product>();
		darkUpgrades = new LinkedList<Product>();
		militaryUpgrades = new LinkedList<Product>();
		alchemyUpgrades = new LinkedList<Product>();
		
		upgradePaths = new LinkedList<LinkedList<Product>>();
		
		naturalUpgrades.add(new Product("Natural Magic", "Harness the power of the world around you and build health draining totems (They're invisible in forests)!", Shop.UPGRADESX * 0.3f, Shop.STARTINGHEIGHT * 0.3f, 5));
		naturalUpgrades.add(new Product("Arboreal Essence", "Grants higher attack power and movement speed while in forests", Shop.UPGRADESX * 0.3f, Shop.STARTINGHEIGHT * 0.3f, 5));
		naturalUpgrades.add(new Product("Nature's Wrath", "Become perfectly hidden to all while fully submerged in the forests", Shop.UPGRADESX * 0.3f, Shop.STARTINGHEIGHT * 0.3f, 5));
		
		darkUpgrades.add(new Product("Dark Arts", "Learn the forbidden techniques and set demonic runes to attack other players.", Shop.UPGRADESX * 0.3f, (Shop.STARTINGHEIGHT - Shop.YSPACING) * 0.3f, 5));
		darkUpgrades.add(new Product("Soul Thief", "Grants increased stats upon slaying enemies", Shop.UPGRADESX * 0.3f, Shop.STARTINGHEIGHT * 0.3f, 5));

		militaryUpgrades.add(new Product("Military", "Research the technologies around placeable, destructible walls.", Shop.UPGRADESX * 0.3f, Shop.STARTINGHEIGHT * 0.3f, 5));
		militaryUpgrades.add(new Product("Fortify", "Surround yourself in armour and double your max health!", Shop.UPGRADESX * 0.3f, Shop.STARTINGHEIGHT * 0.3f, 5));		
		militaryUpgrades.add(new Product("Bigger Artillery", "Grants MUCH higher attack power and increased splash radius", Shop.UPGRADESX * 0.3f, Shop.STARTINGHEIGHT * 0.3f, 5));
		
		alchemyUpgrades.add(new Product("Alchemy", "Research mystical combinations of substances allowing you to place slowing and weakening beacons!", Shop.UPGRADESX * 0.3f, (Shop.STARTINGHEIGHT - Shop.YSPACING) * 0.3f, 5));
		alchemyUpgrades.add(new Product("Lingering Acid Pools", "Makes attacking acid pools last longer when expanded", Shop.UPGRADESX * 0.3f, Shop.STARTINGHEIGHT * 0.3f, 5));
		alchemyUpgrades.add(new Product("Poisoner", "", Shop.UPGRADESX * 0.3f, Shop.STARTINGHEIGHT * 0.3f, 5));
		upgradePaths.add(naturalUpgrades);
		upgradePaths.add(darkUpgrades);
		upgradePaths.add(militaryUpgrades);
		upgradePaths.add(alchemyUpgrades);
	}

	public void determineUpgrades(Player p) {
		//Determines what upgrades are available at a given time
		sales.clear();
		upgrades.clear();
		items.clear();
		
		if(p.upgrades.isEmpty()) {
			//Adding potential upgrades
			upgrades.add(new Product("Magic", "Harness the power of the unknown and perform close ranged attacks!", UPGRADESX * 0.3f, STARTINGHEIGHT * 0.3f, 0));
			upgrades.add(new Product("Science", "Unleash the power of knowledge upon the world and gain a ranged projectile attack!", UPGRADESX * 0.3f, (STARTINGHEIGHT - YSPACING) * 0.3f, 0));
		} else if(p.lastUpgrade.name.equals("Magic")) {
			upgrades.add(upgradePaths.get(0).getFirst());
			upgrades.add(upgradePaths.get(1).getFirst());
		} else if(p.lastUpgrade.name.equals("Science")) {
			upgrades.add(upgradePaths.get(2).getFirst());
			upgrades.add(upgradePaths.get(3).getFirst());
		} else {
			for(LinkedList<Product> path : upgradePaths) {
				if(path.contains(p.lastUpgrade)) {
					if(path.indexOf(p.lastUpgrade) + 1 < path.size()) {
						upgrades.add(path.get(path.indexOf(p.lastUpgrade) + 1));
					}
				}
			}
		}
		int numRepairs = p.getInventory().get("Repair Base") == null? 0 : p.getInventory().get("Repair Base");
		items.add(new Product("Repair Base", "Heals your base for 100 health!", ITEMSX * 0.3f, STARTINGHEIGHT * 0.3f, 50 * numRepairs));
		int numHeals = p.getInventory().get("Heal") == null? 0 : p.getInventory().get("Heal");
		items.add(new Product("Heal", "Heals you base for 50 health!", ITEMSX * 0.3f, (STARTINGHEIGHT - YSPACING) * 0.3f, 50 * numHeals));
		if(p.upgrades.contains("Natural Magic")) {
			items.add(new Product("Totem", "Place these on your current tile to deal AoE damage to anyone near! You can even hide them in forests.", ITEMSX * 0.3f, (STARTINGHEIGHT - YSPACING * 2) * 0.3f, 150));
		} else if(p.upgrades.contains("Dark Arts")) {
			items.add(new Product("Rune", "Place these on your current tile to lay a demonic trap for your opponent equal to your attack power.", ITEMSX * 0.3f, (STARTINGHEIGHT - YSPACING * 2) * 0.3f, 40));
		} else if(p.upgrades.contains("Military")) {
			items.add(new Product("Wall", "Place these in front of you to block a pathway!", ITEMSX * 0.3f, (STARTINGHEIGHT - YSPACING * 2) * 0.3f, 50));
		} else if(p.upgrades.contains("Alchemy")) {
			items.add(new Product("Beacon", "Place these on your current tile to slow and weaken the attack power of your enemies!", ITEMSX * 0.3f, (STARTINGHEIGHT - YSPACING * 2) * 0.3f, 100));
		}
		if(!items.isEmpty()) { //Sets the item to be originally selected
			selected = items.getFirst();
			selected.select();
		} else if(!upgrades.isEmpty()) {
			selected = upgrades.getFirst();
			selected.select();
		} 
		sales.addAll(upgrades);
		sales.addAll(items);
		for(Product product : sales) {
    		product.translate(getX(), getY());
		}
	}
	
	public LinkedList<Product> displayProducts() {
		return sales;
	}
	
	public void selectSide() {
		//Moves the selected Upgrade to the side
		if(!items.isEmpty() && !upgrades.isEmpty()) {
			if(upgrades.contains(selected)) {
				if(upgrades.indexOf(selected) >= items.size()) {
					selected.deselect();
					selected = items.getLast();
				} else {
					selected.deselect();
					selected = items.get(upgrades.indexOf(selected));
				}
			} else if(items.contains(selected)) {
				if(items.indexOf(selected) >= upgrades.size()) {
					selected.deselect();
					selected = upgrades.getLast();
				} else {
					selected.deselect();
					selected = upgrades.get(items.indexOf(selected));
				}
			}
			selected.select();
		}
	}
	
	public void selectDown() {
		//Move the selected upgrade down
		if(upgrades.contains(selected)) {
			if(upgrades.indexOf(selected) + 1 < upgrades.size()) {
				selected.deselect();
				selected = upgrades.get(upgrades.indexOf(selected) + 1);
			} else {
				selected.deselect();
				selected = upgrades.getFirst();
			}
		} else if(items.contains(selected)) {
			if(items.indexOf(selected) + 1 < items.size()) {
				selected.deselect();
				selected = items.get(items.indexOf(selected) + 1);
			} else {
				selected.deselect();
				selected = items.getFirst();
			}
		}
		selected.select();
	}
	
	public void selectUp() {
		//Move the selected upgrade up
		if(upgrades.contains(selected)) {
			if(upgrades.indexOf(selected) > 0) {
				selected.deselect();
				selected = upgrades.get(upgrades.indexOf(selected) - 1);
			} else {
				selected.deselect();
				selected = upgrades.getLast();
			}
		} else if(items.contains(selected)) {
			if(items.indexOf(selected) > 0) {
				selected.deselect();
				selected = items.get(items.indexOf(selected) - 1);
			} else {
				selected.deselect();
				selected = items.getLast();
			}
		}
		selected.select();
	}
	
	public Product getSelected() {
		return selected;
	}

}
