package com.example.dfacreator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import java.io.File;
import java.io.PrintWriter;
import javafx.stage.FileChooser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DFAController {

    @FXML
    private HBox transitionBox;
    @FXML
    private Label welcomeText;

    @FXML
    private TextField stateNumberField, startingStateField, alphabetField;

    @FXML
    private Pane circlePane;

    @FXML
    private Button addAcceptingButton, stateCreatorButton;

    @FXML
    private ComboBox acceptingStatesDropdown;

    @FXML
    private VBox transitionSection;

    // New FXML elements for string testing
    @FXML
    private TextField testStringField;

    @FXML
    private Button testStringButton;

    @FXML
    private Label testResultLabel;

    private final Map<String, Double> stateXPositions = new HashMap<>();
    private final Map<String, Double> stateYPositions = new HashMap<>();

    DFACreator dfa = new DFACreator();
    private Map<String, ComboBox<String>> transitionInputs = new HashMap<>();

    public Polygon createTriangle(double tipX, double tipY, double size, Color color) {
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(
                tipX, tipY,                         // tip of the triangle (pointing direction)
                tipX - size, tipY - size / 2,       // top-left base corner
                tipX - size, tipY + size / 2        // bottom-left base corner
        );
        triangle.setFill(color);
        return triangle;
    }

    // Create an arrow pointing from (startX, startY) to (endX, endY)
    public Polygon createDirectionalTriangle(double startX, double startY, double endX, double endY, double size, Color color) {
        // Calculate direction vector
        double dx = endX - startX;
        double dy = endY - startY;
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length == 0) return createTriangle(endX, endY, size, color);

        // Normalize direction vector
        dx /= length;
        dy /= length;

        // Create triangle pointing in the direction of the line
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(
                endX, endY,                                    // tip of the triangle
                endX - size * dx - size * dy / 2, endY - size * dy + size * dx / 2,  // one base corner
                endX - size * dx + size * dy / 2, endY - size * dy - size * dx / 2   // other base corner
        );
        triangle.setFill(color);
        return triangle;
    }

    @FXML
    public void initialize() {

        stateNumberField.setOnAction(event -> {
            String input = stateNumberField.getText().trim();
            if (!input.matches("\\d+")) {
                System.out.println("Invalid number.");
                return;
            }

            int count = Integer.parseInt(input);
            acceptingStatesDropdown.getItems().clear();

            for (int i = 0; i < count; i++) {
                acceptingStatesDropdown.getItems().add("Q" + i);
            }

            System.out.println("Dropdown populated with " + count + " states.");
        });

    }

    @FXML
    protected void onStateCreation() {
        String input = stateNumberField.getText();
        int stateNumber = Integer.parseInt(input);
        String startingState = startingStateField.getText();
        dfa.setStartState(startingState);
        circlePane.getChildren().clear();

        stateXPositions.clear();
        stateYPositions.clear();

        double centerX = 300;
        double centerY = 250;
        double radius = 150;

        for (int i = 0; i < stateNumber; i++) {
            double angle = 2 * Math.PI * i / stateNumber;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            Circle circle = new Circle(x, y, 20);
            circle.setStyle("-fx-fill: lightblue; -fx-stroke: black; -fx-stroke-width: 2;");
            Label label = new Label("Q" + i);
            label.setLayoutX(x - 10);
            label.setLayoutY(y - 8);
            circlePane.getChildren().addAll(circle, label);
            dfa.addState(label.getText());

            // Store positions for later use, (note they are also used right now for the creation of the accepting circles)
            stateXPositions.put("Q" + i, x);
            stateYPositions.put("Q" + i, y);
        }

        Set<String> acceptingStates = dfa.getAcceptingStates();
        ArrayList<String> acceptingArrayList = new ArrayList<>();
        acceptingArrayList.addAll(acceptingStates);
        for (int j = 0; j < dfa.getAcceptingStates().size(); j++) {
            String stateName = acceptingArrayList.get(j);
            double x = stateXPositions.get(stateName);
            double y = stateYPositions.get(stateName);
            Circle circle = new Circle(x, y, 25);
            circle.setStyle("-fx-fill: transparent; -fx-stroke: black; -fx-stroke-width: 2;");
            circlePane.getChildren().addAll(circle);
        }

        // Draw starting state arrow
        if (startingState != null && stateXPositions.containsKey(startingState)) {
            double stateX = stateXPositions.get(startingState);
            double stateY = stateYPositions.get(startingState);

            // Calculate arrow start position (50 pixels away from state center)
            double arrowStartX = stateX - 50;
            double arrowStartY = stateY;
            double arrowEndX = stateX - 20;
            double arrowEndY = stateY;

            Line startingArrowLine = new Line(arrowStartX, arrowStartY, arrowEndX, arrowEndY);
            Polygon startArrow = createDirectionalTriangle(arrowStartX, arrowStartY, arrowEndX, arrowEndY, 10, Color.BLACK);
            circlePane.getChildren().addAll(startingArrowLine, startArrow);
        }
    }

    @FXML
    protected void onAddAccepting() {
        String input = stateNumberField.getText();
        int stateNumber = Integer.parseInt(input);
        String selected = (String) acceptingStatesDropdown.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dfa.addAcceptingState(selected);
            System.out.println("Added accepting state to DFA: " + selected);
        }
    }

    @FXML
    private void onAddAlphabet() {
        String input = alphabetField.getText().trim();
        String[] parts = input.split(",");

        ArrayList<String> alphabet = new ArrayList<>();

        for (String part : parts) {
            String symbol = part.trim();
            if (!symbol.isEmpty()) {
                alphabet.add(symbol);
                dfa.setAlphabet(symbol);
            }
        }
        buildTransitionInputs();
        System.out.println("Parsed alphabet: " + alphabet);
    }

    private void buildTransitionInputs() {
        transitionBox.getChildren().clear();
        transitionInputs.clear();

        for (String symbol : dfa.getAlphabet()) {
            VBox symbolColumn = new VBox(10);
            symbolColumn.setStyle("-fx-padding: 10; -fx-border-color: gray; -fx-border-width: 1;");

            Label symbolHeader = new Label("Input '" + symbol + "'");
            symbolHeader.setStyle("-fx-font-weight: bold;");
            symbolColumn.getChildren().add(symbolHeader);

            for (String state : dfa.getStates()) {
                HBox row = new HBox(5);
                Label stateLabel = new Label("On " + state + " →");
                ComboBox<String> toStateDropdown = new ComboBox<>();
                toStateDropdown.getItems().addAll(dfa.getStates());
                toStateDropdown.setPrefWidth(100);

                row.getChildren().addAll(stateLabel, toStateDropdown);
                symbolColumn.getChildren().add(row);

                String key = state + "," + symbol;
                transitionInputs.put(key, toStateDropdown);
            }

            transitionBox.getChildren().add(symbolColumn);
        }
        Button addTransitionButton = new Button("Add Transitions");
        addTransitionButton.setOnAction(e -> onAddTransitions());
        transitionSection.getChildren().add(addTransitionButton);
    }

    private void onAddTransitions() {
        System.out.println("Drawing transitions...");

        for (Map.Entry<String, ComboBox<String>> entry : transitionInputs.entrySet()) {
            String key = entry.getKey();
            ComboBox<String> dropdown = entry.getValue();
            String targetState = dropdown.getValue();

            if (targetState == null || targetState.isEmpty()) continue;

            String[] parts = key.split(",");
            String fromState = parts[0];
            String symbol = parts[1];

            // Adding the transitions to the u DFACreator class so I can use DFASimulation to test the DFA
            dfa.addTransition(fromState, symbol, targetState);

            if (!stateXPositions.containsKey(fromState) || !stateXPositions.containsKey(targetState))
                continue;

            double startX = stateXPositions.get(fromState);
            double startY = stateYPositions.get(fromState);
            double endX = stateXPositions.get(targetState);
            double endY = stateYPositions.get(targetState);

            if (fromState.equals(targetState)) {
                // Draw self-loop
                double loopCenterX = startX;
                double loopCenterY = startY - 40;
                Circle loop = new Circle(loopCenterX, loopCenterY, 15);
                loop.setStroke(Color.BLACK);
                loop.setFill(Color.TRANSPARENT);
                loop.setStrokeWidth(2);

                // Add small arrow to show direction
                Polygon loopArrow = createTriangle(loopCenterX + 15, loopCenterY, 6, Color.BLACK);

                Label label = new Label(symbol);
                label.setLayoutX(loopCenterX - 5);
                label.setLayoutY(loopCenterY - 25);
                label.setStyle("-fx-font-weight: bold; -fx-background-color: white; -fx-padding: 2;");

                circlePane.getChildren().addAll(loop, loopArrow, label);
            } else {
                // Calculate line endpoints on circle edges (not centers)
                double dx = endX - startX;
                double dy = endY - startY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Normalize direction vector
                double unitX = dx / distance;
                double unitY = dy / distance;

                // Adjust start and end points to circle edges (radius = 20)
                double lineStartX = startX + 20 * unitX;
                double lineStartY = startY + 20 * unitY;
                double lineEndX = endX - 20 * unitX;
                double lineEndY = endY - 20 * unitY;

                // Draw line
                Line line = new Line(lineStartX, lineStartY, lineEndX, lineEndY);
                line.setStrokeWidth(2);

                // Create directional arrow
                Polygon arrow = createDirectionalTriangle(lineStartX, lineStartY, lineEndX, lineEndY, 8, Color.BLACK);

                // Position label at midpoint of the line
                double labelX = (lineStartX + lineEndX) / 2;
                double labelY = (lineStartY + lineEndY) / 2;

                Label label = new Label(symbol);
                label.setLayoutX(labelX - 10);
                label.setLayoutY(labelY - 15);
                label.setStyle("-fx-font-weight: bold; -fx-background-color: white; -fx-padding: 2; -fx-border-color: lightgray; -fx-border-width: 1;");

                circlePane.getChildren().addAll(line, arrow, label);
            }
        }
    }


    @FXML
    protected void onTestString() {
        String testString = testStringField.getText().trim();

        // Check if DFA is properly set up
        if (dfa.getStartState() == null || dfa.getStartState().isEmpty()) {
            testResultLabel.setText("Error: No start state defined!");
            testResultLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            return;
        }

        if (dfa.getAlphabet().isEmpty()) {
            testResultLabel.setText("Error: No alphabet defined!");
            testResultLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            return;
        }

        if (dfa.getTransitions().isEmpty()) {
            testResultLabel.setText("Error: No transitions defined!");
            testResultLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            return;
        }

        // Create DFA simulation and actually USE it to test the string
        DFASimulation simulation = new DFASimulation(dfa);
        String result = simulation.testString(testString);

        // Display result in the label
        if (result.startsWith("Accepted")) {
            testResultLabel.setText("✓ " + result);
            testResultLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            testResultLabel.setText("✗ " + result);
            testResultLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
    }

    @FXML
    protected void onClearCanvas() {
        circlePane.getChildren().clear();

        stateXPositions.clear();
        stateYPositions.clear();

        transitionInputs.clear();

        dfa = new DFACreator();

        stateNumberField.clear();
        startingStateField.clear();
        alphabetField.clear();
        testStringField.clear();
        testResultLabel.setText("");

        System.out.println("Canvas and DFA cleared successfully.");
    }
    private Stage stage;
    private Scene scene;
    private Parent root;


    @FXML
    protected void onDFAHistory(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("DFA-history.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    protected void onExportDFA() {
        // Get the current working directory
        String currentDir = System.getProperty("user.dir");
        File folder = new File(currentDir, "DFASaves");

        // Create the folder if it doesn't exist
        if (!folder.exists()) {
            boolean created = folder.mkdir();
            if (!created) { 
                System.out.println("Failed to create DFASaves directory.");
                return;
            }
        }

        // Find the next available filename DFA1.txt, DFA2.txt, ...
        int fileIndex = 1;
        File file;
        do {
            file = new File(folder, "DFA" + fileIndex + ".txt");
            fileIndex++;
        } while (file.exists());

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("States:");
            for (String state : dfa.getStates()) {
                writer.println("  " + state);
            }

            writer.println("\nAlphabet:");
            for (String symbol : dfa.getAlphabet()) {
                writer.println("  " + symbol);
            }

            writer.println("\nStart State:");
            writer.println("  " + dfa.getStartState());

            writer.println("\nAccepting States:");
            for (String accepting : dfa.getAcceptingStates()) {
                writer.println("  " + accepting);
            }

            writer.println("\nTransitions:");
            for (Map.Entry<String, Map<String, String>> fromEntry : dfa.getTransitions().entrySet()) {
                String fromState = fromEntry.getKey();
                for (Map.Entry<String, String> trans : fromEntry.getValue().entrySet()) {
                    String symbol = trans.getKey();
                    String toState = trans.getValue();
                    writer.println("  " + fromState + " --" + symbol + "--> " + toState);
                }
            }

            System.out.println("DFA exported automatically to: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}