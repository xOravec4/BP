package cz.muni.fi.xlabuda;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import messif.objects.impl.ObjectFeature;
import messif.objects.util.SequenceMatchingCost;



/**
 *
 * MainFrame - application for visualization of the local descriptors in images.
 *
 * @author Marian Labuda
 * @version 1.1
 */
public class MainFrame extends JFrame {

    //******************** Constants ********************//
    /** Main frame width in pixels */
    protected static final int MAIN_FRAME_WIDTH = 600;

    /** Main frame height in pixels */
    protected static final int MAIN_FRAME_HEIGHT = 400;
    
    protected static final int MAIN_FRAME_MINIMUM_WIDTH = 480;
    protected static final int MAIN_FRAME_MINIMUM_HEIGHT =  360;

    public static final int FIRST_IMAGE_PANEL = 1;
    public static final int SECOND_IMAGE_PANEL = 2;
    
    public enum VisualisationType {BRUTEFORCE, NEEDLEMANWUNSCH, SMITHWATERMAN, NONE};
    VisualisationType visualisationType = VisualisationType.NONE;
    private Projection projection = null;


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
    private JMenuItem projectionLineColor;
    private JMenu projectionSecondImage;
    private JMenuItem projectionFirstImageToX;
    
    private JMenuItem projectionFirstImageToY;
    private JMenuItem projectionSecondImageToX;
    private JMenuItem projectionSecondImageToY;
    private JMenuItem projectionFirstImageCustom;
    private JMenuItem projectionSecondImageCustom;
    private JMenuItem projectionTogglePositionFirstImage;
    private JMenuItem projectionTogglePositionSecondImage;
    private JMenuItem projectionResetFirstImage;
    private JMenuItem projectionResetSecondImage;
    
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

    private JMenuItem showSimilarDescriptorsChoice;
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
    private JPanel comparativePanelNWSW;
    // nested panel
    private JPanel glasspaneButtonPanel;

    // Components of comparative Panel
    private JButton hideGlasspaneButton;
    private JButton antialiasButton;
    private JButton tresholdButton;
    private JLabel similarDescriptorsTotalCountLabel;
    private JLabel similarDescriptorsVisibleCountLabel;
    private JLabel NWSWTotalDescriptorsSimiliraty;
    private JLabel NWSWCurrentDescriptorSimiliraty;
    private JButton NWSWMode1;
    private JButton NWSWMode2;
    private JButton NWSWMode3;
    private JButton NWSWModeReload;

    private ImagePanel imagePanel;
    private ImageScrollPane imageScrollPane;

    private ImagePanel secondImagePanel;
    private ImageScrollPane secondImageScrollPane;
    
    private ProjectionGlassPane projectionGlassPane;
    
    private Projection visualizationProjectionFirstImage = null;
    private Projection visualizationProjectionSecondImage = null;
    
    private JDialog ComputeVisualizationDialog = null;
    private JProgressBar ComputeVisualizationProgressBar = null;
    SequenceMatchingCost cost = null;
    Color defaultProjectionColor = Color.red;
    Color defaultComparisonColor = Color.GREEN;

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
        projectionLineColor = new JMenuItem();
        projectionFirstImageToX = new JRadioButtonMenuItem();
        projectionFirstImageToY = new JRadioButtonMenuItem();
        projectionSecondImageToX = new JRadioButtonMenuItem();
        projectionSecondImageToY = new JRadioButtonMenuItem();
        projectionFirstImageCustom = new JRadioButtonMenuItem();
        projectionSecondImageCustom = new JRadioButtonMenuItem();
        projectionTogglePositionFirstImage = new JMenuItem();
        projectionTogglePositionFirstImage.setEnabled(false);
        projectionTogglePositionSecondImage = new JMenuItem();
        projectionTogglePositionSecondImage.setEnabled(first);
        projectionResetFirstImage = new JMenuItem();
        projectionResetSecondImage  = new JMenuItem();      
        
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

        showSimilarDescriptorsChoice = new JMenu();
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
        comparativePanelNWSW = new JPanel(); 
        glasspaneButtonPanel = new JPanel();

        imagePanel = new ImagePanel();
        imagePanel.setParentMainFrame(this);
        imageScrollPane = new ImageScrollPane(imagePanel, this);

        secondImagePanel = new ImagePanel();
        secondImagePanel.setParentMainFrame(this);
        secondImageScrollPane = new ImageScrollPane(secondImagePanel, this);
        
        hideGlasspaneButton = new JButton();
        antialiasButton = new JButton();
        tresholdButton = new JButton();
        similarDescriptorsTotalCountLabel = new JLabel();
        similarDescriptorsVisibleCountLabel = new JLabel();
        NWSWCurrentDescriptorSimiliraty = new JLabel();
        NWSWTotalDescriptorsSimiliraty = new JLabel();
        NWSWMode1 = new JButton();
        NWSWMode2 = new JButton();
        NWSWMode3 = new JButton();
        NWSWModeReload = new JButton();
        
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
        
