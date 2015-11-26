package cz.muni.fi.xlabuda;
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import messif.objects.LocalAbstractObject;
import messif.objects.impl.ObjectFeature;
import javax.swing.JFrame;
import javax.swing.OverlayLayout;

/**
 * Panel for for visualization of image with descriptors.
 * 
 * @author Marian Labuda
 * @version 1.0
 */
public class ImageScrollPane extends JPanel {

    public static final int ICON_WIDTH = 24;
    public static final int ICON_HEIGHT = 24;

    private ResourceBundle localLanguage;

    private MainFrame mainFrame;
    private ImagePanel imagePanel;

    private JPanel labelsPanel;
    private JPanel northLabelPanel;
    private JPanel southLabelPanel;

    private JPanel buttonsPanel;
    //top panel contains both labels and buttons panels
    private JPanel topPanel;
    private JPanel upperButtonPanel;
    private JPanel bottomButtonPanel;
    private JScrollPane scrollPane;

    private JLabel fileLabel;
    private JLabel zoomLabel;
    private JLabel sizeLabel;
    private JLabel loadedDescriptorsLabel;
    private JLabel visibleDescriptorsLabel;
    
    private JButton openImageButton;
    private JButton openDatasetButton;
    private JButton setZoomButton;
    private JButton fitToScreenButton;
    private JButton fitToWidthButton;
    private JButton fitToHeightButton;
    private JButton undoButton;
    private JButton redoButton;
    private JButton scaleFilterButton;
    private JButton colorButton;
    private JButton sizeButton;
    private JButton defaultButton;
    
    private ProjectionPanel bottomProjectionPanel;
    private ProjectionPanel sideProjectionPanel;
    private JButton switchProjectionsPanels;
    
    private int order;
    
    private JFrame glassTest = new JFrame("GlassPane");

