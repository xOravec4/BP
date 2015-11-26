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
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

/**
 *
 * @author Tomas
 */
public class ProjectionPanel extends JPanel implements MouseMotionListener{
    
        /** Image size - width & height in pixels */
    private int width;
    private int height;
    private int xPanelShift = 0;
    private int yPanelShift = 0;
    private double zoomScale = 1;
    
    private int imageHorizontalScrollValue = 0;
    private int imageVerticalScrollValue = 0;
    private float zoom = 1;
    
    private String position = "bot";
    private enum Position {horizontal, verical};
    private Position pos;
    private int pointSize = 5;
    
    
    Map<ObjectFeature, Float> dataMap = new HashMap<ObjectFeature, Float>();
    List<ObjectFeature> dataList = new ArrayList<ObjectFeature>();
    
    public ProjectionPanel()
    {   
        
        //this.setBackground(Color.white);
        //this.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
        addMouseMotionListener(this);
        
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e){
            
            }
            
             public void mousePressed(MouseEvent e){}
             
             public void mouseReleased(MouseEvent e){}
             
             public void mouseEntered(MouseEvent e) {}
             
             public void mouseExited(MouseEvent e) {
                 getProjectionGlassPane().set(null, 0, getParentImageScrollPane().getImagePanel().getImage());
             }
             
             public void mouseMoved(MouseEvent e) {
              }
             
