//This class is inherits from the sprite class and is specifically designed for player controlled characters
//It takes input from the user and handles all interactions the player has with the rest of the game and stores the player's Products and items that they've collected
package com.mygdx.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
//This class is inherits from the sprite class and is specifically designed for player controlled characters
//It takes input from the user and handles all interactions the player has with the rest of the game and stores the player's Products and items that they've collected
package com.mygdx.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player extends Sprite{

    /** the movement velocity */
    private Vector2 velocity = new Vector2(); //The current velocity of the player

    private float defSpeed = 60; //The default movement speed value for a player provided no effects are on them, but can change with Products

    public HashSet<String> upgrades = new HashSet<String>(); //Stores the player's current upgrades
    public Product lastUpgrade; //The latest upgrade the player has acquired
    public int money; //The current amount of gold/money the player has and can use to purchase items
    private HashMap<String, Integer> inventory; //Stores the player's purchases
    public ArrayList<Placeable> placeables; //List of all placed down placeables pertaining to the player

    private HashSet<String> tilesOn = new HashSet<String>(); //The tiles that the player is currently standing on

    public boolean baseDown = false; //Flag storing whether or not this player has placed a base
    private int[] basePos; //The position in tiles at which the player has stored their base

    private Shop shop;

    private int attackPower; //The amount of damage the player's attack will do
    private int tempAttackMod; //A temporary increase or decrease in damage that the player deals
    private Hitbox attack; //The actuall hitbox sprite that the player produces
    private float attackCooldown; //A period of time the player must wait in between consecutive attacks
    private int health; //The amount of damage the player can sustain before being knocked out
    public int maxHealth; //The maximum amount of health a player can have at any given time
    private float invincibility; //The number of seconds of invincibility a player has remaining
    private String lastDirectionFaced; //The last direction a player moved in
    private Rectangle baseHitbox; //A rectangular hitbox for the player's base
    private int baseHealth; //The amount of damage the player's base can sustain
    private float respawnTimer; //How long the opponent has left until they respawn after dying
    private int deathCount; //The amount of times the player has died
    private boolean nearBeacon;

    public String gameState; //The current state of the game for this player

    public Player(Sprite sprite, int x, int y) {
        //Constructor that sets it to the appropriate size for a player and some initiation
        super(sprite);
        setPosition(x, y);
        setSize(16, 16);
        basePos = new int[2];
        health = 100;
        maxHealth = 100;
        attackPower = 5;
        lastDirectionFaced = "Right"; //arbitrary default value
        money = 105;
        inventory = new HashMap<String, Integer>();
        inventory.put("Totem", 0);
        inventory.put("Rune", 0);
        inventory.put("Wall", 0);
        inventory.put("Beacon", 0);
        invincibility = 0;
        placeables = new ArrayList<Placeable>();
        shop = new Shop();
        gameState = "Field";
    }

    public void update(float delta, OrthographicCamera camera, Field map) {
        //Updates the player's position and handles interactions with the map
        float moveSpeedMod = 1; //Modifier for movement speed based on tile positioning
        tilesOn.clear();

        // save old position
        float oldX = getX(), oldY = getY(), tileWidth = map.ground.getTileWidth(), tileHeight = map.ground.getTileHeight();
        boolean collisionX = false, collisionY = false; //flags for determining collisions

        //Determining all tile types the player is currently standing on
        tilesOn.add((String) map.ground.getCell((int) ((getX()) / tileWidth), (int) ((getY() + getHeight()) / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) ((getX()) / tileWidth), (int) ((getY() + getHeight() / 2) / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) ((getX()) / tileWidth), (int) (getY() / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) (((getX()) + getWidth()) / tileWidth), (int) ((getY() + getHeight()) / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) (((getX()) + getWidth()) / tileWidth), (int) ((getY() + getHeight() / 2) / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) (((getX()) + getWidth()) / tileWidth), (int) (getY() / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) (((getX()) + getWidth() / 2) / tileWidth), (int) (getY() / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) (((getX()) + getWidth() / 2) / tileWidth), (int) ((getY() + getHeight()) / tileHeight)).getTile().getProperties().get("TerrainType"));


        // move on x

        moveSpeedMod = moveMod(map); //Determining what move speed modifiers should be applied based on terrain

        if(getX() + getWidth() <= map.ground.getWidth() * map.ground.getTileWidth() && getX() >= 0) {
            //Moves the player horizontally provided they are not at the edges of the map
            translateX(velocity.x * delta * moveSpeedMod);
        }
        if(getX() < 0 || getX() + getWidth() > map.ground.getWidth() * map.ground.getTileWidth()) {
            //Applies collisions at edges of the screen
            collisionX = true;
        }
        if(collisionX) {
            setX(oldX);
        }
        if(getY() + getHeight() <= map.ground.getHeight() * map.ground.getTileHeight() && getY() >= 0) {
            //Moves the player vertically provided they are not at the edges of the map
            translateY(velocity.y * delta * moveSpeedMod);
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

        //Stopping the player for when there is no input
        velocity.x = 0;
        velocity.y = 0;

        if(attackCooldown > 0) {
            attackCooldown -= delta;
        }
        if(invincibility > 0) {
            invincibility -= delta;
        }
        resetBeacon();

    }

    public float moveMod(Field map) {
        //Adjusts the movement speed of the player based on the tiles it is on
        float movedefSpeedMod = 1;
        if(tilesOn.contains("forest")) { //Reduces move speed if in a forest, unless they have the appropriate Product
            if(upgrades.contains("Arboreal Essence")) { //Checking for Arboreal Essence
                movedefSpeedMod *= 1.5;
            } else {
                movedefSpeedMod /= 2;
            }
        }
        if(tilePos(map)[0] == basePos[0] && tilePos(map)[1] == basePos[1]) { //Increases movement speed dramatically in base primarily so the camera will follow much faster upon respawn
            movedefSpeedMod *= 3;
        }
        if(nearBeacon) {
            movedefSpeedMod /= 3;
        }
        return movedefSpeedMod;
    }

    public int[] tilePos(Field map) {
        //Returns the tile that the center of the player sprite is on
        int[] tilePos = new int[2];
        tilePos[0] = (int) ((getX() + getWidth() / 2) / map.ground.getTileWidth());
        tilePos[1] = (int) ((getY() + getHeight() / 2) / map.ground.getTileHeight());
        return tilePos;
    }

    public void setBase(Field map, Shop shop) {
        //Sets the player's base directly above them
        if(map.setBase(tilePos(map)[0], tilePos(map)[1])) {
            baseDown = true; //Confirms that the base was successfully placed
            basePos[0] = tilePos(map)[0];
            basePos[1] = tilePos(map)[1] + 1;
            shop.determineUpgrades(this); //Determines the available upgrades for this player's shop
            baseHitbox = new Rectangle((tilePos(map)[0] - 1) * map.ground.getTileWidth(), (tilePos(map)[1] + 1) * map.ground.getTileHeight(), map.ground.getTileWidth() * 3, map.ground.getTileHeight() * 2);
            baseHealth = 500;
        }
    }

    public void setTexture() {
        //Sets the texture of the player character to the appropriate one based on the most recent Product path
        if(upgrades.size() <= 2) {
            setTexture(new Texture(lastUpgrade.name + "Avatar.png"));
        }
    }

    public String kbInput(Field map, OrthographicCamera camera) {
        //Gets player related keyboard input and returns a string to send to the other player
        String signal = "p";
        //Movement speed calculations
        if(gameState.equals("Field")) {
            if(Gdx.input.isKeyPressed(Keys.D)){
                moveRight();
                signal += " moveRight";
            }
            if(Gdx.input.isKeyPressed(Keys.A)){
                moveLeft();
                signal += " moveLeft";
            }
            if(Gdx.input.isKeyPressed(Keys.W)){
                moveUp();
                signal += " moveUp";
            }
            if(Gdx.input.isKeyPressed(Keys.S)){
                moveDown();
                signal += " moveDown";
            }
            if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                //Places down base if not done, otherwise attempts to enter the Product/shop menu, provided the player is in their base, otherwise attacks
                if(baseDown && gameState.equals("Field")) {
                    if(tilePos(map)[0] == basePos[0] && tilePos(map)[1] == basePos[1]) {
                        gameState = "Shop";
                    } else if(attackCooldown <= 0) {
                        attack();
                    }
                } else if(!baseDown) {
                    setBase(map, shop);
                }
                signal += " space";
            }
            if(Gdx.input.isKeyJustPressed(Keys.Q)) { //Attempts to place a placeable
                placeItem(map);
                signal += " place";
            }
        } else if(gameState.equals("Shop")) { //Handles all shop purchase inputs
            if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
                gameState = "Field";
                signal += " exitShop";
            }
            if(Gdx.input.isKeyJustPressed(Keys.A) || Gdx.input.isKeyJustPressed(Keys.D)) {
                getShop().selectSide();
                signal += " selectSide";
            }
            if(Gdx.input.isKeyJustPressed(Keys.S)) {
                getShop().selectDown();
                signal += " selectDown";
            }
            if(Gdx.input.isKeyJustPressed(Keys.W)) {
                getShop().selectUp();
                signal += " selectUp";
            }
            if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                signal += " purchase";
                if(getShop().upgrades.contains(getShop().getSelected())) { //Differentiates between buying an item and choosing an upgrade
                    if(getShop().getSelected().requirement <= 0) {
                        Upgrade(getShop().getSelected());
                        gameState = "Upgraded";
                        setTexture();
                    }
                } else if(getShop().sales.contains(getShop().getSelected())) {
                    if(money >= getShop().getSelected().requirement) {
                        purchase(getShop());
                        gameState = "Upgraded";
                    }
                }
            }
        }
        return signal;
    }

    public void networkInput(Field map, OrthographicCamera camera, String signal) {
        HashSet<String> signals = new HashSet<String>();
        for(String s : signal.split(" ")) {
            signals.add(s);
        }
        //Movement speed calculations
        if(gameState.equals("Field")) {

            if(signals.contains("moveRight")){
                moveRight();
            }
            if(signals.contains("moveLeft")){
                moveLeft();
            }
            if(signals.contains("moveUp")){
                moveUp();
            }
            if(signals.contains("moveDown")){
                moveDown();
            }
            if(signals.contains("space")) {
                //Places down base if not done, otherwise attempts to enter the Product/shop menu, provided the player is in their base, otherwise attacks
                if(baseDown && gameState.equals("Field")) {
                    if(tilePos(map)[0] == basePos[0] && tilePos(map)[1] == basePos[1]) {
                        gameState = "Shop";
                    } else if(attackCooldown <= 0) {
                        attack();
                    }
                } else if(!baseDown) {
                    setBase(map, shop);
                }
            }
            if(signals.contains("place")) { //Attempts to place a placeable
                placeItem(map);
            }
        } else if(gameState.equals("Shop")) { //Handles all shop purchase inputs
            if(signals.contains("exitShop")) {
                gameState = "Field";
            }
            if(signals.contains("selectSide")) {
                shop.selectSide();
            }
            if(signals.contains("selectDown")) {
                shop.selectDown();
            }
            if(signals.contains("selectUp")) {
                shop.selectUp();
            }
            if(signals.contains("purchase")) {
                if(shop.upgrades.contains(shop.getSelected())) { //Differentiates between buying an item and choosing an upgrade
                    if(shop.getSelected().requirement <= 0) {
                        Upgrade(shop.getSelected());
                        gameState = "Upgraded";
                        setTexture();
                    }
                } else if(shop.sales.contains(shop.getSelected())) {
                    if(money >= shop.getSelected().requirement) {
                        purchase(shop);
                        gameState = "Upgraded";
                    }
                }
            }
        }
    }

    public void purchase(Shop shop) {
        ///Purchases specifically an item from the shop
        money -= shop.getSelected().requirement; //Reduces the player's money by the cost of the item
        int curValue = inventory.get(shop.getSelected().name) == null? 0 : inventory.get(shop.getSelected().name); //Determines how many of the item the player already has
        inventory.put(shop.getSelected().name, curValue + 1); //Increases the item in the player's inventory by 1
        if(shop.getSelected().name.equals("Repair Base")) { //Repairs the base by 100 health
            repairBase(100);
        } else if(shop.getSelected().name.equals("Heal")) { //Heals the player by 50 HP to a maximum of their max health
            health = Math.min(health + 50, maxHealth);
        }
    }

    public void Upgrade(Product u) {
        //Adds an upgrade to the list of this player's upgrade and gives them a full heal
        upgrades.add(u.name);
        if(u.name.equals("Fortify")) { //Increases max health immediately for fortify
            maxHealth = 200;
        }
        health = maxHealth;
        lastUpgrade = u;
    }
    //Different methods to move the player. Split up this way mostly for ease of access for the second player
    public void moveDown() {
        velocity.y = -defSpeed;
        lastDirectionFaced = "Down";
    }

    public void moveUp() {
        velocity.y = defSpeed;
        lastDirectionFaced = "Up";
    }

    public void moveLeft() {
        velocity.x = -defSpeed;
        lastDirectionFaced = "Left";
    }

    public void moveRight() {
        velocity.x = defSpeed;
        lastDirectionFaced = "Right";
    }

    public void repairBase(int i) {
        //Repairs the base up to a maximum health of 500
        baseHealth = Math.max(baseHealth + i, 500);
    }

    public void attack() {
        //Unleashes an attack based on the upgrades acquired
        if(!isAttacking()) { //Prevents the player from attacking while they already have an attack active
            if(tilesOn.contains("forest") && upgrades.contains("Arboreal Essence")) { //Giving the appropriate attack mod increase for the Arboreal Essence upgrade in forests
                tempAttackMod = 4;
            }
            if(nearBeacon) {
                tempAttackMod -= getAttackPower() / 3;
            }
            if(upgrades.contains("Magic")) { //Creates a magic hitbox for the magic upgrade paths
                attack = new HitboxMagic(this);
                attackCooldown = 0.2f;
            } else if(upgrades.contains("Science")) { //Creates a science hitbox for the science upgrade paths
                attack = new HitboxScience(this);
                attackCooldown = 0.75f;
            }
        }
    }

    public void takeDamage(int damage, float invin) {
        //Takes damage, handles death, and sets invincibility for damage taken
        if(health >= 0) {
            System.out.println(health);
            health -= damage;
            setInvincibility(invin);
            if(health <= 0) { //Handles death
                death();
            }
        }
    }

    public void death() {
        //Covers death actions
        deathCount++;
        respawnTimer = deathCount * 3 + 5;
    }

    public void countRespawn(float delta, Field map) {
        //Handles all respawn actions
        if(respawnTimer > 0) {
            respawnTimer -= delta; //Counts down on the respawn timer
            if(respawnTimer <= 0) { //Clears the player's placeable items, resets their health, and gives them some respawn invincibility
                setInvincibility(5);
                health = maxHealth;
                inventory.put("Totem", 0);
                inventory.put("Rune", 0);
                inventory.put("Wall", 0);
                inventory.put("Beacons", 0);
            } else if(respawnTimer <= 5) { //Setting the player's position ahead of time, mostly to give the camera scrolling a head start
                setPosition(basePos[0] * map.ground.getTileWidth(), basePos[1] * map.ground.getTileHeight());
            }
        }
    }

    public void damageBase(int damage) {
        //Handles damage to the base
        baseHealth -= damage;
    }

    public void placeItem(Field map) {
        //Places a placeable item onto the field from the player's inventory
        if(inventory.get("Totem") > 0 || inventory.get("Rune") > 0 || inventory.get("Wall") > 0 || inventory.get("Beacon") > 0) { //Ensuring there is a placeable item in inventory
            boolean placed = false;
            if(upgrades.contains("Natural Magic")) {
                //Tries to place a totem
                placed = map.setPlaceable(tilePos(map)[0], tilePos(map)[1], Field.TOTEM);
                if(placed) {
                    placeables.add(new Placeable(this, tilePos(map)[0] * map.ground.getTileWidth(), tilePos(map)[1] * map.ground.getTileHeight(), map, new Sprite(new Texture("Totem.png"))));
                    int curValue = inventory.get("Totem") == null? 0 : inventory.get("Totem");
                    inventory.put("Totem", curValue - 1);
                }

            } else if(upgrades.contains("Dark Arts")) {
                //tries to place a rune
                placed = map.setPlaceable(tilePos(map)[0], tilePos(map)[1], Field.RUNEEFFECTS);
                if(placed) {
                    placeables.add(new Placeable(this, tilePos(map)[0] * map.ground.getTileWidth(), tilePos(map)[1] * map.ground.getTileHeight(), map, new Sprite(new Texture("Rune.png"))));
                    int curValue = inventory.get("Rune") == null? 0 : inventory.get("Rune");
                    inventory.put("Rune", curValue - 1);
                }
            } else if(upgrades.contains("Military")) {
                //tries to place a wall
                int x, y;
                if(lastDirectionFaced.equals("Up")) {
                    x = 0;
                    y = 1;
                } else if(lastDirectionFaced.equals("Right")) {
                    x = 1;
                    y = 0;
                } else if(lastDirectionFaced.equals("Down")) {
                    x = 0;
                    y = -1;
                } else {
                    x = -1;
                    y = 0;
                }
                placed = map.setPlaceable(tilePos(map)[0] + x, tilePos(map)[1] + y, Field.WALL);
                if(placed) {
                    placeables.add(new Placeable(this, (tilePos(map)[0] + x) * map.ground.getTileWidth(), (tilePos(map)[1] + y) * map.ground.getTileHeight(), map, new Sprite(new Texture("Wall.png"))));
                    int curValue = inventory.get("Wall") == null? 0 : inventory.get("Wall");
                    inventory.put("Wall", curValue - 1);
                }

            } else if(upgrades.contains("Alchemy")) {
                //Tries to place a beacon
                placed = map.setPlaceable(tilePos(map)[0], tilePos(map)[1], Field.BEACON);
                if(placed) {
                    placeables.add(new Placeable(this, tilePos(map)[0] * map.ground.getTileWidth(), tilePos(map)[1] * map.ground.getTileHeight(), map, new Sprite(new Texture("Beacon.png"))));
                    int curValue = inventory.get("Beacon") == null? 0 : inventory.get("Beacon");
                    inventory.put("Beacon", curValue - 1);
                }
            }
        }
    }

    public void beaconEffect() {
        //Determines the beacon effects in attack and movement calculations
        nearBeacon = true;
    }

    public boolean isVisible(Field map) {
        //Determines if the player should be visible or not
        boolean visible = true;
        if(respawnTimer > 0) { //Turns invisible while respawning
            visible = false;
        }
        if(upgrades.contains("Nature's Wrath") && map.ground.getCell(tilePos(map)[0], tilePos(map)[1]).getTile().getId() == Field.FOREST) {//Turns invisible while in forests with nature's wrath
            visible = false;
        }
        return visible;
    }

    public boolean isAlive() {
        //Checks if the player is alive
        return health > 0;
    }

    public Rectangle getBaseHitbox() {
        return baseHitbox;
    }

    public int getBaseHealth() {
        return baseHealth;
    }

    public boolean isAttacking() {
        return attack != null;
    }

    public Hitbox getAttack() {
        return attack;
    }

    public void setAttack(Hitbox a) {
        attack = a;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int damage) {
        attackPower = damage;
    }

    public int getAttackMod() {
        return tempAttackMod;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int hp) {
        health = hp;
    }

    public void setInvincibility(float seconds) {
        invincibility = seconds;
    }

    public boolean isInvincible() {
        return invincibility > 0;
    }

    public String getLastDirectionFaced() {
        return lastDirectionFaced;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getdefSpeed() {
        return defSpeed;
    }

    public void setdefSpeed(float defSpeed) {
        this.defSpeed = defSpeed;
    }

    public HashMap<String, Integer> getInventory() {
        return inventory;
    }

    public void gainMoney(int gold) {
        money += gold;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String state) {
        gameState = state;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public void resetBeacon() {
        nearBeacon = false;
    }

    public float getRespawnTimer() {
        return respawnTimer;
    }

    public boolean isBaseAlive() {
        //Returns a win condition to make sure this player can still win
        boolean winCon = true;
        if(baseDown && baseHealth <= 0) { //Sets a loss if the player has placed a base and the base has been slain
            winCon = false;
        } else if(!baseDown && health <= 0) { //Sets a loss if the player has not placed a base and has themselves died
            winCon = false;
        }

        return winCon;
    }

}

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player extends Sprite{

    /** the movement velocity */
    private Vector2 velocity = new Vector2(); //The current velocity of the player

    private float defSpeed = 60; //The default movement speed value for a player provided no effects are on them, but can change with Products

    public HashSet<String> upgrades = new HashSet<String>(); //Stores the player's current upgrades
    public Product lastUpgrade; //The latest upgrade the player has acquired
    public int money; //The current amount of gold/money the player has and can use to purchase items
    private HashMap<String, Integer> inventory; //Stores the player's purchases
    public ArrayList<Placeable> placeables; //List of all placed down placeables pertaining to the player
    
    private HashSet<String> tilesOn = new HashSet<String>(); //The tiles that the player is currently standing on
    
    public boolean baseDown = false; //Flag storing whether or not this player has placed a base
    private int[] basePos; //The position in tiles at which the player has stored their base
    
    private Shop shop;
    
    private int attackPower; //The amount of damage the player's attack will do
    private int tempAttackMod; //A temporary increase or decrease in damage that the player deals
    private Hitbox attack; //The actuall hitbox sprite that the player produces
    private float attackCooldown; //A period of time the player must wait in between consecutive attacks
    private int health; //The amount of damage the player can sustain before being knocked out
    public int maxHealth; //The maximum amount of health a player can have at any given time
    private float invincibility; //The number of seconds of invincibility a player has remaining
    private String lastDirectionFaced; //The last direction a player moved in
    private Rectangle baseHitbox; //A rectangular hitbox for the player's base
    private int baseHealth; //The amount of damage the player's base can sustain
    private float respawnTimer; //How long the opponent has left until they respawn after dying
    private int deathCount; //The amount of times the player has died
    private boolean nearBeacon;
    
    public String gameState; //The current state of the game for this player

    public Player(Sprite sprite, int x, int y) {
    	//Constructor that sets it to the appropriate size for a player and some initiation
        super(sprite);
        setPosition(x, y);
        setSize(16, 16);
        basePos = new int[2];
        health = 100;
        maxHealth = 100;
        attackPower = 5;
        lastDirectionFaced = "Right"; //arbitrary default value
        money = 105;
        inventory = new HashMap<String, Integer>();
        inventory.put("Totem", 0);
        inventory.put("Rune", 0);
        inventory.put("Wall", 0);
        inventory.put("Beacon", 0);
        invincibility = 0;
        placeables = new ArrayList<Placeable>();
        shop = new Shop();
        gameState = "Field";
    }

    public void update(float delta, OrthographicCamera camera, Field map) {
    	//Updates the player's position and handles interactions with the map
        float moveSpeedMod = 1; //Modifier for movement speed based on tile positioning
    	tilesOn.clear();

        // save old position
        float oldX = getX(), oldY = getY(), tileWidth = map.ground.getTileWidth(), tileHeight = map.ground.getTileHeight();
        boolean collisionX = false, collisionY = false; //flags for determining collisions
        
        //Determining all tile types the player is currently standing on
        tilesOn.add((String) map.ground.getCell((int) ((getX()) / tileWidth), (int) ((getY() + getHeight()) / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) ((getX()) / tileWidth), (int) ((getY() + getHeight() / 2) / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) ((getX()) / tileWidth), (int) (getY() / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) (((getX()) + getWidth()) / tileWidth), (int) ((getY() + getHeight()) / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) (((getX()) + getWidth()) / tileWidth), (int) ((getY() + getHeight() / 2) / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) (((getX()) + getWidth()) / tileWidth), (int) (getY() / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) (((getX()) + getWidth() / 2) / tileWidth), (int) (getY() / tileHeight)).getTile().getProperties().get("TerrainType"));
        tilesOn.add((String) map.ground.getCell((int) (((getX()) + getWidth() / 2) / tileWidth), (int) ((getY() + getHeight()) / tileHeight)).getTile().getProperties().get("TerrainType"));
        
        
        // move on x
        
        moveSpeedMod = moveMod(map); //Determining what move speed modifiers should be applied based on terrain
        
        if(getX() + getWidth() <= map.ground.getWidth() * map.ground.getTileWidth() && getX() >= 0) {
        	//Moves the player horizontally provided they are not at the edges of the map
        	translateX(velocity.x * delta * moveSpeedMod);
        }
        if(getX() < 0 || getX() + getWidth() > map.ground.getWidth() * map.ground.getTileWidth()) { 
        	//Applies collisions at edges of the screen
        	collisionX = true;
        }
        if(collisionX) {
            setX(oldX);
        } 
        if(getY() + getHeight() <= map.ground.getHeight() * map.ground.getTileHeight() && getY() >= 0) {
        	//Moves the player vertically provided they are not at the edges of the map
        	translateY(velocity.y * delta * moveSpeedMod);
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
        
        //Stopping the player for when there is no input
        velocity.x = 0;
        velocity.y = 0;
        
        if(attackCooldown > 0) {
        	attackCooldown -= delta;
        }
        if(invincibility > 0) {
        	invincibility -= delta;
        }
        resetBeacon();
        
    }
    
    public float moveMod(Field map) {
    	//Adjusts the movement speed of the player based on the tiles it is on
    	float movedefSpeedMod = 1;
    	if(tilesOn.contains("forest")) { //Reduces move speed if in a forest, unless they have the appropriate Product
        	if(upgrades.contains("Arboreal Essence")) { //Checking for Arboreal Essence
        		movedefSpeedMod *= 1.5;
        	} else {
        		movedefSpeedMod /= 2;
        	}
        }
    	if(tilePos(map)[0] == basePos[0] && tilePos(map)[1] == basePos[1]) { //Increases movement speed dramatically in base primarily so the camera will follow much faster upon respawn
    		movedefSpeedMod *= 3;
    	}
    	if(nearBeacon) {
    		movedefSpeedMod /= 3;
    	}
    	return movedefSpeedMod;
    }
    
    public int[] tilePos(Field map) {
    	//Returns the tile that the center of the player sprite is on 
    	int[] tilePos = new int[2];
    	tilePos[0] = (int) ((getX() + getWidth() / 2) / map.ground.getTileWidth());
    	tilePos[1] = (int) ((getY() + getHeight() / 2) / map.ground.getTileHeight());
    	return tilePos;
    }
    
    public void setBase(Field map, Shop shop) {
    	//Sets the player's base directly above them
    	if(map.setBase(tilePos(map)[0], tilePos(map)[1])) {
        	baseDown = true; //Confirms that the base was successfully placed
        	basePos[0] = tilePos(map)[0];
        	basePos[1] = tilePos(map)[1] + 1;
          	shop.determineUpgrades(this); //Determines the available upgrades for this player's shop
          	baseHitbox = new Rectangle((tilePos(map)[0] - 1) * map.ground.getTileWidth(), (tilePos(map)[1] + 1) * map.ground.getTileHeight(), map.ground.getTileWidth() * 3, map.ground.getTileHeight() * 2);
          	baseHealth = 500;
    	}
    }
    
    public void setTexture() {
    	//Sets the texture of the player character to the appropriate one based on the most recent Product path
    	if(upgrades.size() <= 2) {
    		setTexture(new Texture(lastUpgrade.name + "Avatar.png"));
    	}
    }
    
    public String kbInput(Field map, OrthographicCamera camera) {
    	//Gets player related keyboard input and returns a string to send to the other player
    	String signal = "p";
    	//Movement speed calculations
    	if(gameState.equals("Field")) {
    		if(Gdx.input.isKeyPressed(Keys.D)){
    			moveRight();
    			signal += " moveRight";
            } 
            if(Gdx.input.isKeyPressed(Keys.A)){
            	moveLeft();
            	signal += " moveLeft";
            } 
            if(Gdx.input.isKeyPressed(Keys.W)){
            	moveUp();
            	signal += " moveUp";
            } 
            if(Gdx.input.isKeyPressed(Keys.S)){
            	moveDown();
            	signal += " moveDown";
            } 
            if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            	//Places down base if not done, otherwise attempts to enter the Product/shop menu, provided the player is in their base, otherwise attacks
            	if(baseDown && gameState.equals("Field")) {
            		if(tilePos(map)[0] == basePos[0] && tilePos(map)[1] == basePos[1]) {
            			gameState = "Shop";
            		} else if(attackCooldown <= 0) {
                		attack();
                	}
            	} else if(!baseDown) {
            		setBase(map, shop);
            	}
            	signal += " space";
            }
            if(Gdx.input.isKeyJustPressed(Keys.Q)) { //Attempts to place a placeable
    			placeItem(map);
    			signal += " place";
    		}
    	} else if(gameState.equals("Shop")) { //Handles all shop purchase inputs
    		if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            	gameState = "Field";
            	signal += " exitShop";
            }
    		if(Gdx.input.isKeyJustPressed(Keys.A) || Gdx.input.isKeyJustPressed(Keys.D)) {
    			getShop().selectSide();
    			signal += " selectSide";
    		}
    		if(Gdx.input.isKeyJustPressed(Keys.S)) {
    			getShop().selectDown();
    			signal += " selectDown";
    		}
    		if(Gdx.input.isKeyJustPressed(Keys.W)) {
    			getShop().selectUp();
    			signal += " selectUp";
    		}
    		if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
    			signal += " purchase";
    			if(getShop().upgrades.contains(getShop().getSelected())) { //Differentiates between buying an item and choosing an upgrade
    				if(getShop().getSelected().requirement <= 0) {
    					Upgrade(getShop().getSelected());
        				gameState = "Upgraded";
        				setTexture();
    				}
    			} else if(getShop().sales.contains(getShop().getSelected())) {
    				if(money >= getShop().getSelected().requirement) {
    					purchase(getShop());
    					gameState = "Upgraded";
    				}
    			}
    		}
    	}
    	return signal;
    }
    
    public void networkInput(Field map, OrthographicCamera camera, String signal) {
    	HashSet<String> signals = new HashSet<String>();
    	for(String s : signal.split(" ")) {
    		signals.add(s);
    	}
    	//Movement speed calculations
    	if(gameState.equals("Field")) {
    		
    		if(signals.contains("moveRight")){
    			moveRight();
            } 
            if(signals.contains("moveLeft")){
            	moveLeft();
            } 
            if(signals.contains("moveUp")){
            	moveUp();
            } 
            if(signals.contains("moveDown")){
            	moveDown();
            } 
            if(signals.contains("space")) {
            	//Places down base if not done, otherwise attempts to enter the Product/shop menu, provided the player is in their base, otherwise attacks
            	if(baseDown && gameState.equals("Field")) {
            		if(tilePos(map)[0] == basePos[0] && tilePos(map)[1] == basePos[1]) {
            			gameState = "Shop";
            		} else if(attackCooldown <= 0) {
                		attack();
                	}
            	} else if(!baseDown) {
            		setBase(map, shop);
            	}
            }
            if(signals.contains("place")) { //Attempts to place a placeable
    			placeItem(map);
    		}
    	} else if(gameState.equals("Shop")) { //Handles all shop purchase inputs
    		if(signals.contains("exitShop")) {
            	gameState = "Field";
            }
    		if(signals.contains("selectSide")) {
    			shop.selectSide();
    		}
    		if(signals.contains("selectDown")) {
    			shop.selectDown();
    		}
    		if(signals.contains("selectUp")) {
    			shop.selectUp();
    		}
    		if(signals.contains("purchase")) {
    			if(shop.upgrades.contains(shop.getSelected())) { //Differentiates between buying an item and choosing an upgrade
    				if(shop.getSelected().requirement <= 0) {
    					Upgrade(shop.getSelected());
        				gameState = "Upgraded";
        				setTexture();
    				}
    			} else if(shop.sales.contains(shop.getSelected())) {
    				if(money >= shop.getSelected().requirement) {
    					purchase(shop);
    					gameState = "Upgraded";
    				}
    			}
    		}
    	}
    }
    
    public void purchase(Shop shop) {
    	///Purchases specifically an item from the shop
    	money -= shop.getSelected().requirement; //Reduces the player's money by the cost of the item
		int curValue = inventory.get(shop.getSelected().name) == null? 0 : inventory.get(shop.getSelected().name); //Determines how many of the item the player already has
		inventory.put(shop.getSelected().name, curValue + 1); //Increases the item in the player's inventory by 1
		if(shop.getSelected().name.equals("Repair Base")) { //Repairs the base by 100 health
			repairBase(100);
		} else if(shop.getSelected().name.equals("Heal")) { //Heals the player by 50 HP to a maximum of their max health
			health = Math.min(health + 50, maxHealth);
		}
    }
    
    public void Upgrade(Product u) {
    	//Adds an upgrade to the list of this player's upgrade and gives them a full heal
    	upgrades.add(u.name);
    	if(u.name.equals("Fortify")) { //Increases max health immediately for fortify
    		maxHealth = 200;
    	}
    	health = maxHealth;
    	lastUpgrade = u;
    }
    //Different methods to move the player. Split up this way mostly for ease of access for the second player
    public void moveDown() {
    	velocity.y = -defSpeed;
        lastDirectionFaced = "Down";
	}

	public void moveUp() {
    	velocity.y = defSpeed;
        lastDirectionFaced = "Up";
	}

	public void moveLeft() {
    	velocity.x = -defSpeed;
        lastDirectionFaced = "Left";
	}

	public void moveRight() {
    	velocity.x = defSpeed;
        lastDirectionFaced = "Right";
	}

	public void repairBase(int i) {
		//Repairs the base up to a maximum health of 500
		baseHealth = Math.min(baseHealth + i, 500);
	}

	public void attack() {
    	//Unleashes an attack based on the upgrades acquired
    	if(!isAttacking()) { //Prevents the player from attacking while they already have an attack active
    		if(tilesOn.contains("forest") && upgrades.contains("Arboreal Essence")) { //Giving the appropriate attack mod increase for the Arboreal Essence upgrade in forests
    			tempAttackMod = 4;
    		}
    		if(nearBeacon) {
    			tempAttackMod -= getAttackPower() / 3;
    		}
    		if(upgrades.contains("Magic")) { //Creates a magic hitbox for the magic upgrade paths
        		attack = new HitboxMagic(this);
        		attackCooldown = 0.2f;
        	} else if(upgrades.contains("Science")) { //Creates a science hitbox for the science upgrade paths
        		attack = new HitboxScience(this);
        		attackCooldown = 0.75f;
        	}
    	}
    }
    
    public void takeDamage(int damage, float invin) {
    	//Takes damage, handles death, and sets invincibility for damage taken
		if(health >= 0) {
    		health -= damage;
        	setInvincibility(invin);
        	if(health <= 0) { //Handles death
        		death();
        	}
    	}
    }
    
    public void death() {
    	//Covers death actions
    	deathCount++;
    	respawnTimer = deathCount * 3 + 5;
    }
    
    public void countRespawn(float delta, Field map) {
    	//Handles all respawn actions
    	if(respawnTimer > 0) { 
    		respawnTimer -= delta; //Counts down on the respawn timer
        	if(respawnTimer <= 0) { //Clears the player's placeable items, resets their health, and gives them some respawn invincibility
        		setInvincibility(5);
        		health = maxHealth;
        		inventory.put("Totem", 0);
                inventory.put("Rune", 0);
                inventory.put("Wall", 0);
                inventory.put("Beacons", 0);
        	} else if(respawnTimer <= 5) { //Setting the player's position ahead of time, mostly to give the camera scrolling a head start
        		setPosition(basePos[0] * map.ground.getTileWidth(), basePos[1] * map.ground.getTileHeight());
        	}
    	}
    }
    
    public void damageBase(int damage) {
    	//Handles damage to the base
    	baseHealth -= damage;
    }
    
    public void placeItem(Field map) {
    	//Places a placeable item onto the field from the player's inventory
    	if(inventory.get("Totem") > 0 || inventory.get("Rune") > 0 || inventory.get("Wall") > 0 || inventory.get("Beacon") > 0) { //Ensuring there is a placeable item in inventory
    		boolean placed = false;
    		if(upgrades.contains("Natural Magic")) {
    			//Tries to place a totem
    			placed = map.setPlaceable(tilePos(map)[0], tilePos(map)[1], Field.TOTEM);
    			if(placed) {
    				placeables.add(new Placeable(this, tilePos(map)[0] * map.ground.getTileWidth(), tilePos(map)[1] * map.ground.getTileHeight(), map, new Sprite(new Texture("Totem.png"))));
    				int curValue = inventory.get("Totem") == null? 0 : inventory.get("Totem");
					inventory.put("Totem", curValue - 1);
    			}
    			
    		} else if(upgrades.contains("Dark Arts")) {
    			//tries to place a rune
    			placed = map.setPlaceable(tilePos(map)[0], tilePos(map)[1], Field.RUNEEFFECTS);
    			if(placed) {
    				placeables.add(new Placeable(this, tilePos(map)[0] * map.ground.getTileWidth(), tilePos(map)[1] * map.ground.getTileHeight(), map, new Sprite(new Texture("Rune.png"))));
    				int curValue = inventory.get("Rune") == null? 0 : inventory.get("Rune");
					inventory.put("Rune", curValue - 1);
    			}
    		} else if(upgrades.contains("Military")) {
    			//tries to place a wall
    			int x, y;
    			if(lastDirectionFaced.equals("Up")) {
    				x = 0;
    				y = 1;
    			} else if(lastDirectionFaced.equals("Right")) {
    				x = 1;
    				y = 0;
    			} else if(lastDirectionFaced.equals("Down")) {
    				x = 0;
    				y = -1;
    			} else {
    				x = -1;
    				y = 0;
    			}
    			placed = map.setPlaceable(tilePos(map)[0] + x, tilePos(map)[1] + y, Field.WALL);
    			if(placed) {
        			placeables.add(new Placeable(this, (tilePos(map)[0] + x) * map.ground.getTileWidth(), (tilePos(map)[1] + y) * map.ground.getTileHeight(), map, new Sprite(new Texture("Wall.png"))));
        			int curValue = inventory.get("Wall") == null? 0 : inventory.get("Wall");
					inventory.put("Wall", curValue - 1);
    			}
    			
    		} else if(upgrades.contains("Alchemy")) {
    			//Tries to place a beacon
    			placed = map.setPlaceable(tilePos(map)[0], tilePos(map)[1], Field.BEACON);
    			if(placed) {
    				placeables.add(new Placeable(this, tilePos(map)[0] * map.ground.getTileWidth(), tilePos(map)[1] * map.ground.getTileHeight(), map, new Sprite(new Texture("Beacon.png"))));
    				int curValue = inventory.get("Beacon") == null? 0 : inventory.get("Beacon");
					inventory.put("Beacon", curValue - 1);
    			}
    		}
    	}
    }
    
    public void beaconEffect() {
    	//Determines the beacon effects in attack and movement calculations
    	nearBeacon = true;
    }
    
    public boolean isVisible(Field map) {
    	//Determines if the player should be visible or not
    	boolean visible = true;
    	if(respawnTimer > 0) { //Turns invisible while respawning
    		visible = false;
    	}
    	if(upgrades.contains("Nature's Wrath") && map.ground.getCell(tilePos(map)[0], tilePos(map)[1]).getTile().getId() == Field.FOREST) {//Turns invisible while in forests with nature's wrath
    		visible = false;
    	}
    	return visible;
    }
    
    public boolean isAlive() {
    	//Checks if the player is alive
    	return health > 0;
    }
    
    public Rectangle getBaseHitbox() {
    	return baseHitbox;
    }
    
    public int getBaseHealth() {
    	return baseHealth;
    }
    
    public boolean isAttacking() {
    	return attack != null;
    }
    
    public Hitbox getAttack() {
    	return attack;
    }
    
    public void setAttack(Hitbox a) {
    	attack = a;
    }
    
    public int getAttackPower() {
    	return attackPower;
    }
    
    public void setAttackPower(int damage) {
    	attackPower = damage;
    }
    
    public int getAttackMod() {
		return tempAttackMod;
	}
    
    public int getHealth() {
    	return health;
    }
    
    public void setHealth(int hp) {
    	health = hp;
    }
    
    public void setInvincibility(float seconds) {
    	invincibility = seconds;
    }
    
    public boolean isInvincible() {
    	return invincibility > 0;
    }
    
    public String getLastDirectionFaced() {
    	return lastDirectionFaced;
    }
    
    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getdefSpeed() {
        return defSpeed;
    }

    public void setdefSpeed(float defSpeed) {
        this.defSpeed = defSpeed;
    }
    
    public HashMap<String, Integer> getInventory() {
		return inventory;
	}
    
	public void gainMoney(int gold) {
		money += gold;	
	}
	
	public String getGameState() {
		return gameState;
	}
	
	public void setGameState(String state) {
		gameState = state;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}
	
	public void resetBeacon() {
		nearBeacon = false;
	}
	
	public float getRespawnTimer() {
		return respawnTimer;
	}
	
	public boolean isBaseAlive() {
		//Returns a win condition to make sure this player can still win
		boolean winCon = true;
		if(baseDown && baseHealth <= 0) { //Sets a loss if the player has placed a base and the base has been slain
			winCon = false;
		} else if(!baseDown && health <= 0) { //Sets a loss if the player has not placed a base and has themselves died
			winCon = false;
		}
		
		return winCon;
	}
	
}
