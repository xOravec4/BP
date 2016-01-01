package cz.muni.fi.xlabuda;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import messif.objects.impl.ObjectFeatureByteL2;
import messif.objects.impl.ObjectFeatureSet;
import messif.objects.impl.ObjectFeatureSetSumOfSimilar;
import messif.objects.keys.AbstractObjectKey;
import messif.objects.util.StreamGenericAbstractObjectIterator;

/**
 * File chooser for the datasets of the descriptors.
 *
 * @author Marian Labuda
 * @version 1.0
 */
public class DescriptorsDatasetFileChooser extends JDialog {
    
    //************************* Attributes ********************//
    private ResourceBundle localLanguage;

    private DatasetSwingWorker worker;
    private boolean cancelInvoked = false;

    private File descriptorsFile;
    private LocalDescriptors descriptors;

    private JLabel numberOfReadSetLabel;

    private StatusPanel statusPanel;
    
    private JFileChooser descriptorsChooser;
    private ImagePanel panel;
    private int paneNumber;

    public DescriptorsDatasetFileChooser(ImagePanel panel, int paneNumber) {
        
        
        
        super(panel.getParentImageScrollPane().getParentMainFrame(), null, ModalityType.DOCUMENT_MODAL);
        panel.getParentImageScrollPane().getParentMainFrame().setComparationMode(MainFrame.VisualisationType.NONE);
        this.panel = panel;
        this.paneNumber = paneNumber;

        localLanguage = ResourceBundle.getBundle("DescriptorsChooser", MainFrame.locale);

        setTitle(localLanguage.getString("title"));

        descriptorsChooser = new JFileChooser();

        numberOfReadSetLabel = new JLabel();
        numberOfReadSetLabel.setText("   " + localLanguage.getString("read_set_label") + "0");

        String currentDirectory = Utils.getDirectoryProperty("directory");
        if (currentDirectory != null) {
            descriptorsChooser.setCurrentDirectory(new File(currentDirectory));
        } else {
            descriptorsChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }

        descriptorsChooser.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event) {
                descriptorsChooserActionPerformed(event);
            }
        });

        add(descriptorsChooser);

        setLocation(new Point(
               Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2).intValue() - 200,
               Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2).intValue() - 150));

        setResizable(false);
        pack();
        setVisible(true);
    }

    private void descriptorsChooserActionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            descriptorsFile = descriptorsChooser.getSelectedFile();

            descriptors = null;

            worker = new DatasetSwingWorker();
            worker.execute();
        }
        if (event.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
            this.dispose();
        }
    }

    public boolean swingWorkerRunning() {
        return !worker.isDone();
    }

    public void cancelWorkerAndExitWindow() {
        worker.cancel(false);
        dispose();
    }

    
    private class DatasetSwingWorker extends SwingWorker<ObjectFeatureSet, String> {

        @Override
        protected ObjectFeatureSet doInBackground() {
            ObjectFeatureSet retSet = null;
            String dlgTitle = null;
            String dlgMsg = null;
            
            try {
                MainFrame frame = panel.getParentImageScrollPane().getParentMainFrame();
                descriptors = frame.getSelectedDescriptorsType().cast(frame.
                        getSelectedDescriptorsType().newInstance());
                StreamGenericAbstractObjectIterator<ObjectFeatureSet> iterator 
                     = new StreamGenericAbstractObjectIterator<ObjectFeatureSet>(ObjectFeatureSetSumOfSimilar.class, descriptorsFile.getAbsolutePath());
                int numberOfSetRead = 0;
                if (iterator.hasNext()) {
                    statusPanel = new StatusPanel(DescriptorsDatasetFileChooser.this);
                    DescriptorsDatasetFileChooser.this.setModal(false);
                    Utils.setDirectoryProperty(descriptorsFile.getParentFile(), "directory");
                    while (iterator.hasNext()) {
                        if (isCancelled()) {
                            cancelInvoked = true;
                            break;
                        }
                        numberOfSetRead++;
                        String locatorURI = iterator.next().getLocatorURI();
                        String descriptorsName = locatorURI.substring(0, locatorURI.lastIndexOf("."));
                        descriptorsName = descriptorsName.substring(descriptorsName.lastIndexOf("/") + 1);
                        String descriptorsExtension = locatorURI.substring(
                                locatorURI.lastIndexOf(".") + 1);
                        String requiredName = panel.getImageFileName().substring(0,
                                panel.getImageFileName().lastIndexOf("."));
                        requiredName = requiredName.substring(requiredName.lastIndexOf("/") + 1);
                        String requiredExtension = panel.getImageFileName().substring(
                                panel.getImageFileName().lastIndexOf(".") + 1);
                        publish(String.valueOf(numberOfSetRead));
                        if (requiredName.equals(descriptorsName) && 
                                (((descriptorsExtension.equals("jpg") || descriptorsExtension.equals("jpeg")) &&
                                (requiredExtension.equals("jpg") || requiredExtension.equals("jpeg"))) ||
                                descriptorsExtension.equals(requiredExtension))) {
                            ObjectFeatureSet descriptorSet = iterator.getCurrentObject();
                            if (descriptorSet.getSize() != 0) {
                                retSet = descriptorSet;
                                break;
                            }
                        }
                    }
                } else {
                    dlgTitle = localLanguage.getString("jop_title_corrupted_file");
                    dlgMsg   = localLanguage.getString("jop_msg_corrupted_file");
                }
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } catch (IllegalStateException ex) {
                if (statusPanel != null) {
                    statusPanel.dispose();
                    DescriptorsDatasetFileChooser.this.setModal(true);
                }
                ex.printStackTrace();

                dlgTitle = localLanguage.getString("jop_title_corrupted_file");
                dlgMsg   = localLanguage.getString("jop_msg_corrupted_file");
            } catch (IOException ioe) {
                if (statusPanel != null) {
                    statusPanel.dispose();
                    DescriptorsDatasetFileChooser.this.setModal(true);
                }

                ioe.printStackTrace();

                dlgTitle = localLanguage.getString("jop_title_cannot_read");
                dlgMsg   = localLanguage.getString("jop_msg_cannot_read");
            }
            
            // Try to read the descriptors from non-MESSIF format!
            if (retSet == null) {
                retSet = readNonMessifFormat(descriptorsFile);
                if (retSet != null) {   // Ok, so do not show any error message
                    dlgTitle = null;
                    dlgMsg = null;
                }
            }

            if (!cancelInvoked && retSet == null) {
                if (statusPanel != null) {
                    statusPanel.dispose();
                    DescriptorsDatasetFileChooser.this.setModal(true);
                }
                dlgTitle = localLanguage.getString("jop_title_not_exist");
                dlgMsg   = localLanguage.getString("jop_msg_not_exist");
            }

            if (dlgTitle != null && dlgMsg != null) {
                JOptionPane.showMessageDialog(DescriptorsDatasetFileChooser.this, dlgMsg, dlgTitle,JOptionPane.ERROR_MESSAGE);
            }
            
            return retSet;
        }

        @Override
        protected void process(List<String> labelValues) {
            Iterator<String> iterator = labelValues.iterator();
            while (iterator.hasNext()) {
                numberOfReadSetLabel.setText(numberOfReadSetLabel.getText().substring(
                        0,  numberOfReadSetLabel.getText().lastIndexOf(":") + 2) + iterator.next());
            }
        }

        @Override
        protected void done() {
            if (!isCancelled()) {
                try {
                    if (get() != null) {
                        if (statusPanel != null) {
                            statusPanel.dispose();
                        }
                        descriptors.setParentImagePanel(panel);
                        descriptors.setDescriptors(get());
                        panel.setDescriptors(descriptors);
                        panel.getParentImageScrollPane().setDescriptorsLoaded(true);
                        panel.getParentImageScrollPane().setDescriptorsLabels();
                        MainFrame mainFrame = panel.getParentImageScrollPane().getParentMainFrame();
                        mainFrame.setStateOfDescriptorsMenu(true, paneNumber);
                        mainFrame.setStateOfProjectionMenu(true, paneNumber);
                        boolean sameTypeOfDescriptors = true;
                        if (paneNumber == MainFrame.FIRST_IMAGE_PANEL) {
                            sameTypeOfDescriptors = mainFrame.setFirstDescriptorsLoaded(true);
                        }
                        if (paneNumber == MainFrame.SECOND_IMAGE_PANEL) {
                            sameTypeOfDescriptors = mainFrame.setSecondDescriptorsLoaded(true);
                        }
                        dispose();
                        if (descriptors.getLocationType() == LocalDescriptors.ABSOLUTE_LOCATION) {
                            Dialogs.absoluteLocationDialog(null, descriptors);
                        }
                        if (!sameTypeOfDescriptors) {
                            mainFrame.showEncapsulatingDialogWarning();
                        }
                    }
                } catch (InterruptedException ex) {
                } catch (ExecutionException ex) {}
            }
        }

        private ObjectFeatureSet readNonMessifFormat(File f) {
            BufferedReader r = null;
            try {
                ObjectFeatureSet set = new ObjectFeatureSetSumOfSimilar();
                set.setObjectKey(new AbstractObjectKey(f.getName()));
                r = new BufferedReader(new FileReader(f));
                String line;
                while ((line = r.readLine()) != null) {
                    String[] p = line.split(" ");
                    short[] data = new short[p.length-4];
                    for (int i = 0; i < data.length; i++) {
                        data[i] = Short.parseShort(p[i+4]);
//                        data[i] = (short) (Short.parseShort(p[i+4])-127);
                    }
                    ObjectFeatureByteL2 o = new ObjectFeatureByteL2(Float.parseFloat(p[0]), Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3]), data);
                    set.addObject(o);
                }
                if (set.getObjectCount() == 0)
                    return null;
                else
                    return set;
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DescriptorsDatasetFileChooser.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            } catch (IOException ex) {
                Logger.getLogger(DescriptorsDatasetFileChooser.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            } finally {
                try {
                    r.close();
                } catch (IOException ex) {
                    Logger.getLogger(DescriptorsDatasetFileChooser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private class StatusPanel extends JDialog {

        public StatusPanel(Dialog parent) {
            super(parent);
            JButton cancelButton = new JButton();
            
            cancelButton.setText(localLanguage.getString("cancel_button"));
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancelActionPerformed();
                }
            });

            setLayout(new BorderLayout());

            add(numberOfReadSetLabel, BorderLayout.CENTER);
            add(cancelButton, BorderLayout.SOUTH);
            this.addWindowListener(new WindowListener() {

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
            setTitle(localLanguage.getString("frame_title"));

            setLocation(new Point(
               Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2).intValue() - 120,
               Double.valueOf(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2).intValue() - 60));

            setAlwaysOnTop(true);
            setResizable(false);
            setVisible(true);
        }

        private void cancelActionPerformed() {
            worker.cancel(false);
            dispose();
        }
    }
}