             public void mouseDragged(MouseEvent e) {
              }
            
        });
    }
    
    
    
    public void setData(Map<ObjectFeature, Float> dataMap)
    {
        this.dataMap = new HashMap(dataMap);
        this.dataList = null;
    }
    
    public void setData(List<ObjectFeature> dataList)
    {
        this.dataList = new ArrayList(dataList);
        this.dataMap = null;
    }
    
    public void setXPanelShift(int shift) throws IOException{
        xPanelShift = shift;
        throw new IOException();
        /**/
    }
    public void setYPanelShift(int shift)  {
        yPanelShift = shift;
        /**/
    }
    
    public void zoomIN()
    {
        //zoomScale *= 1.2;
    
    }
    
    public void setPosition(String pos){
        position = pos;
    }
    
    private void checkPosition(){
        if(this.getWidth() > this.getHeight())
            pos = Position.horizontal;
        else
            pos = Position.verical;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        checkPosition();
         super.paintComponent(g);
         
        if (dataMap != null) {

            g.setColor(Color.RED);

            if (pos == Position.horizontal) {
                for (Map.Entry<ObjectFeature, Float> entry : dataMap.entrySet()) {
                    ObjectFeature key = entry.getKey();
                    float value = entry.getValue();
                    g.fillOval((int) (this.getWidth() * value), 1, pointSize, pointSize);
                }
            } 
            else {
                for (Map.Entry<ObjectFeature, Float> entry : dataMap.entrySet()) {
                    ObjectFeature key = entry.getKey();
                    float value = entry.getValue();
                    g.fillRect(1, (int) (this.getHeight() * value), pointSize, pointSize);
                }
            }
        }
              
        else if (dataList != null) {
            g.setColor(Color.RED);
            if (pos == Position.horizontal) {
                float step = (float) this.getWidth() / (float) dataList.size();
                for (int i = 0; i < dataList.size(); i++) {
                    if (dataList.get(i) != null) {
                        g.fillRect((int) ((float) i * step), 1, pointSize, pointSize);
                        // System.out.println("Debug: "+ step +" "+ this.getWidth() +" "+ dataList.size());
                    }else{
                        g.setColor(Color.black);
                        g.fillRect((int) ((float) i * step), 1, pointSize, pointSize);
                        g.setColor(Color.red);
                    }
                        
                }
            } 
            else {
                //System.out.println("im here");
                float step = (float) this.getHeight() / (float) dataList.size();
                for (int i = 0; i < dataList.size(); i++) {
                    if (dataList.get(i) != null) {
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
       /* if(position == "bot"){
        ImageScrollPane pane = (ImageScrollPane) getParent().getParent();
        return pane;
        }
        else{
          ImageScrollPane pane = (ImageScrollPane) getParent();  
          return pane;
        }*/
        ImageScrollPane pane = (ImageScrollPane) getParent();  
          return pane;
    }
    
    public ProjectionGlassPane getProjectionGlassPane() {
        ProjectionGlassPane pane =  (ProjectionGlassPane) getParentImageScrollPane().getParentMainFrame().getGlassPane();
        return pane;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.println("Mouse dragged (" + e.getX() + ',' + e.getY() + ')');
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
        if(pos == Position.horizontal){  //bottom panel
            if (dataMap != null) {
                for (Map.Entry<ObjectFeature, Float> entry : dataMap.entrySet()) {
                    ObjectFeature key = entry.getKey();
                    float valuee = entry.getValue();
                    float value = this.getWidth() * valuee;
                    if (  e.getX() >= value && e.getX() <= value +pointSize) {
                        getProjectionGlassPane().setActivePanel(this.getParentImageScrollPane());
                        getProjectionGlassPane().set(key, value, getParentImageScrollPane().getImagePanel().getImage());
                        return;
                    }
                }
            }
            else if(dataList != null){
                int dataListSize = dataList.size();
                float step = (float) this.getWidth() / (float) dataList.size();
                for(int i=0; i< dataListSize;i++){
                    float a =  ((float) i * step);
                    if (  e.getX() >= a && e.getX() <= a + pointSize) {
                      getProjectionGlassPane().setActivePanel(this.getParentImageScrollPane());
                      getProjectionGlassPane().set(dataList.get(i), a, i, getParentImageScrollPane().getImagePanel().getImage());
                      return;
                  }
                }
            }
        }
        
        else{
            if(dataMap != null)
            for (Map.Entry<ObjectFeature, Float> entry : dataMap.entrySet()) {
                ObjectFeature key = entry.getKey();
                float valuee = entry.getValue();
                float value = this.getHeight() * valuee;
                if(value > e.getY() - pointSize && value < e.getY()){
                    //getProjectionGlassPane().setProjectionPanelPosition("top");
                    //getProjectionGlassPane().setActivePanel(getParentImageScrollPane().getOrder());
                    getProjectionGlassPane().setActivePanel(this.getParentImageScrollPane());
                    getProjectionGlassPane().set(key, value, getParentImageScrollPane().getImagePanel().getImage());  
                    return;
                }
            }
            if(dataList != null ){
                int dataListSize = dataList.size();
                float step = (float) this.getHeight() / (float) dataList.size();
                for(int i=0; i< dataListSize;i++){
                    float a =  ((float) i * step);
                  if (  e.getY() >= a && e.getY() <= a + pointSize) {
                      
                      getProjectionGlassPane().setActivePanel(this.getParentImageScrollPane());
                      getProjectionGlassPane().set(dataList.get(i), a, i, getParentImageScrollPane().getImagePanel().getImage());                      
                      return;
                  }
                }
            }
            
            
        }
        
         getProjectionGlassPane().set(null, 0, null);
        
        
        
       //return; 
        
        
        
        
      //  System.out.println("Mouse moved (" + e.getX() + ',' + e.getY() + ')');
       /* 
         for (Map.Entry<ObjectFeature, Float> entry : dataMap.entrySet()) {
                ObjectFeature key = entry.getKey();
                float valuee = entry.getValue();
                
                if(this.getWidth() > this.getHeight()){
                    
                    float value = this.getWidth() * valuee;
                    if(value > e.getX() && value < e.getX()+6){
                        System.out.println("aaaaaaaaaaaaaaaa");
                        getProjectionGlassPane().setProjectionPanelPosition("bot");
                        getProjectionGlassPane().set(key, value, getParentImageScrollPane().getImagePanel().getImage());
                        return; 
                    }      
                   
                }
                
                else{
                    float value = this.getHeight() * valuee;
                    if(value > e.getY() && value < e.getY()+6){
                        getProjectionGlassPane().setProjectionPanelPosition("top");
                        getProjectionGlassPane().set(key, value, getParentImageScrollPane().getImagePanel().getImage());  
                    return;
                    }
                    
                }
                
          */      
                
                
                
                
                
                
                /*
                
                
                 float value = this.getWidth() * valuee;
                if(value > e.getX() && value < e.getX()+6){
                    //System.out.println("Nasiel sa deskriptor");
                    if (position.equals("bot")) {
                         getProjectionGlassPane().setProjectionPanelPosition("bot");
                         getProjectionGlassPane().set(key, value, getParentImageScrollPane().getImagePanel().getImage());
                    }
                    else{
                        System.out.println("sending" + value);
                         getProjectionGlassPane().setProjectionPanelPosition("top");
                         getProjectionGlassPane().set(key, value, getParentImageScrollPane().getImagePanel().getImage());    
                    }
                 return;   
                }*/
               // g.fillRect((int) value, 1, 5, 5);
       /// }
        // getProjectionGlassPane().set(null, 0, getParentImageScrollPane().getImagePanel().getImage());

    }
    
    public ObjectFeature getDescriptorAt(int i){
        return dataList.get(i);
    }
    
    public int getDescriptorValueAt(int i){
        if(pos == Position.horizontal){
            float step = (float) this.getWidth() / (float) dataList.size();
            return (int)((float) i * step);
        }
        else{
           float step = (float) this.getHeight() / (float) dataList.size();
           return (int)((float) i * step); 
        }
    }
    
    public void clear(){
        dataList = null;
        dataMap = null;
        revalidate();
        repaint();
    }
    

}
