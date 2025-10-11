package com.example.dfacreator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DFAHistoryController {

    @FXML
    private ListView<String> dfaFilesListView;

    @FXML
    private Button sortButton;

    private List<File> dfaFiles = new ArrayList<>(); // Store actual File objects

    @FXML
    public void initialize() {
        loadDFAFiles();

        sortButton.setOnAction(event -> {
            sortFilesByLastModified();
        });
    }

    private void loadDFAFiles() {
        dfaFilesListView.getItems().clear();
        dfaFiles.clear();

        File folder = new File(System.getProperty("user.dir"), "DFASaves");

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("DFASaves folder does not exist.");
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (files != null) {
            for (File f : files) {
                dfaFiles.add(f);
                dfaFilesListView.getItems().add(formatFileDisplay(f));
            }
        }
    }

    private void sortFilesByLastModified() {
        // Sort the file objects by last modified date
        dfaFiles.sort(new DFAFileModifiedComparator());

        // Update the ListView display
        dfaFilesListView.getItems().clear();
        for (File f : dfaFiles) {
            dfaFilesListView.getItems().add(formatFileDisplay(f));
        }
    }

    private String formatFileDisplay(File file) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String lastModified = dateFormat.format(new Date(file.lastModified()));
        return file.getName() + " (Modified: " + lastModified + ")";
    }
}