package com.example.dfacreator;

import java.io.File;
import java.util.Comparator;

public class DFAFileModifiedComparator implements Comparator<File> {

    @Override
    public int compare(File f1, File f2) {

        return Long.compare(f2.lastModified(), f1.lastModified());
    }
}