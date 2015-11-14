package cz.muni.fi.xlabuda;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import messif.objects.impl.ObjectFeatureSet;
import messif.objects.impl.ObjectFeatureSetSumOfSimilar;
import messif.objects.util.StreamGenericAbstractObjectIterator;

/**
 * File chooser for the descriptors
 *
 * @author Marian Labuda
 * @version 1.0
 */
public class AutomaticDescriptorsSelector {

    public static boolean loadDescriptors(ImagePanel panel, int paneNumber) {

        File currentDirectory = null;
        String currentDirectoryString = Utils.getDirectoryProperty("directory");
        String requiredFileName =
                panel.getImageFileName().substring(0, panel.getImageFileName().lastIndexOf("."));
        String requiredExtension = panel.getImageFileName().substring(
                                    panel.getImageFileName().lastIndexOf(".") + 1);

        if (currentDirectoryString != null) {
            currentDirectory = new File(currentDirectoryString);
        } else {
            currentDirectory = new File(System.getProperty("user.dir"));
        }

        StreamGenericAbstractObjectIterator<ObjectFeatureSet> iterator;
        LocalDescriptors descriptors = null;

        for (File file: currentDirectory.listFiles()) {
            if (file.isFile()) {
                String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
                if (requiredFileName.equals(fileName)) {
                    try {
                        iterator = new StreamGenericAbstractObjectIterator<ObjectFeatureSet>(
                                ObjectFeatureSetSumOfSimilar.class, file.getAbsolutePath());
                        ObjectFeatureSet setOfDescriptors = (ObjectFeatureSet) iterator.next();
                        String objectExtension = setOfDescriptors.getLocatorURI().substring(
                                    setOfDescriptors.getLocatorURI().lastIndexOf(".") + 1);
                        String objectName = setOfDescriptors.getLocatorURI().substring(
                                0, setOfDescriptors.getLocatorURI().lastIndexOf("."));
                        if ((objectName.equals(requiredFileName)) && ((requiredExtension.equals(
                                objectExtension)) || ((requiredExtension.equals("jpg") || 
                                requiredExtension.equals("jpeg")) && (objectExtension.equals("jpg") ||
                                objectExtension.equals("jpeg"))))) {

                                MainFrame mainFrame = (MainFrame) panel.getParentImageScrollPane().
                                        getParentMainFrame();

                                descriptors = mainFrame.getSelectedDescriptorsType().cast(
                                        mainFrame.getSelectedDescriptorsType().newInstance());

                                descriptors.setParentImagePanel(panel);
                                panel.setDescriptors(descriptors);
                                panel.getParentImageScrollPane().setDescriptorsLoaded(true);
                                descriptors.setDescriptors(setOfDescriptors);
                                panel.getParentImageScrollPane().setDescriptorsLabels();

                                Utils.setDirectoryProperty(file.getParentFile(), "directory");

                                mainFrame.setStateOfDescriptorsMenu(true, paneNumber);
                                boolean sameTypeOfDescriptors = true;
                                if (paneNumber == MainFrame.FIRST_IMAGE_PANEL) {
                                    sameTypeOfDescriptors = mainFrame.setFirstDescriptorsLoaded(true);
                                }
                                if (paneNumber == MainFrame.SECOND_IMAGE_PANEL) {
                                    sameTypeOfDescriptors = mainFrame.setSecondDescriptorsLoaded(true);
                                }
                                if (descriptors.getLocationType() == LocalDescriptors.ABSOLUTE_LOCATION) {
                                    Dialogs.absoluteLocationDialog(mainFrame, descriptors);
                                }
                                if (!sameTypeOfDescriptors) {
                                        mainFrame.showEncapsulatingDialogWarning();
                                }
                                return true;
                        }
                    } catch (IllegalArgumentException ex) {
                    } catch (IOException ex) {
                    } catch (IllegalStateException ex) {
                    } catch (NoSuchElementException ex) {
                    } catch (ClassCastException ex) {
                    } catch (InstantiationException ex) {
                    } catch (IllegalAccessException ex) {
                    }
                }
            }
        }
        return false;
    }
}