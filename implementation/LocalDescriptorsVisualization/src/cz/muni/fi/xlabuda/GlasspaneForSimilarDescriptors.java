package cz.muni.fi.xlabuda;

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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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

/**
 * Glasspane for visualization connections between similar descriptors
 * in two images mode
 *
 * @author Marian Labuda
 * @version 1.1
 */
public final class GlasspaneForSimilarDescriptors extends JComponent {

    public static final int MAXIMUM_DISTANCE_TRESHOLD = 500;

    // maximal distance where to paint line with another color as others
    public static final int MAXIMUM_LINE_DISTANCE = 10;

    private int pointer = 0;
    private int tempCounter;

    private boolean antialiasingOn = false;

    private ResourceBundle localLanguage;

    private double minTreshold = 0;
    private double maxTreshold = MAXIMUM_DISTANCE_TRESHOLD;

    private ImageScrollPane firstScrollPane;
    private ImageScrollPane secondScrollPane;

    private LocalDescriptors firstDescriptors;
    private LocalDescriptors secondDescriptors;

    private CalculateDescriptorsSwingWorker calculateWorker;

    private List<ObjectFeature[]> similarDescriptors;

    private List<Point[]> pointsOfSimilarDescriptors;

    private static Color similarDescriptorsColor = Color.RED;
    private static Color hooverColor = Color.BLUE;

    private Point nearestDescriptorPoint;
    private Point[] similarPoints;

    private Point hooveredPoint;

    private List<ObjectFeature[]> nearestVisibleSimilarDescriptors;
    private List<ObjectFeature[]> undoClickedDescriptors;
    private List<ObjectFeature[]> redoClickedDescriptors;

    private int paneNumber;

    SimilarDescriptorsSwingWorker worker;

