/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */ 
package cz.muni.fi.xlabuda;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JPanel;
import messif.objects.impl.ObjectFeature;
import java.util.HashMap;
import java.util.List;

/**
 * Panel for visualizations of projections and results of sequence matching algorithms.
 *
 * @author Tomas Oravec
 * @version 1.0
 */
public class ProjectionPanel extends JPanel implements MouseMotionListener{
    
    
    private enum Position {horizontal, verical};
    private Position pos;
    private int pointSize = 5;
    
    
    Map<ObjectFeature, Float> projectionDescriptors = new HashMap<ObjectFeature, Float>();
    List<ObjectFeature> comparisonDescriptors = new ArrayList<ObjectFeature>();
    
    public ProjectionPanel()
    {   

        addMouseMotionListener(this);
        
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e){
            
            }
            
             public void mousePressed(MouseEvent e){}
             
             public void mouseReleased(MouseEvent e){}
             
             public void mouseEntered(MouseEvent e) {}
             
             public void mouseExited(MouseEvent e) {
                 if(comparisonDescriptors != null ){
                    getProjectionGlassPane().setHooveredComparisonDescriptor(null);
                    return;
                }
                else if(projectionDescriptors != null ){
                    getProjectionGlassPane().setHooveredProjectionDescriptor(null,0);
                    return;
                }
             }
             
             public void mouseMoved(MouseEvent e) {
              }
             
