package cz.muni.fi.xlabuda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingWorker;
import messif.objects.impl.ObjectFeature;
import static messif.objects.impl.ObjectFeatureSetNeedlemanWunsch.max3;
import messif.objects.util.SequenceMatchingCost;

/**
 * Counts alignment of two sequences and results are set to ProjectionPanels and can be obtained again by get methods.
 *
 * @author Tomas Oravec
 * @version 1.0
 */

public class NeedlemanWunsch extends SwingWorker<Void, Void>{
    
    private List<ObjectFeature> result1 = new ArrayList<ObjectFeature>();
    private List<ObjectFeature> result2 = new ArrayList<ObjectFeature>();
    
    private float similarity = 0;
    private SequenceMatchingCost cost = SequenceMatchingCost.SIFT_DEFAULT;
    List <ObjectFeature> list1 = null;
    List <ObjectFeature> list2 = null;
    MainFrame mainFrame = null;
    private boolean exitVisualization = false;
    
     NeedlemanWunsch(List <ObjectFeature> list1,  List <ObjectFeature> list2, MainFrame mainFrame) {
         this(SequenceMatchingCost.SIFT_DEFAULT, list1, list2, mainFrame);
         
     }
    
    NeedlemanWunsch(SequenceMatchingCost cost, List <ObjectFeature> list1,  List <ObjectFeature> list2, MainFrame mainFrame) {
        this.cost = cost;
        this.list1 = list1;
        this.list2 = list2;
        this.mainFrame = mainFrame;
        
        execute();

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
    
    private static int getMaxIndex(float a, float b){
        if( a > b )
            return 1;
        if( b > a)
            return 2;
        return 1;
    }

    @Override
    protected Void doInBackground() throws Exception {
        
        mainFrame.ShowVisualizationProgressBar(list1.size());
        
        int n = list1.size();       
        int m = list2.size();       
        
        
        if (m == 0 || n == 0){
            exitVisualization = true;
            return null;
        }
            

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
        
        while(sideIndex > 0 || topIndex > 0){  //backtrace
         
           
            if(topIndex == 0){
                sideIndex--;
                if(sideIndex == 0)
                    break;
                result1.add(null);          
                result2.add(list2.get(sideIndex-1)); 
                continue;
            }
            
            if(sideIndex == 0){
                topIndex--;
                if(topIndex == 0)
                    break;
                result1.add(list1.get(topIndex-1));
                result2.add(null); 
                continue;
            }
            
            float c2 = Math.max(0, cost.getCost(list1.get(topIndex-1), list2.get(sideIndex-1)));   
            int maxIndex = getMaxIndex(d[topIndex-1][sideIndex-1] + c2,d[topIndex-1][sideIndex] - cost.getGapOpening(), d[topIndex][sideIndex-1] - cost.getGapOpening());  
                    
                    
            if(maxIndex == 1){
                topIndex--;
                sideIndex--;
                if(sideIndex > 0 && topIndex > 0){
                    result1.add(list1.get(topIndex-1));
                    result2.add(list2.get(sideIndex-1));   
                    continue;
                }
                if(sideIndex == 0 && topIndex > 0){
                    result1.add(list1.get(topIndex-1));
                    result2.add(null);   
                    continue;
                }
                if(sideIndex > 0 && topIndex == 0){
                    result1.add(null);
                    result2.add(list2.get(sideIndex-1));     
                    continue;
                }
                if(sideIndex > 0 && topIndex == 0){
                    break;
                }
                
            }
            if(maxIndex == 2){
                topIndex--;
                result1.add(list1.get(topIndex-1));
                result2.add(result2.size()-1, null);  
                continue;
            }
            if(maxIndex == 3){
                sideIndex--;
                result1.add(result1.size()-1, null);
                result2.add(list2.get(sideIndex-1)); 
                continue;
            }    
        }
        
        Collections.reverse(result1);
        Collections.reverse(result2);
     
        similarity = d[n][m];  
           
        mainFrame.setComparationMode(MainFrame.VisualisationType.NEEDLEMANWUNSCH);
        
        mainFrame.getFirstScrollPane().getBottomProjectionPanel().setComparisonDescriptors(result1);
        mainFrame.getFirstScrollPane().getSideProjectionPanel().setComparisonDescriptors(result1);
        mainFrame.getSecondScrollPane().getBottomProjectionPanel().setComparisonDescriptors(result2);
        mainFrame.getSecondScrollPane().getSideProjectionPanel().setComparisonDescriptors(result2);
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

        if(exitVisualization){
            mainFrame.setComparationMode(MainFrame.VisualisationType.NONE);
        }
        
        mainFrame.HideVisualizationProgressBar();
    }
    
    
}
