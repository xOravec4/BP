package cz.muni.fi.xlabuda;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import messif.objects.LocalAbstractObject;
import messif.objects.impl.ObjectFeature;

/**
 * Class for encapsulating & drawing SIFT descriptors
 * @author Marian Labuda
 * @version 1.0
 */
public class SIFTLocalDescriptors extends LocalDescriptors {

    @Override
    protected Shape getFeatureShape(ObjectFeature descriptor) {
        double arrowAngle = Math.toRadians(20);

        // left arm of arrow
        double x = 5 - Math.cos(arrowAngle)*2;
        double y = Math.sin(arrowAngle)*2;

        Path2D.Double path = new Path2D.Double();
        path.moveTo(0, 0);
        path.lineTo(5, 0);
        path.lineTo(x, y);

        // right arm of arrow
        x = 5 - Math.cos(-arrowAngle)*2;
        y = Math.sin(-arrowAngle)*2;

        path.moveTo(5, 0);
        path.lineTo(x, y);

        return path;
    }
    
    public String getDescriptorsDescription() {
        return "SIFT";
    }
}