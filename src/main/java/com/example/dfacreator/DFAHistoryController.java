package com.example.dfacreator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.File;

public class DFAHistoryController {

    @FXML
    private ListView<String> dfaFilesListView;

    @FXML
    private Button sortButton;

    @FXML
    public void initialize() {
        loadDFAFiles();

        sortButton.setOnAction(event -> {
            dfaFilesListView.getItems().sort(new DFAFileNameComparator());
        });
    }

    private void loadDFAFiles() {
        dfaFilesListView.getItems().clear();

        File folder = new File(System.getProperty("user.dir"), "DFASaves");

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("DFASaves folder does not exist.");
            return;
        }

        File[] dfaFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (dfaFiles != null) {
            for (File f : dfaFiles) {
                dfaFilesListView.getItems().add(f.getName());
            }
        }
    }
}
