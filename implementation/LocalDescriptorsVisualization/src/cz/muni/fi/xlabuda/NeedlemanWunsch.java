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
import java.util.Set;
import javax.swing.SwingWorker;
import messif.objects.impl.ObjectFeature;
import messif.objects.impl.ObjectFeatureSet;
import static messif.objects.impl.ObjectFeatureSetNeedlemanWunsch.max3;
import messif.objects.util.SequenceMatchingCost;
import messif.utility.ResetableIterator;

/**
 *
 * @author Tomas
 */
public class NeedlemanWunsch extends SwingWorker<Void, Void>{
    
    
    private List<ObjectFeature> result1 = new ArrayList<ObjectFeature>();
    private List<ObjectFeature> result2 = new ArrayList<ObjectFeature>();
    
    private float similarity = 0;
    private SequenceMatchingCost cost = SequenceMatchingCost.SIFT_DEFAULT;
    List <ObjectFeature> list1 = null;
    List <ObjectFeature> list2 = null;
    MainFrame mainFrame = null;
    //SequenceMatchingCost cost

    
     NeedlemanWunsch(List <ObjectFeature> list1,  List <ObjectFeature> list2, MainFrame mainFrame) {
         this(SequenceMatchingCost.SIFT_DEFAULT, list1, list2, mainFrame);
         SequenceMatchingCost a = new SequenceMatchingCost();
         
     }
    
    NeedlemanWunsch(SequenceMatchingCost cost, List <ObjectFeature> list1,  List <ObjectFeature> list2, MainFrame mainFrame) {
        this.cost = cost;
        this.list1 = list1;
        this.list2 = list2;
        this.mainFrame = mainFrame;
        
        execute();

        
        if(true) return;
        //list1 = list2;
        
        if(list1.equals(list2)){
            System.out.println("EQUALS");
        }
        else
            System.out.println("NOT EQUALS");
        
        System.out.println("PRINT TWO INPUT LISTS START");
        /*
        for(int i=0;i<list1.size();i++){
            System.out.print("VALUE: " + list1.get(i).getX() +" vs "+ list2.get(i).getX());
            if(list1.get(i).getX() != list2.get(i).getX())
                System.out.println("ERROOOOR value");
            else{
                System.out.println("OK");
            }
            if(list1.get(i).equals(list2.get(i)))
                System.out.println("equals");
            else{
                System.out.println("ERROR deskriptory");
            }
        }
                */
        
        for(int i=0;i<list1.size()-1;i++){
            if(true)break;
            if(list1.get(i).getX() == list1.get(i+1).getX()){
                System.out.println("ERRROOOOOOOOOOOR THE SAME " + list1.get(i).getX() + " " + list1.get(i+1).getX());
                System.out.println(list1.get(i));
                
                System.out.println(list2.get(i));
                System.out.println();
                System.out.println();
            }
            else{
                System.out.println("ok, dif");
                System.out.println( list1.get(i).getX() + " " + list1.get(i+1).getX());
                System.out.println(list1.get(i));
                
                System.out.println(list2.get(i));
                System.out.println();
                System.out.println();
            }

        }
        System.out.println("PRINT TWO INPUT LISTS END");
        
        
        int n = list1.size();       // length of this "string"
        int m = list2.size();       // length of o."string"
        
        
        if (m == 0 || n == 0)
            return;

        //create matrix (n+1)x(m+1)
        final float[][] d = new float[n + 1][m + 1];

        //put row and column numbers in place
        for (int i = 0; i <= n; i++) {
            d[i][0] = i * cost.getGapOpening() * (-1);
        }
        for (int j = 0; j <= m; j++) {
            d[0][j] = j * cost.getGapOpening() * (-1);
        }
        
        Iterator <ObjectFeature>it1 = list1.iterator();
        Iterator <ObjectFeature>it2 = list2.iterator();

        // cycle through rest of table filling values from the lowest cost value of the three part cost function
        for (int i = 1; i <= n; i++) {
            ObjectFeature o1 = it1.next();

            for (int j = 1; j <= m; j++) {
                ObjectFeature o2 = it2.next();
                // get the substution cost
                float c = Math.max(0, cost.getCost(o1, o2));

                // find lowest cost at point from three possible
                d[i][j] = max3(d[i - 1][j] - cost.getGapOpening(), d[i][j - 1] - cost.getGapOpening(), d[i - 1][j - 1] + c);
            }
            it2 = list2.iterator();
        }

        
        int topIndex = n;
        int sideIndex = m;
        
        result1.add(list1.get(topIndex-1));
        result2.add(list2.get(sideIndex-1));
        
        
        
        
        
        while(sideIndex > 1 || topIndex > 1){
         

                        
            if(topIndex == 1){
                sideIndex--;
                result1.add(null);
                result2.add(list2.get(sideIndex-1)); 
                continue;
            }
            
            if(sideIndex == 1){
                topIndex--;
                result1.add(list1.get(topIndex-1));
                result2.add(null); 
                continue;
            }
            
            int maxIndex = getMaxIndex(d[topIndex-1][sideIndex-1],d[topIndex-1][sideIndex] - cost.getGapOpening(), d[topIndex][sideIndex-1] - cost.getGapOpening());
            
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
                //System.out.println("a");
                continue;
            }
            if(maxIndex == 3){
                sideIndex--;
                result1.add(null);
                result2.add(list2.get(sideIndex-1)); 
                //System.out.println("b");
                continue;
            }
            
            
        }
        
        Collections.reverse(result1);
        Collections.reverse(result2);

       // System.out.println("Result: " + Math.max(d[n][m], 0));
        
