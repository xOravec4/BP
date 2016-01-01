package cz.muni.fi.xlabuda;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * Image file chooser - choose image for visualization of descriptors.
 *
 * @author Marian Labuda
 * @version 1.0
 */
public class ImageFileChooser extends JDialog{

    //************************* Attributes ********************//

    private ResourceBundle localLanguage;
    private JFileChooser imageFileChooser;

    /** Required component to provide image visualization */
    private ImagePanel imagePanel;
    private int paneNumber;

    public ImageFileChooser(ImagePanel imagePanel, int imagePaneNumber) {
        super(imagePanel.getParentImageScrollPane().getParentMainFrame(), null,
                JDialog.DEFAULT_MODALITY_TYPE);
        this.imagePanel = imagePanel;
        paneNumber = imagePaneNumber;

        PreviewPanel preview = new PreviewPanel();

        localLanguage = ResourceBundle.getBundle("ImageChooser", MainFrame.locale);
        setTitle(localLanguage.getString("title"));

        imageFileChooser = new JFileChooser();
        imageFileChooser.addChoosableFileFilter(new ImageFilter());
        imageFileChooser.setAcceptAllFileFilterUsed(false);
        imageFileChooser.setAccessory(preview);
        imageFileChooser.addPropertyChangeListener(preview);

        String currentDirectory = Utils.getDirectoryProperty("directory");
        if (currentDirectory != null) {
            imageFileChooser.setCurrentDirectory(new File(currentDirectory));
        } else {
            imageFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }

        add(imageFileChooser);

        imageFileChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                imageFileActionPerformed(event);
            }
        });

        //**** Set location (almost) to the center of the screen ****//
        setLocation(new Point(
               Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2).intValue() - 200,
               Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2).intValue() - 150));

        setResizable(false);
        pack();
        setVisible(true);
    }

    private void imageFileActionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            try {
                BufferedImage image = ImageIO.read(imageFileChooser.getSelectedFile());
                if (image == null) {
                    JOptionPane.showMessageDialog(this, localLanguage.getString("jop_err_msg"),
                        localLanguage.getString("jop_err_title"), JOptionPane.ERROR_MESSAGE);
                } else {
                    
                    // Clear flag of loaded image
                    if (imagePanel.getImage() != null) {
                        if(imagePanel.getParentImageScrollPane().getParentMainFrame().getVisualizationType() != MainFrame.VisualisationType.NONE){
                            imagePanel.getParentImageScrollPane().getParentMainFrame().setComparationMode(MainFrame.VisualisationType.NONE);
                            imagePanel.getParentImageScrollPane().getParentMainFrame().resetProjectionsBoth();
                        }
                        ImageScrollPane scrollPane = (ImageScrollPane) 
                                imagePanel.getParentImageScrollPane();
                        MainFrame mainFrame = scrollPane.getParentMainFrame();
                        mainFrame.setComparationMode(MainFrame.VisualisationType.NONE);
                        if (paneNumber == MainFrame.FIRST_IMAGE_PANEL) {
                            mainFrame.setFirstDescriptorsLoaded(false);
                        }
                        if (paneNumber == MainFrame.SECOND_IMAGE_PANEL) {
                            mainFrame.setSecondDescriptorsLoaded(false);
                        }
                    }
                    // Prepare image panel for the new image
                    imagePanel.zoom(1);
                    imagePanel.setNewAffineTransform();
                    imagePanel.setBufferedImage(image);
                    imagePanel.setImageFileName(imageFileChooser.getSelectedFile().getName());
                    imagePanel.getParentImageScrollPane().setImageLoaded(true);
                    imagePanel.getParentImageScrollPane().setDescriptorsLoaded(false);
                    Utils.setDirectoryProperty(imageFileChooser.getSelectedFile().getParentFile(),
                            "directory");
                    MainFrame mainFrame = (MainFrame) imagePanel.getParentImageScrollPane().getParentMainFrame();
                    mainFrame.setStateOfImageMenu(true, paneNumber);
                    mainFrame.setStateOfDescriptorsMenu(false, paneNumber);
                    mainFrame.setStateOfProjectionMenu(false, paneNumber);

                    dispose();
                    if (!AutomaticDescriptorsSelector.loadDescriptors(imagePanel, paneNumber)) {
                        DescriptorsDatasetFileChooser descriptorsChooser =
                            new DescriptorsDatasetFileChooser(imagePanel, paneNumber);
                    } 
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, localLanguage.getString("jop_err_msg"),
                        localLanguage.getString("jop_err_title"), JOptionPane.ERROR_MESSAGE);
            }
        }
        if (event.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
            this.dispose();
        }
    }

    private class PreviewPanel extends JPanel implements PropertyChangeListener {

        private static final int SIZE = 155;

        private int width, height;
        private ImageIcon icon;
        private Image image;
        private Color backgroundColor;

        public PreviewPanel() {
            // scaling image will set correct dimension
            setPreferredSize(new Dimension(SIZE, -1));
            backgroundColor = getBackground();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();

            if (propertyName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                File selectedFile = (File) evt.getNewValue();
                String fileName;

                if (selectedFile == null) {
                    return;
                } else {
                    fileName = selectedFile.getAbsolutePath();
                }

                if (fileName != null) {
                    if (fileName.toLowerCase().endsWith(".jpg") ||
                        fileName.toLowerCase().endsWith(".jpeg") ||
                        fileName.toLowerCase().endsWith(".png") ||
                        fileName.toLowerCase().endsWith(".gif")) {

                        icon = new ImageIcon(fileName);
                        image = icon.getImage();
                        scaleImage();
                        repaint();
                    }
                }
            }
        }

        private void scaleImage() {
            width = image.getWidth(this);
            height = image.getHeight(this);

            double scale = 1.0;

            if (width > height) {
                scale = (double) (SIZE - 5) / width;
                width = SIZE - 5;
                height = (int) (height * scale);
            } else {
                if (getHeight() > 150) {
                    scale = (double) (SIZE-5) / height;
                    height = SIZE - 5;
                    width = (int)(width * scale);
                } else {
                    scale = (double) getHeight() / height;
                    height = getHeight();
                    width = (int)(width * scale);
                }
            }

            image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        }

        @Override
        public void paintComponent(Graphics g) {
            // fill background
            g.setColor(backgroundColor);
            g.fillRect(0, 0, SIZE, getHeight());

            // draw image into center
            g.drawImage(image, getWidth() / 2 - width / 2 + 5,
                getHeight() / 2 - height / 2, this);
        }
    }
}