             public void mouseDragged(MouseEvent e) {
              }
            
        });
    }
    
    
    
    public void setProjectionDescriptors(Map<ObjectFeature, Float> dataMap)
    {
        this.projectionDescriptors = new HashMap(dataMap);
        this.comparisonDescriptors = null;
    }
    
    public void setComparisonDescriptors(List<ObjectFeature> dataList)
    {
        this.comparisonDescriptors = new ArrayList(dataList);
        this.projectionDescriptors = null;
    }
    

    
    private void checkPosition(){
        if(this.getWidth() > this.getHeight())
            pos = Position.horizontal;
        else
            pos = Position.verical;
    }
    
    
    //paints projections points or results of sequence mathing algorithms, depending on what is set.
    @Override
    public void paintComponent(Graphics g) {
        checkPosition();
         super.paintComponent(g);
         
        if (projectionDescriptors != null) {

            g.setColor(Color.RED);

            if (pos == Position.horizontal) {
                for (Map.Entry<ObjectFeature, Float> entry : projectionDescriptors.entrySet()) {
                    ObjectFeature key = entry.getKey();
                    float value = entry.getValue();
                    g.fillOval((int) (this.getWidth() * value), 1, pointSize, pointSize);
                }
            } 
            else {
                for (Map.Entry<ObjectFeature, Float> entry : projectionDescriptors.entrySet()) {
                    ObjectFeature key = entry.getKey();
                    float value = entry.getValue();
                    g.fillRect(1, (int) (this.getHeight() * value), pointSize, pointSize);
                }
            }
        }
              
        else if (comparisonDescriptors != null) {
            g.setColor(Color.RED);
            ImageScrollPane otherISP = getParentImageScrollPane().getParentMainFrame().getOtherISP(getParentImageScrollPane());
            if (pos == Position.horizontal) {
                float step = (float) this.getWidth() / (float) comparisonDescriptors.size();
                for (int i = 0; i < comparisonDescriptors.size(); i++) {
                    if (comparisonDescriptors.get(i) != null) {  
                        if(otherISP.getBottomProjectionPanel().descriptorExistsAt(i)){
                            g.setColor(Color.green);
                        }
                        else{
                            g.setColor(Color.red);
                        }
                        g.fillRect((int) ((float) i * step), 1, pointSize, pointSize);
                    }else{   
                        g.setColor(Color.black);
                        g.fillRect((int) ((float) i * step), 1, pointSize, pointSize);
                        g.setColor(Color.red);
                    }        
                }

            } 
            else {
                float step = (float) this.getHeight() / (float) comparisonDescriptors.size();
                for (int i = 0; i < comparisonDescriptors.size(); i++) {
                    if (comparisonDescriptors.get(i) != null) {
                        if(otherISP.getBottomProjectionPanel().descriptorExistsAt(i)){
                            g.setColor(Color.green);
                        }
                        else{
                            g.setColor(Color.red);
                        }
                        g.fillRect(1, (int) ((float) i * step), pointSize, pointSize);
                    }
                    else{
                        g.setColor(Color.black);
                        g.fillRect(1, (int) ((float) i * step), pointSize, pointSize);
                        g.setColor(Color.red); 
                    }
                }
            }

        }
    }
    

    
    public ImageScrollPane getParentImageScrollPane() {
        ImageScrollPane pane = (ImageScrollPane) getParent();  
          return pane;
    }
    
    public ProjectionGlassPane getProjectionGlassPane() {
        ProjectionGlassPane pane =  (ProjectionGlassPane) getParentImageScrollPane().getParentMainFrame().getGlassPane();
        return pane;
    }
    
    public MainFrame getMainFrame() {
        return getParentImageScrollPane().getParentMainFrame();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    //finds hoovered descriptor and sends it to projectionGlassPane
    @Override
    public void mouseMoved(MouseEvent e) {
        
        if(pos == Position.horizontal){  
            if (projectionDescriptors != null) {
                for (Map.Entry<ObjectFeature, Float> entry : projectionDescriptors.entrySet()) {
                    ObjectFeature key = entry.getKey();
                    float valuee = entry.getValue();
                    float value = this.getWidth() * valuee;
                    if (  e.getX() >= value && e.getX() <= value +pointSize) {
                        getProjectionGlassPane().setActivePanel(this.getParentImageScrollPane());
                        getProjectionGlassPane().setHooveredProjectionDescriptor(key, value);
                        return;
                    }
                }
            }
            else if(comparisonDescriptors != null){

                int dataListSize = comparisonDescriptors.size();
                float step = (float) this.getWidth() / (float) comparisonDescriptors.size();
                for(int i=0; i< dataListSize;i++){
                    float a =  ((float) i * step);
                    if (  e.getX() >= a && e.getX() <= a + pointSize) {
                      if(comparisonDescriptors.get(i) == null)
                          continue;
                      getProjectionGlassPane().setActivePanel(this.getParentImageScrollPane());
                      getProjectionGlassPane().setHooveredComparisonDescriptor(comparisonDescriptors.get(i));
                      return;
                  }
                }
                for(int i=0; i< dataListSize;i++){
                    float a =  ((float) i * step);
                    if (  e.getX() >= a && e.getX() <= a + pointSize) {
                      getProjectionGlassPane().setActivePanel(this.getParentImageScrollPane());
                      getProjectionGlassPane().setHooveredComparisonDescriptor(comparisonDescriptors.get(i));
                      return;
                  }
                }
            }
        }
        
        else{
            if(projectionDescriptors != null)
            for (Map.Entry<ObjectFeature, Float> entry : projectionDescriptors.entrySet()) {
                ObjectFeature key = entry.getKey();
                float valuee = entry.getValue();
                float value = this.getHeight() * valuee;
                if(value > e.getY() - pointSize && value < e.getY()){
                    getProjectionGlassPane().setActivePanel(this.getParentImageScrollPane());
                    getProjectionGlassPane().setHooveredProjectionDescriptor(key, value);  
                    return;
                }
            }
            if(comparisonDescriptors != null ){
                int dataListSize = comparisonDescriptors.size();
                float step = (float) this.getHeight() / (float) comparisonDescriptors.size();
                for(int i=0; i< dataListSize;i++){
                    float a =  ((float) i * step);
                  if (  e.getY() >= a && e.getY() <= a + pointSize) {
                      if(comparisonDescriptors.get(i) == null)
                          continue;
                      getProjectionGlassPane().setActivePanel(this.getParentImageScrollPane());
                      getProjectionGlassPane().setHooveredComparisonDescriptor(comparisonDescriptors.get(i));                      
                      return;
                  }
                }
                for(int i=0; i< dataListSize;i++){
                    float a =  ((float) i * step);
                  if (  e.getY() >= a && e.getY() <= a + pointSize) {
                      
                      getProjectionGlassPane().setActivePanel(this.getParentImageScrollPane());
                      getProjectionGlassPane().setHooveredComparisonDescriptor(comparisonDescriptors.get(i));                      
                      return;
                  }
                }
            }
            
            
        }
        
        if(comparisonDescriptors != null ){
            getProjectionGlassPane().setHooveredComparisonDescriptor(null);
            return;
        }
        else if(projectionDescriptors != null ){
            getProjectionGlassPane().setHooveredProjectionDescriptor(null,0);
            return;
        }
        
        getProjectionGlassPane().setHooveredProjectionDescriptor(null,0);
        
        
      

    }
    
    public ObjectFeature getComparisonDescriptorAt(int i){
        if(comparisonDescriptors == null)
            return null;
        if(comparisonDescriptors.size()-1 <i && i >0)
            return null;
        return comparisonDescriptors.get(i);
    }
    
    private int getComparisonDescriptorValueAt(int i){
        if(pos == Position.horizontal){
            float step = (float) this.getWidth() / (float) comparisonDescriptors.size();
            return (int)((float) i * step);
        }
        else{
           float step = (float) this.getHeight() / (float) comparisonDescriptors.size();
           return (int)((float) i * step); 
        }
    }
    
    public int getComparisonDescriptorValueAt(ObjectFeature o){
        int index = comparisonDescriptors.indexOf(o);
        if(index != -1){
            return getComparisonDescriptorValueAt(index);
        }
        return 0;
    }
    
    public int getComparisonDescriptorIndex(ObjectFeature o){
        if(comparisonDescriptors == null)
            return -1;
        
        return comparisonDescriptors.indexOf(o);
    }
    
    public void clear(){
        comparisonDescriptors = null;
        projectionDescriptors = null;
        revalidate();
        repaint();
    }
    
    public boolean descriptorExistsAt(int i){
        if(comparisonDescriptors == null)
            return false;
        
        if(comparisonDescriptors.size() < i)
            return false;
        
        if(comparisonDescriptors.get(i) == null)
            return false;
        
        return true;
                
    }
    
    public List<ObjectFeature> getComparisonDescriptors(){
        if(comparisonDescriptors != null)
            return new ArrayList<ObjectFeature>(comparisonDescriptors);
        else
            return null;
    }
    

}
