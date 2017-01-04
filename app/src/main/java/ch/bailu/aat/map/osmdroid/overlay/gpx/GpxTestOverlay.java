package ch.bailu.aat.map.osmdroid.overlay.gpx;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

import ch.bailu.aat.coordinates.BoundingBoxE6;
import ch.bailu.aat.dispatcher.DispatcherInterface;
import ch.bailu.aat.gpx.GpxList;
import ch.bailu.aat.gpx.GpxListWalker;
import ch.bailu.aat.gpx.GpxPointNode;
import ch.bailu.aat.gpx.GpxSegmentNode;
import ch.bailu.aat.map.osmdroid.OsmInteractiveView;
import ch.bailu.aat.map.osmdroid.overlay.MapPainter;

public class GpxTestOverlay extends GpxOverlay {

    private final Paint segmentPaint;
    private final Paint markerPaint;
    private int boxCount=0;

    public GpxTestOverlay(OsmInteractiveView map, DispatcherInterface d, int iid) {
        super(map, Color.DKGRAY);

        segmentPaint = createPaint(Color.BLACK);
        markerPaint = createPaint(Color.DKGRAY);

        d.addTarget(this, iid);
    }

    private Paint createPaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(3);
        return paint;
    }


    @Override
    public void draw(MapPainter p) {
        boxCount = 0;
        new Walker(p).walkTrack(getGpxList());

        p.canvas.drawTextTop(String.valueOf(boxCount),4);
    }


    private class Walker extends GpxListWalker {
        //private MapTwoNodes paintHelper;
        private final MapPainter painter;


        public Walker(MapPainter p) {
            //paintHelper = new MapTwoNodes(p);
            painter = p;

        }


        @Override
        public boolean doList(GpxList track) {
            return true; //drawBoxIfVisible(track.getDelta().getBoundingBox(), trackPaint);
        }

        @Override
        public boolean doSegment(GpxSegmentNode segment) {
            return drawBoxIfVisible(segment.getBoundingBox(), segmentPaint);
        }

        @Override
        public boolean doMarker(GpxSegmentNode marker) {
            drawBoxIfVisible(marker.getBoundingBox(), markerPaint);
            return false;
        }

        @Override
        public void doPoint(GpxPointNode point) {}

        private boolean drawBoxIfVisible(BoundingBoxE6 box, Paint paint) {
            if (painter.projection.isVisible(box)) {
                boxCount++;
                drawRect(rectFromBox(box), paint);
                return true;
            }
            return false;
        }


        private Rect rectFromBox(BoundingBoxE6 box) {
            return painter.projection.toMapPixels(box);
        }


        private void drawRect(Rect rect, Paint paint) {
            painter.canvas.canvas.drawRect(rect, paint);
        }
    }

}