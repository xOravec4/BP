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
 *
 * @author Marian Labuda
 * @version 1.0
 */
public class SURFLocalDescriptors extends LocalDescriptors{

    @Override
    protected Shape getFeatureShape(ObjectFeature descriptor) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(0, 0);
        path.lineTo(1.5, 0);
        path.lineTo(1.5, 1.5);
        path.lineTo(-1.5, 1.5);
        path.lineTo(-1.5, -1.5);
        path.lineTo(1.5, -1.5);
        path.lineTo(1.5, 0);
        return path;
    }

    public String getDescriptorsDescription() {
        return "SURF";
    }
}