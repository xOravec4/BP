/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.xlabuda;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import messif.objects.impl.ObjectFeature;
import messif.objects.impl.ObjectFeatureSet;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 *
 * @author Tomas
 */
public class Projection  {
    
    
    private ProjectionTo projectionTo;
    //private Map<ObjectFeature, Float> result;
    
    public Projection(ProjectionTo projectionTo)
    {
       this.projectionTo = projectionTo;
    }
    
    public Map<ObjectFeature, Float> getProjection(Set<ObjectFeature> objFeatureList)
    {
        Map<ObjectFeature, Float> result = new HashMap<ObjectFeature, Float>();
        
        switch (projectionTo)
        {
            case X :
                for(ObjectFeature objectFeature : objFeatureList)
                {
                    result.put(objectFeature, objectFeature.getX());
                }
                return result;
            case Y:
                for(ObjectFeature objectFeature : objFeatureList)
                {
                    result.put(objectFeature, objectFeature.getY());
                }
                return result;
            default:
                return null;
                
        }
    }
    
    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
    Comparator<K> valueComparator =  new Comparator<K>() {
        public int compare(K k1, K k2) {
            int compare = map.get(k2).compareTo(map.get(k1));
            if (compare == 0) return 1;
            else return compare;
        }
    };
    Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
    sortedByValues.putAll(map);
    return sortedByValues;
}
    
    public List<ObjectFeature> getSortedProjection(Set<ObjectFeature> objFeatureList){
        
        Map<ObjectFeature, Float> result = getProjection(objFeatureList);
        
        List<ObjectFeature> keys = new ArrayList<ObjectFeature>();
        List<Float> values = new ArrayList<Float>();
        
        for (Map.Entry<ObjectFeature, Float> entry : result.entrySet()) {
        
            if(keys.size() == 0){
                keys.add(entry.getKey());
                values.add(entry.getValue());
                System.out.println("a");
                continue;
            }
            
            for(int i=0;i<keys.size();i++){
                if( entry.getValue() >= values.get(i)){
                    keys.add(i+1, entry.getKey());
                    values.add(i+1, entry.getValue());
                    System.out.println("b");
                    break;
                }
                if(i==keys.size()-1){
                    keys.add( entry.getKey());
                    values.add( entry.getValue());
                    System.out.println("c");
                    break;
                }
                
            }   
            
            
            
        }
        System.out.println("STARt");
        for(int i=0;i<values.size();i++){
            System.out.println(values.get(i));
        }
        System.out.println("STOP");
        return keys;
        
        

        
        /*
        List list = new LinkedList(getProjection(objFeatureList).entrySet());
         Collections.sort(list, new Comparator() {
              public int compare(Object o1, Object o2) {
                   return ((Comparable) ((Map.Entry) (o1)).getValue())
                  .compareTo(((Map.Entry) (o2)).getValue());
              }
         });

        List result = new ArrayList();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            result.add(entry.getKey());
        }
        return result;
        */
    }

    
    public void projectionToLine(Point a1, Point a2)
    {
        
       
    }
    
    public static float nearestPointOnLine(double ax, double ay, double bx, double by, double px, double py) {
                    
                     Point2D   dest = new Point2D.Double();
               
 
                double apx = px - ax;
                double apy = py - ay;
                double abx = bx - ax;
                double aby = by - ay;
 
                double ab2 = abx * abx + aby * aby;
                double ap_ab = apx * abx + apy * aby;
                double t = ap_ab / ab2;
                if (true) {
                        if (t < 0) {
                                t = 0;
                        } else if (t > 1) {
                                t = 1;
                        }
                }
                dest.setLocation(ax + abx * t, ay + aby * t);
               // System.out.println(ax+" " + ay +" " + bx+" " + by +" " + px+" " + py +" RESULT:"+dest.getX()   );
               // return (float)dest.getX();
                return (float) Math.sqrt(Math.pow((ax - dest.getX()), 2) + Math.pow((ay - dest.getY()), 2)) / (float) Math.sqrt(Math.pow((ax - bx), 2) + Math.pow((ay - by), 2));
        }
    
        public static Point2D nearestPointOnLine2(double ax, double ay, double bx, double by, double px, double py) {
               
                     Point2D   dest = new Point2D.Double();
               
 
                double apx = px - ax;
                double apy = py - ay;
                double abx = bx - ax;
                double aby = by - ay;
 
                double ab2 = abx * abx + aby * aby;
                double ap_ab = apx * abx + apy * aby;
                double t = ap_ab / ab2;
                if (true) {
                        if (t < 0) {
                                t = 0;
                        } else if (t > 1) {
                                t = 1;
                        }
                }
                dest.setLocation(ax + abx * t, ay + aby * t);
               // System.out.println(ax+" " + ay +" " + bx+" " + by +" " + px+" " + py +" RESULT:"+dest.getX()   );
                return dest;
               // return (float) Math.sqrt(Math.pow((ax - dest.getX()), 2) + Math.pow((ay - dest.getY()), 2));
        }
    
   /* public void sort()
    {
        Collections.sort(projectionDescriptors, new Comparator<ObjectFeature>() {
        @Override public int compare(ObjectFeature p1, ObjectFeature p2) {
            return (int)p1.getX() - (int)p2.getX(); 
        }

    });
    }*/
    

    
}
