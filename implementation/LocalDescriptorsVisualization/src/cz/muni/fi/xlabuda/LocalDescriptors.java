package cz.muni.fi.xlabuda;
 
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import messif.objects.impl.ObjectFeature;
import messif.objects.impl.ObjectFeatureSet;
import java.util.*;
/**
 * Abstract class for all types of descriptors.
 *
 * @author Marian Labuda
 * @version 1.0
 */
public abstract class LocalDescriptors {
    
    
    private Projection projection = null;

    public int projectionAxis = 0;
    //************* CONSTANTS ****************//
    
    // location of descriptors is absolute - width and height
    public static final int ABSOLUTE_LOCATION = 0;

    // location of descriptors is relative - (0;1)
    public static final int RELATIVE_LOCATION = 1;


    // distance in pixels, how far can be descriptor, after clicked, hidden
    public static final float MAX_CLICKED_POINT_DISTANCE = 14;

    // constants has to be same
    public static final int MAX_NUMBER_OF_UNDO_OPERATIONS = 12;
    public static final int MAX_NUMBER_OF_REDO_OPERATIONS = 12;

    private static final float DEFAULT_SHAPE_THICKNESS = 1.3f;
    private static final Color DEFAULT_COLOR = Color.RED;
    private static final double DEFAULT_SHAPE_SIZE = 1.0;

    //************* ATTRIBUTES ****************//
    private ObjectFeatureSet descriptors;

    private Class<? extends ObjectFeature> encapsulatingClass = null;

    // absolute or relative location
    private int locationType;

    // image size corresponding to the descriptors
    private Dimension imageSize;

    // percentage scale of images which was used to receive descriptors
    private double widthScale;
    private double heightScale;

    private Set<ObjectFeature> visibleDescriptors;

    // hidden descriptors by rectangle - all out of bounds
    private Set<ObjectFeature> hiddenDescriptorsByRectangle;
    
    // hidden descriptors by click - get nearest descriptor to clicked point
    private Set<ObjectFeature> hiddenDescriptorsByClick;

    // hidden descriptors by size filter from visible descriptors
    private Set<ObjectFeature> hiddenVisibleDescriptorsBySizeFilter;

    // hidden descriptors by size filter from rectangle descriptors
    private Set<ObjectFeature> hiddenRectangleDescriptorsBySizeFilter;

    // hidden descriptors by size filter from hidden clicked descriptors
    private Set<ObjectFeature> hiddenClickedDescriptorsBySizeFilter;

    // for Undo operation
    private List<ObjectFeature> lastClickedDescriptors;

    // for Redo operation
    private List<ObjectFeature> redoDescriptors;

    // rectangle for visualization
    private Rectangle rectangle;

    // if descriptors are ready to be drawn
    private boolean descriptorsReadyToDrawn;

    private ImagePanel parentImagePanel;
    private Graphics graphics;

    private Color color = DEFAULT_COLOR;
    private float shapeThickness = DEFAULT_SHAPE_THICKNESS;
    private double descriptorsSize = DEFAULT_SHAPE_SIZE;
    
    private double minimumScale = 0;
    private double maximumScale = 0;

    private Double minimumScaleFilter = null;
    private Double maximumScaleFilter = null;
    
    private int ProjectionPointAx = 0;
    private int ProjectionPointAy = 0;
    private int ProjectionPointBx = 5000;
    private int ProjectionPointBy = 5000;
    
    boolean visualizationMode = false;
    
    private boolean locked = false;
    

    public LocalDescriptors() {        
        visibleDescriptors = new HashSet<ObjectFeature>();

        hiddenDescriptorsByRectangle = new HashSet<ObjectFeature>();
        hiddenDescriptorsByClick = new HashSet<ObjectFeature>();

        hiddenVisibleDescriptorsBySizeFilter = new HashSet<ObjectFeature>();
        hiddenRectangleDescriptorsBySizeFilter = new HashSet<ObjectFeature>();
        hiddenClickedDescriptorsBySizeFilter = new HashSet<ObjectFeature>();
        
        lastClickedDescriptors = new ArrayList<ObjectFeature>();
        redoDescriptors = new ArrayList<ObjectFeature>();
        
        //ProjectionPointA.setLocation(0, 0);
        ProjectionPointAy=6;
        
       
    }


