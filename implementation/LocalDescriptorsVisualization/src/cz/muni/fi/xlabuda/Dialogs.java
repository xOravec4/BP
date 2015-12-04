package cz.muni.fi.xlabuda;

import cz.muni.fi.xlabuda.MainFrame.VisualisationType;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.scene.control.RadioButton;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import messif.objects.impl.ObjectFeature;

/**
 * Input and other dialogs for main frame.
 *
 * @author Marián Labuda
 * @version 1.0
 */
public class Dialogs {

    private static ResourceBundle localLanguage = ResourceBundle.getBundle("Dialogs", MainFrame.locale);

    /**
     * Create dialog for zooming image, with input in percentage
     * @param frame parent main frame
     * @param panel panel where to zoom
     */
    public static void zoomFormDialog(MainFrame frame, final ImagePanel panel) {
        final JDialog dialog;
        final JOptionPane optionPane = new JOptionPane();

        JPanel buttonPanel = new JPanel();

        final JComboBox zoomSizeComboBox = new JComboBox();

        JButton approveButton = new JButton();
        JButton cancelButton = new JButton();

        optionPane.setLayout(new BorderLayout());

        Object[] possibleZoom = {"10", "25", "50", "75", "100", "150", "200", "500", "1000"};

        zoomSizeComboBox.setModel(new DefaultComboBoxModel(possibleZoom));
        zoomSizeComboBox.setEditable(true);

        boolean insert = false;
        int position = 5;
        double currentZoom = panel.getZoomScale() * 100;

        for (int i=0; i<possibleZoom.length; i++) {
            double currentPossible = Double.valueOf((String) possibleZoom[i]);
            if (currentPossible == currentZoom) {
                position = i;
                break;
            }
            if (currentPossible < currentZoom) {
                if (i+1 < possibleZoom.length) {
                    double nextPossible = Double.valueOf((String) possibleZoom[i+1]);
                    if (nextPossible > currentZoom) {
                        zoomSizeComboBox.insertItemAt(currentZoom, i+1);
                        insert = true;
                        position = i+1;
                        break;
                    }
                }
            }
        }

        final boolean finalInsert = insert;
        final int insertPosition = position;

        zoomSizeComboBox.setSelectedIndex(position);

        buttonPanel.add(approveButton);
        buttonPanel.add(cancelButton);

        optionPane.add(new JLabel(localLanguage.getString("zd_message")), BorderLayout.NORTH);
        optionPane.add(zoomSizeComboBox, BorderLayout.CENTER);
        optionPane.add(buttonPanel, BorderLayout.SOUTH);

        dialog = optionPane.createDialog(panel.getParentImageScrollPane().getParentMainFrame(),
                localLanguage.getString("zd_title"));

        approveButton.setText(localLanguage.getString("button"));
        approveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String zoomSizeString = (String) zoomSizeComboBox.getSelectedItem();
                try {
                    double zoomSize = Double.valueOf(zoomSizeString);
                    panel.zoom(zoomSize / 100);
                    if (finalInsert) {
                        zoomSizeComboBox.removeItemAt(insertPosition);
                    }
                    dialog.dispose();
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(panel.getParentImageScrollPane().getParentMainFrame(),
                            localLanguage.getString("jop_msg_wrong_format"),
                            localLanguage.getString("jop_tit_wrong_format"),
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        cancelButton.setText(localLanguage.getString("cancel_button"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Create new dialog with color chooser to change color of descriptors shapes
     * @param frame parent main frame
     * @param descriptors class of encapsulated descriptors to change color of shapes
     */
    public static void colorChooserDialog(MainFrame frame, LocalDescriptors descriptors) {
        Color newColor = JColorChooser.showDialog(
                     frame, localLanguage.getString("ccd_title"),
                     descriptors.getDefaultColor());
        if (newColor != null) {
            descriptors.setDescriptorColor(newColor);
        }
    }

    public static void similarDescriptorsColorChooserDialog(MainFrame frame) {
        GlasspaneForSimilarDescriptors glasspane = (GlasspaneForSimilarDescriptors) frame.getGlassPane();
        Color newColor = JColorChooser.showDialog(
                     frame, localLanguage.getString("ccsd_title"),
                     glasspane.getSimilarDescriptorsColor());
        if (newColor != null) {
            glasspane.setSimilarDescriptorsColor(newColor);
        }
    }

    public static void hooveredDescriptorColorChooserDialog(MainFrame frame) {
        if(frame.getVisualizationType() == MainFrame.VisualisationType.NEEDLEMANWUNSCH ||
                frame.getVisualizationType() == MainFrame.VisualisationType.NEEDLEMANWUNSCH){
        
            ProjectionGlassPane glasspane = (ProjectionGlassPane) frame.getGlassPane();
            Color newColor = JColorChooser.showDialog(
                    frame, localLanguage.getString("cchd_title"),
                    glasspane.getHooverColor());
            if (newColor != null) {
                glasspane.setHooverColor(newColor);
            }
        
        
        
        } else {
            GlasspaneForSimilarDescriptors glasspane = (GlasspaneForSimilarDescriptors) frame.getGlassPane();
            Color newColor = JColorChooser.showDialog(
                    frame, localLanguage.getString("cchd_title"),
                    glasspane.getHooverColor());
            if (newColor != null) {
                glasspane.setHooverColor(newColor);
            }

        }

    }

    public static void absoluteLocationDialog(final Component parentComponent, final LocalDescriptors descriptors) {
        final JDialog dialog;
        final JOptionPane optionPane = new JOptionPane();
        final JComboBox imageSizeComboBox = new JComboBox();

        JPanel buttonPanel = new JPanel();

        JLabel label = new JLabel();

        JButton approveButton = new JButton();
        JButton cancelButton = new JButton();

        String[] possibleImageSizes = new String[] {
            "64x64", "96x96", "128x128", "320x200", "640x480", "720x348",
            "720x480", "640x480", "800x600", "1024x768", "1280x1024",
            "1440x900", "1600x1200", "1680x1050", "1920x1200"};
      
        imageSizeComboBox.setModel(new DefaultComboBoxModel(possibleImageSizes));
        imageSizeComboBox.setEditable(true);

        int imageWidth = descriptors.getParentImagePanel().getImage().getWidth();
        int imageHeight = descriptors.getParentImagePanel().getImage().getHeight();

        int setPosition = -1;
        String currentSize = imageWidth + "x" + imageHeight;
        for (int i=0; i < possibleImageSizes.length; i++) {
            int width = Integer.parseInt(possibleImageSizes[i].split("x")[0]);
            int height = Integer.parseInt(possibleImageSizes[i].split("x")[1]);
            if (width == imageWidth) {
                if (height < imageHeight) {
                    imageSizeComboBox.insertItemAt(currentSize, i+1);
                    setPosition = i+1;
                }
                if (height > imageHeight) {
                    imageSizeComboBox.insertItemAt(currentSize, i);
                    setPosition = i;
                }
                if (height == imageHeight) {
                    setPosition = i;
                }
                break;
            }
            if (width < imageWidth) {
                if (i+1 < possibleImageSizes.length) {
                    int nextWidth = Integer.parseInt(possibleImageSizes[i+1].split("x")[0]);
                    int nextHeight = Integer.parseInt(possibleImageSizes[i+1].split("x")[1]);
                    if (nextWidth > imageWidth) {
                        imageSizeComboBox.insertItemAt(currentSize, i+1);
                        setPosition = i+1;
                        break;
                    }
                    if (nextWidth == imageWidth) {
                        if (nextHeight == imageHeight) {
                            setPosition = i+1;
                            break;
                        }
                        if (nextHeight > imageHeight) {
                            imageSizeComboBox.insertItemAt(currentSize, i+1);
                            setPosition = i+1;
                            break;
                        }
                    }
                }
            }
        }
        if (setPosition == -1) {
            // Image is bigger than predefined sizes
            setPosition = imageSizeComboBox.getItemCount();
            imageSizeComboBox.addItem(currentSize);
        }
        imageSizeComboBox.setSelectedIndex(setPosition);
        
        label.setText("<html>" + localLanguage.getString("size_dialog_label1") + "<br>" +
                localLanguage.getString("size_dialog_label2") + "<html>");

        buttonPanel.add(approveButton);
        buttonPanel.add(cancelButton);

        optionPane.setLayout(new BorderLayout());
        optionPane.add(label, BorderLayout.NORTH);
        optionPane.add(imageSizeComboBox, BorderLayout.CENTER);
        optionPane.add(buttonPanel, BorderLayout.SOUTH);

        dialog = optionPane.createDialog(parentComponent, localLanguage.getString("size_title"));

        approveButton.setText(localLanguage.getString("button"));
        approveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String item = (String) imageSizeComboBox.getSelectedItem();
                String[] size = ((String) imageSizeComboBox.getSelectedItem()).split("x");
                if (size.length != 2 || item.indexOf("x") != item.lastIndexOf("x")) {
                    JOptionPane.showMessageDialog(parentComponent, localLanguage.getString("jop_msg_wrong_format"),
                        localLanguage.getString("jop_tit_wrong_format"), javax.swing.JOptionPane.WARNING_MESSAGE);
                } else {
                    try {
                        int width = Integer.parseInt(size[0]);
                        int height = Integer.parseInt(size[1]);
                        descriptors.setImageSize(new Dimension(width,height));
                        dialog.dispose();
                    } catch (NumberFormatException exception) {
                        JOptionPane.showMessageDialog(parentComponent, localLanguage.getString("jop_msg_wrong_format"),
                            localLanguage.getString("jop_tit_wrong_format"), javax.swing.JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        cancelButton.setText(localLanguage.getString("cancel_button"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.addWindowListener(new java.awt.event.WindowListener() {
            public void windowOpened(WindowEvent e) {}

            public void windowClosing(WindowEvent e) {}

            public void windowClosed(WindowEvent e) {
                if (descriptors.getImageSize() == null) {
                    descriptors.setImageSize(new Dimension (
                            descriptors.getParentImagePanel().getImage().getWidth(),
                            descriptors.getParentImagePanel().getImage().getHeight()));
                }
            }

            public void windowIconified(WindowEvent e) {}

            public void windowDeiconified(WindowEvent e) {}

            public void windowActivated(WindowEvent e) {}

            public void windowDeactivated(WindowEvent e) {}
        });

        dialog.pack();
        dialog.setVisible(true);
    }

    public static void scaleFilterDialog(final ImageScrollPane pane, final LocalDescriptors descriptors) {
        final JDialog dialog;
        final JOptionPane optionPane = new JOptionPane();
        
        Double curMin = descriptors.getScaleFilter()[0];
        Double curMax = descriptors.getScaleFilter()[1];
        
        final Double max = Math.ceil(descriptors.getScaleRange()[1] + 0.1);
        
        final SpinnerModel minimumModel = new SpinnerNumberModel(curMin, new Double(Math.min(0, curMin)), max, new Double(0.1));
        final SpinnerModel maximumModel = new SpinnerNumberModel(curMax, new Double(Math.min(0, curMin)), max, new Double(0.1));
        
        final JPanel spinnerPanel = new JPanel();
        final JSpinner minimumSpinner = new JSpinner(minimumModel);
        final JSpinner maximumSpinner = new JSpinner(maximumModel);
                        
        JLabel minimumLabel = new JLabel(localLanguage.getString("min"));
        JLabel maximumLabel = new JLabel(localLanguage.getString("max"));
        
        minimumSpinner.setPreferredSize(new Dimension(70,25));
        maximumSpinner.setPreferredSize(new Dimension(70,25));
        
        spinnerPanel.add(minimumLabel);
        spinnerPanel.add(minimumSpinner);
        spinnerPanel.add(maximumLabel);
        spinnerPanel.add(maximumSpinner);        
        
        JButton approveButton = new JButton();
        approveButton.setText(localLanguage.getString("button"));

        optionPane.setMessage(new Object[] {spinnerPanel});
        optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
        optionPane.setOptions(new Object[] {approveButton});

        dialog = optionPane.createDialog(pane.getParentMainFrame(), localLanguage.getString("scale_filter_title"));

        approveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double minValue = ((Double)minimumSpinner.getValue()).doubleValue();
                double maxValue = ((Double)maximumSpinner.getValue()).doubleValue();
                if (minValue <= maxValue) {
                    descriptors.setScaleFilter(minValue, maxValue);
                    pane.setScaleFilterActive(minValue, maxValue);
                    dialog.dispose();
                }
            }
        });

        dialog.setSize(290,110);
        dialog.setVisible(true);
    }

    /**
     * Create new dialog to set treshold of similar descriptors
     * @param frame parent main frame
     */
    public static void tresholdDialog(final MainFrame frame, final boolean createGlasspane) {
        final JDialog dialog;
        final JOptionPane optionPane = new JOptionPane();

        double setMinimumTreshold = 0;
        double setMaximumTreshold = GlasspaneForSimilarDescriptors.MAXIMUM_DISTANCE_TRESHOLD;
        if (!createGlasspane) {
            GlasspaneForSimilarDescriptors glasspane = 
                    (GlasspaneForSimilarDescriptors) frame.getGlassPane();
            setMinimumTreshold = glasspane.getDistanceTreshold()[0];
            setMaximumTreshold = glasspane.getDistanceTreshold()[1];            
        }
        
        final SpinnerModel minimumModel = new SpinnerNumberModel(setMinimumTreshold,
                0, GlasspaneForSimilarDescriptors.MAXIMUM_DISTANCE_TRESHOLD, 0.1);
        final SpinnerModel maximumModel = new SpinnerNumberModel(setMaximumTreshold,
                0, GlasspaneForSimilarDescriptors.MAXIMUM_DISTANCE_TRESHOLD, 0.1);
        
        final JPanel spinnerPanel = new JPanel();
        final JSpinner minimumSpinner = new JSpinner(minimumModel);
        final JSpinner maximumSpinner = new JSpinner(maximumModel);
        
        JLabel minimumLabel = new JLabel(localLanguage.getString("min"));
        JLabel maximumLabel = new JLabel(localLanguage.getString("max"));
        
        spinnerPanel.add(minimumLabel);
        spinnerPanel.add(minimumSpinner);
        spinnerPanel.add(maximumLabel);
        spinnerPanel.add(maximumSpinner);        
        
        JButton approveButton = new JButton();
        approveButton.setText(localLanguage.getString("button"));

        optionPane.setMessage(new Object[] {localLanguage.getString("td_message"), spinnerPanel});
        optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
        optionPane.setOptions(new Object[] {approveButton});

        dialog = optionPane.createDialog(frame, localLanguage.getString("td_title"));

        approveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double minValue = ((Double) minimumSpinner.getValue()).doubleValue();
                double maxValue = ((Double) maximumSpinner.getValue()).doubleValue();
                if (minValue <= maxValue) {
                    frame.setTresholdAndCreateGlasspane(minValue, maxValue, createGlasspane);
                    dialog.dispose();
                }
            }
        });

        dialog.setVisible(true);
    }

    public static void shapeSizeSilderDialog(MainFrame parentFrame, final LocalDescriptors descriptors) {
        
        
        final JDialog dialog;
        final JOptionPane optionPane = new JOptionPane();
        final JSlider slider = new JSlider();

        slider.setMinimum(0);
        slider.setMaximum(5);
        slider.setValue(Double.valueOf(descriptors.getDescriptorsSize()).intValue());
        slider.setMajorTickSpacing(1);
        slider.setSnapToTicks(true);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        JButton approveButton = new JButton();
        approveButton.setText(localLanguage.getString("button"));

        optionPane.setMessage(new Object[] {localLanguage.getString("ss_message"), slider});
        optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
        optionPane.setOptions(new Object[] {approveButton});

        dialog = optionPane.createDialog(parentFrame, localLanguage.getString("ss_title"));

        approveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                descriptors.setDescriptorsSize(slider.getValue());
                descriptors.getParentImagePanel().repaint();
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }
    
     public static void ProjectionSelection(final MainFrame frame, final int algorithm) {
        
        final JDialog dialog;
        final JOptionPane optionPane = new JOptionPane();
        
        
        
        final JRadioButton optionToX = new JRadioButton("Projection to X");
        final JRadioButton optionToY = new JRadioButton("Projection to Y");
        final JRadioButton optionDefined = new JRadioButton("Use current projections");
 
        ButtonGroup group = new ButtonGroup();
        group.add(optionToX);
        group.add(optionToY);
        group.add(optionDefined);
        
        JPanel asd = new JPanel();
        asd.setLayout(new BoxLayout(asd, BoxLayout.PAGE_AXIS));
        asd.add(optionToX);
        asd.add(optionToY);
        asd.add(optionDefined);
        
        if(!frame.bothProjectionsSet()){
            optionDefined.setEnabled(false);
            optionDefined.setText("Use current projections (one or both projections are not enabled)");
        }

        final JButton approveButton = new JButton();
        approveButton.setText(localLanguage.getString("button"));
        
        
        if(!frame.bothProjectionsSet()){
            optionDefined.setEnabled(false);
        }
        approveButton.setEnabled(false);

        optionPane.setMessage(new Object[] {"Choose projection:", asd});
        optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
        optionPane.setOptions(new Object[] {approveButton});

        optionToX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                approveButton.setEnabled(true);
            }
        });
        optionToY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                approveButton.setEnabled(true);
            }
        });
        optionDefined.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                approveButton.setEnabled(true);
            }
        });

        dialog = optionPane.createDialog(frame, "Projection selection");
        
        approveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                frame.setVisualisationType(algorithm);
                System.out.println("algp"+algorithm);
                
                Projection projection1 = null;
                Projection projection2 = null;
                Set <ObjectFeature> first = frame.getFirstScrollPane().getImagePanel().getDescriptors().getVisibleDescriptors();
                Set <ObjectFeature> second = frame.getSecondScrollPane().getImagePanel().getDescriptors().getVisibleDescriptors();
                
                
                if(optionToX.isSelected()){
                System.out.println("X");
                    projection1 = new Projection(ProjectionTo.X);
                    projection2 = new Projection(ProjectionTo.X);
                }
                else if(optionToY.isSelected()){
                System.out.println("Y");
                    projection1 = new Projection(ProjectionTo.Y);
                    projection2 = new Projection(ProjectionTo.Y);
                }
                else if(optionDefined.isSelected()){
                System.out.println("DEF");
                    projection1 = frame.getFirstScrollPane().getImagePanel().getDescriptors().getProjection();
                    projection2 = frame.getSecondScrollPane().getImagePanel().getDescriptors().getProjection();
                }
                
                
                frame.setProjectionGlassPane();
                
                List<ObjectFeature> result1 = null;
                List<ObjectFeature> result2 = null;
                float similatity = 0;
                if(algorithm == 1){
                    NeedlemanWunsch needlemanWunsch = new NeedlemanWunsch( projection1.getSortedProjection(first),  projection2.getSortedProjection(second)); 
                    result1 = needlemanWunsch.getFirstSequence();
                    result2 = needlemanWunsch.getSecondSequence();
                    similatity = needlemanWunsch.getSimilarity();
                }
                else if(algorithm == 2){
                    SmithWaterman smithWaterman = new SmithWaterman( projection1.getSortedProjection(first),  projection2.getSortedProjection(second)); 
                    result1 = smithWaterman.getFirstSequence();
                    result2 = smithWaterman.getSecondSequence();
                    similatity = smithWaterman.getSimilarity();
                
                }
                 
                frame.setComparationMode(true);
                frame.getFirstScrollPane().getBottomProjectionPanel().setData(result1);
                frame.getFirstScrollPane().getSideProjectionPanel().setData(result1);
                frame.getSecondScrollPane().getBottomProjectionPanel().setData(result2);
                frame.getSecondScrollPane().getSideProjectionPanel().setData(result2);
                
                
                frame.getFirstScrollPane().SetBottomProjectionPanelVisible();
                frame.getSecondScrollPane().SetBottomProjectionPanelVisible();
                frame.getFirstScrollPane().SetSideProjectionPanelInvisible();
                frame.getSecondScrollPane().SetSideProjectionPanelInvisible();
                
                frame.SetNWSWTotalSimilarity(similatity);
                dialog.dispose();
            }
        });
        
        
        
        dialog.setVisible(true);

     }
}