    public GlasspaneForSimilarDescriptors(ImageScrollPane firstPane, ImageScrollPane secondPane,
            double minimumTreshold, double maximumTreshold) {
        localLanguage = ResourceBundle.getBundle("Dialogs", MainFrame.locale);
        minTreshold = minimumTreshold;
        maxTreshold = maximumTreshold;

        pointsOfSimilarDescriptors = new ArrayList<Point[]>();
        firstScrollPane = firstPane;
        secondScrollPane = secondPane;

        firstDescriptors = firstPane.getImagePanel().getDescriptors();
        secondDescriptors = secondPane.getImagePanel().getDescriptors();

        undoClickedDescriptors = new ArrayList<ObjectFeature[]>();
        redoClickedDescriptors = new ArrayList<ObjectFeature[]>();

        nearestVisibleSimilarDescriptors = new ArrayList<ObjectFeature[]>();

        setDistanceTreshold(minimumTreshold, maximumTreshold);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;

        if (antialiasingOn) {
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        paintLines(graphics2D);
    }

    public Color getSimilarDescriptorsColor() {
        return similarDescriptorsColor;
    }

    public void setSimilarDescriptorsColor(Color color) {
        similarDescriptorsColor = color;
    }

    public Color getHooverColor() {
        return hooverColor;
    }

    public void setHooverColor(Color color) {
        hooverColor = color;
    }

    public void setAntialiasing(boolean antialiasingOn) {
        this.antialiasingOn = antialiasingOn;
        repaint();
    }

    public boolean getAntialiasing() {
        return antialiasingOn;
    }

    private void paintLines(Graphics2D graphics) {
        if (hooveredPoint != null) {
            if (hooveredPoint.equals(getHooveredDescriptorPoint())) {
                if (tempCounter == pointer) {
                    pointer = 0;
                }
            }
        }
        hooveredPoint = getHooveredDescriptorPoint();
        
        similarPoints = null;
        List<Point[]> similar = new ArrayList<Point[]>(pointsOfSimilarDescriptors);

        Iterator<Point[]> iterator = similar.iterator();
        Point[] points;
        int index;
        int counter = 0;

        graphics.setColor(similarDescriptorsColor);

        if (hooveredPoint != null) {
            Point[] hooveredPoints = null;
            
            if (paneNumber == MainFrame.FIRST_IMAGE_PANEL) {
                index = 0;
            } else {
                index = 1;
            }
            while (iterator.hasNext()) {
                points = iterator.next();
                if (points[index].equals(hooveredPoint)) {
                    if (pointer == -1) {
                        pointer = 0;
                    }
                    if (counter == pointer) {
                        hooveredPoints = points;
                    } else {
                        graphics.drawLine((int) points[0].getX(), (int) points[0].getY(),
                            (int) points[1].getX(), (int) points[1].getY());
                    }
                    counter++;
                } else {
                    graphics.drawLine((int) points[0].getX(), (int) points[0].getY(),
                            (int) points[1].getX(), (int) points[1].getY());
                }
            }
            if (hooveredPoints != null) {
                graphics.setColor(hooverColor);
                graphics.setStroke(new BasicStroke(2.0f));
                similarPoints = hooveredPoints;
                graphics.drawLine((int) hooveredPoints[0].getX(), (int) hooveredPoints[0].getY(),
                        (int) hooveredPoints[1].getX(), (int) hooveredPoints[1].getY());
                graphics.setColor(similarDescriptorsColor);
                graphics.setStroke(new BasicStroke(1.0f));
            }
        } else {
            while (iterator.hasNext()) {
                points = iterator.next();
                graphics.drawLine((int) points[0].getX(), (int) points[0].getY(),
                            (int) points[1].getX(), (int) points[1].getY());
            }
        }

        tempCounter = counter;
        if (pointer == counter) {
            pointer = -1;
        }
    }

    private Point getHooveredDescriptorPoint() {
        if (nearestDescriptorPoint == null || similarDescriptors == null) {
            return null;
        }
        ObjectFeature similarDescriptor = null;
        ImageScrollPane scrollPane;
        ImageScrollPane nextScrollPane;
        LocalDescriptors firstPanesDescriptors;
        LocalDescriptors secondPanesDescriptors;
        Point mainPanelCorner = firstScrollPane.getParentMainFrame().getMainPanel().getLocation();
        // index in array of descriptors - 0 mean left image panel, 1 mean right (first / second)
        int index = 0;
        if (paneNumber == MainFrame.FIRST_IMAGE_PANEL) {
            scrollPane = firstScrollPane;
            nextScrollPane = secondScrollPane;
            firstPanesDescriptors = firstDescriptors;
            secondPanesDescriptors = secondDescriptors;
            index = 0;
        } else {
            scrollPane = secondScrollPane;
            nextScrollPane = firstScrollPane;
            firstPanesDescriptors = secondDescriptors;
            secondPanesDescriptors = firstDescriptors;
            index = 1;
        }

        Iterator<ObjectFeature[]> descriptors = similarDescriptors.iterator();

        nearestVisibleSimilarDescriptors.clear();
        while (descriptors.hasNext()) {
            ObjectFeature[] actuallySimilarDescriptors = descriptors.next();
            ObjectFeature descriptor = actuallySimilarDescriptors[index];
            Point firstDescriptorPoint = firstPanesDescriptors.getDescriptorPoint(descriptor);
            if (firstDescriptorPoint.equals(nearestDescriptorPoint)) {
                similarDescriptor = descriptor;

                Point secondDescriptorPoint = secondPanesDescriptors.getDescriptorPoint(
                        actuallySimilarDescriptors[Math.abs(index - 1)]);

                Point firstLocationOnGlassPane = getLocationOnGlassPane(firstDescriptorPoint,
                        getScrollPaneCorner(scrollPane), mainPanelCorner, 
                        getViewportRange(scrollPane), scrollPane.getImagePanel().getZoomScale());
                Point secondLocationOnGlassPane = getLocationOnGlassPane(secondDescriptorPoint,
                        getScrollPaneCorner(nextScrollPane), mainPanelCorner, 
                        getViewportRange(nextScrollPane), nextScrollPane.getImagePanel().getZoomScale());

                if (firstLocationOnGlassPane != null && secondLocationOnGlassPane != null) {
                    nearestVisibleSimilarDescriptors.add(actuallySimilarDescriptors);
                }
            }
        }

        Point locationOnGlassPane = null;
        if (similarDescriptor != null) {
            Point scrollPaneCorner = getScrollPaneCorner(scrollPane);

            Point descriptorPoint = firstPanesDescriptors.getDescriptorPoint(similarDescriptor);
            locationOnGlassPane = getLocationOnGlassPane(descriptorPoint, scrollPaneCorner,
                            mainPanelCorner, getViewportRange(scrollPane),
                            scrollPane.getImagePanel().getZoomScale());
        }
        return locationOnGlassPane;
    }

    public final void calculatePoints() {
        if (worker != null) {
            if (!worker.isDone()) {
                worker.cancel(true);
            }
        }
        worker = new SimilarDescriptorsSwingWorker();
        worker.execute();
    }

    public void recalculateDescriptors() {
        calculateWorker = new CalculateDescriptorsSwingWorker();
        calculateWorker.execute();
    }

    public void setDistanceTreshold(double minimumTreshold, double maximumTreshold) {
        minTreshold = minimumTreshold;
        maxTreshold = maximumTreshold;
        
        calculateWorker = new CalculateDescriptorsSwingWorker();
        calculateWorker.execute();
    }

    public double[] getDistanceTreshold() {
        double[] tresholdRange = {minTreshold, maxTreshold};
        return tresholdRange;
    }

    public void setDescriptorPoint(Point point, int paneNumber) {
        if (nearestDescriptorPoint != null && point == null) {
            pointer = 0;
            nearestDescriptorPoint = null;
            repaint();
        }
        if (point != null && !point.equals(nearestDescriptorPoint)) {
                pointer = 0;
                nearestDescriptorPoint = point;
                this.paneNumber = paneNumber;
                repaint();
        }
    }

    public void undoClickedSimilar() {
        if (!undoClickedDescriptors.isEmpty()) {
            ObjectFeature[] undoDescriptors =
                    undoClickedDescriptors.get(undoClickedDescriptors.size() - 1);
            similarDescriptors.add(undoDescriptors);
            if (redoClickedDescriptors.size() == LocalDescriptors.MAX_NUMBER_OF_REDO_OPERATIONS) {
                redoClickedDescriptors.remove(0);
            }
            redoClickedDescriptors.add(undoDescriptors);
            undoClickedDescriptors.remove(undoDescriptors);
            calculatePoints();
        }
    }

    public void redoClickedSimilar() {
        if (!redoClickedDescriptors.isEmpty()) {
            ObjectFeature[] redoDescriptors =
                    redoClickedDescriptors.get(redoClickedDescriptors.size() - 1);
            similarDescriptors.remove(redoDescriptors);
            if (undoClickedDescriptors.size() == LocalDescriptors.MAX_NUMBER_OF_UNDO_OPERATIONS) {
                undoClickedDescriptors.remove(0);
            }
            undoClickedDescriptors.add(redoDescriptors);
            redoClickedDescriptors.remove(redoDescriptors);
            calculatePoints();
        }
    }
    
    public void hideSelectedSimilar() {
        if (similarPoints != null) {
            Iterator<ObjectFeature[]> iterator= nearestVisibleSimilarDescriptors.iterator();
            Point mainPanelCorner = firstScrollPane.getParentMainFrame().getMainPanel().getLocation();
            while (iterator.hasNext()) {
                ObjectFeature[] hooveredDescriptors = iterator.next();

                Point firstLocationOnGlasspane = getLocationOnGlassPane(
                        firstDescriptors.getDescriptorPoint(hooveredDescriptors[0]),
                        getScrollPaneCorner(firstScrollPane), mainPanelCorner,
                        getViewportRange(firstScrollPane),
                        firstScrollPane.getImagePanel().getZoomScale());

                Point secondLocationOnGlasspane = getLocationOnGlassPane(
                        secondDescriptors.getDescriptorPoint(hooveredDescriptors[1]),
                        getScrollPaneCorner(secondScrollPane), mainPanelCorner,
                        getViewportRange(secondScrollPane),
                        secondScrollPane.getImagePanel().getZoomScale());

                if (similarPoints[0].equals(firstLocationOnGlasspane) &&
                        similarPoints[1].equals(secondLocationOnGlasspane)) {
                    if (undoClickedDescriptors.size() == LocalDescriptors.MAX_NUMBER_OF_UNDO_OPERATIONS) {
                        undoClickedDescriptors.remove(0);
                    }
                    undoClickedDescriptors.add(hooveredDescriptors);
                    similarDescriptors.remove(hooveredDescriptors);
                    calculatePoints();
                    return;
                }
            }
        }
    }

    public void switchSimilarDescriptor() {
        pointer++;
        repaint();
    }

    public int getFirstPanelDescriptorsCount() {
        Iterator<Point[]> iterator = pointsOfSimilarDescriptors.iterator();
        Set<Point> points = new HashSet<Point>();
        while (iterator.hasNext()) {
            points.add(iterator.next()[0]);
        }
        return points.size();
    }

    public int getSecondPanelDescriptorsCount() {
        Iterator<Point[]> iterator = pointsOfSimilarDescriptors.iterator();
        Set<Point> points = new HashSet<Point>();
        while (iterator.hasNext()) {
            points.add(iterator.next()[1]);
        }
        return points.size();
    }

    public int getTotalSimilarCount() {
        return similarDescriptors.size();
    }

    public int getVisibleSimilarCount() {
        return pointsOfSimilarDescriptors.size();
    }

    ///////////////////////////////////////////////////////////////
    /////////// SWING WORKER TO CALCULATE DESCRIPTORS /////////////
    ////////////////// CORRESPONDING TO TRESHOLD //////////////////
    ///////////////////////////////////////////////////////////////
    private class CalculateDescriptorsSwingWorker extends
            SwingWorker<List<ObjectFeature[]>, Void> {

        StatusPanel panel = null;

        @Override
        protected List<ObjectFeature[]> doInBackground() {
            double minimumTreshold = minTreshold;
            double maximumTreshold = maxTreshold;
            
            Set<ObjectFeature> secondPaneSet = secondDescriptors.getVisibleDescriptors();

            List<ObjectFeature[]> similarDescriptors =
                    new ArrayList<ObjectFeature[]>();

            Iterator<ObjectFeature> firstIterator = 
                    firstDescriptors.getVisibleDescriptors().iterator();
            Iterator<ObjectFeature> secondIterator;
        
            boolean cancelInvoked = false;

            if (firstIterator.hasNext()) {
                if (panel == null) {
                    panel = new StatusPanel();
                } else {
                    panel.dispose();
                    panel = new StatusPanel();
                }

                while (firstIterator.hasNext()) {
                    if (isCancelled()) {
                        cancelInvoked = true;
                        break;
                    }
                    ObjectFeature firstDescriptor = firstIterator.next();
                    secondIterator = secondPaneSet.iterator();
                    while (secondIterator.hasNext()) {
                        if (isCancelled()) {
                            cancelInvoked = true;
                            break;
                        }
                        ObjectFeature secondDescriptor = secondIterator.next();
                        double distance = (double) firstDescriptor.getDistance(secondDescriptor);
                        if (distance <= maximumTreshold && distance >= minimumTreshold) {
                            ObjectFeature[] correspondingDescriptors =
                                {firstDescriptor, secondDescriptor};
                            similarDescriptors.add(correspondingDescriptors);
                        }
                    }
                }
                if (cancelInvoked) {
                    return null;
                }
                return similarDescriptors;
            }
            return null;
        }

        @Override
        protected void done() {
            if (panel != null) {
                    panel.dispose();
            }

            if (!isCancelled()) {
                try {
                    if (get() != null) {
                        similarDescriptors = get();
                    } else {
                        similarDescriptors.clear();
                    }
                    worker = new SimilarDescriptorsSwingWorker();
                    worker.execute();
                } catch (InterruptedException ignore) {
                } catch (ExecutionException ignore) {
                }
            } else {
                similarDescriptors.clear();
            }
        }

        private class StatusPanel extends JDialog {

            public StatusPanel() {
                JButton cancelButton = new JButton();
                cancelButton.setText(localLanguage.getString("cancel_button"));
                cancelButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelActionPerformed();
                    }
                });

                setLayout(new BorderLayout());

                add(new JLabel(localLanguage.getString("calculate_worker_msg")), BorderLayout.CENTER);
                add(cancelButton, BorderLayout.SOUTH);
                addWindowListener(new WindowListener() {

                    public void windowOpened(WindowEvent e) {}

                    public void windowClosed(WindowEvent e) {}

                    public void windowIconified(WindowEvent e) {}

                    public void windowDeiconified(WindowEvent e) {}

                    public void windowActivated(WindowEvent e) {}

                    public void windowDeactivated(WindowEvent e) {}

                    public void windowClosing(WindowEvent e) {
                        cancelActionPerformed();
                    }
                });

                setSize(new Dimension(250,110));
                setTitle(localLanguage.getString("calculate_worker_title"));

                setLocation(new Point(
                   (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2) - 120,
                   (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2) - 60));

                setResizable(false);
                setAlwaysOnTop(true);
                setVisible(true);
                pack();
            }

