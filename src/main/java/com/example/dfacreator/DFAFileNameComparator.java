package com.example.dfacreator;

import java.util.Comparator;

public class DFAFileNameComparator implements Comparator<String> {

    @Override
    public int compare(String f1, String f2) {
        return extractNumber(f1) - extractNumber(f2);
    }

    private int extractNumber(String fileName) {
        try {
            int start = fileName.indexOf("DFA") + 3;
            int end = fileName.lastIndexOf(".txt");
            if (end == -1) end = fileName.length();
            String numberStr = fileName.substring(start, end);
            return Integer.parseInt(numberStr);
        } catch (Exception e) {
            return Integer.MAX_VALUE; // malformed names go last
        }
    }
}
