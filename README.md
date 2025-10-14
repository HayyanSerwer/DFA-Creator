# How To Run

1) Clone the Repository
```bash
git clone https://github.com/HayyanSerwer/DFA-Creator
cd DFA-Creator
```
2) Open in your JavaFX-enabled IDE
   - Set up JavaFX libraries in your project.
   - Set DFAController as the main controller.
   - Ensure FXML files (DFA-creator.fxml, DFA-history.fxml) are correctly loaded.
   
3) Run the Application
   - Run the application via your IDE’s run configuration. 

## How to Use

- Enter Number of States
Type the number of DFA states (e.g., 4) and press Enter.

- Enter Start State
Input a valid start state (e.g., Q0).

- Define Accepting States
Use the dropdown to select accepting states and click Add Accepting.

- Define Alphabet
Enter comma-separated alphabet symbols (e.g., a,b) and click the corresponding button to confirm.

- Add Transitions
Choose transition targets from dropdowns for each (state, symbol) pair and click Add Transitions.

- Test Input Strings
Enter a string to test and click the Test String button to simulate DFA behavior.

- Export DFA
Click the export button to save the DFA to a uniquely named file in the DFASaves directory.

- Navigate to History View
Use the "DFA History" button to navigate to the history scene (DFA-history.fxml).

- Clear Canvas
Click Clear Canvas to reset the canvas and DFA configuration.

## Example Export File
```text
States:
  Q0
  Q1

Alphabet:
  a
  b

Start State:
  Q0

Accepting States:
  Q1

Transitions:
  Q0 --a--> Q1
  Q0 --b--> Q0
  Q1 --a--> Q0
  Q1 --b--> Q1
```
