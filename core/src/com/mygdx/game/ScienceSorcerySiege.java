//Main class that handles all overview of the game
package com.mygdx.game;


import java.util.ArrayList;
import java.util.Scanner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class ScienceSorcerySiege extends ApplicationAdapter{
	SpriteBatch batch; //The batch that draws sprites
	float w; //The width of the screen in pixels
	float h; //The width of the screen in pixels
	Field map; //The game map that players traverse
	OrthographicCamera camera; //The camera that displays certain points of the screen
	TiledMapRenderer tiledMapRenderer; //Allows the tiled map to be rendered
	Player[] players; //Array of both players, 0 is the one this player is controlling, 1 is the opponent
	ArrayList<Enemy> enemies; //ArrayList containing all the enemies currently on the field
	float spawnTimer; //Spawn timer for how causing enemies to spawn in periodically
	private float totalTime; //The total amount elapsed since the start of the game
	//Unselected product fonts
	BitmapFont itemFont;
	BitmapFont descFont;
	//Bold fonts
	BitmapFont itemFontS;
	BitmapFont descFontS;

	BitmapFont generalFont; //General font used for information such as godl and health
	BitmapFont endFont; //Font used for things such as the respawn timer and you win/lose screens
	boolean respawnCountdown; //The countdown until the player controlled character respawns

	boolean isHost;
	public MainServer host;
	public MainClient client;

	@Override
	public void create () {
		//Initiates fields
		Scanner kb = new Scanner(System.in);
		String input = "";
		while(!input.equals("H") && !input.equals("F")) {
			System.out.println("Would you like to Host (H) or find (F) a game?");
			input = kb.nextLine();
			if(input.equals("H")) {
				host = new MainServer(" 10.88.193.255");
				host.run();
				System.out.println("hell");
				isHost = true;
			} else if(input.equals("F")) {
				client = new MainClient("10.88.193.255");
				client.run();
				isHost = false;
			}
		}
		kb.close();
		batch = new SpriteBatch();
		//setting variables for easy access to screen dimensions
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		camera.zoom -= 0.7; //Zooming in the camera to adjust for small tile sizes
		camera.setToOrtho(false, w, h);
		camera.update();
		itemFont = new BitmapFont();
		descFont = new BitmapFont();
		itemFont.getData().setScale(0.5f);
		itemFont.setColor(0, 0, 0, 0.7f);
		descFont.getData().setScale(0.4f);
		descFont.setColor(0, 0, 0, 0.7f);

		itemFontS = new BitmapFont();
		descFontS = new BitmapFont();
		generalFont = new BitmapFont();
		itemFontS.getData().setScale(0.5f);
		itemFontS.setColor(0, 0, 0, 1);
		descFontS.getData().setScale(0.4f);
		descFontS.setColor(0, 0, 0, 1);
		generalFont.getData().setScale(0.65f);
		itemFontS.setColor(0, 0, 0, 1);

		endFont = new BitmapFont();
		endFont.getData().setScale(2);

		Texture playerTex = new Texture("playerStartSprite.png"); //The original player character that everyone starts as

		players = new Player[2];


		enemies = new ArrayList<Enemy>();

		if(isHost) {
			map = new Field(10); //Creates the field

			host.send(map.toString());
			players[0] = new Player(new Sprite(playerTex), 5, 5); //Player controlled
			players[1] = new Player(new Sprite(playerTex), 100, 100); //Enemy controlled
		} else {
			players[0] = new Player(new Sprite(playerTex), 100, 100); //Player controlled
			players[1] = new Player(new Sprite(playerTex), 5, 5); //Enemy controlled
			while (client.hostMap.equals("")) {
				System.out.println("hi");
			}// d
			map = new Field(client.hostMap, 10); //Creates the field
		}
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
		players[0].setGameState("Field"); //Sets both players' game states to field, which is where they'll begin
		players[1].setGameState("Field");
	}

	@Override
	public void render () {
		//Processes all game actions and draws onto the screen
		if(players[0].isBaseAlive() && players[1].isBaseAlive()) { //Ensures that the game has not yet been won/lost before actually playing it
			String signalSent = ""; //String to send to opponent to communicate actions done
			if(players[0].isAlive()) {
				respawnCountdown = false;
				signalSent = players[0].kbInput(map, camera); //Getting all player input and determining the game state based off of that
				if(isHost) {
					host.send(signalSent);
				}
				else{
					client.send(signalSent);
				}
			} else {
				respawnCountdown = true; //ensures that the respawn countdown will be drawn while the player is dead
			}
			if(players[1].isAlive()) {
				if(isHost){
					players[1].networkInput(map, camera, host.moveInput); //Processing the opponent's actions
				}
				else{
					players[1].networkInput(map, camera, client.moveInput); //Processing the opponent's actions
				}
				//players[1].networkInput(map, camera, signalReceived);
			}
			for(Player player : players) {
				player.update(Gdx.graphics.getRawDeltaTime(), camera, map); //Delta time used for smooth movements
				if(player.baseDown) {
					for(Product p : player.getShop().displayProducts()) {
						if(player.getShop().upgrades.contains(p)) {
							p.passTime(Gdx.graphics.getRawDeltaTime()); //Counting down the timer on all upgrades available to players
						}
					}
				}
				player.countRespawn(Gdx.graphics.getRawDeltaTime(), map); //Counts down each player's respawn timer if they are dead
			}

			if(isHost){
				host.send(spawnEnemies());  //Spawns enemies onto the map when appropriate
			}
			else{
				spawnEnemies(client.eInput);
			}
			spawnEnemies(); //Spawns enemies onto the map when appropriate
			if(players[0].getGameState().equals("Field")) { //Handles camera work
				if(players[0].getX() - camera.position.x > w * camera.zoom - 250  && camera.position.x + w * camera.zoom / 2 < map.ground.getWidth() * map.ground.getTileWidth()) { //Scrolling when at right side of the screen
					camera.translate(players[0].getdefSpeed() * Gdx.graphics.getRawDeltaTime() * players[0].moveMod(map), 0);

				} else if(players[0].getX() - camera.position.x < -1 * w * camera.zoom + 250 && camera.position.x > w * camera.zoom / 2) { //Scrolling when at left
					camera.translate(-players[0].getdefSpeed() * Gdx.graphics.getRawDeltaTime() * players[0].moveMod(map), 0);

				}
				if(players[0].getY() - camera.position.y > h * camera.zoom - 250  && camera.position.y + h * camera.zoom / 2 < map.ground.getHeight() * map.ground.getTileHeight()) { //Scrolling when at the top of the screen
					camera.translate(0, players[0].getdefSpeed() * Gdx.graphics.getRawDeltaTime() * players[0].moveMod(map));
				} else if(players[0].getY() - camera.position.y < -1 * h * camera.zoom + 250 && camera.position.y > h * camera.zoom / 2) { //Scrolling when at left
					camera.translate(0, -players[0].getdefSpeed() * Gdx.graphics.getRawDeltaTime() * players[0].moveMod(map));
				}
			}
			for(Player p : players) {
				if(p.getGameState().equals("Upgraded")) { //recalculates the products that should be on display for the player
					p.setGameState("Shop");
					p.getShop().determineUpgrades(p);
				}
			}
			if(players[0].getGameState().equals("Shop")) { //Sets the position of the shop
				players[0].getShop().setPosition(camera.position.x - w / 2 * camera.zoom, camera.position.y - h / 2 * camera.zoom);
				for(Product p : players[0].getShop().displayProducts()) {
					p.setPosition(players[0].getShop().getX() + p.relativeX, players[0].getShop().getY() + p.relativeY);
				}

			}
			ArrayList<Enemy> outdatedEnemies = new ArrayList<Enemy>();
			for(Enemy e : enemies) {
				//e.moveDir(totalTime);
				for(Player p : players) {
					if(e.isNear(p) && p.isVisible(map)) {
						e.moveTo(p, map);
					}
				}
				e.move(Gdx.graphics.getRawDeltaTime(), map);
				if(e.getLevel() < (int) totalTime / 35 - 2) {
					//Removing enemies that are far weaker, mostly as a balancing mechanic for Soul Thief to minimize farming of early low level enemies that were never defeated
					outdatedEnemies.add(e);
				}
			}
			enemies.removeAll(outdatedEnemies);
			for(Player p : players) {
				if(p.isAttacking()) {
					p.getAttack().animate();
					ArrayList<Enemy> deadEnemies = new ArrayList<Enemy>();
					for(Enemy e : enemies) {
						if(p.isAttacking()) {
							if(p.getAttack().collide(e)) {
								if(e.getHealth() <= 0) {
									deadEnemies.add(e);
									if(p.upgrades.contains("Soul Thief")) {
										p.setAttackPower(p.getAttackPower() + 1);
										p.setHealth(Math.min(p.getHealth() + 1, p.maxHealth));
										p.setdefSpeed(p.getdefSpeed() + 1);
									}
								}
							}
						}
					}
					enemies.removeAll(deadEnemies);
				}
				for(Player opponent : players) {
					if(!opponent.equals(p)) {
						ArrayList<Placeable> brokenItems = new ArrayList<Placeable>();
						for(Placeable item : p.placeables) {
							if(item.getType().equals("Dark Arts") && item.isVisible()) {
								brokenItems.add(item);
								item.takeDamage(item.getHP(), map);
							}
							if(opponent.getBoundingRectangle().overlaps(item.getBoundingRectangle()) && item.getType().equals("Dark Arts")) {
								opponent.takeDamage(item.attack, 1.5f);
								item.setVisibility(true);
							} else if(item.getType().equals("Natural Magic") || item.getType().equals("Alchemy")) {
								item.dealEffect(opponent, map, totalTime);
							}
							if(item.isDestroyed()) {
								brokenItems.add(item);
							}
						}
						p.placeables.removeAll(brokenItems);
						if(p.isAttacking()) {
							p.getAttack().collide(opponent, map);
						}
					}
				}
			}
			countTime();
			//Drawing all objects
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			//Drawing tiles
			camera.update();
			tiledMapRenderer.setView(camera);
			tiledMapRenderer.render();
			batch.setProjectionMatrix(camera.combined);

			batch.begin();
			for(Player p : players) {
				if(p.isVisible(map)) {
					p.draw(batch);
				}
				for(Enemy e : enemies) {
					e.collide(p);
					e.draw(batch);
				}
				if(p.isAttacking()) {
					p.getAttack().draw(batch);
				}
				for(Placeable item : p.placeables) {
					if(item.isVisible()) {
						item.draw(batch);
					}
				}
			}

			if(players[0].getGameState().equals("Shop")) {
				players[0].getShop().draw(batch);
				for(Product p : players[0].getShop().displayProducts()) {
					p.draw(batch);
					GlyphLayout itemGlyth = new GlyphLayout();
					String reqName = players[0].getShop().upgrades.contains(p)? " seconds" : " gold";
					if(p.selected) {
						itemGlyth.setText(itemFontS, p.name);
						itemFontS.draw(batch, itemGlyth, p.getX() + (p.getWidth() - itemGlyth.width) / 2, p.getY() + p.getHeight() - 3);

						GlyphLayout descGlyth = new GlyphLayout();
						descGlyth.setText(descFontS, p.description, descFontS.getColor(), p.getWidth() - 10, (int) (p.getX() + p.getWidth() - 10), true);
						descFontS.draw(batch, descGlyth, p.getX() + (p.getWidth() - descGlyth.width) / 2, p.getY() + p.getHeight() - 10);

						if(p.requirement > 0) {
							GlyphLayout timeGlyth = new GlyphLayout();
							timeGlyth.setText(descFontS, (int) p.requirement + reqName, descFontS.getColor(), p.getWidth() - 10, (int) (p.getX() + p.getWidth() - 10), true);
							descFontS.draw(batch, timeGlyth, p.getX() + 5, p.getY() + p.getHeight() - 3);
						}

					} else {
						itemGlyth.setText(itemFont, p.name);
						itemFont.draw(batch, itemGlyth, p.getX() + (p.getWidth() - itemGlyth.width) / 2, p.getY() + p.getHeight() - 3);

						GlyphLayout descGlyth = new GlyphLayout();
						descGlyth.setText(descFont, p.description, descFont.getColor(), p.getWidth() - 10, (int) (p.getX() + p.getWidth() - 10), true);
						descFont.draw(batch, descGlyth, p.getX() + (p.getWidth() - descGlyth.width) / 2, p.getY() + p.getHeight() - 10);

						if(p.requirement > 0) {
							GlyphLayout timeGlyth = new GlyphLayout();
							timeGlyth.setText(descFont, (int) p.requirement + reqName, descFont.getColor(), p.getWidth() - 10, (int) (p.getX() + p.getWidth() - 10), true);
							descFont.draw(batch, timeGlyth, p.getX() + 5, p.getY() + p.getHeight() - 3);
						}
					}
				}
				generalFont.draw(batch, "Base Health: " + players[0].getBaseHealth(), (w / 2 - 100) * camera.zoom + camera.position.x - w / 2 * camera.zoom, (50) * camera.zoom + camera.position.y - w / 2 * camera.zoom);

			}
			if(respawnCountdown) {
				endFont.draw(batch, "" + ((int) players[0].getRespawnTimer() + 1), (w / 2 - 50) * camera.zoom + camera.position.x - w / 2 * camera.zoom, (h / 2) * camera.zoom + camera.position.y - w / 2 * camera.zoom);
			}
			generalFont.draw(batch, "Gold: " + players[0].money, (w - 150) * camera.zoom + camera.position.x - w / 2 * camera.zoom, (h - 20) * camera.zoom + camera.position.y - w / 2 * camera.zoom);
			generalFont.draw(batch, "Health: " + players[0].getHealth(), (w - 950) * camera.zoom + camera.position.x - w / 2 * camera.zoom, (h - 20) * camera.zoom + camera.position.y - w / 2 * camera.zoom);
			batch.end();
		} else {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			if(players[0].isBaseAlive()) {
				endFont.draw(batch, "You Win!", (w / 2 - 250) * camera.zoom + camera.position.x - w / 2 * camera.zoom, (h / 2) * camera.zoom + camera.position.y - w / 2 * camera.zoom);
			} else{
				endFont.draw(batch, "You Lose...", (w / 2 - 250) * camera.zoom + camera.position.x - w / 2 * camera.zoom, (h / 2) * camera.zoom + camera.position.y - w / 2 * camera.zoom);
			}
			batch.end();
		}

	}

	private String spawnEnemies() {
		//
		String enemyPos = "e";
		int numEnemiesSpawn = (int) totalTime / 35 + 1;
		if(totalTime >= 10 && spawnTimer <= 0) {
			for(int i = 0; i < numEnemiesSpawn; i++) {
				Enemy potentialEnemy;
				float enemyX = randint(5, map.size - 5) * 32;
				float enemyY = randint(5, map.size - 5) * 32;
				boolean invalidPos = false;
				for(MapLayer layer : map.layers) {
					TiledMapTileLayer l = (TiledMapTileLayer) layer;
					if(!invalidPos) {
						invalidPos = (Boolean) l.getCell((int) ((enemyX) / l.getTileWidth()), (int) ((enemyY + 16 - 1) / l.getTileHeight())).getTile().getProperties().get("blocked");
					}
					if(!invalidPos) {
						invalidPos = (Boolean) l.getCell((int) ((enemyX) / l.getTileWidth()), (int) ((enemyY) / l.getTileHeight())).getTile().getProperties().get("blocked");
					}
					if(!invalidPos) {
						invalidPos = (Boolean) l.getCell((int) ((enemyX + 16 - 1) / l.getTileWidth()), (int) ((enemyY + 16 - 1) / l.getTileHeight())).getTile().getProperties().get("blocked");
					}
					if(!invalidPos) {
						invalidPos = (Boolean) l.getCell((int) ((enemyX + 16 - 1) / l.getTileWidth()), (int) ((enemyY) / l.getTileHeight())).getTile().getProperties().get("blocked");
					}
				}
				if(!invalidPos) {
					potentialEnemy = new Enemy((int) totalTime, enemyX, enemyY);
					for(Player p : players) {
						if(!potentialEnemy.isNear(p)) {
							enemies.add(potentialEnemy);
							spawnTimer = randint(5, 20);
							enemyPos += (" " +potentialEnemy.getX()+","+potentialEnemy.getY());
						}
					}
				}
			}
		}
		return enemyPos;
	}

	private void spawnEnemies(String signal){
		for(String s : signal.split(" ")){
			if(!s.isEmpty()){
				float enemyX = Float.parseFloat(s.split(",")[0]);
				float enemyY = Float.parseFloat(s.split(",")[1]);
				enemies.add(new Enemy((int)totalTime,enemyX,enemyY ));
			}
		}
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

	public void countTime() {
		totalTime += Gdx.graphics.getRawDeltaTime();
		if(spawnTimer > 0) {
			spawnTimer -= Gdx.graphics.getRawDeltaTime();
		}
	}

	public static int randint(int low, int high){
		return (int)(Math.random()*(high-low+1) + low);
	}
}