    //************* SET METHODS ***************//

    /**
     * Set default settings - color and shape thickness
     */
    public final void setDefaultSettings() {
        parentImagePanel.setXPanelShift(0);
        parentImagePanel.setYPanelShift(0);
        parentImagePanel.zoom(1);
        parentImagePanel.removeRectangleForVisualization();
        color = DEFAULT_COLOR;
        shapeThickness = DEFAULT_SHAPE_THICKNESS;
        descriptorsSize = DEFAULT_SHAPE_SIZE;
        parentImagePanel.repaint();
        parentImagePanel.getParentImageScrollPane().setDescriptorsLabels();
    }

    /**
     * Set the set of descriptors
     * @param descriptors descriptors in the set
     */
    public final void setDescriptors(ObjectFeatureSet descriptors) {
        cancelProjection();
        this.descriptors = descriptors;
        boolean absoluteLocation = false;

        Iterator<ObjectFeature> iterator = descriptors.iterator();
        if (iterator.hasNext()) {
            ObjectFeature firstDescriptor = iterator.next();
            if (firstDescriptor.getX() >= 1 || firstDescriptor.getY() >= 1) {
                absoluteLocation = true;
            }
            encapsulatingClass = firstDescriptor.getClass();
            visibleDescriptors.add(firstDescriptor);
        }
        while (iterator.hasNext()) {
            ObjectFeature currentDescriptor = iterator.next();
            if (currentDescriptor.getX() >= 1 || currentDescriptor.getY() >= 1) {
                absoluteLocation = true;
            }
            visibleDescriptors.add(currentDescriptor);
        }
        if (visibleDescriptors.size() > 0)
            encapsulatingClass = visibleDescriptors.iterator().next().getClass();
        if (parentImagePanel != null) {
            descriptorsReadyToDrawn = true;
        }
        calculateScaleRange();
        setScaleFilter(minimumScale, maximumScale);

        if (absoluteLocation) {
            locationType = ABSOLUTE_LOCATION;
        } else {
            locationType = RELATIVE_LOCATION;
        }

        setDefaultSettings();
    }

    /**
     * Set color of descriptors
     * @param color Color of descriptors
     */
    public final void setDescriptorColor(Color color) {
        this.color = color;
    }

    /**
     * Set if descriptors are ready to drawn (has parent panel and set of descriptors)
     * @param true if descriptors are ready, false otherwise
     */
    public final void setDescriptorsReadyToDrawn(boolean ready) {
        descriptorsReadyToDrawn = ready;
    }

    /**
     * Set new size of descriptors shapes. Size multiply default size
     * @param size new size of descriptors
     */
    public final void setDescriptorsSize(double size) {
        descriptorsSize = size;
    }

    public void setImageSize(Dimension size) {
        imageSize = size;
        widthScale = parentImagePanel.getImage().getWidth() / size.getWidth();
        heightScale = parentImagePanel.getImage().getHeight() / size.getHeight();
    }

    public Dimension getImageSize() {
        return imageSize;
    }

    /**
     * @param parentImagePanel
     */
    public final void setParentImagePanel(ImagePanel parentImagePanel) {
        this.parentImagePanel = parentImagePanel;
        if (descriptors != null) {
            descriptorsReadyToDrawn = true;
        }
    }

