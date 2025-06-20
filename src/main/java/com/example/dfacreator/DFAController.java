package com.example.dfacreator;

import javafx.fxml.FXML;
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
import javafx.scene.shape.QuadCurve;

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

    private final Map<String, Double> stateXPositions = new HashMap<>();
    private final Map<String, Map<String, Integer>> transitionCounts = new HashMap<>();

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

    // Create arrow head for curved transitions
    public Polygon createArrowHead(double x, double y, double angle, double size, Color color) {
        Polygon arrow = new Polygon();

        // Calculate arrow points based on angle
        double arrowLength = size;
        double arrowWidth = size * 0.6;

        double x1 = x;
        double y1 = y;
        double x2 = x - arrowLength * Math.cos(angle - Math.PI/6);
        double y2 = y - arrowLength * Math.sin(angle - Math.PI/6);
        double x3 = x - arrowLength * Math.cos(angle + Math.PI/6);
        double y3 = y - arrowLength * Math.sin(angle + Math.PI/6);

        arrow.getPoints().addAll(x1, y1, x2, y2, x3, y3);
        arrow.setFill(color);
        return arrow;
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
        transitionCounts.clear();

        double spacing = 90;

        for (int i = 0; i < stateNumber; i++) {
            double x = spacing * i + 50; // Add offset to prevent edge clipping
            double y = 200;
            Circle circle = new Circle(x, y, 20);
            circle.setStyle("-fx-fill: lightblue; -fx-stroke: black; -fx-stroke-width: 2;");
            Label label = new Label("Q" + i);
            label.setLayoutX(x - 10);
            label.setLayoutY(y - 8);
            circlePane.getChildren().addAll(circle, label);
            dfa.addState(label.getText());
            stateXPositions.put("Q" + i, x);
        }

        // Drawing the outer circles for the accepting states
        Set<String> acceptingStates = dfa.getAcceptingStates();
        ArrayList<String> acceptingArrayList = new ArrayList<>();
        acceptingArrayList.addAll(acceptingStates);
        for (int j = 0; j < dfa.getAcceptingStates().size(); j++) {
            int statevalue = Integer.parseInt(acceptingArrayList.get(j).substring(1));
            double x = spacing * statevalue + 50;
            Circle circle = new Circle(x, 200, 25);
            circle.setStyle("-fx-fill: transparent; -fx-stroke: black; -fx-stroke-width: 2;");
            circlePane.getChildren().addAll(circle);
        }

        if (startingState != null) {
            int positionNumber = Integer.parseInt(startingState.substring(1));
            double x = spacing * positionNumber + 50;
            Line startingArrowLine = new Line(x - 50, 200, x - 20, 200);
            Polygon startArrow = createTriangle(x - 20, 200, 10, Color.BLACK);
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

        // Clear existing transition counts
        transitionCounts.clear();

        for (Map.Entry<String, ComboBox<String>> entry : transitionInputs.entrySet()) {
            String key = entry.getKey();
            ComboBox<String> dropdown = entry.getValue();
            String targetState = dropdown.getValue();

            if (targetState == null || targetState.isEmpty()) continue;

            String[] parts = key.split(",");
            String fromState = parts[0];
            String symbol = parts[1];

            if (!stateXPositions.containsKey(fromState) || !stateXPositions.containsKey(targetState))
                continue;

            // Track transitions between states to avoid overlap
            String transitionKey = fromState + "->" + targetState;
            transitionCounts.putIfAbsent(fromState, new HashMap<>());
            int currentCount = transitionCounts.get(fromState).getOrDefault(targetState, 0);
            transitionCounts.get(fromState).put(targetState, currentCount + 1);

            double startX = stateXPositions.get(fromState);
            double endX = stateXPositions.get(targetState);
            double y = 200;

            if (fromState.equals(targetState)) {
                // Draw self-loop
                drawSelfLoop(startX, y, symbol, currentCount);
            } else {
                // Draw curved transition
                drawCurvedTransition(startX, y, endX, y, symbol, currentCount);
            }
        }
    }

    private void drawSelfLoop(double centerX, double centerY, String symbol, int loopIndex) {
        double loopRadius = 25 + (loopIndex * 15); // Increase radius for multiple loops
        double loopCenterY = centerY - loopRadius - 20;

        Circle loop = new Circle(centerX, loopCenterY, loopRadius);
        loop.setStroke(Color.BLACK);
        loop.setStrokeWidth(2);
        loop.setFill(Color.TRANSPARENT);

        // Arrow for self-loop
        double arrowX = centerX + loopRadius * Math.cos(Math.PI / 4);
        double arrowY = loopCenterY + loopRadius * Math.sin(Math.PI / 4);
        Polygon arrow = createArrowHead(arrowX, arrowY, Math.PI / 4 + Math.PI / 2, 8, Color.BLACK);

        // Label positioned above the loop
        Label label = new Label(symbol);
        label.setLayoutX(centerX - 5);
        label.setLayoutY(loopCenterY - loopRadius - 15);
        label.setStyle("-fx-font-weight: bold; -fx-background-color: white; -fx-padding: 2;");

        circlePane.getChildren().addAll(loop, arrow, label);
    }

    private void drawCurvedTransition(double startX, double startY, double endX, double endY, String symbol, int curveIndex) {
        // Calculate the base curve
        double midX = (startX + endX) / 2;
        double midY = (startY + endY) / 2;

        // Calculate perpendicular offset for curve
        double dx = endX - startX;
        double dy = endY - startY;
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length == 0) return; // Avoid division by zero

        // Normalize perpendicular vector
        double perpX = -dy / length;
        double perpY = dx / length;

        // Offset amount based on curve index (for multiple transitions between same states)
        double offset = 40 + (curveIndex * 30);
        if (curveIndex % 2 == 1) offset = -offset; // Alternate curve direction

        double controlX = midX + perpX * offset;
        double controlY = midY + perpY * offset;

        // Adjust start and end points to account for circle radius
        double circleRadius = 20;
        double angle = Math.atan2(dy, dx);

        double adjustedStartX = startX + circleRadius * Math.cos(angle);
        double adjustedStartY = startY + circleRadius * Math.sin(angle);
        double adjustedEndX = endX - circleRadius * Math.cos(angle);
        double adjustedEndY = endY - circleRadius * Math.sin(angle);

        // Create quadratic curve
        QuadCurve curve = new QuadCurve();
        curve.setStartX(adjustedStartX);
        curve.setStartY(adjustedStartY);
        curve.setControlX(controlX);
        curve.setControlY(controlY);
        curve.setEndX(adjustedEndX);
        curve.setEndY(adjustedEndY);
        curve.setStroke(Color.BLACK);
        curve.setStrokeWidth(2);
        curve.setFill(Color.TRANSPARENT);

        // Calculate arrow position and angle at the end of the curve
        double t = 0.9; // Position along curve for arrow (90% of the way)
        double arrowX = Math.pow(1-t, 2) * adjustedStartX + 2*(1-t)*t * controlX + t*t * adjustedEndX;
        double arrowY = Math.pow(1-t, 2) * adjustedStartY + 2*(1-t)*t * controlY + t*t * adjustedEndY;

        // Calculate tangent angle at arrow position
        double tangentX = 2*(1-t) * (controlX - adjustedStartX) + 2*t * (adjustedEndX - controlX);
        double tangentY = 2*(1-t) * (controlY - adjustedStartY) + 2*t * (adjustedEndY - controlY);
        double arrowAngle = Math.atan2(tangentY, tangentX);

        Polygon arrow = createArrowHead(arrowX, arrowY, arrowAngle, 8, Color.BLACK);

        // Position label at the middle of the curve
        t = 0.5;
        double labelX = Math.pow(1-t, 2) * adjustedStartX + 2*(1-t)*t * controlX + t*t * adjustedEndX;
        double labelY = Math.pow(1-t, 2) * adjustedStartY + 2*(1-t)*t * controlY + t*t * adjustedEndY;

        Label label = new Label(symbol);
        label.setLayoutX(labelX - 5);
        label.setLayoutY(labelY - 10);
        label.setStyle("-fx-font-weight: bold; -fx-background-color: white; -fx-padding: 2; -fx-border-color: lightgray; -fx-border-width: 1;");

        circlePane.getChildren().addAll(curve, arrow, label);
    }
}