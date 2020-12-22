//This class stores and randomly generates the game map as well as modifies it as necessary

package com.mygdx.game;

import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Field extends TiledMap{
    public int size; //The width and height in tiles of the map
    public TiledMapTileLayer ground;
    public TiledMapTileLayer objects; //The layer in which objects such as placeables and bases are stored
    public MapLayers layers;
    //Don't ask me why these IDs are all one higher than they are in the Field.tsx file, I really don't know but they are
    public static final int FOREST = 2;
    public static final int PLAIN = 3;
    private static final int BASETL = 12;
    private static final int BASETM = 7;
    private static final int BASETR = 8;
    private static final int BASEBL = 9;
    private static final int BASEENTRANCE = 10;
    private static final int BASEBR = 11;
    public static final int WATER1 = 13;
    public static final int AIR = 16; //Generic clear tile used to facilitate collisions with other objects
    public static final int RUNE = 17;
    public static final int TOTEM = 18;
    public static final int WALL = 19;
    public static final int BEACON = 23;
    public static final int RUNEEFFECTS = 20;

    TiledMap importTiles = new TmxMapLoader().load("MapTemplate.tmx");
    public TiledMapTileSet tiles = importTiles.getTileSets().getTileSet(0); //The available tiles that the map builds with

    public Field(int size) {
        //Constructor for a game map, randomly generates tiles
        this.size = size; //The width and height, in tiles, of the map
        layers = getLayers();
        ground = new TiledMapTileLayer(size, size, 32, 32);
        objects = new TiledMapTileLayer(size, size, 32, 32);

        for(int i = 0; i < size; i++) {
            //Setting all the border tiles to be plain tiles to ensure a consistent, navigable access point to each base
            Cell cellTop = new Cell();
            Cell cellBottom = new Cell();
            Cell cellLeft = new Cell();
            Cell cellRight = new Cell();
            cellTop.setTile(tiles.getTile(PLAIN));
            cellBottom.setTile(tiles.getTile(PLAIN));
            cellLeft.setTile(tiles.getTile(PLAIN));
            cellRight.setTile(tiles.getTile(PLAIN));
            ground.setCell(i, 0, cellTop);
            ground.setCell(i, size - 1, cellBottom);
            ground.setCell(0, i, cellLeft);
            ground.setCell(size - 1, i, cellRight);
        }
        for (int x = 1; x < size - 1; x++) { //Setting random tiles across the map with a line of symmetry across a diagonal
            for (int y = x; y < size - 1; y++) {
                int tileDeterminer = randint(1, 100); //Using a random integer to determine a random tile
                int lakeMod = 0;
                if(ground.getCell(x - 1, y).getTile().getId() == WATER1 || ground.getCell(x, y - 1).getTile().getId() == WATER1) { //Making it more likely to create water tiles next to other water tiles to simulate more realistic lake structures
                    lakeMod += 20;
                }
                int forestMod = 0;
                if(ground.getCell(x - 1, y).getTile().getId() == FOREST || ground.getCell(x, y - 1).getTile().getId() == FOREST) { //Making it more likely to create forest tiles next to other forest tiles to simulate more realistic forest structures
                    forestMod += 8;
                }
                //Laying down the appropriate cells in the layer
                Cell cell = new Cell();
                Cell cellReflect = new Cell(); //The cell to be laid on the opposite end of the map to create symmetry
                int tileType;
                if(tileDeterminer <= 6 + lakeMod) {
                    tileType = WATER1;
                } else if(tileDeterminer <= 30 + forestMod) {
                    tileType = FOREST;
                } else {
                    tileType = PLAIN;
                }
                cell.setTile(tiles.getTile(tileType));
                cellReflect.setTile(tiles.getTile(tileType));

                ground.setCell(x, y, cell);
                ground.setCell(y, x, cellReflect);
            }
        }
        for(int x = 0; x < size; x++) {
            for(int y = 0; y < size; y++) {
                objects.setCell(x, y, new Cell().setTile(tiles.getTile(AIR))); //Setting all empty object tiles to be air initially
            }
        }
        layers.add(ground);
        layers.add(objects);
    }

    public Field(String grid, int size) {
        this.size = size; //The width and height, in tiles, of the map
        layers = getLayers();
        ground = new TiledMapTileLayer(size, size, 32, 32);
        objects = new TiledMapTileLayer(size, size, 32, 32);
        for(int x = 0; x < size; x++) {
            for(int y = 0; y < size; y++) {
//                System.out.println(x+" "+y);
                Cell groundCell = new Cell();
                Cell objectCell = new Cell();
                Character c = grid.charAt(x + y * (size + 2));
                if(c.equals('p')) {
                    groundCell.setTile(tiles.getTile(PLAIN));
                } else if(c.equals('f')) {
                    groundCell.setTile(tiles.getTile(FOREST));
                } else {
                    groundCell.setTile(tiles.getTile(WATER1));
                }
                objectCell.setTile(tiles.getTile(AIR));
                ground.setCell(x, y, groundCell);
                objects.setCell(x, y, objectCell);
            }
        }


        layers.add(ground);
        layers.add(objects);
    }

    public boolean setBase(int x, int y) {
        //Sets the player's base at Tile position (x, y) and returns whether it was successful or not
        if(x > 0 && x < size && y < size - 1) { //Checks to see if the base would fit in the map
            Cell TR = new Cell();
            Cell TM = new Cell();
            Cell TL = new Cell();
            Cell BR = new Cell();
            Cell BM = new Cell();
            Cell BL = new Cell();
            TR.setTile(tiles.getTile(BASETR));
            TM.setTile(tiles.getTile(BASETM));
            TL.setTile(tiles.getTile(BASETL));
            BR.setTile(tiles.getTile(BASEBR));
            BM.setTile(tiles.getTile(BASEENTRANCE));
            BL.setTile(tiles.getTile(BASEBL));
            objects.setCell(x - 1, y + 2, TL);
            objects.setCell(x, y + 2, TM);
            objects.setCell(x + 1, y + 2, TR);
            objects.setCell(x - 1, y + 1, BL);
            objects.setCell(x, y + 1, BM);
            objects.setCell(x + 1, y + 1, BR);
            ground.getCell(x - 1, y + 2).setTile(tiles.getTile(PLAIN));
            ground.getCell(x, y + 2).setTile(tiles.getTile(PLAIN));
            ground.getCell(x + 1, y + 2).setTile(tiles.getTile(PLAIN));
            ground.getCell(x - 1, y + 1).setTile(tiles.getTile(PLAIN));
            ground.getCell(x, y + 1).setTile(tiles.getTile(PLAIN));
            ground.getCell(x + 1, y + 1).setTile(tiles.getTile(PLAIN));
            return true;
        } else {
            return false;
        }
    }

    public int columnAt(int x) {
        //Returns the tile column position at pixel value x
        int column = (int) (x / ground.getTileWidth());
        return column;
    }

    public int rowAt(int y) {
        //Returns the tile row position at pixel value y
        int row = (int) (y / ground.getTileWidth());
        return row;
    }

    public void setAir(int x, int y) {
        //Sets a specific tile to be an air tile
        Cell air = new Cell();
        air.setTile(tiles.getTile(AIR));
        objects.setCell(x, y, air);
    }

    public boolean setPlaceable(int x, int y, int type) {
        //sets a placeable item
        boolean validPos = false;
        if(x >= 0 && x < size && y >= 0 && y < size) {
            if(objects.getCell(x, y).getTile().getId() == AIR) {
                Cell tot = new Cell();
                tot.setTile(tiles.getTile(type));
                objects.setCell(x, y, tot);
                validPos = true;
            }
        }
        return validPos;
    }

    @Override
    public String toString() {
        String grid = "";
        for(int x = 0; x < size; x++) {
            for(int y = 0; y < size; y++) {
                if(ground.getCell(x, y).getTile().getId() == PLAIN) {
                    grid += "p";
                } else if(ground.getCell(x, y).getTile().getId() == FOREST) {
                    grid += "f";
                } else {
                    grid += "w";
                }
            }
            grid += "  ";
        }
        return grid;
    }

    public static int randint(int low, int high){
        return (int)(Math.random()*(high-low+1) + low);
    }
}
