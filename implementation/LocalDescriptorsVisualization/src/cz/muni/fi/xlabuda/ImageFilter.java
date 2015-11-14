package cz.muni.fi.xlabuda;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * File filter - allowed file formats are gif, png, jpg, jpeg and bmp.
 * @author Marian Labuda
 * @version 1.0
 */
public class ImageFilter extends FileFilter {

    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        
        String fileExtension = null;
        String fileName = file.getName();
        int separator = fileName.lastIndexOf(".");

        if (separator > 0 && separator < fileName.length() - 1) {
            fileExtension = fileName.substring(separator+1).toLowerCase();
        } else {
            return false;
        }

        if (fileExtension.equals("png") || fileExtension.equals("gif") ||
            fileExtension.equals("bmp") || fileExtension.equals("jpg") ||
            fileExtension.equals("jpeg")) {
            return true;
        } else {
            return false;
        }
    }

    public String getDescription() {
        return "Image Files [bmp, gif, jpg, jpeg, png]";
    }
}