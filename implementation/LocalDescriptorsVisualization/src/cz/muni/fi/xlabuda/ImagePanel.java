package cz.muni.fi.xlabuda;
 
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JPanel;
import messif.objects.impl.ObjectFeature;

/**
 * Panel for visualization. Panel is nested into scroll pane.
 *
 * @author Marian Labuda
 * @version 1.0
 */
public final class ImagePanel extends JPanel {

    //******************** Attributes ********************//

    /** Zoom scale factor for image in this image panel */
    public static final double ZOOM_FACTOR = 1.2;

    /** Current zoom scale of image */
    private double zoomScale = 1;

    /** Image to paint in this panel */
    private BufferedImage image;

    /** Image file name - with file extension */
    private String imageFileName;

    /** Image size - width & height in pixels */
    private int width;
    private int height;

    /** Set of descriptors */
    private LocalDescriptors descriptors;

    private MainFrame frame;

    private AffineTransform at = new AffineTransform();

    // if rectangle is getting size, or not
    private boolean gettingRectangle;
    private Point rectangleFirstPoint;
    private Point rectangleStartPoint;
    private Point rectangleEndPoint;

    // points required to count scroll shift
    private Point lastPoint;
    private Point currentPoint;

    private int xPanelShift = 0;
    private int yPanelShift = 0;

