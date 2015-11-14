/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.xlabuda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import messif.objects.impl.ObjectFeature;
import static messif.objects.impl.ObjectFeatureSetSmithWaterman.max4;
import messif.objects.util.SequenceMatchingCost;
import messif.utility.ResetableIterator;

/**
 *
 * @author Tomas
 */
public class SmithWaterman {
    
      private List<ObjectFeature> result1 = new ArrayList<ObjectFeature>();
    private List<ObjectFeature> result2 = new ArrayList<ObjectFeature>();
    
    private float similarity = 0;
    
     SmithWaterman(List <ObjectFeature> list1,  List <ObjectFeature> list2){
         this(SequenceMatchingCost.SIFT_DEFAULT, list1, list2);
     }
     
     
     SmithWaterman(SequenceMatchingCost cost, List <ObjectFeature> list1,  List <ObjectFeature> list2) {
        int n = list1.size();       // length of this "string"
        int m = list2.size();       // length of o."string"

        int topIndex = n;
        int sideIndex = m;
        
        if (m == 0 || n == 0){
            similarity = 0;
            return;
        }
            
        float max = 0f;

        float H[][] = new float[m+1][n+1];
        float E[][] = new float[m+1][n+1];
        float F[][] = new float[m+1][n+1];

        // zero the first line and first column
        for (int i = 0; i <= m; i++) {
            H[i][0] = 0.0f;
            E[i][0] = Float.NEGATIVE_INFINITY;//0.0f;
        }
        for (int j = 0; j <= n; j++) {
            H[0][j] = 0.0f;
            F[0][j] = Float.NEGATIVE_INFINITY;//0.0f;
        }
        
        Iterator <ObjectFeature>it1 = list1.iterator();
        Iterator <ObjectFeature>it2 = list2.iterator();

        for (int i = 1; i < m; i++) {
            System.out.println("prvy " + i + " / " + m);
            ObjectFeature o2 = it2.next();
            for (int j = 1; j < n; j++) {
                System.out.println("druhy " + j + " / " + n);
                ObjectFeature o1 = it1.next();
                E[i][j] = Math.max(E[i][j-1] - cost.getGapContinue(), H[i][j-1] - cost.getGapOpening());
                F[i][j] = Math.max(F[i-1][j] - cost.getGapContinue(), H[i-1][j] - cost.getGapOpening());
                
                H[i][j] = max4(
                        0,
                        E[i][j],
                        F[i][j],
                        H[i-1][j-1] + cost.getCost(o2, o1)
                        );

                if (H[i][j] > max) {
                    // i and j holds now the end of the sequence
                    max = H[i][j];
                    sideIndex = i;
                    topIndex = j;
                }
            }
            it1 = list1.iterator();
           
            
        }
        similarity = max;
        
        

        boolean horizontally = false;
        boolean vertically = false;
        result1.add(list1.get(topIndex-1));
        result2.add(list2.get(sideIndex-1));
        
        while(sideIndex > 1 || topIndex > 1){
         

                        
            if(topIndex == 1){
                sideIndex--;
                result1.add(null);
                result2.add(list2.get(sideIndex-1)); 
                vertically = true;
                horizontally = false;
                continue;
            }
            
            if(sideIndex == 1){
                topIndex--;
                result1.add(list1.get(topIndex-1));
                result2.add(null); 
                vertically = false;
                horizontally = true;
                continue;
            }
            
            int maxIndex = 1;
            
           if(vertically){
                maxIndex = getMaxIndex(H[topIndex-1][sideIndex-1],H[topIndex-1][sideIndex] - cost.getGapContinue(), H[topIndex][sideIndex-1] - cost.getGapOpening());
           }
           
           if(horizontally){
                maxIndex = getMaxIndex(H[topIndex-1][sideIndex-1],H[topIndex-1][sideIndex] - cost.getGapOpening(), H[topIndex][sideIndex-1] - cost.getGapContinue());
           }
            
            if(maxIndex == 1){
                topIndex--;
                sideIndex--;
                result1.add(list1.get(topIndex-1));
                result2.add(list2.get(sideIndex-1)); 
                continue;
            }
            if(maxIndex == 2){
                topIndex--;
                result1.add(list1.get(topIndex-1));
                result2.add(null); 
                System.out.println("a");
                continue;
            }
            if(maxIndex == 3){
                sideIndex--;
                result1.add(null);
                result2.add(list2.get(sideIndex-1)); 
                System.out.println("b");
                continue;
            }
            
            
        }
        
        Collections.reverse(result1);
        Collections.reverse(result2);
        
    }
     
      public List<ObjectFeature> getFirstSequence(){
        return new ArrayList(result1);
    }
    
    public List<ObjectFeature> getSecondSequence(){
        return new ArrayList(result2);
    }
    
    public float getSimilarity(){
        return Math.max(similarity, 0); 
    }
    
    private static int getMaxIndex(float a, float b, float c){
        
        if( a > b && a > c){
                return 1;
        }
        
        if( b > a && b > c){
                return 2;
        }
        
        if( c > a && c > b){
                return 3;
        }       
        
        return 1;
    }
}