        showSimilarDescriptorsChoice.setText(localLanguage.getString("mb_show_similar"));

        showSimilarDescriptors.setText(localLanguage.getString("mb_show_similar"));
        showSimilarDescriptors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dialogs.tresholdDialog(thisInstance, true);
            }
        });

        hideSimilarDescriptors.setText(localLanguage.getString("mb_hide_similar"));
        hideSimilarDescriptors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setComparationMode(VisualisationType.NONE);
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
                if(visualisationType == VisualisationType.NEEDLEMANWUNSCH ||
                        visualisationType == VisualisationType.SMITHWATERMAN){
                    if (ItemEvent.SELECTED == e.getStateChange()) {
                    ((ProjectionGlassPane) getGlassPane()).setAntialiasing(true);
                    } else {
                        ((ProjectionGlassPane) getGlassPane()).setAntialiasing(false);
                    }
                    return;
                }
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
                thisInstance.resetProjectionsBoth();
                thisInstance.setComparationMode(VisualisationType.NONE);
                oneImageModeComponents();

            }
        });

        twoImagesModeItem.setText(localLanguage.getString("mb_two_img"));
        twoImagesModeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                twoImageModeComponents();
            }
        });

        
        projectionFirstImage.setText(localLanguage.getString("mb_first"));
        projectionSecondImage.setText(localLanguage.getString("mb_second"));
        
        projectionLineColor.setText(localLanguage.getString("mb_similar_color"));
        projectionLineColor.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
                Color newColor = JColorChooser.showDialog(
                     thisInstance, "",
                     defaultProjectionColor);
        if (newColor != null) {
            defaultProjectionColor = newColor;
           ((ProjectionGlassPane)getGlassPane()).setHooverColor(newColor);
        }
            }
        
        
        });
        
        
 
        
         smithWatermanButton.setText(localLanguage.getString("mb_smithwaterman"));
         smithWatermanButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
                    Dialogs.SequenceMatchingAlgorithmScrogingDialog(thisInstance, visualisationType.SMITHWATERMAN);
            }
        
        
        });
        
        needlemanWunschButton.setText(localLanguage.getString("mb_needlemanwunsh"));
        needlemanWunschButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
                    Dialogs.SequenceMatchingAlgorithmScrogingDialog(thisInstance, visualisationType.NEEDLEMANWUNSCH);
            }
        });
        
        projectionResetFirstImage.setText(localLanguage.getString("mb_reset"));
        projectionResetFirstImage.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
                resetProjection(imageScrollPane);
                revalidate();
            }
        });
        projectionResetSecondImage.setText(localLanguage.getString("mb_reset"));
        projectionResetSecondImage.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
                resetProjection(secondImageScrollPane);
                revalidate();
            }
        });
        
        projectionFirstImageToX.setText(localLanguage.getString("mb_x"));
        projectionFirstImageToX.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
                
                setProjection(imageScrollPane, ProjectionTo.X);
                if(true)return;
                resetProjection(imageScrollPane);
                
                imageScrollPane.getImagePanel().getDescriptors().setProjection(ProjectionTo.X);
                projectionResetFirstImage.setEnabled(true);
                imageScrollPane.EnableProjectionPanelsToggling(true);

                setProjectionGlassPane();
               
                if(!imageScrollPane.isProjectionVisible()){
                    imageScrollPane.SetBottomProjectionPanelVisible();
                }
                projectionFirstImageToX.setSelected(true);
                projectionFirstImageToY.setSelected(false);
                projectionFirstImageCustom.setSelected(false);
                revalidate();
            }
        });
        
        projectionSecondImageToX.setText(localLanguage.getString("mb_x"));
        projectionSecondImageToX.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
            
            setProjection(secondImageScrollPane, ProjectionTo.X);
                if(true)return;
                resetProjection(secondImageScrollPane);
                secondImageScrollPane.getImagePanel().getDescriptors().setProjection(ProjectionTo.X);  

                projectionResetSecondImage.setEnabled(true);
                secondImageScrollPane.EnableProjectionPanelsToggling(true);
                setProjectionGlassPane();
                
                if(!secondImageScrollPane.isProjectionVisible()){
                    secondImageScrollPane.SetBottomProjectionPanelVisible();
                }
                
                projectionSecondImageToX.setSelected(true);
                projectionSecondImageToY.setSelected(false);
                projectionSecondImageCustom.setSelected(false);
                revalidate();
            }
        });
        
        projectionFirstImageToY.setText(localLanguage.getString("mb_y"));
        projectionFirstImageToY.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) { 
            
            setProjection(imageScrollPane, ProjectionTo.Y);
                if(true)return;
                resetProjection(imageScrollPane);
                imageScrollPane.getImagePanel().getDescriptors().setProjection(ProjectionTo.Y); 
                projectionResetFirstImage.setEnabled(true);
                imageScrollPane.EnableProjectionPanelsToggling(true);

                setProjectionGlassPane();
                
                if(!imageScrollPane.isProjectionVisible()){
                    imageScrollPane.SetBottomProjectionPanelVisible();
                }
                projectionFirstImageToX.setSelected(false);
                projectionFirstImageToY.setSelected(true);
                projectionFirstImageCustom.setSelected(false);
                revalidate();
            }
        });
        
        projectionSecondImageToY.setText(localLanguage.getString("mb_y"));
        projectionSecondImageToY.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {  
            
            setProjection(secondImageScrollPane, ProjectionTo.Y);
                if(true)return;
                resetProjection(secondImageScrollPane);
                secondImageScrollPane.getImagePanel().getDescriptors().setProjection(ProjectionTo.Y);               

                projectionResetSecondImage.setEnabled(true);
                secondImageScrollPane.EnableProjectionPanelsToggling(true);
                setProjectionGlassPane();
                
                if(!secondImageScrollPane.isProjectionVisible()){
                    secondImageScrollPane.SetBottomProjectionPanelVisible();
                }
                projectionSecondImageToX.setSelected(false);
                projectionSecondImageToY.setSelected(true);
                projectionSecondImageCustom.setSelected(false);
                revalidate();
            }
        });
        
        projectionFirstImageCustom.setText(localLanguage.getString("mb_custom"));
        projectionFirstImageCustom.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) { 
            
            setProjection(imageScrollPane, ProjectionTo.CUSTOM);
                if(true)return;
                resetProjection(imageScrollPane);
                projectionResetFirstImage.setEnabled(true);
                imageScrollPane.EnableProjectionPanelsToggling(true);
                
                imageScrollPane.getImagePanel().showProjectionPoints();
                
                setProjectionGlassPane();
                Point2D a = imageScrollPane.getImagePanel().getFirstProjectionPoint();
                Point2D b = imageScrollPane.getImagePanel().getSecondProjectionPoint();
                imageScrollPane.getImagePanel().getDescriptors().setProjection(ProjectionTo.CUSTOM, a, b); 
                

                imageScrollPane.getBottomProjectionPanel().setVisible(true);
                imageScrollPane.getBottomProjectionPanel().repaint();
                
                projectionFirstImageToX.setSelected(false);
                projectionFirstImageToY.setSelected(false);
                projectionFirstImageCustom.setSelected(true);

            revalidate();
            }
            
        });
        projectionSecondImageCustom.setText(localLanguage.getString("mb_custom"));
        projectionSecondImageCustom.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) { 
            
              
            setProjection(secondImageScrollPane, ProjectionTo.CUSTOM);
                if(true)return;
                resetProjection(secondImageScrollPane);
                projectionResetSecondImage.setEnabled(true);
                secondImageScrollPane.getImagePanel().showProjectionPoints();
                secondImageScrollPane.EnableProjectionPanelsToggling(true);
                
                setProjectionGlassPane();
                Point2D a = secondImageScrollPane.getImagePanel().getFirstProjectionPoint();
                Point2D b = secondImageScrollPane.getImagePanel().getSecondProjectionPoint();
                secondImageScrollPane.getImagePanel().getDescriptors().setProjection(ProjectionTo.CUSTOM, a, b); 
                

                secondImageScrollPane.getBottomProjectionPanel().setVisible(true);
                secondImageScrollPane.getBottomProjectionPanel().repaint();
                
                projectionSecondImageToX.setSelected(false);
                projectionSecondImageToY.setSelected(false);
                projectionSecondImageCustom.setSelected(true);

            revalidate();
            }
            
        });
        
        projectionTogglePositionFirstImage.setText(localLanguage.getString("mb_toggle"));
        projectionTogglePositionFirstImage.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {                   
                imageScrollPane.ToggleProjectionPanels();
            }
        });
        
        projectionTogglePositionSecondImage.setText(localLanguage.getString("mb_toggle"));
        projectionTogglePositionSecondImage.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {                   
                secondImageScrollPane.ToggleProjectionPanels();
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
                       localLanguage.getString("help_sixth_line") + "\n" +
                       localLanguage.getString("help_sixth_line") + "\n"+
                       localLanguage.getString("help_7_line") + "\n"+
                       localLanguage.getString("help_8_line") + "\n"+
                       localLanguage.getString("help_9_line") + "\n"+
                       localLanguage.getString("help_10_line") + "\n"+
                       localLanguage.getString("help_11_line") + "\n",
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

        url = getClass().getResource("icons/nwswmode1.png");
        ImageIcon nwswmode1Icon = new ImageIcon(url);
        Image nwswmode1Image = nwswmode1Icon.getImage().getScaledInstance(ImageScrollPane.ICON_WIDTH,
                ImageScrollPane.ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newNwswmode1Icon = new ImageIcon(nwswmode1Image);
        
        url = getClass().getResource("icons/nwswmode2.png");
        ImageIcon nwswmode2Icon = new ImageIcon(url);
        Image nwswmode2Image = nwswmode2Icon.getImage().getScaledInstance(ImageScrollPane.ICON_WIDTH,
                ImageScrollPane.ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newNwswmode2Icon = new ImageIcon(nwswmode2Image);
        
        url = getClass().getResource("icons/nwswmode3.png");
        ImageIcon nwswmode3Icon = new ImageIcon(url);
        Image nwswmode3Image = nwswmode3Icon.getImage().getScaledInstance(ImageScrollPane.ICON_WIDTH,
                ImageScrollPane.ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newNwswmode3Icon = new ImageIcon(nwswmode3Image);
        
        url = getClass().getResource("icons/reload.png");
        ImageIcon nwswmodeReloadIcon = new ImageIcon(url);
        Image nwswmodeReloadImage = nwswmodeReloadIcon.getImage().getScaledInstance(ImageScrollPane.ICON_WIDTH,
                ImageScrollPane.ICON_HEIGHT,
                Image.SCALE_SMOOTH);
        ImageIcon newNwswmodeReloadIcon = new ImageIcon(nwswmodeReloadImage);
        
        NWSWModeReload.setIcon(newNwswmodeReloadIcon);
        NWSWModeReload.setToolTipText("Reload");
        NWSWModeReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //NWSWModeReload.setEnabled(false);
                thisInstance.RefreshVisualization();
            }
        });
        
        NWSWMode1.setIcon(newNwswmode1Icon);
        NWSWMode1.setToolTipText("Connect similar descriptors.");
        NWSWMode1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((ProjectionGlassPane)getGlassPane()).setVisualizationOneLine();
            }
        });
        
        NWSWMode2.setIcon(newNwswmode2Icon);
        NWSWMode2.setToolTipText("Connect similar descriptors to final sequence.");
        NWSWMode2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((ProjectionGlassPane)getGlassPane()).setVisualizationThreeLines();
            }
        });
        
        NWSWMode3.setIcon(newNwswmode3Icon);
        NWSWMode3.setToolTipText("Only mouse hoover.");
        NWSWMode3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((ProjectionGlassPane)getGlassPane()).setVisualizationHover();
            }
        });
        
        hideGlasspaneButton.setIcon(newHideIcon);
        hideGlasspaneButton.setToolTipText(localLanguage.getString("hide_button"));
        hideGlasspaneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setComparationMode(VisualisationType.NONE);
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
        


        glasspaneButtonPanel.add(antialiasButton);
        glasspaneButtonPanel.add(tresholdButton);
        glasspaneButtonPanel.add(hideGlasspaneButton);
        
        comparativePanelNWSW.add(NWSWTotalDescriptorsSimiliraty);
        comparativePanelNWSW.add(hideGlasspaneButton);
        comparativePanelNWSW.add(NWSWModeReload);
        comparativePanelNWSW.add(NWSWMode1);
        comparativePanelNWSW.add(NWSWMode2);
        comparativePanelNWSW.add(NWSWMode3);
        comparativePanelNWSW.add(NWSWCurrentDescriptorSimiliraty);

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
        compareDescriptorsMenu.add(showSimilarDescriptorsChoice);
        showSimilarDescriptorsChoice.add(showSimilarDescriptors);
        showSimilarDescriptorsChoice.add(needlemanWunschButton);
        showSimilarDescriptorsChoice.add(smithWatermanButton);
        compareDescriptorsMenu.add(hideSimilarDescriptors);
        compareDescriptorsMenu.addSeparator();
        compareDescriptorsMenu.add(setTresholdItem);
        compareDescriptorsMenu.add(setLinesColorItem);
        compareDescriptorsMenu.add(setHooveredDescriptorColorItem);
        compareDescriptorsMenu.add(antialiasingCheckboxItem);
        showSimilarDescriptors.setEnabled(false);
        hideSimilarDescriptors.setEnabled(false);
        needlemanWunschButton.setEnabled(false);
        smithWatermanButton.setEnabled(false);
        setTresholdItem.setEnabled(false);
        setLinesColorItem.setEnabled(false);
        setHooveredDescriptorColorItem.setEnabled(false);
        antialiasingCheckboxItem.setEnabled(false);
        compareDescriptorsMenu.setText(localLanguage.getString("menu_bar_compare_descriptors"));
      
        //***** Build projection menu *****//
       // projectionMenu.add(projectionMenuX);
        projectionMenu.add(projectionFirstImage);
        projectionMenu.add(projectionSecondImage);
        projectionMenu.add(new JSeparator());
        projectionMenu.add(projectionLineColor);
        projectionLineColor.setEnabled(false);
        
        
        
        projectionFirstImage.add(projectionFirstImageToX);
        projectionFirstImage.add(projectionFirstImageToY);
        projectionFirstImage.add(projectionFirstImageCustom);
        projectionFirstImage.add(new JSeparator());
        projectionFirstImage.add(projectionTogglePositionFirstImage);
        projectionFirstImage.setEnabled(false);
        projectionFirstImage.add(projectionResetFirstImage);
        projectionResetFirstImage.setEnabled(false);
        
        projectionSecondImage.add(projectionSecondImageToX);
        projectionSecondImage.add(projectionSecondImageToY);
        projectionSecondImage.add(projectionSecondImageCustom);
        projectionSecondImage.add(new JSeparator());
        projectionSecondImage.add(projectionTogglePositionSecondImage);
        projectionSecondImage.setEnabled(false);
        projectionSecondImage.add(projectionResetSecondImage);
        projectionResetSecondImage.setEnabled(false);

        projectionMenu.setText(localLanguage.getString("mb_projection"));
        
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
        menuBar.add(projectionMenu);
        menuBar.add(compareDescriptorsMenu);
        menuBar.add(modeMenu);
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
        
        //keybindings
        KeyStroke ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK );
        projectionFirstImageToX.setAccelerator(ctrlX);
        
        KeyStroke ctrlY = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK );
        projectionFirstImageToY.setAccelerator(ctrlY);
        
        KeyStroke ctrlL = KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK );
        projectionFirstImageCustom.setAccelerator(ctrlL);
        
        KeyStroke altX = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.ALT_DOWN_MASK );
        projectionSecondImageToX.setAccelerator(altX);
        
        KeyStroke altY = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.ALT_DOWN_MASK );
        projectionSecondImageToY.setAccelerator(altY);
        
        KeyStroke altL = KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.ALT_DOWN_MASK );
        projectionSecondImageCustom.setAccelerator(altL);
        
        KeyStroke f1 = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
        oneImageModeItem.setAccelerator(f1);
        
        KeyStroke f2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0 );
        twoImagesModeItem.setAccelerator(f2);
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
        pane.getImagePanel().getDescriptors().recalculateVisualization();
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
        setStateOfProjectionMenu(false, SECOND_IMAGE_PANEL);
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

    public void setStateOfProjectionMenu(Boolean enabled, int imagePaneNumber) {
        if (imagePaneNumber == FIRST_IMAGE_PANEL) {
            projectionFirstImage.setEnabled(enabled);
        }
        if (imagePaneNumber == SECOND_IMAGE_PANEL) {
            projectionSecondImage.setEnabled(enabled);

        }
    }
    
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
            needlemanWunschButton.setEnabled(false);
            smithWatermanButton.setEnabled(false);
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
                    //needlemanWunschButton.setEnabled(true);
                    //smithWatermanButton.setEnabled(true);
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
        setComparationMode(VisualisationType.BRUTEFORCE);
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
    
    

    public void setComparationMode(VisualisationType newType) {
        
        boolean turnedOn = true;
        
        if(newType == VisualisationType.NONE){
            turnedOn = false;
        }
        else
            visualisationType = newType;
        
        if(firstImageDescriptors.isEnabled()){
            projectionFirstImage.setEnabled(!turnedOn);
        }
        if(secondImageDescriptors.isEnabled())
            projectionSecondImage.setEnabled(!turnedOn);
        

        
        if(visualisationType == VisualisationType.NEEDLEMANWUNSCH ||
                visualisationType == VisualisationType.SMITHWATERMAN){
            
                if(turnedOn){
                    comparativePanelNWSW.add(hideGlasspaneButton, 1);
                    getContentPane().add(comparativePanelNWSW, BorderLayout.NORTH);
                    comparativePanelNWSW.setVisible(true);
                    NWSWTotalDescriptorsSimiliraty.setText("Total similarity: NONE");
                    NWSWCurrentDescriptorSimiliraty.setText("Current two descriptors similarity: NONE");
                }
                else{
                    comparativePanelNWSW.setVisible(false);
                    getContentPane().remove(comparativePanelNWSW);
                    visualisationType = VisualisationType.NONE;
                }
                
                imageScrollPane.EnableProjectionPanelsToggling(turnedOn);
                secondImageScrollPane.EnableProjectionPanelsToggling(turnedOn);
                hideSimilarDescriptors.setEnabled(turnedOn);
                setTresholdItem.setEnabled(false);
                setLinesColorItem.setEnabled(turnedOn);
                setHooveredDescriptorColorItem.setEnabled(turnedOn);
                antialiasingCheckboxItem.setEnabled(turnedOn);
            
                showSimilarDescriptors.setEnabled(!turnedOn);
                needlemanWunschButton.setEnabled(!turnedOn);
                smithWatermanButton.setEnabled(!turnedOn);
                
                projectionFirstImage.setEnabled(!turnedOn);
                projectionSecondImage.setEnabled(!turnedOn);
                
                
                
                if(!turnedOn){
                    imageScrollPane.getBottomProjectionPanel().clear();
                    imageScrollPane.getSideProjectionPanel().clear();
                    secondImageScrollPane.getBottomProjectionPanel().clear();
                    secondImageScrollPane.getSideProjectionPanel().clear();
                    
                    projectionResetFirstImage.setEnabled(true);
                    projectionResetFirstImage.setEnabled(true);
                    imageScrollPane.EnableProjectionPanelsToggling(true);
                    secondImageScrollPane.EnableProjectionPanelsToggling(true);

                    setProjectionGlassPane();
                    imageScrollPane.getImagePanel().getDescriptors().setVisualizationMode(false);
                    secondImageScrollPane.getImagePanel().getDescriptors().setVisualizationMode(false);
                    
                    revalidate();
                } 
                  return;
        }
        
        
        //if bruteforce...
        imageScrollPane.resetProjection();
        secondImageScrollPane.resetProjection();
        
        
        if (!turnedOn) {
            getContentPane().remove(comparativePanel);
            setGlassPane(new JComponent() {});
            getGlassPane().setEnabled(false);
            getGlassPane().setVisible(false);
        } else {
            if (!comparationMode) {
                comparativePanel.add(hideGlasspaneButton, 2);
                getContentPane().add(comparativePanel, BorderLayout.NORTH);
            }
        }

        comparationMode = turnedOn;

        if (turnedOn) {
            hideSimilarDescriptors.setEnabled(true);
            showSimilarDescriptors.setEnabled(false);
            needlemanWunschButton.setEnabled(false);
            smithWatermanButton.setEnabled(false);
            resetProjectionsBoth();
        } else {
            resetProjectionsBoth();
            if (imageScrollPane.getImagePanel().getDescriptors() != null &&
                    secondImageScrollPane.getImagePanel().getDescriptors() != null) {
                
                showSimilarDescriptors.setEnabled(true);
            } else {
                showSimilarDescriptors.setEnabled(false);
                needlemanWunschButton.setEnabled(false);
                smithWatermanButton.setEnabled(false);
            }
            hideSimilarDescriptors.setEnabled(false);
        }

        setTresholdItem.setEnabled(turnedOn);
        setLinesColorItem.setEnabled(turnedOn);
        setHooveredDescriptorColorItem.setEnabled(turnedOn);
        antialiasingCheckboxItem.setEnabled(turnedOn);

        if(!turnedOn)
                    visualisationType = visualisationType.NONE;
        
        imageScrollPane.setDescriptorsLabels();
        secondImageScrollPane.setDescriptorsLabels();
        
        
        repaintAll();
    }

    public boolean isShowSimilarDescriptorsModeOLD() {
        return comparationMode;
    }
    
    public boolean isBruteforceVisualizationMode(){
        if(visualisationType == visualisationType.BRUTEFORCE)
            return true;
        return false;
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
    
    public VisualisationType getVisualizationType(){
        return visualisationType;
    }

    public void resetProjectionsBoth(){
        resetProjection(imageScrollPane);
        resetProjection(secondImageScrollPane);
        projectionLineColor.setEnabled(false);
    }
    public void resetProjection(ImageScrollPane isp){
        
        if(isp == null)
            return;
        if(isp.getImagePanel().getDescriptors() == null)
            return;
        if(isp.getImagePanel().getDescriptors().getProjectionType() == null)
            return;

        
        if(!projectionSet(getOtherISP(isp))){
            projectionLineColor.setEnabled(false);
        }
        
        needlemanWunschButton.setEnabled(false);
        smithWatermanButton.setEnabled(false); 
                   
        ProjectionTo type = isp.getImagePanel().getDescriptors().getProjectionType();

        isp.getImagePanel().HideCustomProjectionPoints();           
        
        isp.EnableProjectionPanelsToggling(false);
        
        
        isp.getImagePanel().getDescriptors().cancelProjection();
        isp.hideProjectionPanels();
        resetProjectionSelectionMenu(isp);
    }
    
    public void resetProjectionSelectionMenu(ImageScrollPane isp) {
        if(isp == imageScrollPane){
            projectionFirstImageToX.setSelected(false);
            projectionFirstImageToY.setSelected(false);
            projectionFirstImageCustom.setSelected(false);
            projectionResetFirstImage.setEnabled(false);
            projectionTogglePositionFirstImage.setEnabled(false);
        }
        if(isp == secondImageScrollPane){
            projectionSecondImageToX.setSelected(false);
            projectionSecondImageToY.setSelected(false);
            projectionSecondImageCustom.setSelected(false);
            projectionResetSecondImage.setEnabled(false);
            projectionTogglePositionSecondImage.setEnabled(false);

        } 
    }
    
    public ImageScrollPane getOtherISP(ImageScrollPane isp){
        if(isp == imageScrollPane)
            return secondImageScrollPane;
        else
            return imageScrollPane;
    }
    
    public boolean projectionSet(ImageScrollPane isp){
        if(isp.getImagePanel().getDescriptors() != null)
            if(isp.getImagePanel().getDescriptors().getProjectionType() != null)
                return true;
        return false;
    }
    
    public boolean bothProjectionsSet(){
        return projectionSet(imageScrollPane) && projectionSet(secondImageScrollPane);
    }
    
    public void setProjectionGlassPane(){
     
        projectionGlassPane = new ProjectionGlassPane(imageScrollPane, secondImageScrollPane, defaultProjectionColor, defaultComparisonColor, antialiasingEnabled());
        setGlassPane(projectionGlassPane);
        projectionGlassPane.setEnabled(true);
        projectionGlassPane.setVisible(true);
        revalidate();
    }
    
    public void setVisualisationType(VisualisationType visualisationType){
        this.visualisationType = visualisationType;
    }
    
    public void setVisualisationType(int i){
        if (i ==0)
        this.visualisationType = VisualisationType.BRUTEFORCE;
        if (i == 1)
        this.visualisationType = VisualisationType.NEEDLEMANWUNSCH;
        if (i == 2)
        this.visualisationType = VisualisationType.SMITHWATERMAN;
    }
    
 
    public void SetNWSWTotalSimilarity(float a){
        NWSWTotalDescriptorsSimiliraty.setText("Total similarity: " + Float.toString(a));
    }
    
    public void SetNWSWCurrentSimilarity(ObjectFeature o1, ObjectFeature o2){
        SetNWSWCurrentSimilarity(cost.getCost(o1, o2));
    }
    
    public void SetNWSWCurrentSimilarity(Float a){
        if(a != null)
            NWSWCurrentDescriptorSimiliraty.setText("Current two descriptors similarity: " +Float.toString(a));
        else
            NWSWCurrentDescriptorSimiliraty.setText("Current two descriptors similarity: NONE");
    }
    
    public int getComparativePanelNWSWHeight(){
        if(comparativePanelNWSW.isVisible() && (visualisationType == VisualisationType.NEEDLEMANWUNSCH || visualisationType == VisualisationType.SMITHWATERMAN))
            return comparativePanelNWSW.getHeight();
        else
            return 0;
    }
    
    public void setSequenceMatchingScoring(SequenceMatchingCost cost){
        this.cost = cost;
    }
    
    public void RefreshVisualization(){
                    this.setEnabled(false);
                    Set <ObjectFeature> first = imageScrollPane.getImagePanel().getDescriptors().getVisibleDescriptors();
                    Set <ObjectFeature> second = secondImageScrollPane.getImagePanel().getDescriptors().getVisibleDescriptors();
                    

                    lockImagePanels(true);
                    ((ProjectionGlassPane)getGlassPane()).setPause(true);
                    ((ProjectionGlassPane)getGlassPane()).setHooveredComparisonDescriptor(null);
                    if(visualisationType == VisualisationType.NEEDLEMANWUNSCH){
                        new NeedlemanWunsch(cost,  imageScrollPane.getImagePanel().getDescriptors().getProjection().getSortedProjection(first),  secondImageScrollPane.getImagePanel().getDescriptors().getProjection().getSortedProjection(second), getMainFrame()); 

                    }
                        else if(visualisationType == VisualisationType.SMITHWATERMAN)
                        new SmithWaterman(cost,  imageScrollPane.getImagePanel().getDescriptors().getProjection().getSortedProjection(first),  secondImageScrollPane.getImagePanel().getDescriptors().getProjection().getSortedProjection(second), getMainFrame()); 
                    
                   
    }
    
    public MainFrame getMainFrame(){
        return this;
    }
    
    public synchronized void ShowVisualizationProgressBar(int max){
    
 
                final int MAXIMUM = 100;
                JPanel panel;

                ComputeVisualizationDialog = new JDialog();
                ComputeVisualizationProgressBar = new JProgressBar(0, max);
                ComputeVisualizationProgressBar.setIndeterminate(true);

                JLabel msgLabel = new JLabel();
                msgLabel.setText("Computing similarity...");

                panel = new JPanel(new BorderLayout(5, 5));
                panel.add(msgLabel, BorderLayout.PAGE_START);
                panel.add(ComputeVisualizationProgressBar, BorderLayout.CENTER);
                panel.setBorder(BorderFactory.createEmptyBorder(11, 11, 11, 11));
        
                ComputeVisualizationDialog.getContentPane().add(panel);
                ComputeVisualizationDialog.setResizable(false);
                ComputeVisualizationDialog.pack();
                ComputeVisualizationProgressBar.setIndeterminate(false);
                ComputeVisualizationDialog.setSize(500, ComputeVisualizationDialog.getHeight());
                ComputeVisualizationDialog.setLocationRelativeTo(thisInstance);
                ComputeVisualizationDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                ComputeVisualizationDialog.setAlwaysOnTop(true);
                ComputeVisualizationDialog.setVisible(true);
                msgLabel.setBackground(panel.getBackground());
                
                
                
    }
    
    public synchronized void setDataToVisualizationProgressBar(int current){
        ComputeVisualizationProgressBar.setValue(current);
    }
    
    public synchronized void HideVisualizationProgressBar(){
        ComputeVisualizationDialog.setVisible(false);
        ComputeVisualizationDialog.dispose();
        ComputeVisualizationDialog = null;
        thisInstance.setEnabled(true);
        thisInstance.toFront();   
    }
    
    public void lockImagePanels(boolean bool){
        imageScrollPane.getImagePanel().setLock(bool);
        secondImageScrollPane.getImagePanel().setLock(bool);
    }
    
    public void setProjection(ImageScrollPane isp, ProjectionTo projectionTo){
        
                resetProjection(isp);

                if(projectionTo == ProjectionTo.CUSTOM){
                    isp.getImagePanel().showProjectionPoints();
                    Point2D a = secondImageScrollPane.getImagePanel().getFirstProjectionPoint();
                    Point2D b = secondImageScrollPane.getImagePanel().getSecondProjectionPoint();
                    isp.getImagePanel().getDescriptors().setProjection(projectionTo, a, b);
                }
                else{
                    isp.getImagePanel().getDescriptors().setProjection(projectionTo);
                }
                
                if(isp == imageScrollPane){
                    projectionTogglePositionFirstImage.setEnabled(true);
                    projectionResetFirstImage.setEnabled(true);
                }   
                else{
                    projectionResetSecondImage.setEnabled(true);
                    projectionTogglePositionSecondImage.setEnabled(true);
                }
                    
                isp.EnableProjectionPanelsToggling(true);

                setProjectionGlassPane();
               
                if(isp == imageScrollPane){
                    if(!imageScrollPane.isProjectionVisible()){
                        imageScrollPane.SetBottomProjectionPanelVisible();
                    }
                }
                else{
                    if(!secondImageScrollPane.isProjectionVisible()){
                        secondImageScrollPane.SetBottomProjectionPanelVisible();
                    }
                }
                
                if(isp == imageScrollPane){
                    projectionFirstImageToX.setSelected(false);
                    projectionFirstImageToY.setSelected(false);
                    projectionFirstImageCustom.setSelected(false);
                    switch (projectionTo){
                        case X: projectionFirstImageToX.setSelected(true);
                                break;
                        case Y: projectionFirstImageToY.setSelected(true);
                                break;
                        case CUSTOM:  projectionFirstImageCustom.setSelected(true);
                    }   
                }
                else{
                    projectionSecondImageToX.setSelected(false);
                    projectionSecondImageToY.setSelected(false);
                    projectionSecondImageCustom.setSelected(false);
                    switch (projectionTo){
                        case X: projectionSecondImageToX.setSelected(true);
                                break;
                        case Y: projectionSecondImageToY.setSelected(true);
                                break;
                        case CUSTOM:  projectionSecondImageCustom.setSelected(true);
                    }
                }
                
                if(bothProjectionsSet()){
                    needlemanWunschButton.setEnabled(true);
                    smithWatermanButton.setEnabled(true);
                }
                else{
                    needlemanWunschButton.setEnabled(false);
                    smithWatermanButton.setEnabled(false); 
                }
                
                projectionLineColor.setEnabled(true);
        
    }
    
    public int getMenuBarHeight(){
        return menuBar.getHeight();
    }
    
    public boolean antialiasingEnabled(){
        return antialiasingCheckboxItem.getState();
    }
    
    public void setDefaultLinesColor(Color color){
        defaultComparisonColor = color;
    }
    
    

}