    /**
     * Rectangle where descriptors are visible. Only descriptors in rectangle
     * will be drawn. Set have to be empty before fullfil with descriptors
     * (it's controlled in image panel (higher layer) - overwritten descriptors
     * is not supported.
     * @param rectangleForVisualization rectangle for visualization, if parameter is null
     * than rectangle will be just removed
     */
    public final void setRectangleForVisualization(Rectangle rectangleForVisualization) {
        if (rectangleForVisualization == null) {
            rectangle = null;
            visibleDescriptors.addAll(hiddenDescriptorsByRectangle);
            hiddenVisibleDescriptorsBySizeFilter.addAll(hiddenRectangleDescriptorsBySizeFilter);
            hiddenDescriptorsByRectangle.clear();
            hiddenRectangleDescriptorsBySizeFilter.clear();
        } else {
            rectangle = rectangleForVisualization;

            hiddenDescriptorsByRectangle.addAll(getDescriptorsHiddenByRectangle(visibleDescriptors,
                    rectangleForVisualization));
            visibleDescriptors.removeAll(hiddenDescriptorsByRectangle);
            
            hiddenRectangleDescriptorsBySizeFilter.addAll(getDescriptorsHiddenByRectangle(
                    hiddenVisibleDescriptorsBySizeFilter, rectangleForVisualization));
            visibleDescriptors.removeAll(hiddenRectangleDescriptorsBySizeFilter);
        }
        parentImagePanel.getParentImageScrollPane().getParentMainFrame().repaintAll();
        parentImagePanel.getParentImageScrollPane().setDescriptorsLabels();
    }

    /**
     * Get descriptors hidden by rectangle.
     * @param descriptors
     * @param rectangleForVisualization
     * @return set of hidden descriptors by rectangle
     */
    private Set<ObjectFeature> getDescriptorsHiddenByRectangle(Set<ObjectFeature> descriptors,
            Rectangle rectangleForVisualization) {
        Iterator<ObjectFeature> iterator = descriptors.iterator();
        Set<ObjectFeature> hiddenDescriptors = new HashSet<ObjectFeature>();
        while (iterator.hasNext()) {
            ObjectFeature descriptor = iterator.next();
            if (!rectangleForVisualization.contains(getDescriptorPoint(descriptor))) {
                hiddenDescriptors.add(descriptor);
            }
        }
        return hiddenDescriptors;
    }

    /**
     * Set line thickness of the shape
     * @param thickness Line width
     */
    public final void setShapeThickness(float thickness) {
        shapeThickness = thickness;
    }

    /**
     * Set new size filter range for visualization of descriptors
     * @param minimumSizeFilter New minimum size
     * @param maximumSizeFilter New maximum size
     */
    public final void setScaleFilter(Double minimumSizeFilter, Double maximumSizeFilter) {
        if (minimumScale != 0 && maximumScale != 0) {
            this.minimumScaleFilter = new Double(minimumSizeFilter);
            this.maximumScaleFilter = new Double(maximumSizeFilter);

            updateSizeFilter(visibleDescriptors, hiddenVisibleDescriptorsBySizeFilter);
            updateSizeFilter(hiddenDescriptorsByClick, hiddenClickedDescriptorsBySizeFilter);
            if (rectangle != null) {
                updateSizeFilter(hiddenDescriptorsByRectangle, hiddenRectangleDescriptorsBySizeFilter);
                setRectangleForVisualization(rectangle);
            }
            
            // set filter for list of last clicked descriptors. descriptors out of range will be removed
            if (!lastClickedDescriptors.isEmpty()) {
                for (int i = lastClickedDescriptors.size()-1; i >= 0; i--) {
                    if (!filterDescriptorByScale(lastClickedDescriptors.get(i))) {
                        lastClickedDescriptors.remove(lastClickedDescriptors.get(i));
                    }
                }
            }

            if (!redoDescriptors.isEmpty()) {
                for (int i = redoDescriptors.size() - 1; i >= 0; i--) {
                    if (!filterDescriptorByScale(redoDescriptors.get(i))) {
                        redoDescriptors.remove(redoDescriptors.get(i));
                    }
                }
            }
        }
        parentImagePanel.getParentImageScrollPane().setDescriptorsLabels();
        recalculateGlasspaneDescriptors();
    }

