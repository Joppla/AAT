package ch.bailu.aat.services.dem.loader;

import ch.bailu.aat.coordinates.SrtmCoordinates;
import ch.bailu.aat.services.dem.tile.Dem3Status;
import ch.bailu.aat.services.dem.tile.Dem3Tile;


public class Dem3Tiles {

    private final static int NUM_TILES=1;

    private final Dem3Tile[] tiles;

    public Dem3Tiles() {
        tiles = new Dem3Tile[NUM_TILES];
        for (int i=0; i< NUM_TILES; i++) tiles[i]=new Dem3Tile();
    }
    


    public Dem3Tile getOldestProcessed() {
        Dem3Tile t=null;
        long stamp=System.currentTimeMillis();
        
        for (int i=0; i<NUM_TILES; i++) {
            if (tiles[i].getStatus() == Dem3Status.LOADING) {
                return null;

            } else if (!tiles[i].isLocked() && tiles[i].getTimeStamp() < stamp) {
                t=tiles[i];
                stamp=t.getTimeStamp();
            }
        }
        
        return t;
    }
    
    
    
    public Dem3Tile get(int index) {
        if (index < tiles.length) return tiles[index];
        return null;
    }
    
    public Dem3Tile get(SrtmCoordinates c) {
        for (int i=0; i<NUM_TILES; i++) {
            if (tiles[i].hashCode() == c.hashCode()) {
                return tiles[i];
            }
        }
        return null;
    }
    
    
    public Dem3Tile get(String id) {
        for (int i=0; i<NUM_TILES; i++) {
            if (id.contains(tiles[i].toString())) {
                return tiles[i];
            }
        }
        return null;
    }

    public boolean have(String id) {
        return get(id) != null;
    }
    public boolean have(SrtmCoordinates c) {
        return get(c) != null;
    }


}
