package cz.muni.fi.xlabuda;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import messif.objects.impl.ObjectFeature;
import messif.objects.impl.ObjectFeatureSet;
import messif.objects.util.SequenceMatchingCost;


/**
 * Glasspane for painting projections and similar descriptors using Needleman-Wunsh and Smith-Waterman.
 *
 * @author Tomas Oravec
 * @version 1.0
 */
public class ProjectionGlassPane extends JComponent {

    ObjectFeature descriptor;
    float value;

    ImageScrollPane imageScrollPane;
    ImageScrollPane secondImageScrollPane;
    ImageScrollPane activeImageScrollPane;
    MainFrame mainFrame;

    private boolean antialiasing = false;
    private Color colorHover = Color.RED;
    private Color colorLines = Color.YELLOW;

    private int pointSize = 5;
    private boolean pause = false;
    private boolean highlitedDescriptor = false;

    private enum Mode {
        singleImageProjection, twoImageVisualization;
    };
    Mode mode;

    private enum VisualizationMode {
        hover, threeLines, oneLine;
    };
    VisualizationMode visualizationMode = VisualizationMode.threeLines;


    public ProjectionGlassPane(ImageScrollPane imageScrollPane, ImageScrollPane secondImageScrollPane, Color colorHoover, Color colorLines, boolean antialiasing) {
        this.imageScrollPane = imageScrollPane;
        this.secondImageScrollPane = secondImageScrollPane;
        activeImageScrollPane = imageScrollPane;
        mainFrame = imageScrollPane.getParentMainFrame();
        this.colorHover = colorHoover;
        this.colorLines = colorLines;
        this.antialiasing = antialiasing;
    }


    public void setActivePanel(ImageScrollPane isp) {
        activeImageScrollPane = isp;
    }

    public void setHooveredProjectionDescriptor(ObjectFeature descriptor, float value) {

        mode = Mode.singleImageProjection;
        this.descriptor = descriptor;
        this.value = value;

        repaint();

    }

    public void setHooveredComparisonDescriptor(ObjectFeature descriptor) {

        mode = Mode.twoImageVisualization;

        highlitedDescriptor = true;

        this.descriptor = descriptor;

        if (descriptor == null) {
            highlitedDescriptor = false;
            mainFrame.SetNWSWCurrentSimilarity(null);
        }
        repaint();

    }

