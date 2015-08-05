package co.oym.geokitandroid;

import com.mapbox.mapboxsdk.tileprovider.MapTile;
import com.mapbox.mapboxsdk.tileprovider.tilesource.WebSourceTileLayer;

/**
 * This class converts map tile coordinate (x,y,z) into OnYourMap coordinate (x,y,z) when downloading tile from OnYourMap Web Services.
 */
public class TileLayer extends WebSourceTileLayer {

    private int tileSizePixels;

    public TileLayer(String url, int tileSizePixels) {
        super("onyourmap", url, false);
        this.tileSizePixels = tileSizePixels;
    }

    public TileLayer(String url, int tileSizePixels, boolean enableSSL) {
        super("onyourmap", url, enableSSL);
        this.tileSizePixels = tileSizePixels;
    }

    protected String parseUrlForTile(String url, MapTile aTile, boolean hdpi) {
        int oymZ = 18 - aTile.getZ();
        int oymX = (int)(aTile.getX() - Math.pow(2, aTile.getZ() - 1));
        int oymY = (int)(Math.pow(2, aTile.getZ() - 1) - 1 - aTile.getY());

        return url.replace("{z}", String.valueOf(oymZ))
                  .replace("{x}", String.valueOf(oymX))
                  .replace("{y}", String.valueOf(oymY));
    }

    public int getTileSizePixels() {
        return tileSizePixels;
    }

}
