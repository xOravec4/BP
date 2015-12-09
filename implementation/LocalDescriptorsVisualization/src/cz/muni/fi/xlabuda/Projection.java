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
import javax.swing.JOptionPane;

/**
 *
 * @author Tomas
 */
public class Projection  {
    
    
    private ProjectionTo projectionTo;
    private Point2D custonProjectionFirstPoint;
    private Point2D custonProjectionSecondPoint;
    //private Map<ObjectFeature, Float> result;
    
    public Projection(ProjectionTo projectionTo)
    {
       this.projectionTo = projectionTo;
       custonProjectionFirstPoint = null;
       custonProjectionSecondPoint = null;
    }
    
    public Projection(ProjectionTo projectionTo, Point2D custonProjectionFirstPoint, Point2D custonProjectionSecondPoint)
    {
       this.projectionTo = ProjectionTo.CUSTOM;
       this.custonProjectionFirstPoint = custonProjectionFirstPoint;
       this.custonProjectionSecondPoint = custonProjectionSecondPoint;
    }
    
    public void setProjectionPoints(Point2D custonProjectionFirstPoint, Point2D custonProjectionSecondPoint)
    {
       this.custonProjectionFirstPoint = custonProjectionFirstPoint;
       //this.custonProjectionFirstPoint = new Point2D()
       this.custonProjectionSecondPoint = custonProjectionSecondPoint;
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
            case CUSTOM:
                for(ObjectFeature objectFeature : objFeatureList)
                {
                    result.put(objectFeature, nearestPointOnLine(custonProjectionFirstPoint.getX(), custonProjectionFirstPoint.getY(),
                            custonProjectionSecondPoint.getX(), custonProjectionSecondPoint.getY(),
                            objectFeature.getX()* 500, objectFeature.getY() * 500));
                    System.out.println(nearestPointOnLine(custonProjectionFirstPoint.getX(), custonProjectionFirstPoint.getY(),
                            custonProjectionSecondPoint.getX(), custonProjectionSecondPoint.getY(),
                            objectFeature.getX() * 500, objectFeature.getY() * 500));
                }
                return result;
            default:
                return null;
                
        }
    }
    
     public Map<ObjectFeature, Float> getProjection(Set<ObjectFeature> objFeatureList, int imageWidth, int imageHeigt, Point2D A, Point2D B)
    {
        Map<ObjectFeature, Float> result = new HashMap<ObjectFeature, Float>();
        custonProjectionFirstPoint = A;
        custonProjectionSecondPoint = B;
        switch (projectionTo)
        {
           case CUSTOM:
                for(ObjectFeature objectFeature : objFeatureList)
                {
                    if(inRange(A.getX(), A.getY(),B.getX(), B.getY(),objectFeature.getX()* imageWidth, objectFeature.getY() * imageHeigt))
                    result.put(objectFeature, nearestPointOnLine(A.getX(), A.getY(),
                            B.getX(), B.getY(),
                            objectFeature.getX()* imageWidth, objectFeature.getY() * imageHeigt));
                    /*System.out.println(nearestPointOnLine(A.getX(), A.getY(),
                            B.getX(), B.getY(),
                            objectFeature.getX() * imageWidth, objectFeature.getY() * imageHeigt));
                    */
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
        
        //ValueComparator bvc = new ValueComparator();
        //Map<ObjectFeature, Float> sorted = new TreeMap(bvc);
        //sorted.putAll(result);
        
        for (Map.Entry<ObjectFeature, Float> entry : result.entrySet()) {
        
            if(keys.size() == 0){
                keys.add(entry.getKey());
                values.add(entry.getValue());
                //System.out.println("ALFA");
                continue;
            }
            
            for(int i=0;i<keys.size();i++){
                //System.out.println("JUST TEST " + entry.getValue() + " vs " + values.get(i));
                if( Math.abs(entry.getValue()- values.get(i)) < 0.00000001){
                   /* System.out.println("ERRORRRRRRRRRRRRRRRRR they are same");
                    System.out.println(entry.getKey());
                    System.out.println(keys.get(i));
                    System.out.println("entry.getKey()   " + entry.getKey().getX() +"  +  " +entry.getKey().getY() );
                    System.out.println("keys.get(i)   " + keys.get(i).getX() +"  <  " +keys.get(i).getY() );
                    System.out.println("ORIENTATION  " + entry.getKey().getOrientation() + " " + keys.get(i).getOrientation());
                    System.out.println("");
                    */
                    
                    if(Math.abs(  (entry.getKey().getOrientation() -  keys.get(i).getOrientation() ) )< 0.00001){
                        
                    }
                    else{
                    
                        if(entry.getKey().getOrientation() <  keys.get(i).getOrientation()){
                        keys.add(i, entry.getKey());
                        values.add(i, entry.getValue()); 
                        break;   
                        }
                        continue;
                    }
                    
                    
                    
                    
                
                    //    10 20 30 40 50
                }
                
                //if( entry.getValue() <= values.get(i)){
                if( entry.getValue() <  values.get(i)){
                    //System.out.println("BETA " + entry.getValue() + " < " + values.get(i));
                    keys.add(i, entry.getKey());
                    values.add(i, entry.getValue());
                    
                    break;
                }
                /*
                if( entry.getValue() == values.get(i)){
                    if(entry.getKey().getY() > keys.get(i).getY()){
                    keys.add(i, entry.getKey());
                    values.add(i, entry.getValue());    
                    }
                    else{
                        keys.add(i+1, entry.getKey());
                        values.add(i+1, entry.getValue()); 
                    }
                    
                }*/
                if(i==keys.size()-1){
                    keys.add( entry.getKey());
                    values.add( entry.getValue());
                    //System.out.println("GAMA");
                    break;
                }
                
            }   
            
            
  
            
            
            
        }
        /*
                  System.out.println("FINAL");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            for(int i=0;i<keys.size()-1;i++){
                if(Math.abs(values.get(i+1)- values.get(i)) < 0.000001){
                System.out.println(values.get(i) +  "        !!!!!!!!!!!!!!!!!!!!");    
                    
                }
                else
                System.out.println(values.get(i));
                //System.out.println(keys.get(i));
                //System.out.println();
            }
        /*
        System.out.println("STARt");
        for(int i=0;i<values.size();i++){

            System.out.println(values.get(i));
        }
        System.out.println("STOP");*/
        //return new ArrayList<ObjectFeature>(sorted.keySet());
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
                
                //System.out.println("Input :" + ax + " " + ay + "  XX " + bx + " " + by+ "  XX " + px + " " + py);
                
                float result = (float) Math.sqrt(Math.pow((ax - dest.getX()), 2) + Math.pow((ay - dest.getY()), 2)) / (float) Math.sqrt(Math.pow((ax - bx), 2) + Math.pow((ay - by), 2));
                //System.out.println("Result  : " +  dest.getX() + " " + dest.getY() + "  vzdialenost: "+ result);
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
        
       // public static 
   /* public void sort()
    {
        Collections.sort(projectionDescriptors, new Comparator<ObjectFeature>() {
        @Override public int compare(ObjectFeature p1, ObjectFeature p2) {
            return (int)p1.getX() - (int)p2.getX(); 
        }

    });
    }*/
    
    public ProjectionTo getProjectionType(){
        return projectionTo;
    }
    
    public Point2D getFirstCustomProjectionPoint(){
        return custonProjectionFirstPoint;
    }
    
    public Point2D getSecondCustomProjectionPoint(){
        return custonProjectionSecondPoint;
    }
    
    public static boolean inRange(double start_x, double start_y, double end_x, double end_y,
                                double point_x, double point_y) {
      double dx = end_x - start_x;
      double dy = end_y - start_y;
      double innerProduct = (point_x - start_x)*dx + (point_y - start_y)*dy;
      return 0 <= innerProduct && innerProduct <= dx*dx + dy*dy;
  }

    

    
}

class ValueComparator implements Comparator {


    @Override public int compare(Object a1, Object b2) {

        ObjectFeature a = (ObjectFeature)a1;
        ObjectFeature b = (ObjectFeature)b2;
        if ( a.getX() > b.getX()) {
            return 1;
        } else if ( Math.abs(a.getX() - b.getX()) < 0.0001){
            if (Math.abs(a.getY() - b.getY()) < 0.0001){
               return 1; 
            }
            return -1;
        }
        return -1;
    }

}