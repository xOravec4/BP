package cz.muni.fi.xlabuda;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import static java.util.Collections.list;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import messif.objects.impl.ObjectFeature;
import messif.objects.impl.ObjectFeatureSet;
import messif.objects.util.SequenceMatchingCost;
import messif.utility.ArrayResetableIterator;
import messif.utility.ResetableIterator;

import messif.objects.*;
import messif.objects.nio.BinaryInput;
import messif.objects.nio.BinarySerializator;
import messif.objects.util.SequenceMatchingCost;
import messif.objects.util.SortDimension;
import messif.utility.ArrayResetableIterator;
import messif.utility.ResetableIterator;

/**
 *
 * MainFrame - application for visualization of the local descriptors in images.
 *
 * @author Marian Labuda
 * @version 1.1
 */
public class MainFrame extends JFrame {

    //******************** Constants ********************//
    private int axis = 0;
    /** Main frame width in pixels */
    protected static final int MAIN_FRAME_WIDTH = 650;

    /** Main frame height in pixels */
    protected static final int MAIN_FRAME_HEIGHT = 335;
    
    protected static final int MAIN_FRAME_MINIMUM_WIDTH = 400;
    protected static final int MAIN_FRAME_MINIMUM_HEIGHT =  360;

    public static final int FIRST_IMAGE_PANEL = 1;
    public static final int SECOND_IMAGE_PANEL = 2;


    //******************** ATTRIBUTES ********************//

    /** Locale for application. New property file is required to add new locale */
    protected static Locale locale = new Locale("en", "US");

    private ResourceBundle localLanguage;

    /** Currently selected mode. True = one image mode. False = two images mode */
    private boolean oneImageMode = true;

    private boolean firstDescriptorsLoaded = false;
    private boolean secondDescriptorsLoaded = false;

    private boolean comparationMode = false;

    private MainFrame thisInstance =  this;

    //******* FRAME COMPONENTS *******//
    private JMenuBar menuBar;

    private JMenu fileMenu;
    private JMenu firstImageFile;
    private JMenu secondImageFile;
    private JMenu descriptorsType;

    private JMenu imageMenu;
    private JMenu firstImageControl;
    private JMenu secondImageControl;

    private JMenu descriptorsMenu;
    private JMenu firstImageDescriptors;
    private JMenu secondImageDescriptors;

    private ButtonGroup descriptorsTypeGroup;
    private Class<? extends LocalDescriptors>[] descriptorsTypes;
    
    private JMenu compareDescriptorsMenu;

    private JMenu projectionMenu; //moje
    private JMenu projectionFirstImage;
    private JMenu projectionSecondImage;
    private JMenuItem projectionFirstImageToX;
    private JMenuItem projectionFirstImageToY;
    private JMenuItem projectionSecondImageToX;
    private JMenuItem projectionSecondImageToY;
    private JMenuItem projectionFirstImageCustom;
    private JMenuItem projectionSecondImageCustom;
    private JMenuItem projectionTogglePositionFirstImage;
    private JMenuItem projectionTogglePositionSecondImage;
    
    private JMenuItem needlemanWunschButton;
    private JMenuItem smithWatermanButton;
    
    private JMenuItem projectionMenuSuitable;
    private JMenuItem projectionMenuCustom;
    private JMenuItem showSideProjectionPanel;
    private JMenuItem showBottomProjectionPanel;
    
    private JMenu modeMenu;

    private JMenu helpMenu;
     
    private JMenuItem loadImageItem;
    private JMenuItem loadSecondImageItem;
    private JMenuItem loadDescriptorsItem;
    private JMenuItem loadDescriptorsItem2;
    private JMenuItem loadDatasetDescriptorsItem;
    private JMenuItem loadDatasetDescriptorsItem2;

    private JMenuItem zoomItem;
    private JMenuItem zoomItem2;
    private JMenuItem zoomInItem;
    private JMenuItem zoomOutItem;
    private JMenuItem zoomInItem2;
    private JMenuItem zoomOutItem2;
    private JMenuItem defaultZoomItem;
    private JMenuItem defaultZoomItem2;

    private JMenuItem fitToWidthItem;
    private JMenuItem fitToWidthItem2;
    private JMenuItem fitToHeightItem;
    private JMenuItem fitToHeightItem2;
    private JMenuItem fitToScreenItem;
    private JMenuItem fitToScreenItem2;


    private JMenuItem colorItem;
    private JMenuItem colorItem2;
    private JMenuItem sizeItem;
    private JMenuItem sizeItem2;
    private JMenuItem scaleFilterItem;
    private JMenuItem sizeFilterItem2;

    private JMenuItem undoDescriptorItem;
    private JMenuItem undoDescriptorItem2;
    private JMenuItem redoDescriptorItem;
    private JMenuItem redoDescriptorItem2;
    private JMenuItem defaultSettingsItem;
    private JMenuItem defaultSettingsItem2;
    private JMenuItem defaultVisualizationItem;
    private JMenuItem defaultVisualizationItem2;

    private JMenuItem showSimilarDescriptors;
    private JMenuItem hideSimilarDescriptors;
    private JMenuItem setTresholdItem;
    private JMenuItem setLinesColorItem;
    private JMenuItem setHooveredDescriptorColorItem;
    private JCheckBoxMenuItem antialiasingCheckboxItem;

    private JMenuItem oneImageModeItem;
    private JMenuItem twoImagesModeItem;
    
    private JMenuItem aboutItem;
    private JMenuItem helpItem;

    private JPanel mainPanel;
    private JPanel comparativePanel;
    // nested panel
    private JPanel glasspaneButtonPanel;

    // Components of comparative Panel
    private JButton hideGlasspaneButton;
    private JButton antialiasButton;
    private JButton tresholdButton;
    private JLabel similarDescriptorsTotalCountLabel;
    private JLabel similarDescriptorsVisibleCountLabel;

    private ImagePanel imagePanel;
    private ImageScrollPane imageScrollPane;

    private ImagePanel secondImagePanel;
    private ImageScrollPane secondImageScrollPane;
    
    private ProjectionGlassPane projectionGlassPane;


    /**
     * Create new instance of MainFrame and initialize the components for the frame
     */
    public MainFrame(Class<? extends LocalDescriptors>[] types) {
        descriptorsTypes = types;
        initComponents();
       
       
             
    }