    /** 
     * Update size filter - split descriptors into correct visualization set
     * @param visibleDescriptors
     * @param hiddenDescriptors
     */
    private void updateSizeFilter(Set<ObjectFeature> visibleDescriptors,
            Set<ObjectFeature> hiddenDescriptors) {
        Set<ObjectFeature> union = new HashSet<ObjectFeature>();

        union.addAll(visibleDescriptors);
        union.addAll(hiddenDescriptors);

        visibleDescriptors.clear();
        hiddenDescriptors.clear();

        hiddenDescriptors.addAll(hideDescriptorsBySizeFilter(union));
        union.removeAll(hiddenDescriptors);
        visibleDescriptors.addAll(union);
    }

    /**
     * Filter set of descriptors by size filter
     * @param descriptors descriptors to filter
     * @return set of descriptors to hide
     */
    private Set<ObjectFeature> hideDescriptorsBySizeFilter(Set<ObjectFeature> descriptors) {
        Iterator<ObjectFeature> iterator = descriptors.iterator();
        Set<ObjectFeature> hiddenDescriptors = new HashSet<ObjectFeature>();
        while (iterator.hasNext()) {
            // cast is required, because abstract is too much general, and doesn't have scale
            ObjectFeature descriptor = (ObjectFeature) iterator.next();
            if (!filterDescriptorByScale(descriptor)) {
                hiddenDescriptors.add(descriptor);
            }
        }
        return hiddenDescriptors;
    }

    /**
     * Compare descriptor with filter range
     * @param descriptor descriptor to filter
     * @return true if descriptor meets the range, false otherwise
     */
    private boolean filterDescriptorByScale(ObjectFeature descriptor) {
        return descriptor.getScale() <= maximumScaleFilter &&
                descriptor.getScale() >= minimumScaleFilter;
    }

    /**
     * Set size range of descriptors (depends on type of the descriptors)
     * Set null if descriptors doesn't have size value
     * @param minimumScale minimum size of descriptor
     * @param maximumScale maximum size of descriptor
     */
    public final void setScaleRange(double minimumScale, double maximumScale) {
        this.minimumScale = minimumScale;
        this.maximumScale = maximumScale;
    }

    //************* GET METHODS ****************//

    /**
     * Get default shapes color given by constants
     * @return Color Default color of shapes
     */
    public final Color getDefaultColor() {
        return DEFAULT_COLOR;
    }

    /**
     * Get default shape thickness given by constant
     * @return float Default shape thickness
     */
    public final float getDefaultShapeThickness() {
        return DEFAULT_SHAPE_THICKNESS;
    }

    /**
     * Get set of descriptors
     * @return ObjectFeatureSet set of descriptors
     */
    public final ObjectFeatureSet getDescriptors() {
        return descriptors;
    }

    /**
     * Get color of descriptors
     * @return Color Color of descriptors
     */
    public final Color getDescriptorColor() {
        return color;
    }
    
    /**
     * Return if descriptors are ready to be drawn in parent panel
     * @return true if ready, false otherwise
     */
    public final boolean getDescriptorsReadyToDrawn() {
        return descriptorsReadyToDrawn;
    }

    /**
     * Get size of descriptors shapes
     * @return double size of descriptors shapes
     */
    public final double getDescriptorsSize() {
        return descriptorsSize;
    }

    /**
     * @return class which encapsulate descriptors
     */
    public Class<? extends ObjectFeature> getEncapsulatingClass() {
        return encapsulatingClass;
    }

    /**
     *
     * @return RELATIVE_LOCATION if location is relative (from range (0;1))
     * ABSOLUTE_LOCATION if location is absolute (width and height)
     */
    public int getLocationType() {
        return locationType;
    }

    /**
     * Get parent image panel, which is responsible for visualization of descriptors
     * @return ImagePanel parent image panel
     */
    public final ImagePanel getParentImagePanel() {
        return parentImagePanel;
    }

