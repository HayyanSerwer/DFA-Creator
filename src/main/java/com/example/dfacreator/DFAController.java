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

            double spacing = 90;


            for (int i =0; i < stateNumber; i++){
                double x = spacing * i;
                double y = 200;
                Circle circle = new Circle(spacing * i, 200, 20);
                circle.setStyle("-fx-fill: lightblue; -fx-stroke: black; -fx-stroke-width: 2;");
                Label label = new Label("Q" + i);
                label.setLayoutX(x - 10);
                label.setLayoutY(y - 8);
                circlePane.getChildren().addAll(circle, label);
                dfa.addState(label.getText());
            }

            //Drawing the outer circles for the accepting states here, added the strings and sets outside of the loops
            Set<String> acceptingStates = dfa.getAcceptingStates();
            ArrayList<String> acceptingArrayList = new ArrayList<>();
            acceptingArrayList.addAll(acceptingStates);
            for (int j = 0; j<dfa.getAcceptingStates().size(); j++){

                int statevalue = Integer.parseInt(acceptingArrayList.get(j).substring(1));
                Circle circle = new Circle(spacing*statevalue, 200, 25);
                circle.setStyle("-fx-fill: transparent; -fx-stroke: black; -fx-stroke-width: 2;");

                circlePane.getChildren().addAll(circle);
            }

            if (startingState != null){
                int positionNumber = Integer.parseInt(startingState.substring(1));
                Line startingArrowLine = new Line(spacing*positionNumber-50, 200, spacing*positionNumber-20, 200);
                Polygon startArrow = createTriangle(spacing*positionNumber-20, 200, 10, Color.BLACK);
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
        private void onAddAlphabet(){
            String input = alphabetField.getText().trim(); // Get and trim the input
            String[] parts = input.split(",");             // Split by commas

            ArrayList<String> alphabet = new ArrayList<>();

            for (String part : parts) {
                String symbol = part.trim();               // Remove spaces around each symbol
                if (!symbol.isEmpty()) {
                    alphabet.add(symbol);
                    dfa.setAlphabet(symbol);
                }
            }
            buildTransitionInputs();
            System.out.println("Parsed alphabet: " + alphabet);
        }

        private void buildTransitionInputs() {
            transitionBox.getChildren().clear(); // Remove old ones
            transitionInputs.clear();

            for (String symbol : dfa.getAlphabet()) {
                VBox symbolColumn = new VBox(10); // This VBox will represent one column for one input symbol
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

                transitionBox.getChildren().add(symbolColumn); // Add each symbol column to the HBox
            }
        }


    }