        System.out.println("First Input size: " + list1.size());
        System.out.println("Second input size: " + list2.size());
        System.out.println("First result size " + result1.size());
        System.out.println("Second result size: " + result2.size());
        similarity = d[n][m];  
               
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

    @Override
    protected Void doInBackground() throws Exception {
        
        System.out.println("First Input size: " + list1.size());
        System.out.println("Second input size: " + list2.size());
                
        mainFrame.ShowVisualizationProgressBar(list1.size());
        for(int i=0;i<list1.size()-1;i++){
            if(true)break;
            if(list1.get(i).getX() == list1.get(i+1).getX()){
                System.out.println("ERRROOOOOOOOOOOR THE SAME " + list1.get(i).getX() + " " + list1.get(i+1).getX());
                System.out.println(list1.get(i));
                
                System.out.println(list2.get(i));
                System.out.println();
                System.out.println();
            }
            else{
                System.out.println("ok, dif");
                System.out.println( list1.get(i).getX() + " " + list1.get(i+1).getX());
                System.out.println(list1.get(i));
                
                System.out.println(list2.get(i));
                System.out.println();
                System.out.println();
            }

        }
        
        for(int i=0;i<list1.size()-1;i++){
            System.out.println("A: "+ list1.get(i));
            System.out.println("B: "+ list2.get(i));
            System.out.println();
        }
        System.out.println("PRINT TWO INPUT LISTS END");
        
        
        int n = list1.size();       // length of this "string"
        int m = list2.size();       // length of o."string"
        
        
        if (m == 0 || n == 0)
            return null;

        //create matrix (n+1)x(m+1)
        final float[][] d = new float[n + 1][m + 1];

        //put row and column numbers in place
        for (int i = 0; i <= n; i++) {
            d[i][0] = i * cost.getGapOpening() * (-1);
        }
        for (int j = 0; j <= m; j++) {
            d[0][j] = j * cost.getGapOpening() * (-1);
        }
        
        Iterator <ObjectFeature>it1 = list1.iterator();
        Iterator <ObjectFeature>it2 = list2.iterator();

        // cycle through rest of table filling values from the lowest cost value of the three part cost function
        for (int i = 1; i <= n; i++) {
            ObjectFeature o1 = it1.next();
            mainFrame.setDataToVisualizationProgressBar(i);
            for (int j = 1; j <= m; j++) {
                ObjectFeature o2 = it2.next();
                // get the substution cost
                float c = Math.max(0, cost.getCost(o1, o2));

                // find lowest cost at point from three possible
                d[i][j] = max3(d[i - 1][j] - cost.getGapOpening(), d[i][j - 1] - cost.getGapOpening(), d[i - 1][j - 1] + c);
            }
            it2 = list2.iterator();
        }

        
        int topIndex = n;
        int sideIndex = m;
        
        result1.add(list1.get(topIndex-1));
        result2.add(list2.get(sideIndex-1));
        
        while(sideIndex > 1 || topIndex > 1){
         

                        
            if(topIndex == 1){
                sideIndex--;
                result1.add(null);              
                result2.add(list2.get(sideIndex-1)); 
                continue;
            }
            
            if(sideIndex == 1){
                topIndex--;
                result1.add(list1.get(topIndex-1));
                result2.add(null); 
                continue;
            }
            
            //int maxIndex = getMaxIndex(d[topIndex-1][sideIndex-1],d[topIndex-1][sideIndex] - cost.getGapOpening(), d[topIndex][sideIndex-1] - cost.getGapOpening());
            float c2 = Math.max(0, cost.getCost(list1.get(topIndex-1), list2.get(sideIndex-1)));
            int maxIndex = getMaxIndex(d[topIndex-1][sideIndex-1] + c2,d[topIndex-1][sideIndex] - cost.getGapOpening(), d[topIndex][sideIndex-1] - cost.getGapOpening());
            
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
                continue;
            }
            if(maxIndex == 3){
                sideIndex--;
                result1.add(null);
                result2.add(list2.get(sideIndex-1)); 
                continue;
            }
            
            
        }
        
        Collections.reverse(result1);
        Collections.reverse(result2);

       // System.out.println("Result: " + Math.max(d[n][m], 0));
        
        System.out.println("First Input size: " + list1.size());
        System.out.println("Second input size: " + list2.size());
        System.out.println("First result size " + result1.size());
        System.out.println("Second result size: " + result2.size());
        similarity = d[n][m];  
        
        System.out.println("THREAD ONE");
        
        mainFrame.setComparationMode(true);
        mainFrame.getFirstScrollPane().getBottomProjectionPanel().setData(result1);
        mainFrame.getFirstScrollPane().getSideProjectionPanel().setData(result1);
        mainFrame.getSecondScrollPane().getBottomProjectionPanel().setData(result2);
        mainFrame.getSecondScrollPane().getSideProjectionPanel().setData(result2);
        mainFrame.getFirstScrollPane().getBottomProjectionPanel().repaint();
        mainFrame.getFirstScrollPane().getSideProjectionPanel().repaint();
        mainFrame.getSecondScrollPane().getBottomProjectionPanel().repaint();
        mainFrame.getSecondScrollPane().getSideProjectionPanel().repaint();

        mainFrame.getFirstScrollPane().SetBottomProjectionPanelVisible();
        mainFrame.getSecondScrollPane().SetBottomProjectionPanelVisible();
        mainFrame.getFirstScrollPane().SetSideProjectionPanelInvisible();
        mainFrame.getSecondScrollPane().SetSideProjectionPanelInvisible();

        mainFrame.SetNWSWTotalSimilarity(similarity);
        

        
        //mainFrame.HideVisualizationProgressBar();
        return null;
    }
    
    
}