    /**
     * Get information about existence of rectangle for visualization
     * @return true if rectangle exist o
     */
    public final boolean getRectangleExistence() {
        return rectangle != null;
    }

    /**
     * Get filtered range of descriptor
     * @return Double[] Two double element - first minimum filter size of descriptor,
     * second - maximum filter size of descriptor
     */
    public final Double[] getScaleFilter() {
        Double[] scaleFilter = {minimumScaleFilter, maximumScaleFilter};
        return scaleFilter;
    }

    /**
     * Get size range of descriptors
     * @return Double[] Two double element - first minimum size of descriptor,
     * second maximum size of descriptor..
     */
    public final double[] getScaleRange() {
        double[] r = {minimumScale, maximumScale};
        return r;
    }
    
    /**
     * Get thicknes of the shapes of descriptors
     * @return float thickness of descriptors in pixels
     */
    public final float getShapeThickness() {
        return shapeThickness;
    }

    /**
     * Get visible descriptors
     * @return Set<ObjectFeature> of visible descriptors
     */
    public final Set<ObjectFeature> getVisibleDescriptors() {
        return visibleDescriptors;
    }

    //********* OTHER METHODS **********//

    /**
     *  descriptors on graphics
     * @param graphics where to paint descriptors
     */
    public final void paint(Graphics2D graphics) {
        
        if (this.graphics == null) {
            this.graphics = graphics;
        }
        Iterator<ObjectFeature> iterator = visibleDescriptors.iterator();
        while (iterator.hasNext()) {
            draw(graphics, iterator.next());
        }
        
        if(projection != null && !locked){
            //recalculateProjection();
        }
        if(visualizationMode){
            
            
            //getParentImagePanel().getParentImageScrollPane().getParentMainFrame().RefreshVisualization();
        }
                
 
        
    }

    /**
     * Set default visualization mode = set all descriptors as visible
     */
    public final void defaultVisualization() {
        visibleDescriptors.clear();
        hiddenDescriptorsByRectangle.clear();
        hiddenDescriptorsByClick.clear();
        hiddenVisibleDescriptorsBySizeFilter.clear();
        hiddenRectangleDescriptorsBySizeFilter.clear();
        hiddenClickedDescriptorsBySizeFilter.clear();
        lastClickedDescriptors.clear();
        redoDescriptors.clear();
        rectangle = null;
        minimumScaleFilter = null;
        maximumScaleFilter = null;
        
        setDescriptors(descriptors);
        parentImagePanel.getParentImageScrollPane().setDescriptorsLabels();
        recalculateGlasspaneDescriptors();
    }
    
 