    public ImageScrollPane(ImagePanel imagePane, MainFrame frame, int order) {
        this.order = order;
        localLanguage = ResourceBundle.getBundle("ImageScrollPane", MainFrame.locale);
        mainFrame = frame;
        imagePanel = imagePane;

        bottomProjectionPanel = new ProjectionPanel();
        sideProjectionPanel = new ProjectionPanel();
        switchProjectionsPanels = new JButton();
        
        
        
        topPanel = new JPanel();

        labelsPanel = new JPanel();
        northLabelPanel = new JPanel();
        southLabelPanel = new JPanel();
        fileLabel = new JLabel();
        zoomLabel = new JLabel();
        sizeLabel = new JLabel();
        loadedDescriptorsLabel = new JLabel();
        visibleDescriptorsLabel = new JLabel();

        java.net.URL url;
        
        url = getClass().getResource("icons/open-image.png");
        ImageIcon imageIcon = new ImageIcon(url);
        Image newImageImage = imageIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newImageIcon = new ImageIcon(newImageImage);

        url = getClass().getResource("icons/descriptors-dataset-icon.png");
        ImageIcon datasetIcon = new ImageIcon(url);
        Image newDatasetImage = datasetIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newDatasetIcon = new ImageIcon(newDatasetImage);

        url = getClass().getResource("icons/zoom-icon.png");
        ImageIcon zoomIcon = new ImageIcon(url);
        Image newZoomImage = zoomIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newZoomIcon = new ImageIcon(newZoomImage);

        url = getClass().getResource("icons/fit-to-width.png");
        ImageIcon fitToWidthIcon = new ImageIcon(url);
        Image newFitToWidthImage = fitToWidthIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newFitToWidthIcon = new ImageIcon(newFitToWidthImage);

        url = getClass().getResource("icons/fit-to-height.png");
        ImageIcon fitToHeightIcon = new ImageIcon(url);
        Image newFitToHeightImage = fitToHeightIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newFitToHeightIcon = new ImageIcon(newFitToHeightImage);

        url = getClass().getResource("icons/fit-to-screen.png");
        ImageIcon fitToScreenIcon = new ImageIcon(url);
        Image newFitToScreenImage = fitToScreenIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newFitToScreenIcon = new ImageIcon(newFitToScreenImage);

        url = getClass().getResource("icons/undo-icon.png");
        ImageIcon undoIcon = new ImageIcon(url);
        Image newUndoImage = undoIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newUndoIcon = new ImageIcon(newUndoImage);

        url = getClass().getResource("icons/redo-icon.png");
        ImageIcon redoIcon = new ImageIcon(url);
        Image newRedoImage = redoIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newRedoIcon = new ImageIcon(newRedoImage);

        url = getClass().getResource("icons/color-icon.png");
        ImageIcon colorIcon = new ImageIcon(url);
        Image newColorImage = colorIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newColorIcon = new ImageIcon(newColorImage);

        url = getClass().getResource("icons/size-icon.jpg");
        ImageIcon sizeIcon = new ImageIcon(url);
        Image newSizeImage = sizeIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newSizeIcon = new ImageIcon(newSizeImage);

        url = getClass().getResource("icons/size-filter-icon.png");
        ImageIcon sizeFilterIcon = new ImageIcon(url);
        Image newSizeFilterImage = sizeFilterIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newSizeFilterIcon = new ImageIcon(newSizeFilterImage);

        url = getClass().getResource("icons/default-icon.png");
        ImageIcon defaultIcon = new ImageIcon(url);
        Image newDefaultImage = defaultIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newDefaultIcon = new ImageIcon(newDefaultImage);

        buttonsPanel = new JPanel(new BorderLayout());
        upperButtonPanel = new JPanel(new FlowLayout());
        bottomButtonPanel = new JPanel(new FlowLayout());

        openImageButton = new JButton(newImageIcon);
        openDatasetButton = new JButton(newDatasetIcon);
        setZoomButton = new JButton(newZoomIcon);
        fitToWidthButton = new JButton(newFitToWidthIcon);
        fitToHeightButton = new JButton(newFitToHeightIcon);
        fitToScreenButton = new JButton(newFitToScreenIcon);

        undoButton = new JButton(newUndoIcon);
        redoButton = new JButton(newRedoIcon);
        colorButton = new JButton(newColorIcon);
        sizeButton = new JButton(newSizeIcon);
        scaleFilterButton = new JButton(newSizeFilterIcon);
        defaultButton = new JButton(newDefaultIcon);

        openImageButton.setToolTipText(localLanguage.getString("open_image_button"));
        openImageButton.setFocusTraversalKeysEnabled(false);
        openImageButton.setFocusPainted(false);
        openImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openImageButtonActionPerformed();
            }
        });

        openDatasetButton.setToolTipText(localLanguage.getString("open_dataset_button"));
        openDatasetButton.setFocusTraversalKeysEnabled(false);
        openDatasetButton.setFocusPainted(false);
        openDatasetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDatasetButtonActionPerformed();
            }
        });
        openDatasetButton.setEnabled(false);

        setZoomButton.setToolTipText(localLanguage.getString("zoom_size_button"));
        setZoomButton.setFocusTraversalKeysEnabled(false);
        setZoomButton.setFocusPainted(false);
        setZoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomButtonActionPerformed();
            }
        });
        setZoomButton.setEnabled(false);     

        fitToWidthButton.setToolTipText(localLanguage.getString("fit_to_width_button"));
        fitToWidthButton.setFocusTraversalKeysEnabled(false);
        fitToWidthButton.setFocusPainted(false);
        fitToWidthButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fitToWidthButtonActionPerformed();
            }
        });
        fitToWidthButton.setEnabled(false);

        fitToHeightButton.setToolTipText(localLanguage.getString("fit_to_height_button"));
        fitToHeightButton.setFocusTraversalKeysEnabled(false);
        fitToHeightButton.setFocusPainted(false);
        fitToHeightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fitToHeightButtonActionPerformed();
            }
        });
        fitToHeightButton.setEnabled(false);

        fitToScreenButton.setToolTipText(localLanguage.getString("fit_to_screen_button"));
        fitToScreenButton.setFocusTraversalKeysEnabled(false);
        fitToScreenButton.setFocusPainted(false);
        fitToScreenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fitToScreenButtonActionPerformed();
            }
        });
        fitToScreenButton.setEnabled(false);

        undoButton.setToolTipText(localLanguage.getString("undo_clicked_button"));
        undoButton.setFocusTraversalKeysEnabled(false);
        undoButton.setFocusPainted(false);
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                undoButtonActionPerformed();
            }
        });
        undoButton.setEnabled(false);

        redoButton.setToolTipText(localLanguage.getString("redo_clicked_button"));
        redoButton.setFocusTraversalKeysEnabled(false);
        redoButton.setFocusPainted(false);
        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                redoButtonActionPerformed();
            }
        });
        redoButton.setEnabled(false);

        colorButton.setToolTipText(localLanguage.getString("color_button"));
        colorButton.setFocusTraversalKeysEnabled(false);
        colorButton.setFocusPainted(false);
        colorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                colorButtonActionPerformed();
            }
        });
        colorButton.setEnabled(false);
        
        sizeButton.setToolTipText(localLanguage.getString("size_button"));
        sizeButton.setFocusTraversalKeysEnabled(false);
        sizeButton.setFocusPainted(false);
        sizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sizeButtonActionPerformed();
            }
        });
        sizeButton.setEnabled(false);

        scaleFilterButton.setToolTipText(localLanguage.getString("size_filter_button"));
        scaleFilterButton.setFocusTraversalKeysEnabled(false);
        scaleFilterButton.setFocusPainted(false);
        scaleFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaleFilterButtonActionPerformed();
            }
        });
        scaleFilterButton.setEnabled(false);

        defaultButton.setToolTipText(localLanguage.getString("default_button"));
        defaultButton.setFocusTraversalKeysEnabled(false);
        defaultButton.setFocusPainted(false);
        defaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                defaultButtonActionPerformed();
            }
        });
        defaultButton.setEnabled(false);

        scrollPane = new JScrollPane(imagePanel);

        scrollPane.getHorizontalScrollBar().addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (mainFrame.isShowSimilarDescriptorsMode()) {
                    GlasspaneForSimilarDescriptors glassPane =
                            (GlasspaneForSimilarDescriptors) mainFrame.getGlassPane();
                    glassPane.calculatePoints();
                }
                mainFrame.repaintAll();
            }
        });

        scrollPane.getVerticalScrollBar().addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (mainFrame.isShowSimilarDescriptorsMode()) {
                    GlasspaneForSimilarDescriptors glassPane =
                            (GlasspaneForSimilarDescriptors) mainFrame.getGlassPane();
                    glassPane.calculatePoints();
                }
                mainFrame.repaintAll();
            }
        });

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        scrollPane.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    if (!e.isShiftDown()) {
                        scrollPane.setWheelScrollingEnabled(false);
                        zoomImagePanel(e.getWheelRotation(), imagePanel);
                        mainFrame.repaintAll();
                        scrollPane.setWheelScrollingEnabled(true);
                    } else {
                        if (mainFrame.getSecondScrollPane().getImagePanel().getImage() != null) {
                            zoomBothImagePanels(mainFrame, e.getWheelRotation());
                        } 
                    }
                }
            }
        });

        labelsPanel.setLayout(new BorderLayout());
        labelsPanel.add(northLabelPanel, BorderLayout.NORTH);
        labelsPanel.add(southLabelPanel, BorderLayout.SOUTH);
        northLabelPanel.add(fileLabel);
        northLabelPanel.add(sizeLabel);
        northLabelPanel.add(zoomLabel);
        southLabelPanel.add(loadedDescriptorsLabel);
        southLabelPanel.add(visibleDescriptorsLabel);

        buttonsPanel.setLayout(new BorderLayout());
        buttonsPanel.add(upperButtonPanel, BorderLayout.NORTH);
        buttonsPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        upperButtonPanel.add(openImageButton);
        upperButtonPanel.add(openDatasetButton);
        upperButtonPanel.add(setZoomButton);
        upperButtonPanel.add(fitToScreenButton);
        upperButtonPanel.add(fitToWidthButton);
        upperButtonPanel.add(fitToHeightButton);

        bottomButtonPanel.add(undoButton);
        bottomButtonPanel.add(redoButton);
        bottomButtonPanel.add(colorButton);
        bottomButtonPanel.add(sizeButton);
        bottomButtonPanel.add(scaleFilterButton);
        bottomButtonPanel.add(defaultButton);
        
        
        topPanel.setLayout(new BorderLayout());
        topPanel.add(labelsPanel, BorderLayout.NORTH);
        topPanel.add(buttonsPanel, BorderLayout.CENTER);
        
        
        setLayout(new BorderLayout());

        setDefaultLabels();

        add(topPanel, BorderLayout.NORTH);  
        
        
        add(scrollPane, BorderLayout.CENTER);
        add(sideProjectionPanel, BorderLayout.WEST);
        add(bottomProjectionPanel, BorderLayout.SOUTH);
        
        sideProjectionPanel.setVisible(false);
        bottomProjectionPanel.setVisible(false);
      /*  JPanel back = new JPanel();
        back.setLayout(new BorderLayout());
        back.add(switchProjectionsPanels, BorderLayout.EAST);
       
        back.add(bottomProjectionPanel, BorderLayout.WEST);
        add(back, BorderLayout.SOUTH );
        */
        // System.out.println("Height je: " + back.getSize().height);
        /*
        bottomProjectionPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    System.out.println("mouseClicked");
                }});
        */
        setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        
       
    
        
       /*
        JPanel back = new JPanel();
        
        LayoutManager overlay = new OverlayLayout(back);
        back.setLayout(overlay);
       // back.setLayout(new OverlayLayout());
        back.setBackground(Color.green); 
        
        JPanel front = new JPanel();
        front.setLayout(new BorderLayout());
        front.setBackground(Color.red);
        front.add(scrollPane, BorderLayout.CENTER);
        front.add(sideProjectionPanel, BorderLayout.WEST);
        front.add(bottomProjectionPanel, BorderLayout.SOUTH );
        front.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        
        ProjectionGlassPane glassPane = new ProjectionGlassPane();
        
        back.add(front, BorderLayout.CENTER);
        back.add(glassPane, BorderLayout.WEST);
        
        
        
        add(back);*/
        /////////////*  /*
        /*
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new BorderLayout());
        //layeredPane.setBackground(Color.green);
        //layeredPane.setPreferredSize(new Dimension(200, 200));
        
        JPanel front = new JPanel();
        front.setLayout(new BorderLayout());
        
        front.setBounds(0, 0, 100, 100);
        front.setBackground(Color.red);
        front.add(scrollPane, BorderLayout.CENTER);
        front.add(sideProjectionPanel, BorderLayout.WEST);
        front.add(bottomProjectionPanel, BorderLayout.SOUTH );
        front.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        
        ProjectionGlassPane glassPane = new ProjectionGlassPane();
        glassPane.setBackground( new Color(Color.TRANSLUCENT) );
        glassPane.setOpaque(false);
        glassPane.setBounds(50, 50, 100, 100);
        
        layeredPane.add(front, new Integer(1));
        layeredPane.add(glassPane, new Integer(2));
        
        add(layeredPane);
        */////////////

        /*
        
        JPanel panel = new JPanel();

       
        panel.setLayout(new BorderLayout());
        panel.add(topPanel, BorderLayout.NORTH);        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(sideProjectionPanel, BorderLayout.WEST);
        panel.add(bottomProjectionPanel, BorderLayout.SOUTH );
        panel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        add(panel);
        */
     //   glassTest.setBackground(Color.GREEN);
        

       // add(glassTest);
        
    }


    //*********** GET METHODS **********//

    /**
     * Get nested panel in scroll pane
     * @return ImagePanel panel containing image with descriptors
     */
    public ImagePanel getImagePanel() {
        return imagePanel;
    }

    /**
     * Get parent main frame
     * @return parent main frame
     */
    public MainFrame getParentMainFrame() {
        return mainFrame;
    }

    @Override
    public Dimension getPreferredSize() {
       if (mainFrame.getOneImageMode()) {
           return new Dimension(mainFrame.getContentPane().getWidth(),
                   mainFrame.getContentPane().getHeight());
        } else {
           return new Dimension(mainFrame.getContentPane().getWidth()/2,
                         mainFrame.getContentPane().getHeight()/2);
        }
    }

    /**
     * Get scroll pane
     * @return JScrollPane scroll pane which contains image with descriptors
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }


    //*************** SET METHODS ***************//

    /**
     * Set default file name label
     */
    public final void setDefaultFileLabel() {
        fileLabel.setText(localLanguage.getString("file") + "   ");
    }

    /**
     * Set default zoom size label
     */
    public final void setDefaultZoomLabel() {
        zoomLabel.setText(localLanguage.getString("zoom_size") + "   ");
    }

    /**
     * Set default image size label
     */
    public final void setDefaultSizeLabel() {
        sizeLabel.setText(localLanguage.getString("img_size") + "   ");
    } 

    public final void setDefaultDescriptorsLabels() {
        loadedDescriptorsLabel.setText(localLanguage.getString("descriptors_loaded"));
        visibleDescriptorsLabel.setText(localLanguage.getString("descriptors_visible"));
    }

    /**
     * Set all labels of image to default values
     */
    public final void setDefaultLabels() {
        setDefaultFileLabel();
        setDefaultZoomLabel();
        setDefaultSizeLabel();
        setDefaultDescriptorsLabels();
    }

    /**
     * State of the descriptors
     * @param loaded - true if loaded, false otherwise
     */
    public void setDescriptorsLoaded(boolean loaded) {
        undoButton.setEnabled(loaded);
        redoButton.setEnabled(loaded);
        scaleFilterButton.setEnabled(loaded);
        colorButton.setEnabled(loaded);
        sizeButton.setEnabled(loaded);
        defaultButton.setEnabled(loaded);
    }

    /**
     * Set file name
     * @param fileName name of loaded image
     */
    public void setFileLabel(String fileName) {
        fileLabel.setText(localLanguage.getString("file") + fileName + "    ");
    }

    /**
     * State of image in Image Panel
     * @param loaded - true if loaded, false otherwise
     */
    public void setImageLoaded(boolean loaded) {
        openDatasetButton.setEnabled(loaded);
        setZoomButton.setEnabled(loaded);
        fitToScreenButton.setEnabled(loaded);
        fitToWidthButton.setEnabled(loaded);
        fitToHeightButton.setEnabled(loaded);
    }    
    
    /**
     * Set zoom size
     * @param size current zoom size of image
     */
    public void setZoomLabel(String size) {
        zoomLabel.setText(localLanguage.getString("zoom_size") + size + "%" + "    ");
    }

    public void setSizeLabel(int width, int height) {
        sizeLabel.setText(localLanguage.getString("img_size") + width + "x" + height + "    ");
    }

    public void setDescriptorsLabels() {
        LocalDescriptors descriptors = imagePanel.getDescriptors();
        if (descriptors != null) {
            int totalCount = descriptors.getDescriptors().getObjectCount();

            int visibleCount;

            if (!mainFrame.isShowSimilarDescriptorsMode()) {
                visibleCount = descriptors.getVisibleDescriptors().size();
                visibleDescriptorsLabel.setText(
                    localLanguage.getString("descriptors_visible") + visibleCount);
            } else {
                GlasspaneForSimilarDescriptors glasspane = 
                        (GlasspaneForSimilarDescriptors) mainFrame.getGlassPane();
                if (mainFrame.getFirstScrollPane().equals(this)) {
                    visibleCount = glasspane.getFirstPanelDescriptorsCount();
                } else {
                    visibleCount = glasspane.getSecondPanelDescriptorsCount();
                }
                visibleDescriptorsLabel.setText(
                    localLanguage.getString("descriptors_similar") + visibleCount);
            }

            loadedDescriptorsLabel.setText(
                    localLanguage.getString("descriptors_loaded") + totalCount);
        }
    }

    //**************** OTHER METHODS *******************//

    /**
     * Zoom on cursor (mouse pointer)
     * @param rotationsDone wheel rotations done
     * @param panel panel where to zoom
     */
    public static void zoomImagePanel(int rotationsDone, ImagePanel panel) {
        JViewport viewport = panel.getParentImageScrollPane().getScrollPane().getViewport();
        Rectangle viewRectangle = viewport.getViewRect();

        double absolutePositionX = (viewRectangle.getX() + (viewRectangle.getWidth() / 2)) /
                (panel.getImage().getWidth() * panel.getZoomScale());
        double absolutePositionY = (viewRectangle.getY() + (viewRectangle.getHeight() / 2)) /
                (panel.getImage().getHeight() * panel.getZoomScale());

        Point viewportCenter;
        int xAxis, yAxis;

        if (rotationsDone < 0) {
            for (int i=-1; i>=rotationsDone; i--) {

                if (viewport.getView().getMousePosition() != null) {
                    // Mouse location on the panel multiplied by scale
                    Point mouseLocation = new Point(
                        Double.valueOf(viewport.getView().getMousePosition().getX() *
                        ImagePanel.ZOOM_FACTOR).intValue(),
                        Double.valueOf(viewport.getView().getMousePosition().getY() *
                        ImagePanel.ZOOM_FACTOR).intValue());

                    // xAxis and yAxis of new view position
                    xAxis = Double.valueOf(mouseLocation.getX() -
                            (viewRectangle.getWidth() / 2)).intValue();
                    yAxis = Double.valueOf(mouseLocation.getY() -
                            (viewRectangle.getHeight() / 2)).intValue();
                } else {
                    xAxis = Integer.MIN_VALUE;
                    yAxis = Integer.MIN_VALUE;
                }

                panel.zoomIn();
                

                if (xAxis > 0) {
                    if (yAxis > 0) {
                        viewport.setViewPosition(
                                new Point(xAxis, yAxis));
                    } else {
                        viewport.setViewPosition(
                                new Point(xAxis, 0));
                    }
                } else {
                    if (yAxis > 0) {
                        viewport.setViewPosition(
                                new Point(0, yAxis));
                    } else {
                        if (xAxis == Integer.MIN_VALUE && yAxis == Integer.MIN_VALUE) {
                            viewportCenter = new Point(
                                Double.valueOf(panel.getImage().getWidth() * panel.getZoomScale() *
                                absolutePositionX - (viewRectangle.getWidth() / 2)).intValue(),
                                Double.valueOf(panel.getImage().getHeight() * panel.getZoomScale() *
                                absolutePositionY - (viewRectangle.getHeight() / 2)).intValue());
                            viewport.setViewPosition(viewportCenter);
                        } else {
                            viewport.setViewPosition(new Point(0,0));
                        }
                    }
                }
            }
        } else {
            for (int i=1; i<=rotationsDone; i++) {
                panel.zoomOut();
                viewportCenter = new Point(
                        Double.valueOf(panel.getImage().getWidth() * panel.getZoomScale() *
                        absolutePositionX - (viewRectangle.getWidth() / 2)).intValue(),
                        Double.valueOf(panel.getImage().getHeight() * panel.getZoomScale() *
                        absolutePositionY - (viewRectangle.getHeight() / 2)).intValue());
                viewport.setViewPosition(viewportCenter);
            }
        }
    }

    /**
     * Zoom on the first and on the second scroll pane of the main frame
     * @param frame containing both scroll panels
     * @param wheelRotationCount
     */
    public static void zoomBothImagePanels(MainFrame frame, int wheelRotationCount) {
        frame.getSecondScrollPane().getScrollPane().setWheelScrollingEnabled(false);
        frame.getFirstScrollPane().getScrollPane().setWheelScrollingEnabled(false);
        zoomImagePanel(wheelRotationCount,
        frame.getSecondScrollPane().getImagePanel());
        zoomImagePanel(wheelRotationCount,
        frame.getFirstScrollPane().getImagePanel());
        frame.getSecondScrollPane().getScrollPane().setWheelScrollingEnabled(true);
        frame.getFirstScrollPane().getScrollPane().setWheelScrollingEnabled(true);
    }

    public void scrollAndShift(double xShift, double yShift, boolean shiftDown)  {
        Point currentPosition = scrollPane.getViewport().getViewPosition();
        int xAxis = Double.valueOf(currentPosition.getX() + xShift).intValue();
        int yAxis = Double.valueOf(currentPosition.getY() + yShift).intValue();

        if (shiftDown) {
            imagePanel.removeRectangleForVisualization();
            if (imagePanel.getXPanelShift() - xShift >= 0) {
                imagePanel.setXPanelShift((int) (imagePanel.getXPanelShift() - xShift));
              // projectionPanel.setXPanelShift((int) (imagePanel.getXPanelShift() - xShift));
                

            }
            if (imagePanel.getYPanelShift() - yShift >= 0) {
                imagePanel.setYPanelShift((int) (imagePanel.getYPanelShift() - yShift));
              // projectionPanel.setYPanelShift((int) (imagePanel.getXPanelShift() - xShift));
            }
            imagePanel.repaint();
            scrollPane.getViewport().revalidate();
        } else {
            if (xAxis < 0) {
                xAxis = 0;
            }
            if (yAxis < 0) {
                yAxis = 0;
            }
            if (xAxis > imagePanel.getWidth() - scrollPane.getViewport().getWidth()) {
                xAxis = imagePanel.getWidth() - scrollPane.getViewport().getWidth();
            }
            if (yAxis > imagePanel.getHeight() - scrollPane.getViewport().getHeight()) {
                yAxis = imagePanel.getHeight() - scrollPane.getViewport().getHeight();
            }
            scrollPane.getViewport().setViewPosition(new Point(xAxis, yAxis));
        }
    }

    ////////////////////////////////////////////////////////
    //****************************************************//
    //***** Private button action performed methods  *****//
    //****************************************************//
    ////////////////////////////////////////////////////////

    private void openImageButtonActionPerformed() {
        int paneNumber;
        if (this.equals(mainFrame.getFirstScrollPane())) {
            paneNumber = MainFrame.FIRST_IMAGE_PANEL;
        } else {
            paneNumber = MainFrame.SECOND_IMAGE_PANEL;
        }
        ImageFileChooser fileChooser = new ImageFileChooser(imagePanel, paneNumber);
    }

    private void openDatasetButtonActionPerformed() {
        imagePanel.zoom(1);
        int paneNumber;
        if (this.equals(mainFrame.getFirstScrollPane())) {
            paneNumber = MainFrame.FIRST_IMAGE_PANEL;
        } else {
            paneNumber = MainFrame.SECOND_IMAGE_PANEL;
        }
        DescriptorsDatasetFileChooser fileChooser =
                new DescriptorsDatasetFileChooser(imagePanel, paneNumber);
    }

    private void zoomButtonActionPerformed() {
        if (imagePanel.getImage() != null) {
            Dialogs.zoomFormDialog(mainFrame, imagePanel);
        }
    }

    private void fitToWidthButtonActionPerformed() {
        if (imagePanel.getImage() != null) {
            imagePanel.fitToWidth();
        }
    }

    private void fitToHeightButtonActionPerformed() {
        if (imagePanel.getImage() != null) {
            imagePanel.fitToHeight();
        }
    }

    private void fitToScreenButtonActionPerformed() {
        if (imagePanel.getImage() != null) {
            imagePanel.fitToScreen();
        }
    }

    private void undoButtonActionPerformed() {
        LocalDescriptors descriptors = imagePanel.getDescriptors();
        if (mainFrame.isShowSimilarDescriptorsMode()) {
            ((GlasspaneForSimilarDescriptors) mainFrame.getGlassPane()).undoClickedSimilar();
        } else {
            if (descriptors != null) {
                if (descriptors.undoOperationIsPossible()) {
                    descriptors.undoVisibleClickedDescriptor();
                }
            }
        }
    }

    private void redoButtonActionPerformed() {
        LocalDescriptors descriptors = imagePanel.getDescriptors();
        if (mainFrame.isShowSimilarDescriptorsMode()) {
            ((GlasspaneForSimilarDescriptors) mainFrame.getGlassPane()).redoClickedSimilar();
        } else {
            if (descriptors != null) {
                if (descriptors.redoOperationIsPossible()) {
                    descriptors.redoVisibleClickedDescriptors();
                }
            }
        }
    }

    private void colorButtonActionPerformed() {
        LocalDescriptors descriptors = imagePanel.getDescriptors();
        if (descriptors != null) {
            Dialogs.colorChooserDialog(mainFrame, descriptors);
        }
    }

    private void sizeButtonActionPerformed() {
        LocalDescriptors descriptors = imagePanel.getDescriptors();
        if (descriptors != null) {
            Dialogs.shapeSizeSilderDialog(mainFrame, descriptors);
        }
    }

    private void scaleFilterButtonActionPerformed() {
        LocalDescriptors descriptors = imagePanel.getDescriptors();
        if (descriptors != null) {
            Dialogs.scaleFilterDialog(this, descriptors);
        }
    }

    void setScaleFilterActive(double minValue, double maxValue) {
        String s = localLanguage.getString("size_filter_button");
        if (minValue != 0 || maxValue != 0) {
            s += String.format(" - showing %.1f - %.1f", minValue, maxValue);
        }
        scaleFilterButton.setToolTipText(s);
    }

    private void defaultButtonActionPerformed() {
        LocalDescriptors descriptors = imagePanel.getDescriptors();
        if (descriptors != null) {
            descriptors.setDefaultSettings();
        }
    }
    
    public ProjectionPanel getBottomProjectionPanel()
    {
        return bottomProjectionPanel;
    }
    
    public ProjectionPanel getSideProjectionPanel()
    {
        return sideProjectionPanel;
    }
    
    public int getOrder(){
        return order;
    }
    
    public void ToggleSideProjectionPanel(){
        if(sideProjectionPanel.isVisible()){
            sideProjectionPanel.setVisible(false);
        }
        else{
            sideProjectionPanel.setVisible(true);
        }
    }
    
    public void SetSideProjectionPanelVisible(){
            sideProjectionPanel.setVisible(true);
    }
    
    public void SetSideProjectionPanelInvisible(){
            sideProjectionPanel.setVisible(false);
    }
    
    public void SetBottomProjectionPanelVisible(){
            bottomProjectionPanel.setVisible(true);
    }
    
    public void SetBottomProjectionPanelInvisible(){
            bottomProjectionPanel.setVisible(false);
    }
    
    public void ToggleBottomProjectionPanel(){
        if(bottomProjectionPanel.isVisible()){
            bottomProjectionPanel.setVisible(false);
        }
        else{
            bottomProjectionPanel.setVisible(true);
        }
    }
    
    public boolean isProjectionVisible(){
        return bottomProjectionPanel.isVisible() || sideProjectionPanel.isVisible();
    }
    
    
    public int getTopPanelHeight(){
        return topPanel.getHeight();
    }
    
    public void resetProjection(){
        bottomProjectionPanel.setVisible(false);
        sideProjectionPanel.setVisible(false);
        getImagePanel().getDescriptors().cancelProjection();
    }

    
    
}