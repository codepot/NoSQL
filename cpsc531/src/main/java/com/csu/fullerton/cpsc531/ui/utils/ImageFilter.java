/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csu.fullerton.cpsc531.ui.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Peter
 */
public class ImageFilter extends FileFilter {

    @Override
    public boolean accept(File pathname) {
        String filename = pathname.getName();
        if (pathname.isDirectory()) {
            return true;

        } else if (filename.endsWith("jpg") || filename.endsWith("png") || filename.endsWith("gif")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getDescription() {

        return "Image files";

    }
}