    /**
     * Set state as hiden to the nearest descriptors to the given point
     * @param point nearest point to descriptor
     */
    public final boolean hideNearestDescriptors(Point point) {
        Iterator<ObjectFeature> iterator = visibleDescriptors.iterator();
        double shortestDistance = Double.MAX_VALUE;
        double calculatedDistance;
        ObjectFeature nearestDescriptor = null;
        Point descriptorPoint;

        Set<ObjectFeature> nearestDescriptors = new HashSet<ObjectFeature>();

        while (iterator.hasNext()) {
            ObjectFeature descriptor = iterator.next();
            descriptorPoint = getDescriptorPoint(descriptor);
            calculatedDistance = Point.distance(descriptorPoint.getX(),
                    descriptorPoint.getY(), point.getX(),point.getY());
            if (calculatedDistance < shortestDistance) {
                shortestDistance = calculatedDistance;
            }
        }

        iterator = visibleDescriptors.iterator();
        while (iterator.hasNext()) {
            ObjectFeature descriptor = iterator.next();
            descriptorPoint = getDescriptorPoint(descriptor);
            calculatedDistance = Point.distance(descriptorPoint.getX(),
                    descriptorPoint.getY(), point.getX(),point.getY());
            if (calculatedDistance == shortestDistance) {
                nearestDescriptors.add(descriptor);
            }
        }

        Iterator<ObjectFeature> nearestIterator = nearestDescriptors.iterator();
        while (nearestIterator.hasNext()) {
            ObjectFeature descriptor = (ObjectFeature) nearestIterator.next();
            if (nearestDescriptor == null) {
                nearestDescriptor = descriptor;
            }
            double x = point.getX() - getDescriptorPoint(nearestDescriptor).getX();
            double y = point.getY() - getDescriptorPoint(nearestDescriptor).getY();
            double rad = Math.atan2(y, x);
            if (rad < 0) {
                rad = Math.abs(rad * 2);
            }

            if (Math.abs(rad - descriptor.getOrientation()) <
                    Math.abs(rad - nearestDescriptor.getOrientation())) {
                nearestDescriptor = descriptor;
            }
        }

        if (shortestDistance <= MAX_CLICKED_POINT_DISTANCE) {

            if (lastClickedDescriptors.size() == MAX_NUMBER_OF_UNDO_OPERATIONS) {
                lastClickedDescriptors.remove(0);
            }
            lastClickedDescriptors.add(nearestDescriptor);

            hiddenDescriptorsByClick.add(nearestDescriptor);
            visibleDescriptors.remove(nearestDescriptor);
            
           parentImagePanel.repaint();
           parentImagePanel.getParentImageScrollPane().setDescriptorsLabels(); 
           
           return true;
        }

        parentImagePanel.repaint();
        parentImagePanel.getParentImageScrollPane().setDescriptorsLabels();
        return false;
    }


