package cz.muni.fi.xlabuda;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import messif.objects.impl.ObjectFeature;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.HashMap;


public class Projection {

    private ProjectionTo projectionTo;
    private Point2D custonProjectionFirstPoint;
    private Point2D custonProjectionSecondPoint;

    public Projection(ProjectionTo projectionTo) {
        this.projectionTo = projectionTo;
        custonProjectionFirstPoint = null;
        custonProjectionSecondPoint = null;
    }

    public Projection(ProjectionTo projectionTo, Point2D custonProjectionFirstPoint, Point2D custonProjectionSecondPoint) {
        this.projectionTo = ProjectionTo.CUSTOM;
        this.custonProjectionFirstPoint = custonProjectionFirstPoint;
        this.custonProjectionSecondPoint = custonProjectionSecondPoint;
    }

    public void setProjectionPoints(Point2D custonProjectionFirstPoint, Point2D custonProjectionSecondPoint) {
        this.custonProjectionFirstPoint = custonProjectionFirstPoint;
        this.custonProjectionSecondPoint = custonProjectionSecondPoint;
    }

    public Map<ObjectFeature, Float> getProjection(Set<ObjectFeature> objFeatureList) {
        Map<ObjectFeature, Float> result = new HashMap<ObjectFeature, Float>();

        switch (projectionTo) {
            case X:
                for (ObjectFeature objectFeature : objFeatureList) {
                    result.put(objectFeature, objectFeature.getX());
                }
                return result;
            case Y:
                for (ObjectFeature objectFeature : objFeatureList) {
                    result.put(objectFeature, objectFeature.getY());
                }
                return result;
            default:
                return null;

        }
    }

    public Map<ObjectFeature, Float> getProjection(Set<ObjectFeature> objFeatureList, int imageWidth, int imageHeigt, Point2D A, Point2D B) {

        Map<ObjectFeature, Float> result = new HashMap<ObjectFeature, Float>();
        custonProjectionFirstPoint = A;
        custonProjectionSecondPoint = B;
        switch (projectionTo) {
            case CUSTOM:
                for (ObjectFeature objectFeature : objFeatureList) {
                    if (inRange(A.getX(), A.getY(), B.getX(), B.getY(), objectFeature.getX() * imageWidth, objectFeature.getY() * imageHeigt)) {
                        result.put(objectFeature, projectPointToLine(A.getX(), A.getY(),
                                B.getX(), B.getY(),
                                objectFeature.getX() * imageWidth, objectFeature.getY() * imageHeigt));
                    }
                }

                return result;
            default:
                return null;
        }
    }

    public List<ObjectFeature> getSortedProjection(Set<ObjectFeature> objFeatureList) {

        Map<ObjectFeature, Float> result = getProjection(objFeatureList);

        List<ObjectFeature> keys = new ArrayList<ObjectFeature>();
        List<Float> values = new ArrayList<Float>();

        for (Map.Entry<ObjectFeature, Float> entry : result.entrySet()) {

            if (keys.size() == 0) {
                keys.add(entry.getKey());
                values.add(entry.getValue());
                continue;
            }

            for (int i = 0; i < keys.size(); i++) {
                if (Math.abs(entry.getValue() - values.get(i)) < 0.00000001) {

                    if (Math.abs((entry.getKey().getOrientation() - keys.get(i).getOrientation())) < 0.00001) {

                    } else {

                        if (entry.getKey().getOrientation() < keys.get(i).getOrientation()) {
                            keys.add(i, entry.getKey());
                            values.add(i, entry.getValue());
                            break;
                        }
                        if (i == keys.size() - 1) {
                            keys.add(entry.getKey());
                            values.add(entry.getValue());
                            break;
                        }
                        continue;
                    }
                }

                if (entry.getValue() < values.get(i)) {
                    keys.add(i, entry.getKey());
                    values.add(i, entry.getValue());
                    break;
                }

                if (i == keys.size() - 1) {
                    keys.add(entry.getKey());
                    values.add(entry.getValue());
                    break;
                }
            }
        }

        return keys;

    }

    public static float projectPointToLine(double ax, double ay, double bx, double by, double px, double py) {

        Point2D dest = new Point2D.Double();

        double apx = px - ax;
        double apy = py - ay;
        double abx = bx - ax;
        double aby = by - ay;

        double ab2 = abx * abx + aby * aby;
        double ap_ab = apx * abx + apy * aby;
        double t = ap_ab / ab2;
        if (t < 0) {
            t = 0;
        } else if (t > 1) {
            t = 1;
        }

        dest.setLocation(ax + abx * t, ay + aby * t);

        return (float) Math.sqrt(Math.pow((ax - dest.getX()), 2) + Math.pow((ay - dest.getY()), 2)) / (float) Math.sqrt(Math.pow((ax - bx), 2) + Math.pow((ay - by), 2));
    }

    public ProjectionTo getProjectionType() {
        return projectionTo;
    }

    public Point2D getFirstCustomProjectionPoint() {
        return custonProjectionFirstPoint;
    }

    public Point2D getSecondCustomProjectionPoint() {
        return custonProjectionSecondPoint;
    }

    public static boolean inRange(double start_x, double start_y, double end_x, double end_y,
            double point_x, double point_y) {
        double dx = end_x - start_x;
        double dy = end_y - start_y;
        double innerProduct = (point_x - start_x) * dx + (point_y - start_y) * dy;
        return 0 <= innerProduct && innerProduct <= dx * dx + dy * dy;
    }

}