    public ImagePanel() {
        setBackground(Color.WHITE);

        addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {

                ///////////////////////////////////
                //////// Rectangle action /////////
                ///////////////////////////////////
                if (e.isAltDown() && !e.isShiftDown() && !e.isControlDown()) {
                    if (descriptors != null && !descriptors.getRectangleExistence()) {
                        //***** Create rectangle area for visualization of the descriptors *****//
                        if (rectangleFirstPoint == null) {
                            gettingRectangle=true;
                            Point firstPoint = new Point(
                                    Double.valueOf(e.getPoint().getX()).intValue(),
                                    Double.valueOf(e.getPoint().getY()).intValue());
                            rectangleFirstPoint = firstPoint;
                            rectangleStartPoint = firstPoint;
                            rectangleEndPoint = firstPoint;
                        }
                        // Current dragged point
                        Point currentPoint = e.getPoint();

                        // Mouse pointer location - right side
                        if (currentPoint.getX() >= rectangleFirstPoint.getX()) {

                            // Mouse pointer location - upper right corner
                            if (currentPoint.getY() < rectangleFirstPoint.getY()) {
                                rectangleStartPoint = new Point(
                                        Double.valueOf(rectangleFirstPoint.getX() / zoomScale).intValue(),
                                        Double.valueOf(currentPoint.getY() / zoomScale).intValue());
                                rectangleEndPoint = new Point(
                                    Double.valueOf(e.getPoint().getX() / zoomScale).intValue(),
                                    Double.valueOf(rectangleFirstPoint.getY() / zoomScale).intValue());

                            // Mouste pointer location - lower right corner
                            } else {
                                rectangleStartPoint = new Point(
                                        Double.valueOf(rectangleFirstPoint.getX() / zoomScale).intValue(),
                                        Double.valueOf(rectangleFirstPoint.getY() / zoomScale).intValue());
                                rectangleEndPoint = new Point (
                                        Double.valueOf(e.getPoint().getX() / zoomScale).intValue(),
                                        Double.valueOf(e.getPoint().getY() / zoomScale).intValue());
                            }

                        // Mouse pointer location - left side
                        } else {

                            // Mouse pointer location - upper left corner
                            if (currentPoint.getY() < rectangleFirstPoint.getY()) {
                               rectangleStartPoint = new Point (
                                        Double.valueOf(e.getPoint().getX() / zoomScale).intValue(),
                                        Double.valueOf(e.getPoint().getY() / zoomScale).intValue());
                               rectangleEndPoint = new Point(
                                        Double.valueOf(rectangleFirstPoint.getX() / zoomScale).intValue(),
                                        Double.valueOf(rectangleFirstPoint.getY() / zoomScale).intValue());

                            // Mouse pointer location - lower left corner
                            } else {
                                rectangleStartPoint = new Point(
                                        Double.valueOf(e.getPoint().getX() / zoomScale).intValue(),
                                        Double.valueOf(rectangleFirstPoint.getY() / zoomScale).intValue());
                               rectangleEndPoint = new Point(
                                       Double.valueOf(rectangleFirstPoint.getX() / zoomScale).intValue(),
                                       Double.valueOf(e.getPoint().getY() / zoomScale).intValue());
                            }
                        }
                        getParentImageScrollPane().getParentMainFrame().repaintAll();
                    }
                }

                if (image != null && !e.isAltDown() && !e.isControlDown() &&
                        e.getButton() != MouseEvent.BUTTON2) {
                    currentPoint = e.getLocationOnScreen();

                    getParentImageScrollPane().scrollAndShift(
                        lastPoint.getX() - currentPoint.getX(),
                        lastPoint.getY() - currentPoint.getY(), e.isShiftDown());
                    lastPoint = currentPoint;
                }
            }

            public void mouseMoved(MouseEvent e) {
                if (frame.isShowSimilarDescriptorsMode()) {
                    Point nearestDescriptorsPoint = null;
                    double distance = Double.MAX_VALUE;
                    double currDistance;
                    Iterator<ObjectFeature> iterator = descriptors.getDescriptors().iterator();
                    while (iterator.hasNext()) {
                        ObjectFeature descriptor = iterator.next();
                        currDistance = Point.distance(e.getX() / zoomScale, e.getY() / zoomScale,
                                descriptors.getDescriptorPoint(descriptor).getX(),
                                descriptors.getDescriptorPoint(descriptor).getY());
                        if (currDistance < distance) {
                            distance = currDistance;
                            nearestDescriptorsPoint = descriptors.getDescriptorPoint(descriptor);
                        }
                    }
                    if (distance < GlasspaneForSimilarDescriptors.MAXIMUM_LINE_DISTANCE) {
                        int paneNumber;
                        if (frame.getFirstScrollPane().getImagePanel().equals(ImagePanel.this)) {
                            paneNumber = MainFrame.FIRST_IMAGE_PANEL;
                        } else {
                            paneNumber = MainFrame.SECOND_IMAGE_PANEL;
                        }
                        Point point = new Point((int) (nearestDescriptorsPoint.getX()),
                                                (int) (nearestDescriptorsPoint.getY()));
                        ((GlasspaneForSimilarDescriptors) frame.getGlassPane()).setDescriptorPoint(
                                point, paneNumber);
                    } else {
                        ((GlasspaneForSimilarDescriptors) frame.getGlassPane()).setDescriptorPoint(
                                null, 0);
                    }
                }
            }
        });

        addMouseListener(new MouseListener() {
            // Left or Right mouse button = hide nearest descriptor
            public void mouseClicked(MouseEvent e) {
                MainFrame frame = getParentImageScrollPane().getParentMainFrame();
                if (frame.isShowSimilarDescriptorsMode()) {
                    GlasspaneForSimilarDescriptors glasspane =
                                (GlasspaneForSimilarDescriptors) frame.getGlassPane();
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        glasspane.switchSimilarDescriptor();
                    }
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        glasspane.hideSelectedSimilar();
                    }
                } else {
                    if (descriptors != null && e.getButton() != MouseEvent.BUTTON2 &&
                            e.getButton() != MouseEvent.BUTTON3) {
                        Point zoomedClickedPoint = e.getPoint();
                        int x = Double.valueOf(zoomedClickedPoint.getX() / zoomScale).intValue();
                        int y = Double.valueOf(zoomedClickedPoint.getY() / zoomScale).intValue();
                        descriptors.hideNearestDescriptors(new Point(x,y));
                    }
                }
                if (e.getButton() == MouseEvent.BUTTON1) {
                descriptors.setProjectionPointA(e.getX(),e.getY() );
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                descriptors.setProjectionPointB(e.getX(),e.getY() );
                descriptors.hideNearestDescriptors(new Point(0,0));
                }
                //System.out.println("aaaaaaaaaa"+e.getX()+" "+e.getY());
            }

            public void mousePressed(MouseEvent e) {
                // Remove rectangle for visualization
                if (descriptors != null && e.getButton() == MouseEvent.BUTTON2 &&
                        rectangleStartPoint != null && !gettingRectangle) {
                    removeRectangleForVisualization();
                    descriptors.setRectangleForVisualization(null);
                    if (frame.isShowSimilarDescriptorsMode()) {
                        ((GlasspaneForSimilarDescriptors) frame.getGlassPane()).recalculateDescriptors();
                    } else {
                        getParentImageScrollPane().getParentMainFrame().repaintAll();
                    }
                }

                // Start move image in panel
                if(image != null && e.getButton() != MouseEvent.BUTTON2) {
                    lastPoint = e.getLocationOnScreen();
                }
            }

            // Create rectangle after end of mouse dragged
            // if rectangle is smaller than 6x6, it won't be created
            public void mouseReleased(MouseEvent e) {
                if (e.isAltDown() && gettingRectangle && !e.isControlDown() && !e.isShiftDown()) {
                    if (rectangleEndPoint.getX() - rectangleStartPoint.getX() > 6 &&
                        rectangleEndPoint.getY() - rectangleStartPoint.getY() > 6) {
                            descriptors.setRectangleForVisualization(new Rectangle(
                            Double.valueOf(rectangleStartPoint.getX()).intValue(),
                            Double.valueOf(rectangleStartPoint.getY()).intValue(),
                            Double.valueOf(rectangleEndPoint.getX() -
                                    rectangleStartPoint.getX()).intValue(),
                            Double.valueOf(rectangleEndPoint.getY() -
                                    rectangleStartPoint.getY()).intValue()));
                    } else {
                        rectangleStartPoint = null;
                        rectangleEndPoint = null;
                        rectangleFirstPoint = null;
                    }
                    if (frame.isShowSimilarDescriptorsMode()) {
                        ((GlasspaneForSimilarDescriptors) frame.getGlassPane()).
                            recalculateDescriptors();
                    } else {
                        repaint();
                    }
                    gettingRectangle = false;
                }
            }

            public void mouseEntered(MouseEvent e) {}

            public void mouseExited(MouseEvent e) {}
            
           
       
    
        });
    }


    //********** Get methods **********//

    /**
     * Get descriptors for this image panel
     * @return LocalDescriptors descendant, containing encapsulated descriptors
     */
    public LocalDescriptors getDescriptors() {
        return descriptors;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public ImageScrollPane getParentImageScrollPane() {
        //ImageScrollPane pane = (ImageScrollPane) getParent().getParent().getParent().getParent();
        ImageScrollPane pane = (ImageScrollPane) getParent().getParent().getParent();
        return pane;
    }

    public double getZoomScale() {
        return zoomScale;
    }

    //********** Set methods **********//

    public void setParentMainFrame(MainFrame frame) {
        this.frame = frame;
    }

    public void setBufferedImage(BufferedImage image) {
        this.image = image;
        width = image.getWidth();
        height = image.getHeight();
        setPreferredSize(getPreferredSize());
        descriptors = null;
        getParentImageScrollPane().getScrollPane().getViewport().revalidate();
    }

    public void setNewAffineTransform() {
        at = new AffineTransform();
    }

    public void setImageFileName(String fileName) {
        imageFileName = fileName;

        getParentImageScrollPane().setFileLabel(fileName);
        setParentScrollPaneSizeLabel();
    }

    public void setDescriptors(LocalDescriptors descriptors) {
        this.descriptors = descriptors;
    }


    //********** Remove methods **********//

    /**
     * Remove image and descriptors
     */
    public void removeLoadedImage() {
        image = null;
        imageFileName = null;
        descriptors = null;
    }

    public void removeRectangleForVisualization() {
        rectangleFirstPoint = null;
        rectangleStartPoint = null;
        rectangleEndPoint = null;
        if (descriptors != null) {
            descriptors.setRectangleForVisualization(null);
        }
    }


    //************* ZOOM ************//
    /**
     * Zoom image to zoomScale
     * @param zoomScale size of image to zoom
     */
    public void zoom(double zoomScale) {
        if (image != null) {
            if (getParentImageScrollPane().getParentMainFrame().isShowSimilarDescriptorsMode()) {
                GlasspaneForSimilarDescriptors glassPane = (GlasspaneForSimilarDescriptors)
                        getParentImageScrollPane().getParentMainFrame().getGlassPane();
                    glassPane.calculatePoints();
            }
            at = AffineTransform.getScaleInstance(zoomScale, zoomScale);

            getParentImageScrollPane().getScrollPane().getViewport().revalidate();
            if (descriptors != null) {
                descriptors.setShapeThickness(descriptors.getShapeThickness() /
                        ((float) zoomScale / (float) this.zoomScale));
            }

            this.zoomScale = zoomScale;
            setParentScrollPaneSizeLabel();
            getParentImageScrollPane().getScrollPane().getViewport().revalidate();
            getParentImageScrollPane().getParentMainFrame().repaintAll();
        }
    }

    /**
     * Zoom image to width of scrollpane's viewport
     */
    public void fitToWidth() {
        zoom(getParentImageScrollPane().getScrollPane().getViewport().getWidth() /
            (double) image.getWidth());
    }

    /**
     * Zoom image to height of scrollpane's viewport
     */
    public void fitToHeight() {
        zoom(getParentImageScrollPane().getScrollPane().getViewport().getHeight() /
                (double) image.getHeight());
    }

    public void fitToScreen() {
        double heightZoomScale = (getParentImageScrollPane().getScrollPane().getViewport().getHeight())
                / (double) image.getHeight();
        double widthZoomScale = (getParentImageScrollPane().getScrollPane().getViewport().getWidth()) /
                (double) image.getWidth();
        if (heightZoomScale > widthZoomScale) {
            fitToWidth();
        } else {
            fitToHeight();
        }
    }

    /**
     * Zoom in image. Size depends on zoom factor
     */
    public void zoomIn() {
        zoom(zoomScale * ZOOM_FACTOR);
    }

    /**
     * Zoom out image. Size depends on zoom factor
     */
    public void zoomOut() {
        zoom(zoomScale / ZOOM_FACTOR);
    }


    /**
     * Set name and current zoom size in JPanel with ScrollPane
     */
    private void setParentScrollPaneSizeLabel() {
        ImageScrollPane parentScrollPane = (ImageScrollPane) this.getParent().getParent().getParent();
        String rawString = String.valueOf(zoomScale * 100);
        String stringScale = rawString.substring(0, rawString.indexOf(".") + 2);
        parentScrollPane.setZoomLabel(stringScale);
        parentScrollPane.setSizeLabel(width, height);
    }

    /**
     * Get preferred size of this image panel. Size depends on size of image.
     * If image is null, size is set to default parent main frame size.
     */
    @Override
    public Dimension getPreferredSize() {
        if (image != null) {
            int dimensionWidth = (int) (zoomScale * image.getWidth() + xPanelShift * zoomScale);
            int dimensionHeight = (int) (zoomScale * image.getHeight() + yPanelShift * zoomScale);
            return new Dimension(dimensionWidth, dimensionHeight);
        }
        else {
            return new Dimension(0,0);
        }
    }

    public void setXPanelShift(int shift)  {
        xPanelShift = shift;
       
        /**/
    }

    public void setYPanelShift(int shift) {
        yPanelShift = shift;
    }

    public int getXPanelShift() {
        return xPanelShift;
    }

    public int getYPanelShift() {
        return yPanelShift;
    }
    
    @Override
    public void paintComponent(Graphics graphics) {
       // System.out.println("paint");
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        if (image != null) {
            graphics2D.transform(at);

            graphics2D.drawImage(image, (int) (xPanelShift / zoomScale), (int) (yPanelShift / zoomScale),
                    null);

            if (descriptors != null) {
                if (!frame.getGlassPane().isVisible() || frame.getGlassPane().getClass() == ProjectionGlassPane.class ) { //UPRAVENE
                    if (descriptors.getDescriptorsReadyToDrawn()) {
                        descriptors.modifyGraphics(graphics2D);
                        descriptors.paint(graphics2D);
                    }
                }
                
                


                if (getParentImageScrollPane().getParentMainFrame().isShowSimilarDescriptorsMode()) {
                    graphics2D.setStroke(new BasicStroke((float) (1 / zoomScale)));
                }
                
                if (rectangleStartPoint != null || rectangleEndPoint != null) {
                    graphics.drawRect(
                        Double.valueOf(rectangleStartPoint.getX()).intValue(),
                        Double.valueOf(rectangleStartPoint.getY()).intValue(),
                        Double.valueOf(rectangleEndPoint.getX() - rectangleStartPoint.getX()).intValue(),
                        Double.valueOf(rectangleEndPoint.getY() - rectangleStartPoint.getY()).intValue());
                }
            }
        }
        
    }

    public void clearRectangle() {
        rectangleStartPoint = null;
        rectangleEndPoint = null;
        rectangleFirstPoint = null;
    }
 
    
}