    /**
     * Modify graphics to set attributes.
     * @param graphics graphics where to modify
     */
    public final void modifyGraphics(Graphics2D graphics) {
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(shapeThickness, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * Determine whether undo operation can be executed = contains visible descriptors
     * @return true, if undo operation is possible to be executed, false otherwise
     */
    public final boolean undoOperationIsPossible() {
        for (int i = lastClickedDescriptors.size() - 1; i>=0; i--) {
            if ((rectangle != null && rectangle.contains(getDescriptorPoint(
                    lastClickedDescriptors.get(i)))) || rectangle == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine wheter redo operation can be executed = contains visible descriptors
     * @return true, if redo operation is possible to be executed, false otherwise
     */
    public final boolean redoOperationIsPossible() {
        for (int i = redoDescriptors.size() - 1; i >= 0; i--) {
            if ((rectangle != null && rectangle.contains(getDescriptorPoint(
                    redoDescriptors.get(i)))) || rectangle == null) {
                return true;
            } 
        }
        return false;
    }

    /**
     * Undo last clicked visible descriptors if it's possible = if rectangle is
     * actually visible (for example, if there is a rectangle, than the descriptor contains)
     */
    public final void undoVisibleClickedDescriptor() {
        ObjectFeature descriptor;
        for (int i = lastClickedDescriptors.size() - 1; i>=0; i--) {
            descriptor = lastClickedDescriptors.get(i);
            if (rectangle == null || 
                    (rectangle != null && rectangle.contains(getDescriptorPoint(descriptor)))) {

                    lastClickedDescriptors.remove(descriptor);
                    visibleDescriptors.add(descriptor);
                    
                    if (redoDescriptors.size() == MAX_NUMBER_OF_REDO_OPERATIONS) {
                        redoDescriptors.remove(0);
                    } 
                    redoDescriptors.add(descriptor);

                    parentImagePanel.repaint();
                    break;
            }
        }
        parentImagePanel.getParentImageScrollPane().setDescriptorsLabels();
    }

    public final void redoVisibleClickedDescriptors() {
        ObjectFeature descriptor;
        for (int i = redoDescriptors.size() - 1; i>=0; i--) {
            descriptor = redoDescriptors.get(i);
            if (rectangle == null ||
                    (rectangle != null && rectangle.contains(getDescriptorPoint(descriptor)))) {

                redoDescriptors.remove(descriptor);
                visibleDescriptors.remove(descriptor);
                if (lastClickedDescriptors.size() == MAX_NUMBER_OF_UNDO_OPERATIONS) {
                    lastClickedDescriptors.remove(0);
                }
                lastClickedDescriptors.add(descriptor);

                parentImagePanel.repaint();
                break;
            }
        }
        parentImagePanel.getParentImageScrollPane().setDescriptorsLabels();
    }

    /**
     * Calculate size range for descriptors
     */
    public final void calculateScaleRange() {
        double minimum = Double.MAX_VALUE;
        double maximum = 0;
        Iterator<ObjectFeature> iterator = getDescriptors().iterator();
        while (iterator.hasNext()) {
            ObjectFeature siftDescriptor = iterator.next();
            if (minimum > siftDescriptor.getScale()) {
                minimum = siftDescriptor.getScale();
            }
            if (maximum < siftDescriptor.getScale()) {
                maximum = siftDescriptor.getScale();
            }
        }
        if (minimum < maximum) {
            setScaleRange(minimum, maximum);
        } else {
            setScaleRange(0, 0);
        }

    }

    /**
     * Return point of descriptor. Distance form of descriptors are different
     * so it is required to get point coordinates in each descriptor class
     * @return Point position of descriptor
     */
    public Point getDescriptorPoint(ObjectFeature descriptor) {
        float x = ((ObjectFeature) descriptor).getX();
        float y = ((ObjectFeature) descriptor).getY();
        if (locationType == ABSOLUTE_LOCATION) {
            return new Point(Double.valueOf(x * widthScale).intValue() +
                             (int) (parentImagePanel.getXPanelShift() / parentImagePanel.getZoomScale()),
                             Double.valueOf(y * heightScale).intValue() +
                             (int) (parentImagePanel.getYPanelShift() / parentImagePanel.getZoomScale()));
        } else {
            return new Point(
                Double.valueOf(getParentImagePanel().getImage().getWidth() * x).intValue() +
                (int) (parentImagePanel.getXPanelShift() / parentImagePanel.getZoomScale()),
                Double.valueOf(getParentImagePanel().getImage().getHeight() * y).intValue() +
                (int) (parentImagePanel.getYPanelShift() / parentImagePanel.getZoomScale()));
        }
    }

    private void recalculateGlasspaneDescriptors() {
        MainFrame frame = parentImagePanel.getParentImageScrollPane().getParentMainFrame();
        frame.recalculateSimilarDescriptors();
        frame.repaintAll();
    }

    /**
     * Draw descriptor on image
     * @param graphics Graphics where to paint descriptor
     * @param descriptor descriptor to be drawn
     */
    public void draw(Graphics2D graphics, ObjectFeature descriptor) {
        Point position;
        if (projectionAxis == 1)
         position = new Point(20, (int) getDescriptorPoint(descriptor).getY());
        else
        position = getDescriptorPoint(descriptor);
        
        if (getDescriptorsSize() == 0) {
            // Draw a small point...
            graphics.drawRect(position.x - 1, position.y - 1, 2, 2);
        } else {
            Shape shape = getFeatureShape(descriptor);
            if (shape != null) {
                double scale = descriptor.getScale();
                AffineTransform transform = AffineTransform.getTranslateInstance(position.getX(), position.getY());
                transform.rotate(descriptor.getOrientation());
                transform.scale(scale * getDescriptorsSize(), scale * getDescriptorsSize());
                shape = transform.createTransformedShape(shape);
                graphics.draw(shape);
            }
            drawFeature(graphics, descriptor);
        }
    }
    
    public void draw(Graphics2D graphics, ObjectFeature descriptor, int a) {
        Point position = getDescriptorPoint(descriptor);
        
        if (getDescriptorsSize() == 0) {
            // Draw a small point...
            graphics.drawRect(20, position.y - 1, 2, 2);
        } else {
            Shape shape = getFeatureShape(descriptor);
            if (shape != null) {
                double scale = descriptor.getScale();
                AffineTransform transform = AffineTransform.getTranslateInstance(20, position.getY());
                transform.rotate(descriptor.getOrientation());
                transform.scale(scale * getDescriptorsSize(), scale * getDescriptorsSize());
                shape = transform.createTransformedShape(shape);
                graphics.draw(shape);
            }
            drawFeature(graphics, descriptor);
        }
    }

    //********* ABSTRACT METHODS **********//

    /**
     * Draw descriptor on image (any drawing)
     * @param graphics Graphics where to paint descriptor
     * @param descriptor descriptor to be drawn
     */
    protected void drawFeature(Graphics2D graphics, ObjectFeature descriptor) {
    }
    
    /**
     * Create the feature shape to be draw directly.
     * @param descriptor descriptor to be drawn
     * @return feature shape in basic position
     */
    protected Shape getFeatureShape(ObjectFeature descriptor) {
        return null;
    }
    
    /**
     * Method to get description of radio menu item in 
     * menu in main frame.
     * @return String which represents description of radio menu item
     */
    public abstract String getDescriptorsDescription();
    
    
    public void setProjectionPointA(int x, int y){
       ProjectionPointAx = x;
       ProjectionPointAy = y;
    }
    
    public void setProjectionPointB(int x, int y){
       ProjectionPointBx = x;
       ProjectionPointBy = y;
    }
    
    public void setProjection(ProjectionTo projectionTo){        
        projection = new Projection(projectionTo);
        recalculateProjection();
    }
    
    public void setProjection(Projection projection){
        this.projection = projection;
        recalculateProjection();
    }
    
    public void setProjection(ProjectionTo projectionTo, Point2D a, Point2D b){
        projection = new Projection(projectionTo, a, b, getParentImagePanel().getImage().getWidth(), getParentImagePanel().getImage().getHeight());
        recalculateProjection();
    }
    
    public void recalculateProjection(){
       
       
        Map <ObjectFeature, Float> proj = null;
        if(projection.getProjectionType() == ProjectionTo.CUSTOM){
            proj = projection.getProjection(visibleDescriptors,
                        getParentImagePanel().getFirstProjectionPoint(),
                        getParentImagePanel().getSecondProjectionPoint()
                        );
        }
        else{
            proj =  projection.getProjection(visibleDescriptors);
        }
        
        ProjectionPanel bottomProjectionPanel = parentImagePanel.getParentImageScrollPane().getBottomProjectionPanel();
        ProjectionPanel sideProjectionPanel = parentImagePanel.getParentImageScrollPane().getSideProjectionPanel();
        bottomProjectionPanel.setProjectionDescriptors(proj);
        sideProjectionPanel.setProjectionDescriptors(proj);
        bottomProjectionPanel.repaint();
        sideProjectionPanel.repaint();
        
    }

    public void recalculateVisualization(){
        
        if(projection == null)
            return; 
        
        recalculateProjection();
        
        if(visualizationMode){
            getParentImagePanel().getParentImageScrollPane().getParentMainFrame().RefreshVisualization();
            return;
        }       
        
    }
    
    public void cancelProjection(){
        projection = null;
        getParentImagePanel().getParentImageScrollPane().getParentMainFrame().resetProjectionSelectionMenu(getParentImagePanel().getParentImageScrollPane());
        getParentImagePanel().HideCustomProjectionPoints();
    }
    
    public ProjectionTo getProjectionType(){
        if(projection == null){
            return null;
        }
        else{
            return projection.getProjectionType();
        }
    }
    
    public Projection getProjection(){
        return projection; 
    }
    
    public void setVisualizationMode(boolean bool){
        visualizationMode = bool;
        if(!bool)
            recalculateVisualization();
    }
    
    public void setLock(boolean locked){
        this.locked = locked;
    }
}