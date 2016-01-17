package ch.bailu.aat.preferences;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;

import android.content.Context;
import ch.bailu.aat.services.cache.BitmapTileObject;
import ch.bailu.aat.services.cache.ElevationColorTile;
import ch.bailu.aat.services.cache.ElevationExperimentalTile;
import ch.bailu.aat.services.cache.ElevationShadeTile;
import ch.bailu.aat.services.cache.TileBitmapFilter;
import ch.bailu.aat.services.cache.ObjectHandle.Factory;
import ch.bailu.aat.services.cache.TileObject.Source;

public class SolidMapTileStack extends SolidCheckList {

    public static final int MIN_ZOOM = 1;
    public static final int MAX_ZOOM=17; // 18 takes way too much space for the gain. 
    
    private final static String KEY = "tile_overlay_";
    
    public final static BitmapTileObject.Source MAPNIK = 
            new BitmapTileObject.Source("Mapnik", MIN_ZOOM, MAX_ZOOM,
                    "http://a.tile.openstreetmap.org/",
                    "http://b.tile.openstreetmap.org/",
                    "http://c.tile.openstreetmap.org/");

    
    public final static Source TRAIL_MTB = 
            new BitmapTileObject.Source("TrailMTB",  MIN_ZOOM, MAX_ZOOM,
                    "http://tile.waymarkedtrails.org/mtb/");

    public final static Source TRAIL_SKATING = 
            new BitmapTileObject.Source("TrailSkating", MIN_ZOOM, MAX_ZOOM,
                    "http://tile.waymarkedtrails.org/skating/");


    public final static Source TRAIL_HIKING = 
            new BitmapTileObject.Source("TrailHiking", MIN_ZOOM, MAX_ZOOM,
                    "http://tile.waymarkedtrails.org/hiking/");


    public final static Source TRAIL_CYCLING = 
            new BitmapTileObject.Source("TrailCycling",  MIN_ZOOM, MAX_ZOOM,
                    "http://tile.waymarkedtrails.org/cycling/");



    public final static Source TRANSPORT_OVERLAY = 
            new BitmapTileObject.Source("OpenPtMap", 5, 16,
                    "http://openptmap.org/tiles/");


    public final static Source ELEVATION_COLOR = 
            new Source() {

        @Override
        public String getName() {
            return "ElevationColor";
        }

        @Override
        public String getID(MapTile t, Context x) {
            return getName() + "/" + t.getZoomLevel() + "/" + t.getX() + "/" + t.getY(); 
        }

        @Override
        public int getMinimumZoomLevel() {
            return 5;
        }

        @Override
        public int getMaximumZoomLevel() {
            return 18;
        }

        @Override
        public Factory getFactory(MapTile mt) {
            return  new ElevationColorTile.Factory(mt);
        }
    };
    

    
    public final static Source ELEVATION_SHADE = 
            new Source() {

        @Override
        public String getName() {
            return "ElevationShade";
        }

        @Override
        public String getID(MapTile t, Context x) {
            return getName() + "/" + t.getZoomLevel() + "/" + t.getX() + "/" + t.getY(); 
        }

        @Override
        public int getMinimumZoomLevel() {
            return 6;
        }

        @Override
        public int getMaximumZoomLevel() {
            return 15;
        }

        @Override
        public Factory getFactory(MapTile mt) {
            return  new ElevationShadeTile.Factory(mt);
        }
    };

    
    public final static Source ELEVATION_EXPERIMENTAL = 
            new Source() {

        @Override
        public String getName() {
            return "Experimental";
        }

        @Override
        public String getID(MapTile t, Context x) {
            return getName() + "/" + t.getZoomLevel() + "/" + t.getX() + "/" + t.getY(); 
        }

        @Override
        public int getMinimumZoomLevel() {
            return 6;
        }

        @Override
        public int getMaximumZoomLevel() {
            return 15;
        }

        @Override
        public Factory getFactory(MapTile mt) {
            return  new ElevationExperimentalTile.Factory(mt);
        }
    };

    private final static TileBitmapFilter[] FILTERS = new TileBitmapFilter[] {
        TileBitmapFilter.OVERLAY_FILTER,    
        TileBitmapFilter.OVERLAY_FILTER,
        
        TileBitmapFilter.GRAYSCALE_FILTER,
        TileBitmapFilter.OVERLAY_FILTER,
        
        TileBitmapFilter.COPY_FILTER,
        
        
        TileBitmapFilter.OVERLAY_FILTER,
        TileBitmapFilter.OVERLAY_FILTER,
        TileBitmapFilter.OVERLAY_FILTER,
        TileBitmapFilter.OVERLAY_FILTER,
        TileBitmapFilter.OVERLAY_FILTER
    };

    
    private final static Source[] SOURCES = new Source[] {
        ELEVATION_COLOR,    
        ELEVATION_EXPERIMENTAL,
        
        MAPNIK,
        MAPNIK,
        
        ELEVATION_SHADE,
        
        TRANSPORT_OVERLAY,
        TRAIL_SKATING,
        TRAIL_HIKING,
        TRAIL_MTB,
        TRAIL_CYCLING,
    };

    

    
    private final SolidBoolean[] enabledArray = new SolidBoolean[SOURCES.length];
    
    
    public SolidMapTileStack (Context context, int preset) {
        Storage s = Storage.global(context);
        
        for (int i=0; i<enabledArray.length; i++) {
            enabledArray[i]=new SolidBoolean(s, KEY+preset+"_"+i);
        }
    }
    
    
    public static boolean isZoomLevelSupported(OnlineTileSourceBase source ,MapTile tile) {
        return 
                tile.getZoomLevel() <= source.getMaximumZoomLevel() &&
                tile.getZoomLevel() >= source.getMinimumZoomLevel();
    }
    
    public int getCountOfEnabled() {
        int c = 0;
        
        for (int i=0; i<enabledArray.length; i++)
            if (enabledArray[i].isEnabled()) c++;
        
        return c;
    }
    
    
    @Override
    public CharSequence[] getStringArray() {
        String[] array = new String[SOURCES.length];
        for (int i=0; i<SOURCES.length; i++)
            array[i] = SOURCES[i].getName();
        return array;
    }

    @Override
    public boolean[] getEnabledArray() {
        boolean[] array = new boolean[enabledArray.length];
        for (int i=0; i<enabledArray.length; i++)
            array[i] = enabledArray[i].isEnabled();
        return array;
    }


    @Override
    public void setEnabled(int i, boolean isChecked) {
        i=Math.min(enabledArray.length, i);
        i=Math.max(0, i);
        enabledArray[i].setValue(isChecked);
    }



    @Override
    public String getKey() {
        return KEY;
    }



    @Override
    public Storage getStorage() {
        return enabledArray[0].getStorage();
    }

    
    @Override
    public boolean hasKey(String s) {
        for (int i=0; i<enabledArray.length; i++) {
            if (enabledArray[i].hasKey(s)){
                return true;
            }
        }
        return false;
    }



    public Source[] getSourceList() {
        Source[]  array = new Source[getCountOfEnabled()];

        int index=0;

        for (int i=0; i<enabledArray.length; i++) {
            if (enabledArray[i].isEnabled()) {
                array[index] = SOURCES[i];
                index++;
            }
        }

        return array;
    }
    
    
    public TileBitmapFilter[] getFilterList() {
        TileBitmapFilter[]  array = new TileBitmapFilter[getCountOfEnabled()];

        int index=0;

        for (int i=0; i<enabledArray.length; i++) {
            if (enabledArray[i].isEnabled()) {
                array[index] = FILTERS[i];
                index++;
            }
        }

        return array;
    }
    
}