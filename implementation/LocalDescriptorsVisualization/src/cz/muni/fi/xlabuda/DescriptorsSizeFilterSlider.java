package cz.muni.fi.xlabuda;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Slider size filter for descriptors to choose range of the visualization.
 *
 * @author Marian Labuda
 * @version 1.0.
 */
public class DescriptorsSizeFilterSlider extends JDialog {

    // multiplier CONSTANT
    private static final int CONSTANT = 1000;

    private ResourceBundle localLanguage;

    private LocalDescriptors descriptors;

    private JSlider minimumSizeSlider;
    private JSlider maximumSizeSlider;
    private JLabel minimumValueLabel;
    private JLabel maximumValueLabel;
    private JLabel minimumNameLabel;
    private JLabel maximumNameLabel;
    private JButton approveButton;

    // New range for the visualization
    private Double newFilterMinSize;
    private Double newFilterMaxSize;

    public DescriptorsSizeFilterSlider(LocalDescriptors descriptors) {
        super(descriptors.getParentImagePanel().getParentImageScrollPane().getParentMainFrame(),
                null, JDialog.DEFAULT_MODALITY_TYPE);
        localLanguage = ResourceBundle.getBundle("SizeFilterSlider", MainFrame.locale);
        setTitle(localLanguage.getString("title"));

        this.descriptors = descriptors;

        minimumNameLabel = new JLabel();
        maximumNameLabel = new JLabel();
        minimumValueLabel = new JLabel();
        maximumValueLabel = new JLabel();
        minimumSizeSlider = new JSlider();
        maximumSizeSlider = new JSlider();
        approveButton = new JButton(localLanguage.getString("button"));

        minimumNameLabel.setText(localLanguage.getString("min_size"));
        maximumNameLabel.setText(localLanguage.getString("max_size"));

        double[] scaleRange = descriptors.getScaleRange();
        int maximumValueFix = 0;
        if (scaleRange[0] != 0 && scaleRange[1] != 0) {
            maximumValueFix = 1;
        }
        int minSizeMultiplyByConstant = Double.valueOf(scaleRange[0] * CONSTANT).intValue();
        int maxSizeMultiplyByConstant = Double.valueOf(scaleRange[1] * CONSTANT).intValue();

        Double[] sizeFilterRange = descriptors.getScaleFilter();
        Double minimumFilterSize = sizeFilterRange[0];
        Double maximumFilterSize = sizeFilterRange[1];
        newFilterMinSize = minimumFilterSize;
        newFilterMaxSize = maximumFilterSize;
        // TODO +1 in maximum 
        int minFilterSizeMultiplyByConstant = Double.valueOf(minimumFilterSize * CONSTANT).intValue();
        int maxFilterSizeMultiplyByConstant = Double.valueOf(maximumFilterSize * CONSTANT).intValue();

        minimumSizeSlider.setPreferredSize(new Dimension(400, 45));
        minimumSizeSlider.setMinimum(minSizeMultiplyByConstant);
        minimumSizeSlider.setMaximum(maxSizeMultiplyByConstant + maximumValueFix);
        minimumSizeSlider.setValue(minFilterSizeMultiplyByConstant);
        minimumSizeSlider.setMinorTickSpacing(CONSTANT/100);
        minimumSizeSlider.setMajorTickSpacing(CONSTANT/10);
        minimumSizeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                minimumSizeSlider.setMaximum(maximumSizeSlider.getValue());
                setNewMinimumSizeFilter(e);
            }
        });

        minimumValueLabel.setText(String.valueOf(minimumFilterSize));

        maximumSizeSlider.setPreferredSize(new Dimension(400, 50));
        maximumSizeSlider.setMinimum(minSizeMultiplyByConstant);
        maximumSizeSlider.setMaximum(maxSizeMultiplyByConstant + maximumValueFix);
        maximumSizeSlider.setValue(maxFilterSizeMultiplyByConstant);
        maximumSizeSlider.setMinorTickSpacing(CONSTANT/100);
        maximumSizeSlider.setMajorTickSpacing(CONSTANT/10);
        maximumSizeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                maximumSizeSlider.setMinimum(minimumSizeSlider.getValue());
                setNewMaximumSizeFilter(e);
            }
        });

        maximumValueLabel.setText(String.valueOf(maximumFilterSize));

        approveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                approveButtonActionPerformed();
            }
        });

        // Set position of the components
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(minimumSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(minimumValueLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(maximumSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(maximumValueLabel))
                            .addComponent(maximumNameLabel)
                            .addComponent(minimumNameLabel)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(250, 250, 250)
                        .addComponent(approveButton)))
                .addContainerGap(140, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(minimumValueLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(minimumNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(minimumSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(maximumNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(maximumSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(maximumValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(approveButton)
                .addGap(21, 21, 21))
        );

        // Set location (almost) to the center of the screen
        setLocation(new Point(
               Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2).intValue() - 300,
               Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2).intValue() - 120));
        setPreferredSize(new Dimension(560, 300));
        setResizable(false);
        setAlwaysOnTop(true);
        pack();
        setVisible(true);
    }

    private void setNewMinimumSizeFilter(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        newFilterMinSize = Double.valueOf((double) slider.getValue() / 1000);
        minimumValueLabel.setText(String.valueOf(newFilterMinSize));
    }

    private void setNewMaximumSizeFilter(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        newFilterMaxSize = Double.valueOf((double) slider.getValue() / CONSTANT);
        maximumValueLabel.setText(String.valueOf(newFilterMaxSize));
    }

    private void approveButtonActionPerformed() {
        descriptors.setScaleFilter(newFilterMinSize, newFilterMaxSize);
        this.dispose();
    }
}