            private void cancelActionPerformed() {
                calculateWorker.cancel(false);
                dispose();
            }
        }
    }

    ///////////////////////////////////////////////////////////////
    /////////// SWING WORKER TO CALCULATE POINTS //////////////////
    ///////////////////////////////////////////////////////////////
    private class SimilarDescriptorsSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() {
            List<Point[]> visibleDescriptors = new ArrayList<Point[]>();

            Rectangle firstVisibleZone = getViewportRange(firstScrollPane);
            Rectangle secondVisibleZone = getViewportRange(secondScrollPane);

            Point firstScrollPaneCorner = getScrollPaneCorner(firstScrollPane);
            Point secondScrollPaneCorner = getScrollPaneCorner(secondScrollPane);

            Point mainPanelCorner = firstScrollPane.getParentMainFrame().getMainPanel().getLocation();

            ObjectFeature firstDescriptor;
            Point firstDescriptorPoint;
            Point firstLocationOnGlassPane;

            ObjectFeature secondDescriptor;
            Point secondDescriptorPoint;
            Point secondLocationOnGlassPane;

            Iterator<ObjectFeature[]> iterator = similarDescriptors.iterator();

            double firstZoomScale = firstDescriptors.getParentImagePanel().getZoomScale();
            double secondZoomScale = secondDescriptors.getParentImagePanel().getZoomScale();

            while (iterator.hasNext()) {
                ObjectFeature[] arrayOfDescriptors = iterator.next();
                firstDescriptor = arrayOfDescriptors[0];
                secondDescriptor = arrayOfDescriptors[1];

                firstDescriptorPoint = firstDescriptors.getDescriptorPoint(firstDescriptor);
                firstLocationOnGlassPane = getLocationOnGlassPane(firstDescriptorPoint,
                            firstScrollPaneCorner, mainPanelCorner, firstVisibleZone, firstZoomScale);

                secondDescriptorPoint = secondDescriptors.getDescriptorPoint(secondDescriptor);
                secondLocationOnGlassPane = getLocationOnGlassPane(secondDescriptorPoint,
                                secondScrollPaneCorner, mainPanelCorner, secondVisibleZone,
                                secondZoomScale);

                if (firstLocationOnGlassPane != null && secondLocationOnGlassPane != null) {
                            Point[] points = {firstLocationOnGlassPane, secondLocationOnGlassPane};
                            visibleDescriptors.add(points);
                }
            }
            pointsOfSimilarDescriptors = visibleDescriptors;
            firstScrollPane.setDescriptorsLabels();
            secondScrollPane.setDescriptorsLabels();
            firstScrollPane.getParentMainFrame().setGlasspaneLabels();
            return null;
        }

        @Override
        public void done() {
            if (!isCancelled()) {
                pointer = 0;
                firstScrollPane.getParentMainFrame().repaintAll();
            }
        }
    }

        /**
         * Get viewport range of scroll pane - required to compute which lines of
         * comparation will be drawn
         * @param scrollPane parent pane of viewport
         * @return Rectangle of viewport range
         */
        public Rectangle getViewportRange(ImageScrollPane scrollPane) {
            double zoomScale = scrollPane.getImagePanel().getZoomScale();
            Rectangle rect = scrollPane.getScrollPane().getViewport().getViewRect();
            return new Rectangle(
                    (int)(rect.getX() / zoomScale),
                    (int) (rect.getY() / zoomScale),
                    (int) (rect.getWidth() / zoomScale),
                    (int) (rect.getHeight() / zoomScale));
        }

        /**
         * Get left upper corner of image scroll pane.
         * + 1 is from border of viewport
         * @param scrollPane
         * @return Point of left upper corner of image scroll pane
         */
        public Point getScrollPaneCorner(ImageScrollPane scrollPane) {
            return new Point(
                    (int) (scrollPane.getScrollPane().getX() + scrollPane.getX()
                    + scrollPane.getParentMainFrame().getContentPane().getX() + 1),
                    (int) (scrollPane.getScrollPane().getY() + scrollPane.getY()
                    + scrollPane.getParentMainFrame().getContentPane().getY() + 1));
        }

        /**
         * Get location of descriptor on glass pane
         * @param descriptorPoint
         * @param corner of scroll pane
         * @return Point where to paint on glass pane or null if Point is not visible
         */
        public Point getLocationOnGlassPane(Point descriptorPoint, Point corner, 
                Point mainPanelCorner, Rectangle visibleZone, double zoomScale) {
            double xDifference;
            double yDifference;
            if (visibleZone.contains(descriptorPoint)) {
                xDifference = (descriptorPoint.getX() - visibleZone.getX()) * zoomScale;
                yDifference = (descriptorPoint.getY() - visibleZone.getY()) * zoomScale;
                return new Point(
                    Double.valueOf(corner.getX() + mainPanelCorner.getX() + xDifference).intValue(),
                    Double.valueOf(corner.getY() + mainPanelCorner.getY() + yDifference).intValue());
            } else {
                return null;
            }
        }
}