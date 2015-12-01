/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.xlabuda;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import messif.objects.impl.ObjectFeature;
import messif.objects.util.SequenceMatchingCost;

/**
 *
 * @author Tomas
 */
public class ProjectionGlassPane extends JComponent {
    
    ObjectFeature descriptor;
    float value;
    String projectionPanelPosition;
    
    java.awt.image.BufferedImage img;
    int order;
    
    ImageScrollPane imageScrollPane;
    ImageScrollPane secondImageScrollPane;
    ImageScrollPane activeImageScrollPane;
    MainFrame mainFrame;
    
    private boolean antialiasing = false;
    private Color descriptorColor = Color.RED;
    
    private enum Mode {singleImageProjection, twoImageVisualization};
    Mode mode;
    
    int activePanel;
    
    public ProjectionGlassPane(ImageScrollPane imageScrollPane, ImageScrollPane secondImageScrollPane){ 
        this.imageScrollPane = imageScrollPane;
        this.secondImageScrollPane = secondImageScrollPane;
        mainFrame = imageScrollPane.getParentMainFrame();
        getParent();
       // super(null);
    }
    
    public void setActivePanel(int i){
        activePanel = i;
    }
    
    public void setProjectionPanelPosition(String pos){
        projectionPanelPosition = pos;
    }
    
    public void setActivePanel(ImageScrollPane isp){
        activeImageScrollPane = isp;
    }
    
    public void test(){
        System.out.println("working");
    }
    
    public void set(ObjectFeature descriptor, float value, java.awt.image.BufferedImage img){
        mode = Mode.singleImageProjection;
        
        this.descriptor = descriptor;
        this.value = value;
        this.img = img;
        repaint();
        
    }
    
    public void set(ObjectFeature descriptor, float value, int order, java.awt.image.BufferedImage img){
        mode = Mode.twoImageVisualization;
        this.descriptor = descriptor;
        this.value = value;
        this.img = img;
        this.order = order;
        repaint();
        
    }
    
    public Rectangle getViewportRange(ImageScrollPane scrollPane) {
            double zoomScale = scrollPane.getImagePanel().getZoomScale();
            Rectangle rect = scrollPane.getScrollPane().getViewport().getViewRect();
            return new Rectangle(
                    (int)(rect.getX() / zoomScale),
                    (int) (rect.getY() / zoomScale),
                    (int) (rect.getWidth() / zoomScale),
                    (int) (rect.getHeight() / zoomScale));
        }
    
    public boolean isInViewPort(ObjectFeature of, ImageScrollPane isp){
        
        Rectangle rec = getViewportRange(isp);
        return rec.contains(new Point2D.Double(of.getX()*isp.getImagePanel().getImage().getWidth(), of.getY()*isp.getImagePanel().getImage().getHeight()));
    }
    
    private Point2D getDescriptorPosition(ImageScrollPane isp, ObjectFeature descriptor ){
        
        int xcord = Double.valueOf(isp.getImagePanel().getImage().getWidth() * descriptor.getX()).intValue();
        int ycord = Double.valueOf(isp.getImagePanel().getImage().getHeight() * descriptor.getY()).intValue();
        
        int xshift = isp.getScrollPane().getHorizontalScrollBar().getValue();
        int yshift = isp.getScrollPane().getVerticalScrollBar().getValue();
        
        xcord *= isp.getImagePanel().getZoomScale();
        ycord *= isp.getImagePanel().getZoomScale();
        
        xcord -= xshift;
        ycord -= yshift;
        
        if(isp == imageScrollPane){
            xcord += imageScrollPane.getScrollPane().getX();
        }
        if(isp == secondImageScrollPane){
            xcord += imageScrollPane.getScrollPane().getX();
            xcord += imageScrollPane.getWidth();
        }
        
        ycord += imageScrollPane.getScrollPane().getY() + imageScrollPane.getY() + 22;
        ycord += imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight();

        return new Point2D.Float(xcord,ycord);
    }
    
    public ImageScrollPane getOtherISP(ImageScrollPane isp){
        if(isp == imageScrollPane)
            return secondImageScrollPane;
        else
            return imageScrollPane;
    }
    
