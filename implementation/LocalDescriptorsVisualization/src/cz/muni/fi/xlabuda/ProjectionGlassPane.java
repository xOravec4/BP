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
import java.util.Map;
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
    ImageScrollPane activeImageScrollPane = imageScrollPane;
    MainFrame mainFrame;

    private boolean antialiasing = false;
    private Color descriptorColor = Color.RED;

    private int pointSize = 5;
    private boolean showAll = false;
    private boolean pause = false;
    private boolean highlitedDescriptor = false;

    private enum Mode {
        singleImageProjection, twoImageVisualization;
    };
    Mode mode;
    
    private enum VisualizationMode{
        hover, threeLines, oneLine;
    };
    VisualizationMode visualizationMode = VisualizationMode.threeLines;

    int activePanel;

    public ProjectionGlassPane(ImageScrollPane imageScrollPane, ImageScrollPane secondImageScrollPane, Color color) {
        this.imageScrollPane = imageScrollPane;
        this.secondImageScrollPane = secondImageScrollPane;
        mainFrame = imageScrollPane.getParentMainFrame();
        descriptorColor = color;
        getParent();
        // super(null);
    }

    public void setActivePanel(int i) {
        activePanel = i;
    }

    public void setProjectionPanelPosition(String pos) {
        projectionPanelPosition = pos;
    }

    public void setActivePanel(ImageScrollPane isp) {
        activeImageScrollPane = isp;
    }

    public void test() {
        System.out.println("working");
    }

    public void set(ObjectFeature descriptor, float value, java.awt.image.BufferedImage img) {
        
        //mode = Mode.singleImageProjection;
        //visualizationMode = VisualizationMode.hover;
        this.descriptor = descriptor;
        this.value = value;
        this.img = img;
        
        if(descriptor == null)
            highlitedDescriptor = false;
        
        repaint();

    }

    public void set(ObjectFeature descriptor, float value, int order, java.awt.image.BufferedImage img) {
        
        mode = Mode.twoImageVisualization;
        
        if(visualizationMode != VisualizationMode.hover)
            highlitedDescriptor = true;
        
        this.descriptor = descriptor;
        this.value = value;
        this.img = img;
        this.order = order;
        
        if(descriptor == null)
            highlitedDescriptor = false;
        
        repaint();

    }
    
    public void setAll(){
        
    }

    public Rectangle getViewportRange(ImageScrollPane scrollPane) {
        double zoomScale = scrollPane.getImagePanel().getZoomScale();
        Rectangle rect = scrollPane.getScrollPane().getViewport().getViewRect();
        return new Rectangle(
                (int) (rect.getX() / zoomScale),
                (int) (rect.getY() / zoomScale),
                (int) (rect.getWidth() / zoomScale),
                (int) (rect.getHeight() / zoomScale));
    }

    public boolean isInViewPort(ObjectFeature of, ImageScrollPane isp) {

        Rectangle rec = getViewportRange(isp);
        return rec.contains(new Point2D.Double(of.getX() * isp.getImagePanel().getImage().getWidth(), of.getY() * isp.getImagePanel().getImage().getHeight()));
    }

    private Point2D getDescriptorPosition(ImageScrollPane isp, ObjectFeature descriptor) {

        int xcord = Double.valueOf(isp.getImagePanel().getImage().getWidth() * descriptor.getX()).intValue();
        int ycord = Double.valueOf(isp.getImagePanel().getImage().getHeight() * descriptor.getY()).intValue();

        int xscroll = isp.getScrollPane().getHorizontalScrollBar().getValue();
        int yscroll = isp.getScrollPane().getVerticalScrollBar().getValue();

        int xshift = isp.getImagePanel().getXPanelShift();
        int yshift = isp.getImagePanel().getYPanelShift();

        xcord *= isp.getImagePanel().getZoomScale();
        ycord *= isp.getImagePanel().getZoomScale();
        //System.out.println("ASD shift " + xshift + " " + yshift);
        xcord -= xscroll;
        ycord -= yscroll;
        xcord += xshift;
        ycord += yshift;

        if (isp == imageScrollPane) {
            xcord += imageScrollPane.getScrollPane().getX();
        }
        if (isp == secondImageScrollPane) {
            xcord += imageScrollPane.getScrollPane().getX();
            xcord += imageScrollPane.getWidth();
        }

        //ycord += imageScrollPane.getScrollPane().getY() + imageScrollPane.getY() + 22;
        ycord += imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight();
        ycord += activeImageScrollPane.getScrollPane().getLocation().y + activeImageScrollPane.getParentMainFrame().getMenuBarHeight();

        return new Point2D.Float(xcord, ycord);
    }

    public ImageScrollPane getOtherISP(ImageScrollPane isp) {
        if (isp == imageScrollPane) {
            return secondImageScrollPane;
        } else {
            return imageScrollPane;
        }
    }

    private int getSecondPaneAlligment(ImageScrollPane isp) {
        if (isp == imageScrollPane) {
            return 0;
        }
        return imageScrollPane.getWidth();
    }

    @Override
    public void paintComponent(Graphics g) {
        
        if(pause)
            return;
        
        imageScrollPane.getParentMainFrame().repaint();
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(descriptorColor);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawString("Glasspane", 0, 20);

        if (antialiasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        ;
        if (descriptor != null || visualizationMode == VisualizationMode.threeLines || visualizationMode== VisualizationMode.oneLine) {


            List<ObjectFeature> firstDataList = new ArrayList<ObjectFeature>();
            
            if (mode == Mode.twoImageVisualization && (visualizationMode == VisualizationMode.threeLines || visualizationMode== VisualizationMode.oneLine)) {
                //activeImageScrollPane = imageScrollPane;
                firstDataList = activeImageScrollPane.getBottomProjectionPanel().getDataList();
                if(highlitedDescriptor){
                        firstDataList.add(descriptor);
                }
            } else {
                firstDataList.add(descriptor);
            }

            //for (ObjectFeature _descriptor : firstDataList) {
            for(int j=0;j<firstDataList.size();j++){
                ObjectFeature _descriptor = firstDataList.get(j);
                
                if(j == firstDataList.size() -1 && highlitedDescriptor){
                    g2d.setColor(Color.GREEN);
                    g2d.setStroke(new BasicStroke(3.5f));
                }
                
                if(_descriptor == null){
                    continue;
                }
                
                if(mode == Mode.twoImageVisualization)
                if(getOtherISP(activeImageScrollPane).getBottomProjectionPanel().getDescriptorAt(activeImageScrollPane.getBottomProjectionPanel().getDescriptorIndex(_descriptor)) == null &&
                        visualizationMode != visualizationMode.hover)
                    continue;
                        
                    
                
                if (mode == Mode.singleImageProjection) {
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (!isInViewPort(_descriptor, activeImageScrollPane)) {
                        //System.err.println("NOT IN VIEWPORT");
                        return;
                    } else {
                    }
                    //System.err.println("IN VIEWPORT");

                //System.out.println(activeImageScrollPane.getBottomProjectionPanel().getLocation().y +" "+ activeImageScrollPane.getLocation().y + " "+ activeImageScrollPane.getParentMainFrame().getMenuBarHeight());
                    //System.out.println("SIP:" + value);
                    Point2D descPos = getDescriptorPosition(activeImageScrollPane, _descriptor);
                    if (activeImageScrollPane.getBottomProjectionPanel().isVisible()) {

                        g2d.drawLine((int) descPos.getX(),
                                (int) descPos.getY(),
                                (int) value + activeImageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(activeImageScrollPane) + pointSize / 2,
                                //activeImageScrollPane.getHeight() + 5
                                activeImageScrollPane.getBottomProjectionPanel().getLocation().y + activeImageScrollPane.getLocation().y + activeImageScrollPane.getParentMainFrame().getMenuBarHeight() + pointSize / 2);
                    }
                    if (activeImageScrollPane.getSideProjectionPanel().isVisible()) {
                        /*
                         if (activeImageScrollPane.getSideProjectionPanel().isVisible()) {
                         value += activeImageScrollPane.getSideProjectionPanel().getWidth();
                         }*/
                        g2d.drawLine((int) descPos.getX(),
                                (int) descPos.getY(),
                                10 + getSecondPaneAlligment(activeImageScrollPane) + +pointSize / 2,
                                //(int) value + activeImageScrollPane.getScrollPane().getY() + activeImageScrollPane.getY() + 22 + + pointSize/2
                                (int) value + activeImageScrollPane.getScrollPane().getLocation().y + activeImageScrollPane.getParentMainFrame().getMenuBarHeight() + pointSize / 2
                        );
                    }
                } else if (mode == Mode.twoImageVisualization) {
                    ObjectFeature descriptor2 = getOtherISP(activeImageScrollPane).getBottomProjectionPanel().getDescriptorAt(activeImageScrollPane.getBottomProjectionPanel().getDescriptorIndex(_descriptor));
                    Point2D descPos = getDescriptorPosition(activeImageScrollPane, _descriptor);
                    if (descriptor2 == null) {
                        mainFrame.SetNWSWCurrentSimilarity(null);
                        if (!(isInViewPort(_descriptor, activeImageScrollPane))) {
                            return;
                        }
                        if (activeImageScrollPane.getBottomProjectionPanel().isVisible()) {
                            g2d.drawLine((int) descPos.getX(),
                                    (int) descPos.getY(),
                                    (int) activeImageScrollPane.getBottomProjectionPanel().getDescriptorValueAt(_descriptor) + imageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(activeImageScrollPane),
                                    imageScrollPane.getHeight() + 5 + +imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                            );
                        } else {
                            g2d.drawLine((int) descPos.getX(),
                                    (int) descPos.getY(),
                                    10 + imageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(activeImageScrollPane),
                                    (int) activeImageScrollPane.getBottomProjectionPanel().getDescriptorValueAt(_descriptor) + activeImageScrollPane.getScrollPane().getY() + activeImageScrollPane.getY() + 22 + +imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                            );
                        }
                        return;
                    }
                    SequenceMatchingCost cost = SequenceMatchingCost.SIFT_DEFAULT;
                    mainFrame.SetNWSWCurrentSimilarity(cost.getCost(_descriptor, descriptor2));
                    Point2D descPos2 = getDescriptorPosition(getOtherISP(activeImageScrollPane), descriptor2);
                    
                    if (visualizationMode != VisualizationMode.oneLine ||
                            j == firstDataList.size() -1 && highlitedDescriptor
                            ) {
                        if (activeImageScrollPane.getBottomProjectionPanel().isVisible()) {
                            //System.out.println("a");
                            if ((isInViewPort(_descriptor, activeImageScrollPane))) {
                                g2d.drawLine((int) descPos.getX(),
                                        (int) descPos.getY(),
                                        (int) activeImageScrollPane.getBottomProjectionPanel().getDescriptorValueAt(_descriptor) + imageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(activeImageScrollPane),
                                        imageScrollPane.getHeight() + 5 + imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                                );
                            }

                        } else if (activeImageScrollPane.getSideProjectionPanel().isVisible()) {
                            if ((isInViewPort(_descriptor, activeImageScrollPane))) {
                                //System.out.println("b");
                                g2d.drawLine((int) descPos.getX(),
                                        (int) descPos.getY(),
                                        10 + imageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(activeImageScrollPane),
                                        (int) activeImageScrollPane.getBottomProjectionPanel().getDescriptorValueAt(_descriptor) + activeImageScrollPane.getScrollPane().getY() + activeImageScrollPane.getY() + 22 + imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                                );
                            }

                        }

                        if (getOtherISP(activeImageScrollPane).getBottomProjectionPanel().isVisible()) {
                            int value2 = getOtherISP(activeImageScrollPane).getBottomProjectionPanel().getDescriptorValueAt(descriptor2);
                            // System.out.println("c");
                            if ((isInViewPort(descriptor2, getOtherISP(activeImageScrollPane)))) {
                                g2d.drawLine((int) descPos2.getX(),
                                        (int) descPos2.getY(),
                                        (int) value2 + imageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(getOtherISP(activeImageScrollPane)),
                                        imageScrollPane.getHeight() + 5 + imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                                );
                            }

                        } else if (getOtherISP(activeImageScrollPane).getSideProjectionPanel().isVisible()) {
                            int value2 = getOtherISP(activeImageScrollPane).getBottomProjectionPanel().getDescriptorValueAt(descriptor2);
                            // System.out.println("d");
                            if ((isInViewPort(descriptor2, getOtherISP(activeImageScrollPane)))) {
                                g2d.drawLine((int) descPos2.getX(),
                                        (int) descPos2.getY(),
                                        10 + getSecondPaneAlligment(getOtherISP(activeImageScrollPane)),
                                        (int) value2 + activeImageScrollPane.getScrollPane().getY() + activeImageScrollPane.getY() + 22 + imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                                );
                            }

                        }

                    }

                    if (isInViewPort(_descriptor, activeImageScrollPane) && isInViewPort(descriptor2, getOtherISP(activeImageScrollPane))) {
                        g2d.drawLine((int) descPos.getX(),
                                (int) descPos.getY(),
                                (int) descPos2.getX(),
                                (int) descPos2.getY()
                        );
                    }

                }
            }
        } else {
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

    public void setAntialiasing(boolean bool) {
        antialiasing = bool;
    }

    public Color getHooverColor() {
        return descriptorColor;
    }

    public void setHooverColor(Color color) {
        descriptorColor = color;
    }
    
    public void showAll(boolean bool){
        /*showAll = bool;
        if(bool)
            mode = Mode.twoImageVisualization;
        else
            mode = Mode.singleImageProjection;
        repaint();*/
    }
    
    public void setVisualizationOneLine(){
        visualizationMode = VisualizationMode.oneLine;
        mode = Mode.twoImageVisualization;
        activeImageScrollPane = imageScrollPane;
        repaint();
    }
    
    public void setVisualizationThreeLines(){
        visualizationMode = VisualizationMode.threeLines;
        mode = Mode.twoImageVisualization;
        activeImageScrollPane = imageScrollPane;
        repaint();
    }
    
    public void setVisualizationHover(){
        visualizationMode = VisualizationMode.hover;
        mode = Mode.twoImageVisualization;
        activeImageScrollPane = imageScrollPane;
        repaint();
    }
    
    public void setPause(boolean bool){
        pause = bool;
        if(!bool){}
            //repaint();
    }
    
    public void clicked(int x, int y){
        
    }
    
}