    private Rectangle getViewportRange(ImageScrollPane scrollPane) {
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

    //gets descriptor position in global application coordinates
    private Point2D getDescriptorPosition(ImageScrollPane isp, ObjectFeature descriptor) {

        int xcord = Double.valueOf(isp.getImagePanel().getImage().getWidth() * descriptor.getX()).intValue();
        int ycord = Double.valueOf(isp.getImagePanel().getImage().getHeight() * descriptor.getY()).intValue();

        int xscroll = isp.getScrollPane().getHorizontalScrollBar().getValue();
        int yscroll = isp.getScrollPane().getVerticalScrollBar().getValue();

        int xshift = isp.getImagePanel().getXPanelShift();
        int yshift = isp.getImagePanel().getYPanelShift();

        xcord *= isp.getImagePanel().getZoomScale();
        ycord *= isp.getImagePanel().getZoomScale();
        xcord -= xscroll;
        ycord -= yscroll;
        xcord += xshift;
        ycord += yshift;

        if (isp == imageScrollPane) {
            xcord += imageScrollPane.getScrollPane().getX();
        }
        if (isp == secondImageScrollPane) {
            xcord += secondImageScrollPane.getScrollPane().getX();
            xcord += imageScrollPane.getWidth();
            xcord += 2;
        }
        ycord += imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight();
        ycord += activeImageScrollPane.getScrollPane().getLocation().y + activeImageScrollPane.getParentMainFrame().getMenuBarHeight();

        return new Point2D.Float(xcord, ycord);
    }

    private ImageScrollPane getOtherISP(ImageScrollPane isp) {
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

    //paints hoovered descriptors / lines between similar descriptors
    @Override
    public void paintComponent(Graphics g) {

        if (pause) {
            return;
        }

        imageScrollPane.getParentMainFrame().repaint();
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(colorLines);
        g2d.setStroke(new BasicStroke(1f));

        if (antialiasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
       
        if (descriptor != null || visualizationMode == VisualizationMode.threeLines || visualizationMode == VisualizationMode.oneLine) {

            List<ObjectFeature> firstDataList = new ArrayList<ObjectFeature>();

            if (mode == Mode.twoImageVisualization && (visualizationMode == VisualizationMode.threeLines || visualizationMode == VisualizationMode.oneLine)) {

                firstDataList = activeImageScrollPane.getBottomProjectionPanel().getComparisonDescriptors();
                if (firstDataList == null) {
                    return;
                }
                firstDataList.add(descriptor);
            } else {
                firstDataList.add(descriptor);
            }

            for (int j = 0; j < firstDataList.size(); j++) {
                ObjectFeature _descriptor = firstDataList.get(j);

                if (j == firstDataList.size() - 1 && highlitedDescriptor) {
                    g2d.setColor(colorHover);
                    g2d.setStroke(new BasicStroke(2));
                }

                if (_descriptor == null) {
                    continue;
                }

                boolean highlited = highlitedDescriptor && j == firstDataList.size() - 1;

                if (mode == Mode.twoImageVisualization) {
                    if (getOtherISP(activeImageScrollPane).getBottomProjectionPanel().getComparisonDescriptorAt(activeImageScrollPane.getBottomProjectionPanel().getComparisonDescriptorIndex(_descriptor)) == null
                            && visualizationMode != visualizationMode.hover && !highlited) {
                        continue;
                    }
                }

                if (mode == Mode.singleImageProjection) {
                    g2d.setColor(colorHover);
                    g2d.setStroke(new BasicStroke(2f));
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (!isInViewPort(_descriptor, activeImageScrollPane)) {
                        return;
                    } else {
                    }

                    Point2D descPos = getDescriptorPosition(activeImageScrollPane, _descriptor);
                    if (activeImageScrollPane.getBottomProjectionPanel().isVisible()) {

                        g2d.drawLine((int) descPos.getX(),
                                (int) descPos.getY(),
                                (int) value + activeImageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(activeImageScrollPane) + pointSize / 2,
                                activeImageScrollPane.getBottomProjectionPanel().getLocation().y + activeImageScrollPane.getLocation().y + activeImageScrollPane.getParentMainFrame().getMenuBarHeight() + pointSize / 2);
                    }
                    if (activeImageScrollPane.getSideProjectionPanel().isVisible()) {
                        g2d.drawLine((int) descPos.getX(),
                                (int) descPos.getY(),
                                10 + getSecondPaneAlligment(activeImageScrollPane) + pointSize / 2,
                                (int) value + activeImageScrollPane.getScrollPane().getLocation().y + activeImageScrollPane.getParentMainFrame().getMenuBarHeight() + pointSize / 2
                        );
                    }
                } else if (mode == Mode.twoImageVisualization) {
                    ObjectFeature descriptor2 = getOtherISP(activeImageScrollPane).getBottomProjectionPanel().getComparisonDescriptorAt(activeImageScrollPane.getBottomProjectionPanel().getComparisonDescriptorIndex(_descriptor));
                    Point2D descPos = getDescriptorPosition(activeImageScrollPane, _descriptor);
                    if (descriptor2 == null) {
                        mainFrame.SetNWSWCurrentSimilarity(null);
                        if (!(isInViewPort(_descriptor, activeImageScrollPane))) {
                            return;
                        }
                        if (activeImageScrollPane.getBottomProjectionPanel().isVisible()) {
                            g2d.drawLine((int) descPos.getX(),
                                    (int) descPos.getY(),
                                    (int) activeImageScrollPane.getBottomProjectionPanel().getComparisonDescriptorValueAt(_descriptor) + imageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(activeImageScrollPane) + (pointSize / 2),
                                    activeImageScrollPane.getBottomProjectionPanel().getLocation().y + activeImageScrollPane.getLocation().y + activeImageScrollPane.getParentMainFrame().getMenuBarHeight() + (pointSize / 2) + imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                            );
                        } else if (activeImageScrollPane.getSideProjectionPanel().isVisible()) {
                            g2d.drawLine((int) descPos.getX(),
                                    (int) descPos.getY(),
                                    10 + getSecondPaneAlligment(activeImageScrollPane) + pointSize / 2,
                                    (int) activeImageScrollPane.getSideProjectionPanel().getComparisonDescriptorValueAt(_descriptor) + activeImageScrollPane.getScrollPane().getLocation().y + activeImageScrollPane.getParentMainFrame().getMenuBarHeight() + pointSize / 2 + +mainFrame.getComparativePanelNWSWHeight()
                            );
                        }
                        return;
                    }
                    SequenceMatchingCost cost = SequenceMatchingCost.SIFT_DEFAULT;

                    if (highlited) {
                        mainFrame.SetNWSWCurrentSimilarity(_descriptor, descriptor2);
                    }

                    Point2D descPos2 = getDescriptorPosition(getOtherISP(activeImageScrollPane), descriptor2);

                    if (visualizationMode != VisualizationMode.oneLine
                            || j == firstDataList.size() - 1 && highlitedDescriptor) {
                        if (activeImageScrollPane.getBottomProjectionPanel().isVisible()) {
                            if ((isInViewPort(_descriptor, activeImageScrollPane))) {
                                g2d.drawLine((int) descPos.getX(),
                                        (int) descPos.getY(),
                                        (int) activeImageScrollPane.getBottomProjectionPanel().getComparisonDescriptorValueAt(_descriptor) + imageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(activeImageScrollPane) + pointSize / 2,
                                        activeImageScrollPane.getBottomProjectionPanel().getLocation().y + activeImageScrollPane.getLocation().y + activeImageScrollPane.getParentMainFrame().getMenuBarHeight() + (pointSize / 2) + imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                                );
                            }

                        } else if (activeImageScrollPane.getSideProjectionPanel().isVisible()) {
                            if ((isInViewPort(_descriptor, activeImageScrollPane))) {
                                g2d.drawLine((int) descPos.getX(),
                                        (int) descPos.getY(),
                                        10 + getSecondPaneAlligment(activeImageScrollPane) + pointSize / 2,
                                        (int) activeImageScrollPane.getSideProjectionPanel().getComparisonDescriptorValueAt(_descriptor) + activeImageScrollPane.getScrollPane().getLocation().y + activeImageScrollPane.getParentMainFrame().getMenuBarHeight() + pointSize / 2 + +mainFrame.getComparativePanelNWSWHeight()
                                );
                            }

                        }

                        if (getOtherISP(activeImageScrollPane).getBottomProjectionPanel().isVisible()) {
                            int value2 = getOtherISP(activeImageScrollPane).getBottomProjectionPanel().getComparisonDescriptorValueAt(descriptor2);
                            if ((isInViewPort(descriptor2, getOtherISP(activeImageScrollPane)))) {
                                g2d.drawLine((int) descPos2.getX(),
                                        (int) descPos2.getY(),
                                        (int) value2 + imageScrollPane.getScrollPane().getX() + getSecondPaneAlligment(getOtherISP(activeImageScrollPane)) + pointSize / 2,
                                        activeImageScrollPane.getBottomProjectionPanel().getLocation().y + activeImageScrollPane.getLocation().y + activeImageScrollPane.getParentMainFrame().getMenuBarHeight() + (pointSize / 2) + imageScrollPane.getParentMainFrame().getComparativePanelNWSWHeight()
                                );
                            }

                        } else if (getOtherISP(activeImageScrollPane).getSideProjectionPanel().isVisible()) {
                            int value2 = getOtherISP(activeImageScrollPane).getSideProjectionPanel().getComparisonDescriptorValueAt(descriptor2);
                            if ((isInViewPort(descriptor2, getOtherISP(activeImageScrollPane)))) {
                                g2d.drawLine((int) descPos2.getX(),
                                        (int) descPos2.getY(),
                                        10 + pointSize / 2 + getSecondPaneAlligment(getOtherISP(activeImageScrollPane)),
                                        (int) value2 + activeImageScrollPane.getScrollPane().getLocation().y + activeImageScrollPane.getParentMainFrame().getMenuBarHeight() + pointSize / 2 + mainFrame.getComparativePanelNWSWHeight()
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

    }

    public void setAntialiasing(boolean bool) {
        antialiasing = bool;
    }

    public Color getHooverColor() {
        return colorHover;
    }

    public Color getLinesColor() {
        return colorLines;
    }

    public void setHooverColor(Color color) {
        colorHover = color;
    }

    public void setLinesColor(Color color) {
        colorLines = color;
    }

    public void setVisualizationOneLine() {
        visualizationMode = VisualizationMode.oneLine;
        mode = Mode.twoImageVisualization;
        repaint();
    }

    public void setVisualizationThreeLines() {
        visualizationMode = VisualizationMode.threeLines;
        mode = Mode.twoImageVisualization;
        repaint();
    }

    public void setVisualizationHover() {
        visualizationMode = VisualizationMode.hover;
        mode = Mode.twoImageVisualization;
        repaint();
    }

    public void setPause(boolean bool) {
        pause = bool;
        repaint();
    }

    public void setModeSingleImage() {
        mode = Mode.singleImageProjection;
    }

    public void setModeTwoImageVisualisation() {
        mode = Mode.twoImageVisualization;
    }

    public void highlightNearestDescriptor(Point2D point, ImageScrollPane isp, int max_distance) {

        ObjectFeatureSet visibleDescriptors = isp.getImagePanel().getDescriptors().getDescriptors();
        Set<ObjectFeature> nearestDescriptors = new HashSet<ObjectFeature>();

        Iterator<ObjectFeature> iterator = visibleDescriptors.iterator();
        Point descriptorPoint;
        double shortestDistance = max_distance;
        double calculatedDistance;
        ObjectFeature result = null;

        while (iterator.hasNext()) {
            ObjectFeature descriptor = iterator.next();
            descriptorPoint = isp.getImagePanel().getDescriptors().getDescriptorPoint(descriptor);
            calculatedDistance = Point.distance(descriptorPoint.getX(),
                    descriptorPoint.getY(), point.getX(), point.getY());
            if (calculatedDistance < shortestDistance) {
                int index = isp.getBottomProjectionPanel().getComparisonDescriptorIndex(descriptor);
                if (index != -1) {
                    if (getOtherISP(isp).getBottomProjectionPanel().getComparisonDescriptorAt(index) != null) {
                        shortestDistance = calculatedDistance;
                        result = descriptor;

                    }
                }

            }
        }
        setActivePanel(isp);
        setHooveredComparisonDescriptor(result);
    }

}