    private int getSecondPaneAlligment(ImageScrollPane isp){
        if(isp == imageScrollPane){
            return 0;
        }
        return imageScrollPane.getWidth();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(descriptorColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawString("Glasspane", 0, 455);
        
        if (antialiasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        if (descriptor != null) {

            /*
             int xcord = Double.valueOf(secondImageScrollPane.getImagePanel().getImage().getWidth() * descriptor.getX()).intValue();
             int ycord = Double.valueOf(img.getHeight() * descriptor.getY()).intValue();


             int xshift = secondImageScrollPane.getScrollPane().getHorizontalScrollBar().getValue();
             int yshift = secondImageScrollPane.getScrollPane().getVerticalScrollBar().getValue();


             xcord *= secondImageScrollPane.getImagePanel().getZoomScale();
             ycord *= secondImageScrollPane.getImagePanel().getZoomScale();

             xcord -= xshift;
             ycord -= yshift;

             ycord += 160;
             xcord += 15;

             g2d.setStroke(new BasicStroke(3));
             int posun = imageScrollPane.getWidth();
             if(projectionPanelPosition == "bot"){
             g2d.drawLine(xcord + posun, ycord ,(int) value + posun, imageScrollPane.getHeight()+5);
                    
             ObjectFeature desc2 = imageScrollPane.getBottomProjectionPanel().getDescriptorAt(order);
             if(desc2 == null)return;
                    
             int xcord2 = Double.valueOf(imageScrollPane.getImagePanel().getImage().getWidth() * desc2.getX()).intValue();
             int ycord2 = Double.valueOf(imageScrollPane.getImagePanel().getImage().getHeight() * desc2.getY()).intValue();
             int xshift2 = imageScrollPane.getScrollPane().getHorizontalScrollBar().getValue();
             int yshift2 = imageScrollPane.getScrollPane().getVerticalScrollBar().getValue();

             xcord2 *= imageScrollPane.getImagePanel().getZoomScale();
             ycord2 *= imageScrollPane.getImagePanel().getZoomScale();

             xcord2 -= xshift;
             ycord2 -= yshift;

             ycord2 += 160;
             xcord2 += 15;
                    
             g2d.drawLine(xcord2, ycord2 ,(int) value, imageScrollPane.getHeight()+5);
             g2d.drawLine(xcord2, ycord2 ,xcord + posun, ycord );
             }
             else
             {
             g2d.drawLine(xcord + posun , ycord, 10 + posun,(int) value+ 160 );   
             }
             */
            if (mode == Mode.singleImageProjection) {
                
                if(!isInViewPort(descriptor,activeImageScrollPane )){
                    System.err.println("NOT IN VIEWPORT");
                    return;
                }
                else
                    System.err.println("IN VIEWPORT");
                    
                System.out.println("SIP:" + value);
                Point2D descPos = getDescriptorPosition(activeImageScrollPane, descriptor);
                if (activeImageScrollPane.getBottomProjectionPanel().isVisible()) {
                    g2d.drawLine((int) descPos.getX(),
                            (int) descPos.getY(),
                            (int) value + activeImageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(activeImageScrollPane),
                            activeImageScrollPane.getHeight() + 5
                    );
                }
                if (activeImageScrollPane.getSideProjectionPanel().isVisible()) {
                    if (activeImageScrollPane.getSideProjectionPanel().isVisible()) {
                        value += activeImageScrollPane.getSideProjectionPanel().getWidth();
                    }
                    g2d.drawLine((int) descPos.getX(),
                            (int) descPos.getY(),
                            10 + getSecondPaneAlligment(activeImageScrollPane),
                            (int) value + activeImageScrollPane.getScrollPane().getY() + activeImageScrollPane.getY() + 22
                    );
                }
            } else if (mode == Mode.twoImageVisualization) {
                ObjectFeature descriptor2 = getOtherISP(activeImageScrollPane).getBottomProjectionPanel().getDescriptorAt(order);
                Point2D descPos = getDescriptorPosition(activeImageScrollPane, descriptor);
                if (descriptor2 == null) {
                    mainFrame.SetNWSWCurrentSimilarity(null);
                    if(!(isInViewPort(descriptor,activeImageScrollPane ) )){
                        return;
                    }
                    if(activeImageScrollPane.getBottomProjectionPanel().isVisible())
                    g2d.drawLine((int) descPos.getX(),
                            (int) descPos.getY(),
                            (int) value + imageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(activeImageScrollPane),
                            imageScrollPane.getHeight() + 5 + + imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                    );
                    else
                    g2d.drawLine((int) descPos.getX(),
                            (int) descPos.getY(),
                            10 + imageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(activeImageScrollPane),
                            (int) value + activeImageScrollPane.getScrollPane().getY() + activeImageScrollPane.getY() + 22 + + imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                    );
                    return;
                }
                SequenceMatchingCost cost = SequenceMatchingCost.SIFT_DEFAULT;
                mainFrame.SetNWSWCurrentSimilarity(cost.getCost(descriptor, descriptor2));
                Point2D descPos2 = getDescriptorPosition(getOtherISP(activeImageScrollPane), descriptor2);
                if (activeImageScrollPane.getBottomProjectionPanel().isVisible()) {
                    System.out.println("a");
                    if((isInViewPort(descriptor,activeImageScrollPane ) )){
                        g2d.drawLine((int) descPos.getX(),
                            (int) descPos.getY(),
                            (int) value + imageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(activeImageScrollPane),
                            imageScrollPane.getHeight() + 5 + imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                    );
                    }
                    
                } else if (activeImageScrollPane.getSideProjectionPanel().isVisible()) {
                    if((isInViewPort(descriptor,activeImageScrollPane ) )){
                        System.out.println("b");
                        g2d.drawLine((int) descPos.getX(),
                                (int) descPos.getY(),
                                10 + imageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(activeImageScrollPane),
                                (int) value + activeImageScrollPane.getScrollPane().getY() + activeImageScrollPane.getY() + 22 + imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                        );
                    }

                }
                
                if (getOtherISP(activeImageScrollPane).getBottomProjectionPanel().isVisible()) {
                    int value2 = getOtherISP(activeImageScrollPane).getBottomProjectionPanel().getDescriptorValueAt(order);
                    System.out.println("c");
                    if((isInViewPort(descriptor2,getOtherISP(activeImageScrollPane) ) )){
                        g2d.drawLine((int) descPos2.getX(),
                            (int) descPos2.getY(),
                            (int) value2 + imageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(getOtherISP(activeImageScrollPane)),
                            imageScrollPane.getHeight() + 5 + imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                        );
                    }
                    
                } else if (getOtherISP(activeImageScrollPane).getSideProjectionPanel().isVisible()) {
                    int value2 = getOtherISP(activeImageScrollPane).getSideProjectionPanel().getDescriptorValueAt(order);
                    System.out.println("d");
                    if((isInViewPort(descriptor2,getOtherISP(activeImageScrollPane) ) )){
                        g2d.drawLine((int) descPos2.getX(),
                            (int) descPos2.getY(),
                            10 + getSecondPaneAlligment(getOtherISP(activeImageScrollPane)),
                            (int) value2 + activeImageScrollPane.getScrollPane().getY() + activeImageScrollPane.getY() + 22 + imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                    );
                    }
                    
                }
                
                if(isInViewPort(descriptor,activeImageScrollPane ) && isInViewPort(descriptor2, getOtherISP(activeImageScrollPane)) ){
                    g2d.drawLine((int) descPos.getX(),
                        (int) descPos.getY(),
                        (int) descPos2.getX(),
                        (int) descPos2.getY()
                    );
                }
                

            }

        }
        else{
                mainFrame.SetNWSWCurrentSimilarity(null);
        }

        /*
        if(descriptor != null){
            
            if(activePanel == 2){
            
                int xcord = Double.valueOf(secondImageScrollPane.getImagePanel().getImage().getWidth() * descriptor.getX()).intValue();
                int ycord = Double.valueOf(img.getHeight() * descriptor.getY()).intValue();


                int xshift = secondImageScrollPane.getScrollPane().getHorizontalScrollBar().getValue();
                int yshift = secondImageScrollPane.getScrollPane().getVerticalScrollBar().getValue();


                xcord *= secondImageScrollPane.getImagePanel().getZoomScale();
                ycord *= secondImageScrollPane.getImagePanel().getZoomScale();

                xcord -= xshift;
                ycord -= yshift;

                 ycord += 160;
                xcord += 15;

                g2d.setStroke(new BasicStroke(2));
                int posun = imageScrollPane.getWidth();
                if(projectionPanelPosition == "bot"){
                    g2d.drawLine(xcord + posun, ycord ,(int) value + posun, imageScrollPane.getHeight()+5);
                }
                else
                {
                    g2d.drawLine(xcord + posun , ycord, 10 + posun,(int) value+ 160 );   
                }
            }
            else if(activePanel == 1){
                int xcord = Double.valueOf(imageScrollPane.getImagePanel().getImage().getWidth() * descriptor.getX()).intValue();
                int ycord = Double.valueOf(img.getHeight() * descriptor.getY()).intValue();


                int xshift = imageScrollPane.getScrollPane().getHorizontalScrollBar().getValue();
                int yshift = imageScrollPane.getScrollPane().getVerticalScrollBar().getValue();


                xcord *= imageScrollPane.getImagePanel().getZoomScale();
                ycord *= imageScrollPane.getImagePanel().getZoomScale();

                xcord -= xshift;
                ycord -= yshift;

                 ycord += 160;
                xcord += 15;

                g2d.setStroke(new BasicStroke(2));

                if(projectionPanelPosition == "bot"){
                    g2d.drawLine(xcord, ycord,(int) value, imageScrollPane.getHeight()+5);
                }
                else
                {
                    g2d.drawLine(xcord, ycord, 10,(int) value+ 160 );   
                }
            }
        }
        */
    }
    public void setAntialiasing(boolean bool){
        antialiasing = bool;
    }
    
    public Color getHooverColor(){
        return descriptorColor;
    }
    
    public void setHooverColor(Color color){
        descriptorColor = color;
    }
}
