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
import javax.swing.SwingWorker;
import messif.objects.impl.ObjectFeature;
import static messif.objects.impl.ObjectFeatureSetSmithWaterman.max4;
import messif.objects.util.SequenceMatchingCost;
import messif.utility.ResetableIterator;

/**
 *
 * @author Tomas
 */
public class SmithWaterman extends SwingWorker<Void, Void>{
    
      private List<ObjectFeature> result1 = new ArrayList<ObjectFeature>();
    private List<ObjectFeature> result2 = new ArrayList<ObjectFeature>();
    
    private float similarity = 0;
    private SequenceMatchingCost cost = SequenceMatchingCost.SIFT_DEFAULT;
    List <ObjectFeature> list1 = null;
    List <ObjectFeature> list2 = null;
    MainFrame mainFrame = null;
    
     SmithWaterman(List <ObjectFeature> list1,  List <ObjectFeature> list2, MainFrame mainFrame){
         this(SequenceMatchingCost.SIFT_DEFAULT, list1, list2, mainFrame);
     }
     
     
     SmithWaterman(SequenceMatchingCost cost, List <ObjectFeature> list1,  List <ObjectFeature> list2, MainFrame mainFrame) {
         
        this.cost = cost;
        this.list1 = list1;
        this.list2 = list2;
        this.mainFrame = mainFrame;
        execute();
        if(true)return;
        mainFrame.ShowVisualizationProgressBar(list2.size());
        
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

        for (int i = 1; i <= m; i++) {
            mainFrame.setDataToVisualizationProgressBar(i);
            ObjectFeature o2 = it2.next();
            for (int j = 1; j <= n; j++) {
                //System.out.println("druhy " + j + " / " + n);
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
        
        for(int i=0;i<m+1;i++){
            for(int j=0;j<n+1;j++){
            System.out.print(H[i][j]+"     ");
        }
         //System.out.println();
        }
        //System.out.println("Max value is " + max + " " + sideIndex + " " + topIndex);

        boolean horizontally = false;
        boolean vertically = false;
        result1.add(list2.get(sideIndex-1));
        result2.add(list1.get(topIndex-1));
        
        
        while(H[sideIndex][topIndex]!= 0 && sideIndex > 1 && topIndex > 1){
        
            float cost2 =  cost.getCost(list1.get(topIndex-1), list2.get(sideIndex-1));
            int maxIndex = getMaxIndex( H[sideIndex-1][topIndex-1] + cost2, E[sideIndex][topIndex], F[sideIndex][topIndex]);
        
            if(maxIndex == 1){
                //if(H[sideIndex-1][topIndex-1] < 0.01){break;}
                topIndex--;
                sideIndex--;
                result1.add(list1.get(topIndex-1));
                result2.add(list2.get(sideIndex-1)); 
                System.out.println("BACKTRACE 1    sideindex " + sideIndex);
                continue;
            }
            if(maxIndex == 2){
                
                //if(H[sideIndex-1][topIndex] < 0.01){break;}
                topIndex--;
                result1.add(list1.get(topIndex-1));
                result2.add(null); 
                System.out.println("BACKTRACE 2 sideindex " + sideIndex);
                continue;
            }
            if(maxIndex == 3){
                //if(H[sideIndex][topIndex-1] < 0.01){break;}
                sideIndex--;
                result1.add(null);
                result2.add(list2.get(sideIndex-1)); 
                System.out.println("BACKTRACE 3 sideindex " + sideIndex);
                continue;
            }

        }
        
        
        
        
        //while(sideIndex > 1 || topIndex > 1){
        /*
        while (H[sideIndex][topIndex] > 0.00001){
         

            /*            
            if(topIndex == 1){
                sideIndex--;
                result1.add(null);
                result2.add(list2.get(sideIndex-1)); 
                vertically = true;
                horizontally = false;
                System.out.println("BACKTRACE 4");
                continue;
            }
            
            if(sideIndex == 1){
                topIndex--;
                result1.add(list1.get(topIndex-1));
                result2.add(null); 
                vertically = false;
                horizontally = true;
                System.out.println("BACKTRACE 5");
                continue;
            }*/
            /*
            int maxIndex = 1;
            float c2 = Math.max(0, cost.getCost(list1.get(topIndex-1), list2.get(sideIndex-1)));
            maxIndex = getMaxIndex(H[sideIndex-1][topIndex-1]+c2,H[sideIndex-1][topIndex] - cost.getGapOpening(), H[sideIndex][topIndex-1] - cost.getGapOpening());
            *///int maxIndex = getMaxIndex(d[topIndex-1][sideIndex-1],d[topIndex-1][sideIndex] + cost.getGapOpening(), d[topIndex][sideIndex-1] + cost.getGapOpening());
            /*
           if(vertically){
               System.out.println("BACKTRACE VERTICALLY");
                maxIndex = getMaxIndex(H[sideIndex-1][topIndex-1],H[sideIndex-1][topIndex] - cost.getGapContinue(), H[sideIndex][topIndex-1] - cost.getGapOpening());
           }         
           else if(horizontally){
               System.out.println("BACKTRACE HORIZONTALLY");
                maxIndex = getMaxIndex(H[sideIndex-1][topIndex-1],H[sideIndex-1][topIndex] - cost.getGapOpening(), H[sideIndex][topIndex-1] - cost.getGapContinue());
           }
           else{
               System.out.println("BACKTRACE DIAGONAL");
                maxIndex = getMaxIndex(H[sideIndex-1][topIndex-1],H[sideIndex-1][topIndex] - cost.getGapOpening(), H[sideIndex][topIndex-1] - cost.getGapOpening());
           }*/
            /*
            if(maxIndex == 1){
                if(H[sideIndex-1][topIndex-1] < 0.01){break;}
                topIndex--;
                sideIndex--;
                result1.add(list1.get(topIndex-1));
                result2.add(list2.get(sideIndex-1)); 
                System.out.println("BACKTRACE 1    sideindex " + sideIndex);
                continue;
            }
            if(maxIndex == 2){
                
                if(H[sideIndex-1][topIndex] < 0.01){break;}
                topIndex--;
                result1.add(list1.get(topIndex-1));
                result2.add(null); 
                System.out.println("BACKTRACE 2 sideindex " + sideIndex);
                continue;
            }
            if(maxIndex == 3){
                if(H[sideIndex][topIndex-1] < 0.01){break;}
                sideIndex--;
                result1.add(null);
                result2.add(list2.get(sideIndex-1)); 
                System.out.println("BACKTRACE 3 sideindex " + sideIndex);
                continue;
            }
            
            
        }*/
        
        Collections.reverse(result1);
        Collections.reverse(result2);
        
        System.out.println("THREAD ONE");
        
        mainFrame.setComparationMode(MainFrame.VisualisationType.SMITHWATERMAN);
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
        
        mainFrame.ShowVisualizationProgressBar(list2.size());
        
        int n = list1.size();       // length of this "string"
        int m = list2.size();       // length of o."string"

        int topIndex = n;
        int sideIndex = m;
        
        if (m == 0 || n == 0){
            similarity = 0;
            return null;
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

        for (int i = 1; i <= m; i++) {
            mainFrame.setDataToVisualizationProgressBar(i);
            ObjectFeature o2 = it2.next();
            for (int j = 1; j <= n; j++) {
                //System.out.println("druhy " + j + " / " + n);
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
        
        result1.add(list1.get(topIndex-1));
        result2.add(list2.get(sideIndex-1));

        
        while(H[sideIndex][topIndex]!= 0 && sideIndex > 1 && topIndex > 1){
        
            float cost2 =  cost.getCost(list1.get(topIndex-1), list2.get(sideIndex-1));
            int maxIndex = getMaxIndex( H[sideIndex-1][topIndex-1] + cost2, E[sideIndex][topIndex], F[sideIndex][topIndex]);
        
            if(maxIndex == 1){
                //if(H[sideIndex-1][topIndex-1] < 0.01){break;}
                topIndex--;
                sideIndex--;
                result1.add(list1.get(topIndex-1));
                result2.add(list2.get(sideIndex-1)); 
                System.out.println("BACKTRACE 1    sideindex " + sideIndex);
                continue;
            }
            if(maxIndex == 2){
                
                //if(H[sideIndex-1][topIndex] < 0.01){break;}
                topIndex--;
                result1.add(list1.get(topIndex-1));
                result2.add(result2.size()-1, null); 
                System.out.println("BACKTRACE 2 sideindex " + sideIndex);
                continue;
            }
            if(maxIndex == 3){
                //if(H[sideIndex][topIndex-1] < 0.01){break;}
                sideIndex--;
                result1.add(result2.size()-1, null);
                result2.add(list2.get(sideIndex-1)); 
                System.out.println("BACKTRACE 3 sideindex " + sideIndex);
                continue;
            }

        }
        System.out.println("H[sideIndex][topIndex] " + H[sideIndex][topIndex]);
        
        Collections.reverse(result1);
        Collections.reverse(result2);
        
        System.out.println("THREAD ONE");
        
        mainFrame.setComparationMode(MainFrame.VisualisationType.SMITHWATERMAN);
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
        
        
        
        
        
        
        
        
        return null;
    }
    
    @Override
    protected void done() {
        ((ProjectionGlassPane)mainFrame.getGlassPane()).setPause(false);
        ((ProjectionGlassPane)mainFrame.getGlassPane()).setModeTwoImageVisualisation();
        ((ProjectionGlassPane)mainFrame.getGlassPane()).setVisualizationOneLine();
        
        mainFrame.getFirstScrollPane().getImagePanel().setLock(false);
        mainFrame.getSecondScrollPane().getImagePanel().setLock(false);
        
        mainFrame.HideVisualizationProgressBar();
    }
}