    private void initComponents() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) { }

        localLanguage = ResourceBundle.getBundle("MainFrame", locale);
        menuBar = new JMenuBar();

        fileMenu = new JMenu();
        firstImageFile = new JMenu();
        secondImageFile = new JMenu();
        descriptorsType = new JMenu();

        imageMenu = new JMenu();
        firstImageControl = new JMenu();
        secondImageControl = new JMenu();

        descriptorsMenu = new JMenu();
        firstImageDescriptors = new JMenu();
        secondImageDescriptors = new JMenu();

        descriptorsTypeGroup = new ButtonGroup();
        
        boolean first = true;
        for (Class<? extends LocalDescriptors> type: descriptorsTypes) {
            ExtendedJRadioButtonMenuItem radioMenuItem = new ExtendedJRadioButtonMenuItem();
            
            try {
                radioMenuItem.setDescriptorsClass(type);
                radioMenuItem.setText(type.newInstance().getDescriptorsDescription());
                descriptorsTypeGroup.add(radioMenuItem);
                descriptorsType.add(radioMenuItem);
                if (first) {
                    radioMenuItem.setSelected(true);
                    first = false;
                }
            } catch (InstantiationException ex) {                
            } catch (IllegalAccessException ex) {
            }            
        }
        
        compareDescriptorsMenu = new JMenu();

        helpMenu = new JMenu();

        projectionMenu = new JMenu();
        projectionFirstImage = new JMenu();
        projectionSecondImage = new JMenu();
        projectionFirstImageToX = new JMenuItem();
        projectionFirstImageToY = new JMenuItem();
        projectionSecondImageToX = new JMenuItem();
        projectionSecondImageToY = new JMenuItem();
        projectionFirstImageCustom = new JMenuItem();
        projectionSecondImageCustom = new JMenuItem();
        projectionTogglePositionFirstImage = new JMenuItem();
        projectionTogglePositionSecondImage = new JMenuItem();
        
        needlemanWunschButton = new JMenuItem();
        smithWatermanButton = new JMenuItem();
        
        projectionMenuSuitable = new JMenuItem();
        projectionMenuCustom = new JMenuItem();
        showSideProjectionPanel = new JMenuItem();
        showBottomProjectionPanel = new JMenuItem();
        
        modeMenu = new JMenu();
        
        loadImageItem = new JMenuItem();
        loadSecondImageItem = new JMenuItem();
        loadDescriptorsItem = new JMenuItem();
        loadDescriptorsItem2 = new JMenuItem();
        loadDatasetDescriptorsItem = new JMenuItem();
        loadDatasetDescriptorsItem2 = new JMenuItem();

        zoomItem = new JMenuItem();
        zoomItem2 = new JMenuItem();
        zoomInItem = new JMenuItem();
        zoomOutItem = new JMenuItem();
        defaultZoomItem = new JMenuItem();
        zoomInItem2 = new JMenuItem();
        zoomOutItem2 = new JMenuItem();
        defaultZoomItem2 = new JMenuItem();
        // SEPARATOR
        fitToWidthItem = new JMenuItem();
        fitToWidthItem2= new JMenuItem();
        fitToHeightItem = new JMenuItem();
        fitToHeightItem2 = new JMenuItem();
        fitToScreenItem = new JMenuItem();
        fitToScreenItem2 = new JMenuItem();


        colorItem = new JMenuItem();
        colorItem2 = new JMenuItem();
        scaleFilterItem = new JMenuItem();
        sizeFilterItem2 = new JMenuItem();
        sizeItem = new JMenuItem();
        sizeItem2 = new JMenuItem();
        // SEPARATOR
        undoDescriptorItem = new JMenuItem();
        undoDescriptorItem2 = new JMenuItem();
        redoDescriptorItem = new JMenuItem();
        redoDescriptorItem2 = new JMenuItem();
        defaultSettingsItem = new JMenuItem();
        defaultSettingsItem2 = new JMenuItem();
        defaultVisualizationItem = new JMenuItem();
        defaultVisualizationItem2 = new JMenuItem();

        showSimilarDescriptors = new JMenuItem();
        hideSimilarDescriptors = new JMenuItem();
        setTresholdItem = new JMenuItem();
        setLinesColorItem = new JMenuItem();
        setHooveredDescriptorColorItem = new JMenuItem();
        antialiasingCheckboxItem = new JCheckBoxMenuItem();

        oneImageModeItem = new JMenuItem();
        twoImagesModeItem = new JMenuItem();

        aboutItem = new JMenuItem();
        helpItem = new JMenuItem();

        mainPanel = new JPanel();
        comparativePanel = new JPanel();
        glasspaneButtonPanel = new JPanel();

        imagePanel = new ImagePanel();
        imagePanel.setParentMainFrame(this);
        imageScrollPane = new ImageScrollPane(imagePanel, this, 1);

        secondImagePanel = new ImagePanel();
        secondImagePanel.setParentMainFrame(this);
        secondImageScrollPane = new ImageScrollPane(secondImagePanel, this, 2);
        
        hideGlasspaneButton = new JButton();
        antialiasButton = new JButton();
        tresholdButton = new JButton();
        similarDescriptorsTotalCountLabel = new JLabel();
        similarDescriptorsVisibleCountLabel = new JLabel();

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        getContentPane().setLayout(new BorderLayout());
        

        firstImageFile.setText(localLanguage.getString("mb_first"));
        secondImageFile.setText(localLanguage.getString("mb_second"));
        descriptorsType.setText(localLanguage.getString("mb_descriptors_type"));

        loadImageItem.setText(localLanguage.getString("mb_file_load_image"));
        loadImageItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loadImageItemActionPerformed(imagePanel, FIRST_IMAGE_PANEL);
            }
        });

        loadSecondImageItem.setText(localLanguage.getString("mb_file_load_image"));
        loadSecondImageItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loadImageItemActionPerformed(secondImagePanel, SECOND_IMAGE_PANEL);
            }
        });
  
        loadDescriptorsItem.setText(localLanguage.getString("mb_file_load_descriptors"));
        loadDescriptorsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loadDescriptorsActionPerformed(imagePanel, FIRST_IMAGE_PANEL);
            }
        });

        loadDescriptorsItem2.setText(localLanguage.getString("mb_file_load_descriptors"));
        loadDescriptorsItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loadDescriptorsActionPerformed(secondImagePanel, SECOND_IMAGE_PANEL);
            }
        });

        loadDatasetDescriptorsItem.setText(localLanguage.getString("mb_file_load_dataset_descriptors"));
        loadDatasetDescriptorsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loadDatasetDescriptorsActionPerformed(imagePanel, FIRST_IMAGE_PANEL);
            }
        });

        loadDatasetDescriptorsItem2.setText(localLanguage.getString("mb_file_load_dataset_descriptors"));
        loadDatasetDescriptorsItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loadDatasetDescriptorsActionPerformed(secondImagePanel, SECOND_IMAGE_PANEL);
            }
        });

        firstImageControl.setText(localLanguage.getString("mb_first"));
        secondImageControl.setText(localLanguage.getString("mb_second"));

        zoomItem.setText(localLanguage.getString("mb_zoom"));
        zoomItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomActionPerformed(imagePanel);
            }
        });

        zoomInItem.setText(localLanguage.getString("mb_zoom_in"));
        zoomInItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                zoomInItemActionPerformed(imagePanel);
            }
        });

        zoomOutItem.setText(localLanguage.getString("mb_zoom_out"));
        zoomOutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                zoomOutItemActionPerformed(imagePanel);
            }
        });

        defaultZoomItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_0, java.awt.event.InputEvent.CTRL_MASK));
        defaultZoomItem.setText(localLanguage.getString("mb_def_zoom"));
        defaultZoomItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                defaultZoomActionPerformed(imagePanel);
            }
        });

        fitToWidthItem.setText(localLanguage.getString("mb_fit_width"));
        fitToWidthItem.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(ActionEvent e) {
               fitToWidthActionPerformed(imagePanel);
           }
        });

        fitToHeightItem.setText(localLanguage.getString("mb_fit_height"));
        fitToHeightItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fitToHeightActionPerformed(imagePanel);
            }
        });

        fitToScreenItem.setText(localLanguage.getString("mb_fit_screen"));
        fitToScreenItem.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(ActionEvent e) {
               fitToScreenActionPerformed(imagePanel);
           }
        });

        zoomItem2.setText(localLanguage.getString("mb_zoom"));
        zoomItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomActionPerformed(secondImagePanel);
            }
        });

        zoomInItem2.setText(localLanguage.getString("mb_zoom_in"));
        zoomInItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                zoomInItemActionPerformed(secondImagePanel);
            }
        });

        zoomOutItem2.setText(localLanguage.getString("mb_zoom_out"));
        zoomOutItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                zoomOutItemActionPerformed(secondImagePanel);
            }
        });

        defaultZoomItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_0, java.awt.event.InputEvent.ALT_MASK));
        defaultZoomItem2.setText(localLanguage.getString("mb_def_zoom"));
        defaultZoomItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                defaultZoomActionPerformed(secondImagePanel);
            }
        });

        fitToWidthItem2.setText(localLanguage.getString("mb_fit_width"));
        fitToWidthItem2.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(ActionEvent e) {
               fitToWidthActionPerformed(secondImagePanel);
           }
        });

        fitToHeightItem2.setText(localLanguage.getString("mb_fit_height"));
        fitToHeightItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fitToHeightActionPerformed(secondImagePanel);
            }
        });

        fitToScreenItem2.setText(localLanguage.getString("mb_fit_screen"));
        fitToScreenItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fitToScreenActionPerformed(secondImagePanel);
            }
        });

        firstImageDescriptors.setText(localLanguage.getString("mb_first"));
        secondImageDescriptors.setText(localLanguage.getString("mb_second"));

        colorItem.setText(localLanguage.getString("mb_descriptors_color"));
        colorItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 descriptorsColorActionPerformed(imagePanel.getDescriptors());
            }
        });

        scaleFilterItem.setText(localLanguage.getString("mb_size_filter"));
        scaleFilterItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaleFilterActionPerformed(imageScrollPane);
            }
        });

        sizeItem.setText(localLanguage.getString("mb_shape_size"));
        sizeItem.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(ActionEvent e) {
               shapeSizeActionPerformed(imagePanel.getDescriptors());
           }
        });

        undoDescriptorItem.setText(localLanguage.getString("mb_undo_descriptor"));
        undoDescriptorItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                undoDescriptorActionPerformed(imagePanel.getDescriptors());
            }
        });

        redoDescriptorItem.setText(localLanguage.getString("mb_redo_descriptor"));
        redoDescriptorItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                redoDescriptorActionPerformed(imagePanel.getDescriptors());
            }
        });

        defaultSettingsItem.setText(localLanguage.getString("mb_default_settings"));
        defaultSettingsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                descriptorsDefaultSettingsActionPerformed(imagePanel.getDescriptors());
            }
        });

        defaultVisualizationItem.setText(localLanguage.getString("mb_default_visualization"));
        defaultVisualizationItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                descriptorsDefaultVisualizationActionPerformed(imagePanel);
            }
        });

        colorItem2.setText(localLanguage.getString("mb_descriptors_color"));
        colorItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                descriptorsColorActionPerformed(secondImagePanel.getDescriptors());
            }
        });

        sizeFilterItem2.setText(localLanguage.getString("mb_size_filter"));
        sizeFilterItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaleFilterActionPerformed(secondImageScrollPane);
            }
        });

        sizeItem2.setText(localLanguage.getString("mb_shape_size"));
        sizeItem2.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(ActionEvent e) {
               shapeSizeActionPerformed(secondImagePanel.getDescriptors());
           }
        });

        undoDescriptorItem2.setText(localLanguage.getString("mb_undo_descriptor"));
        undoDescriptorItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                undoDescriptorActionPerformed(secondImagePanel.getDescriptors());
            }
        });

        redoDescriptorItem2.setText(localLanguage.getString("mb_redo_descriptor"));
        redoDescriptorItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                redoDescriptorActionPerformed(secondImagePanel.getDescriptors());
            }
        });

        defaultSettingsItem2.setText(localLanguage.getString("mb_default_settings"));
        defaultSettingsItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                descriptorsDefaultSettingsActionPerformed(secondImagePanel.getDescriptors());
            }
        });

        defaultVisualizationItem2.setText(localLanguage.getString("mb_default_visualization"));
        defaultVisualizationItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                descriptorsDefaultVisualizationActionPerformed(secondImagePanel);
            }
        });

        showSimilarDescriptors.setText(localLanguage.getString("mb_show_similar"));
        showSimilarDescriptors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dialogs.tresholdDialog(thisInstance, true);
            }
        });

        hideSimilarDescriptors.setText(localLanguage.getString("mb_hide_similar"));
        hideSimilarDescriptors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setComparationMode(false);
            }
        });

        setTresholdItem.setText(localLanguage.getString("mb_treshold"));
        setTresholdItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dialogs.tresholdDialog(thisInstance, false);
            }
        });

        setLinesColorItem.setText(localLanguage.getString("mb_similar_color"));
        setLinesColorItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dialogs.similarDescriptorsColorChooserDialog(thisInstance);
            }
        });

        setHooveredDescriptorColorItem.setText(localLanguage.getString("mb_hoover_color"));
        setHooveredDescriptorColorItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dialogs.hooveredDescriptorColorChooserDialog(thisInstance);
            }
        });

        antialiasingCheckboxItem.setState(false);
        antialiasingCheckboxItem.setText(localLanguage.getString("mb_checkbox_antialias"));
        antialiasingCheckboxItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange()) {
                    ((GlasspaneForSimilarDescriptors) getGlassPane()).setAntialiasing(true);
                } else {
                    ((GlasspaneForSimilarDescriptors) getGlassPane()).setAntialiasing(false);
                }
            }
        });

        oneImageModeItem.setText(localLanguage.getString("mb_one_img"));
        oneImageModeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                oneImageModeComponents();
                setComparationMode(false);
            }
        });

        twoImagesModeItem.setText(localLanguage.getString("mb_two_img"));
        twoImagesModeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                twoImageModeComponents();
            }
        });

        
        projectionFirstImage.setText("First Image");
        projectionSecondImage.setText("Second Image");
        
         smithWatermanButton.setText("SmithWaterman");
         smithWatermanButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
           
           Set <ObjectFeature> first = imageScrollPane.getImagePanel().getDescriptors().getVisibleDescriptors();
           Set <ObjectFeature> second = secondImageScrollPane.getImagePanel().getDescriptors().getVisibleDescriptors();
           
           Projection proj = new Projection(ProjectionTo.X);

           SmithWaterman smithWaterman = new SmithWaterman( proj.getSortedProjection(first),  proj.getSortedProjection(second));         
            
           projectionGlassPane = new ProjectionGlassPane(imageScrollPane, secondImageScrollPane);
                setGlassPane(projectionGlassPane);
                projectionGlassPane.setEnabled(true);
                projectionGlassPane.setVisible(true);
                
           imageScrollPane.getBottomProjectionPanel().setData(smithWaterman.getFirstSequence());
           imageScrollPane.getSideProjectionPanel().setData(smithWaterman.getFirstSequence());
           secondImageScrollPane.getBottomProjectionPanel().setData(smithWaterman.getSecondSequence());
           secondImageScrollPane.getSideProjectionPanel().setData(smithWaterman.getSecondSequence());
           
           imageScrollPane.SetBottomProjectionPanelVisible();
           secondImageScrollPane.SetBottomProjectionPanelVisible();
           imageScrollPane.SetSideProjectionPanelInvisible();
           secondImageScrollPane.SetSideProjectionPanelInvisible();
           revalidate();
            }
        
        
        });
        
        needlemanWunschButton.setText("NeedlemanWunsch");
        needlemanWunschButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
           
           Set <ObjectFeature> first = imageScrollPane.getImagePanel().getDescriptors().getVisibleDescriptors();
           Set <ObjectFeature> second = secondImageScrollPane.getImagePanel().getDescriptors().getVisibleDescriptors();
           
           Projection proj = new Projection(ProjectionTo.X);

           NeedlemanWunsch needlemanWunsch = new NeedlemanWunsch( proj.getSortedProjection(first),  proj.getSortedProjection(second));         
            
           projectionGlassPane = new ProjectionGlassPane(imageScrollPane, secondImageScrollPane);
                setGlassPane(projectionGlassPane);
                projectionGlassPane.setEnabled(true);
                projectionGlassPane.setVisible(true);
                
           imageScrollPane.getBottomProjectionPanel().setData(needlemanWunsch.getFirstSequence());
           imageScrollPane.getSideProjectionPanel().setData(needlemanWunsch.getFirstSequence());
           secondImageScrollPane.getBottomProjectionPanel().setData(needlemanWunsch.getSecondSequence());
           secondImageScrollPane.getSideProjectionPanel().setData(needlemanWunsch.getSecondSequence());
           
           imageScrollPane.SetBottomProjectionPanelVisible();
           secondImageScrollPane.SetBottomProjectionPanelVisible();
           imageScrollPane.SetSideProjectionPanelInvisible();
           secondImageScrollPane.SetSideProjectionPanelInvisible();
           revalidate();
            }
        
        
        });
        
        projectionFirstImageToX.setText("To X");
        projectionFirstImageToX.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
                imageScrollPane.getImagePanel().getDescriptors().setProjectionType(ProjectionTo.X);               
                projectionGlassPane = new ProjectionGlassPane(imageScrollPane, secondImageScrollPane);
                setGlassPane(projectionGlassPane);
                projectionGlassPane.setEnabled(true);
                projectionGlassPane.setVisible(true);
                if(!imageScrollPane.isProjectionVisible()){
                    imageScrollPane.SetBottomProjectionPanelVisible();
                }
                revalidate();
            }
        });
        
        projectionSecondImageToX.setText("To X");
        projectionSecondImageToX.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
                secondImageScrollPane.getImagePanel().getDescriptors().setProjectionType(ProjectionTo.X);               
                projectionGlassPane = new ProjectionGlassPane(imageScrollPane, secondImageScrollPane);
                setGlassPane(projectionGlassPane);
                projectionGlassPane.setEnabled(true);
                projectionGlassPane.setVisible(true);
                if(!secondImageScrollPane.isProjectionVisible()){
                    secondImageScrollPane.SetBottomProjectionPanelVisible();
                }
                revalidate();
            }
        });
        
        projectionFirstImageToY.setText("To Y");
        projectionFirstImageToY.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {              
                imageScrollPane.getImagePanel().getDescriptors().setProjectionType(ProjectionTo.Y);               
                projectionGlassPane = new ProjectionGlassPane(imageScrollPane, secondImageScrollPane);
                setGlassPane(projectionGlassPane);
                projectionGlassPane.setEnabled(true);
                projectionGlassPane.setVisible(true);
                if(!imageScrollPane.isProjectionVisible()){
                    imageScrollPane.SetBottomProjectionPanelVisible();
                }
                revalidate();
            }
        });
        
        projectionSecondImageToY.setText("To Y");
        projectionSecondImageToY.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {              
                secondImageScrollPane.getImagePanel().getDescriptors().setProjectionType(ProjectionTo.Y);               
                projectionGlassPane = new ProjectionGlassPane(imageScrollPane, secondImageScrollPane);
                setGlassPane(projectionGlassPane);
                projectionGlassPane.setEnabled(true);
                projectionGlassPane.setVisible(true);
                if(!secondImageScrollPane.isProjectionVisible()){
                    secondImageScrollPane.SetBottomProjectionPanelVisible();
                }
                revalidate();
            }
        });
        
        projectionFirstImageCustom.setText("Custom");
        projectionSecondImageCustom.setText("Custom");
        
        projectionTogglePositionFirstImage.setText("Toggle");
        projectionTogglePositionFirstImage.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {                   
                if(imageScrollPane.getSideProjectionPanel().isVisible()){
                    imageScrollPane.getSideProjectionPanel().setVisible(false);
                    imageScrollPane.getBottomProjectionPanel().setVisible(true);
                }
                else{
                    imageScrollPane.getSideProjectionPanel().setVisible(true);
                    imageScrollPane.getBottomProjectionPanel().setVisible(false);
                }
            }
        });
        
        projectionTogglePositionSecondImage.setText("Toggle");
        projectionTogglePositionSecondImage.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {                   
                if(secondImageScrollPane.getSideProjectionPanel().isVisible()){
                    secondImageScrollPane.getSideProjectionPanel().setVisible(false);
                    secondImageScrollPane.getBottomProjectionPanel().setVisible(true);
                }
                else{
                    secondImageScrollPane.getSideProjectionPanel().setVisible(true);
                    secondImageScrollPane.getBottomProjectionPanel().setVisible(false);
                }
            }
        });

        projectionMenuSuitable.setText("The most suitable axis");
        projectionMenuCustom.setText("Custom axis");
        projectionMenuCustom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.out.println("asdsasd");
              //  imageScrollPane.
            }
        });

        
        helpItem.setText(localLanguage.getString("help_tit"));
        helpItem.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(ActionEvent event)  {
               javax.swing.JOptionPane.showMessageDialog(null,
                       localLanguage.getString("help_first_line") + "\n" +
                       localLanguage.getString("help_second_line") + "\n" +
                       localLanguage.getString("help_third_line") + "\n" +
                       localLanguage.getString("help_fourth_line") + "\n" +
                       localLanguage.getString("help_fifth_line") + "\n" +
                       localLanguage.getString("help_sixth_line") + "\n",
                       localLanguage.getString("help_tit"),
                       JOptionPane.INFORMATION_MESSAGE);
           }
        });

        aboutItem.setText(localLanguage.getString("about_tit"));
        aboutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        localLanguage.getString("about_first_line") + "\n" + "\n" +
                        localLanguage.getString("about_second_line") + "\n" +
                        localLanguage.getString("about_third_line"),
                        localLanguage.getString("about_tit"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        addWindowListener(new java.awt.event.WindowListener() {
            public void windowOpened(WindowEvent e) {}

            public void windowClosing(WindowEvent e) {}

            public void windowClosed(WindowEvent e) {}

            public void windowIconified(WindowEvent e) {}

            public void windowDeiconified(WindowEvent e) {
                repaintAll();
            }

            public void windowActivated(WindowEvent e) {
                repaintAll();
            }

            public void windowDeactivated(WindowEvent e) {}            
        });

        /*
         * Zoom both images
         */
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isShiftDown()) {
                    if (e.isControlDown()) {
                        if (secondImageScrollPane.getImagePanel().getImage() != null) {
                            ImageScrollPane.zoomBothImagePanels(thisInstance, e.getWheelRotation());
                            
                        }
                    }
                }
            }
        });
        
        java.net.URL url;

        url = getClass().getResource("icons/close-icon.png");
        ImageIcon hideIcon = new ImageIcon(url);
        Image newHideImage = hideIcon.getImage().getScaledInstance(ImageScrollPane.ICON_WIDTH,
                ImageScrollPane.ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newHideIcon = new ImageIcon(newHideImage);

        url = getClass().getResource("icons/antialiasing-icon.jpg");
        ImageIcon antialiasIcon = new ImageIcon(url);
        Image newAntialiasImage = antialiasIcon.getImage().getScaledInstance(ImageScrollPane.ICON_WIDTH,
                ImageScrollPane.ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newAntialiasIcon = new ImageIcon(newAntialiasImage);

        url = getClass().getResource("icons/size-filter-icon.png");
        ImageIcon tresholdIcon = new ImageIcon(url);
        Image newTresholdImage = tresholdIcon.getImage().getScaledInstance(ImageScrollPane.ICON_WIDTH,
                ImageScrollPane.ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newTresholdIcon = new ImageIcon(newTresholdImage);

        hideGlasspaneButton.setIcon(newHideIcon);
        hideGlasspaneButton.setToolTipText(localLanguage.getString("hide_button"));
        hideGlasspaneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setComparationMode(false);
            }
        });

        antialiasButton.setIcon(newAntialiasIcon);
        antialiasButton.setToolTipText(localLanguage.getString("antialias_button"));
        antialiasButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GlasspaneForSimilarDescriptors glasspane =
                        (GlasspaneForSimilarDescriptors) getGlassPane();
                glasspane.setAntialiasing(!glasspane.getAntialiasing());
            }
        });

        tresholdButton.setIcon(newTresholdIcon);
        tresholdButton.setToolTipText(localLanguage.getString("treshold_button"));
        tresholdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dialogs.tresholdDialog(thisInstance, false);
            }
        });
        
        
        showSideProjectionPanel.setText("Toggle side");
        showSideProjectionPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                imageScrollPane.ToggleSideProjectionPanel();
                secondImageScrollPane.ToggleSideProjectionPanel();
            }
        });
        
        
        showBottomProjectionPanel.setText("Toggle bot");
        showBottomProjectionPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                imageScrollPane.ToggleBottomProjectionPanel();
                secondImageScrollPane.ToggleBottomProjectionPanel();
                
            }
        });

        glasspaneButtonPanel.add(antialiasButton);
        glasspaneButtonPanel.add(tresholdButton);
        glasspaneButtonPanel.add(hideGlasspaneButton);

        comparativePanel.add(similarDescriptorsTotalCountLabel);
        comparativePanel.add(glasspaneButtonPanel);
        comparativePanel.add(similarDescriptorsVisibleCountLabel);
        comparativePanel.setBorder(new EtchedBorder());

        //***** Build file menu *****//
        firstImageFile.add(loadImageItem);
        firstImageFile.add(loadDescriptorsItem);
        firstImageFile.add(loadDatasetDescriptorsItem);
        secondImageFile.add(loadSecondImageItem);
        secondImageFile.add(loadDescriptorsItem2);
        secondImageFile.add(loadDatasetDescriptorsItem2);
        secondImageFile.setEnabled(false);
        fileMenu.add(firstImageFile);
        fileMenu.add(secondImageFile);
        fileMenu.addSeparator();
        fileMenu.add(descriptorsType);
        fileMenu.setText(localLanguage.getString("menu_bar_file"));

        //***** Build zoom menu *****//
        firstImageControl.add(zoomItem);
        firstImageControl.add(zoomInItem);
        firstImageControl.add(zoomOutItem);
        firstImageControl.add(defaultZoomItem);
        firstImageControl.addSeparator();
        firstImageControl.add(fitToWidthItem);
        firstImageControl.add(fitToHeightItem);
        firstImageControl.add(fitToScreenItem);
        firstImageControl.setEnabled(false);

        secondImageControl.add(zoomItem2);
        secondImageControl.add(zoomInItem2);
        secondImageControl.add(zoomOutItem2);
        secondImageControl.add(defaultZoomItem2);
        secondImageControl.addSeparator();
        secondImageControl.add(fitToWidthItem2);
        secondImageControl.add(fitToHeightItem2);
        secondImageControl.add(fitToScreenItem2);
        secondImageControl.setEnabled(false);
        
        imageMenu.add(firstImageControl);
        imageMenu.add(secondImageControl);
        imageMenu.setText(localLanguage.getString("menu_bar_image"));

        //***** Build descriptors menu *****//
        firstImageDescriptors.add(colorItem);
        firstImageDescriptors.add(scaleFilterItem);
        firstImageDescriptors.add(sizeItem);
        firstImageDescriptors.addSeparator();
        firstImageDescriptors.add(undoDescriptorItem);
        firstImageDescriptors.add(redoDescriptorItem);
        firstImageDescriptors.add(defaultSettingsItem);
        firstImageDescriptors.add(defaultVisualizationItem);
        firstImageDescriptors.setEnabled(false);

        secondImageDescriptors.add(colorItem2);
        secondImageDescriptors.add(sizeFilterItem2);
        secondImageDescriptors.add(sizeItem2);
        secondImageDescriptors.addSeparator();
        secondImageDescriptors.add(undoDescriptorItem2);
        secondImageDescriptors.add(redoDescriptorItem2);
        secondImageDescriptors.add(defaultSettingsItem2);
        secondImageDescriptors.add(defaultVisualizationItem2);        
        secondImageDescriptors.setEnabled(false);

        descriptorsMenu.add(firstImageDescriptors);
        descriptorsMenu.add(secondImageDescriptors);
        descriptorsMenu.setText(localLanguage.getString("menu_bar_descriptors"));

        //***** Build comparing menu *****//
        compareDescriptorsMenu.add(showSimilarDescriptors);
        compareDescriptorsMenu.add(hideSimilarDescriptors);
        compareDescriptorsMenu.addSeparator();
        compareDescriptorsMenu.add(setTresholdItem);
        compareDescriptorsMenu.add(setLinesColorItem);
        compareDescriptorsMenu.add(setHooveredDescriptorColorItem);
        compareDescriptorsMenu.add(antialiasingCheckboxItem);
        showSimilarDescriptors.setEnabled(false);
        hideSimilarDescriptors.setEnabled(false);
        setTresholdItem.setEnabled(false);
        setLinesColorItem.setEnabled(false);
        setHooveredDescriptorColorItem.setEnabled(false);
        antialiasingCheckboxItem.setEnabled(false);
        compareDescriptorsMenu.setText(localLanguage.getString("menu_bar_compare_descriptors"));
      
        //***** Build projection menu *****//
       // projectionMenu.add(projectionMenuX);
        projectionMenu.add(projectionFirstImage);
        projectionMenu.add(projectionSecondImage);
        
        projectionFirstImage.add(projectionFirstImageToX);
        projectionFirstImage.add(projectionFirstImageToY);
        projectionFirstImage.add(projectionFirstImageCustom);
        projectionFirstImage.add(new JSeparator());
        projectionFirstImage.add(projectionTogglePositionFirstImage);
        
        projectionSecondImage.add(projectionSecondImageToX);
        projectionSecondImage.add(projectionSecondImageToY);
        projectionSecondImage.add(projectionSecondImageCustom);
        projectionSecondImage.add(new JSeparator());
        projectionSecondImage.add(projectionTogglePositionSecondImage);
       
        projectionMenu.add(needlemanWunschButton);
        
        projectionMenu.add(smithWatermanButton);
        /*
        projectionMenu.add(projectionMenuY);
        projectionMenu.add(projectionMenuSuitable);
        projectionMenu.add(projectionMenuCustom);
        projectionMenu.add(showSideProjectionPanel);
        projectionMenu.add(showBottomProjectionPanel);
        */
        projectionMenu.setText("Projection");
        
        //***** Build help menu *****//
        helpMenu.add(helpItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
        helpMenu.setText(localLanguage.getString("menu_bar_help"));
        
        //***** Build mode menu *****//
        modeMenu.add(oneImageModeItem);
        modeMenu.add(twoImagesModeItem);
        oneImageModeItem.setEnabled(false);
        modeMenu.setText(localLanguage.getString("menu_bar_mode"));

        //***** Build menu bar *****//
        menuBar.add(fileMenu);
        menuBar.add(imageMenu);
        menuBar.add(descriptorsMenu);
        menuBar.add(compareDescriptorsMenu);
        menuBar.add(modeMenu);
        menuBar.add(projectionMenu);
        menuBar.add(helpMenu);
      
        setJMenuBar(menuBar);

        Utils.setDefaultCurrentDirectory();

        //***** Place MainFrame in the center of the screen *****//
        int xAxisLocation = (Double.valueOf(dimension.getWidth()).intValue() / 2) - (MAIN_FRAME_WIDTH / 2);
        int yAxisLocation = (Double.valueOf(dimension.getHeight()).intValue() / 2) - (MAIN_FRAME_HEIGHT / 2);
        setLocation(new Point(xAxisLocation, yAxisLocation));

        setPreferredSize(new Dimension(MAIN_FRAME_WIDTH, MAIN_FRAME_HEIGHT));
        setTitle(localLanguage.getString("main_frame_title"));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(imageScrollPane, BorderLayout.CENTER);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(MAIN_FRAME_MINIMUM_WIDTH, MAIN_FRAME_MINIMUM_HEIGHT));

        pack();
        setVisible(true);
    }

    
    //**************** START OF ACTION PERFORMED METHODS ****************//
    private void loadImageItemActionPerformed(ImagePanel panel, int imagePaneNumber) {
        ImageFileChooser imageFileChooser = new ImageFileChooser(panel, imagePaneNumber);
    }

    private void loadDescriptorsActionPerformed(ImagePanel panel, int paneNumber) {
        if (panel.getImage() == null) {
            JOptionPane.showMessageDialog(this, localLanguage.getString("jop_warning_msg_miss_img"),
                    localLanguage.getString("jop_warning_title_miss_img"), JOptionPane.WARNING_MESSAGE);
        } else {
            panel.zoom(1);
            AutomaticDescriptorsSelector.loadDescriptors(panel, paneNumber);
        }
    }

    private void loadDatasetDescriptorsActionPerformed(ImagePanel panel, int paneNumber) {
        if (panel.getImage() == null) {
            JOptionPane.showMessageDialog(this, localLanguage.getString("jop_warning_msg_miss_img"),
                    localLanguage.getString("jop_warning_title_miss_img"), JOptionPane.WARNING_MESSAGE);
        } else {
            panel.zoom(1);
            DescriptorsDatasetFileChooser descriptorsDatasetFileChooser =
                    new DescriptorsDatasetFileChooser(panel, paneNumber);
        }
    }

    private void zoomActionPerformed(ImagePanel panel) {
        Dialogs.zoomFormDialog(this, panel);
    }

    private void zoomInItemActionPerformed(ImagePanel panel) {
        panel.zoomIn();
    }

    private void zoomOutItemActionPerformed(ImagePanel panel) {
        panel.zoomOut();
    }

    private void defaultZoomActionPerformed(ImagePanel panel) {
        panel.zoom(1);
    }

    private void descriptorsColorActionPerformed(LocalDescriptors descriptors) {
        Dialogs.colorChooserDialog(this, descriptors);
    }

    private void scaleFilterActionPerformed(ImageScrollPane pane) {
        Dialogs.scaleFilterDialog(pane, pane.getImagePanel().getDescriptors());
    }

    private void shapeSizeActionPerformed(LocalDescriptors descriptors) {
        Dialogs.shapeSizeSilderDialog(this, descriptors);
    }

    private void descriptorsDefaultSettingsActionPerformed(LocalDescriptors descriptors) {
        descriptors.setDefaultSettings();
    }

    private void descriptorsDefaultVisualizationActionPerformed(ImagePanel panel) {
        panel.getDescriptors().defaultVisualization();
        panel.setXPanelShift(0);
        panel.setYPanelShift(0);
        panel.clearRectangle();
        panel.repaint();
    }

    private void undoDescriptorActionPerformed(LocalDescriptors descriptors) {
        if (comparationMode) {
            ((GlasspaneForSimilarDescriptors) getGlassPane()).undoClickedSimilar();
        } else {
            if (descriptors.undoOperationIsPossible()) {
                descriptors.undoVisibleClickedDescriptor();
            }
        }
    }

    private void redoDescriptorActionPerformed(LocalDescriptors descriptors) {
        if (comparationMode) {
            ((GlasspaneForSimilarDescriptors) getGlassPane()).redoClickedSimilar();
        } else {
            if (descriptors.redoOperationIsPossible()) {
                descriptors.redoVisibleClickedDescriptors();
            }
        }
    }

    private void fitToWidthActionPerformed(ImagePanel panel) {
        panel.fitToWidth();
    }

    private void fitToHeightActionPerformed(ImagePanel panel) {
        panel.fitToHeight();
    }

    private void fitToScreenActionPerformed(ImagePanel panel) {
        panel.fitToScreen();
    }

    //**************** END OF ACTION PERFORMED METHODS ****************//


    //*************** Private helpful methods **********************//


    //**************** REBUILD MAIN FRAME ****************************//
    //***** Switch to one image mode *****//
    private void oneImageModeComponents() {
        oneImageMode = true;

        setMenuItemState();
        buildOneImageModeComponents();
    }

    //***** Switch to two images mode *****//
    private void twoImageModeComponents() {
        oneImageMode = false;

        setMenuItemState();
        buildTwoImageModeComponents();
    }

    //***** Set state of the components to corresponding mode *****//
    private void setMenuItemState() {
        oneImageModeItem.setEnabled(!oneImageMode);
        twoImagesModeItem.setEnabled(oneImageMode);
        secondImageFile.setEnabled(!oneImageMode);
        setStateOfImageMenu(false, SECOND_IMAGE_PANEL);
        setStateOfDescriptorsMenu(false, SECOND_IMAGE_PANEL);
    }

    //***** Build components for two images mode *****//
    private void buildTwoImageModeComponents() {
        mainPanel.remove(imageScrollPane);
        mainPanel.add(imageScrollPane, BorderLayout.WEST);
        mainPanel.add(secondImageScrollPane, BorderLayout.EAST);
        mainPanel.validate();
    }
    
    //***** Build components for one image mode *****//
    private void buildOneImageModeComponents() {
        secondImagePanel.removeLoadedImage();
        secondImageScrollPane.setDefaultLabels();
        mainPanel.remove(imageScrollPane);
        mainPanel.remove(secondImageScrollPane);
        mainPanel.add(imageScrollPane, BorderLayout.CENTER);
        mainPanel.validate();
    }

    public Class<? extends LocalDescriptors> getSelectedDescriptorsType() {
        Enumeration<AbstractButton> buttons = descriptorsTypeGroup.getElements();
        while (buttons.hasMoreElements()) {
            ExtendedJRadioButtonMenuItem radioButton = 
                (ExtendedJRadioButtonMenuItem) buttons.nextElement();
            if (radioButton.isSelected()) {
                return radioButton.getDescriptorsClass();
            }
        }
        return null;
    }

    //************ SET AND GET METHODS *************************//

    public void setGlasspaneLabels() {
        GlasspaneForSimilarDescriptors glasspane = (GlasspaneForSimilarDescriptors) getGlassPane();
        similarDescriptorsTotalCountLabel.setText(localLanguage.getString("total_similar_label") +
                glasspane.getTotalSimilarCount());
        similarDescriptorsVisibleCountLabel.setText(localLanguage.getString("visible_similar_label") +
                glasspane.getVisibleSimilarCount());
    }

    public void setStateOfDescriptorsMenu(Boolean enabled, int imagePaneNumber) {
        if (imagePaneNumber == FIRST_IMAGE_PANEL) {
            firstImageDescriptors.setEnabled(enabled);
        }
        if (imagePaneNumber == SECOND_IMAGE_PANEL) {
            secondImageDescriptors.setEnabled(enabled);
        }
    }

    public void setStateOfImageMenu(Boolean enabled, int imagePaneNumber) {
        if (imagePaneNumber == FIRST_IMAGE_PANEL) {
            firstImageControl.setEnabled(enabled);
        }
        if (imagePaneNumber == SECOND_IMAGE_PANEL) {
            secondImageControl.setEnabled(enabled);
        }
    }

    // return false if type of encapsulating object are different, true otherwise - due to dialog
    private boolean setDescriptorsLoaded(boolean loaded) {
        if (!loaded) {
            showSimilarDescriptors.setEnabled(false);
            hideSimilarDescriptors.setEnabled(false);
            setTresholdItem.setEnabled(false);
            setLinesColorItem.setEnabled(false);
            setHooveredDescriptorColorItem.setEnabled(false);
            antialiasingCheckboxItem.setEnabled(false);
            setGlassPane(new JComponent() {});
        }
        
        // Global update...
        if (firstDescriptorsLoaded && secondDescriptorsLoaded) {
            if (imagePanel.getDescriptors().getClass().equals(secondImagePanel
                    .getDescriptors().getClass())) {
                if (imagePanel.getDescriptors().getEncapsulatingClass().equals(secondImagePanel
                        .getDescriptors().getEncapsulatingClass())) {
                    showSimilarDescriptors.setEnabled(true);
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    // return false if type of encapsulating object are different, true otherwise - due to dialog
    public boolean setFirstDescriptorsLoaded(boolean loaded) {
        firstDescriptorsLoaded = loaded;
        return setDescriptorsLoaded(loaded);
    }

    public boolean setSecondDescriptorsLoaded(boolean loaded) {
        secondDescriptorsLoaded = loaded;
        return setDescriptorsLoaded(loaded);
    }

    public void showEncapsulatingDialogWarning() {
        javax.swing.JOptionPane.showMessageDialog(this,
                            localLanguage.getString("encapsulating_dialog_msg_1") + "\n" +
                            localLanguage.getString("encapsulating_dialog_msg_2") + "\n" +
                            localLanguage.getString("encapsulating_dialog_msg_3"),
                            localLanguage.getString("encapsulating_dialog_title"),
                            JOptionPane.WARNING_MESSAGE);
    }

    /**
     *
     * @param minValue minimum treshold value
     * @param maxValue maximum treshold value
     * @param createGlasspane if create glasspane or not - just modify treshold
     */
    public void setTresholdAndCreateGlasspane(double minValue, double maxValue,
            boolean createGlasspane) {
        GlasspaneForSimilarDescriptors glasspane;
        if (createGlasspane) {
             glasspane = new GlasspaneForSimilarDescriptors(imageScrollPane,
                    secondImageScrollPane, minValue, maxValue);
             setGlassPane(glasspane);
             glasspane.setEnabled(true);
             glasspane.setVisible(true);
        } else {
            glasspane = (GlasspaneForSimilarDescriptors) getGlassPane();
            glasspane.setDistanceTreshold(minValue, maxValue);
        }
        setComparationMode(true);
    }

    /**
     * Repaint images, descriptors and revalidate scroll panes
     * or repaint glasspane - comparation of similar descriptors
     */
    public void repaintAll() {
        if (imagePanel.getImage() != null) {
            imagePanel.repaint();
        }
        if (!oneImageMode) {
            if (secondImagePanel.getImage() != null) {
                secondImagePanel.repaint();
            }
        }
        if (comparationMode) {
            getGlassPane().repaint();
        }
    }

    public ImageScrollPane getFirstScrollPane() {
        return imageScrollPane;
    }

    public ImageScrollPane getSecondScrollPane() {
        return secondImageScrollPane;
    }

    /**
     * Method for obtain informations about currently selected mode
     * @return true if currently selected mode is one image mode
     */
    public boolean getOneImageMode() {
        return oneImageMode;
    }

    public void setComparationMode(boolean turnedOn) {
        if (!turnedOn) {
            getContentPane().remove(comparativePanel);
            setGlassPane(new JComponent() {});
            getGlassPane().setEnabled(false);
            getGlassPane().setVisible(false);
        } else {
            if (!comparationMode) {
                getContentPane().add(comparativePanel, BorderLayout.NORTH);
            }
        }

        comparationMode = turnedOn;

        if (turnedOn) {
            hideSimilarDescriptors.setEnabled(true);
            showSimilarDescriptors.setEnabled(false);
        } else {
            if (imageScrollPane.getImagePanel().getDescriptors() != null &&
                    secondImageScrollPane.getImagePanel().getDescriptors() != null) {
                
                showSimilarDescriptors.setEnabled(true);
            } else {
                showSimilarDescriptors.setEnabled(false);
            }
            hideSimilarDescriptors.setEnabled(false);
        }

        setTresholdItem.setEnabled(turnedOn);
        setLinesColorItem.setEnabled(turnedOn);
        setHooveredDescriptorColorItem.setEnabled(turnedOn);
        antialiasingCheckboxItem.setEnabled(turnedOn);

        imageScrollPane.setDescriptorsLabels();
        secondImageScrollPane.setDescriptorsLabels();
        
        repaintAll();
    }

    public boolean isShowSimilarDescriptorsMode() {
        return comparationMode;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void recalculateSimilarDescriptors() {
        if (comparationMode) {
            GlasspaneForSimilarDescriptors glasspane = (GlasspaneForSimilarDescriptors) getGlassPane();
            glasspane.recalculateDescriptors();
        }
    }
    
    public void SetProjectionAxis(int i)
    {
        axis = i;
    }

    /**
     * This method start application
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
           public void run() {
               Class[] types = {SIFTLocalDescriptors.class, SURFLocalDescriptors.class};
               MainFrame mainFrame = new MainFrame(types);
            }
        });
    }
    
    class ExtendedJRadioButtonMenuItem extends JRadioButtonMenuItem {
        
        private Class<? extends LocalDescriptors> descriptorsClass;
        
        public void setDescriptorsClass(Class<? extends LocalDescriptors> descriptorsClass) {
            this.descriptorsClass = descriptorsClass;
        }
        
        public Class<? extends LocalDescriptors> getDescriptorsClass() {
            return descriptorsClass;
        }
    }
}