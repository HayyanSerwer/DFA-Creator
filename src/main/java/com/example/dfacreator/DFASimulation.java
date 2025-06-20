package com.example.dfacreator;

import java.util.Map;

public class DFASimulation {
    DFACreator dfa1 = new DFACreator();
    public void TestString(String test) {
        String currentState = dfa1.startState;

        for (int i = 0; i < test.length(); i++) {
            String symbol = String.valueOf(test.charAt(i));

            if (!dfa1.getAlphabet().contains(symbol)) {
                System.out.println("Invalid symbol '" + symbol + "' at position " + i);
                return;
            }
            Map<String, String> transitionFromCurrent = dfa1.getTransitions().get(currentState);
            if (!transitionFromCurrent.containsKey(symbol)){ // Pretty sure this would only happen in an NFA but ill leave it in
                System.out.println("No transition from " + currentState + " on " + symbol);
                return;
            }
            currentState = transitionFromCurrent.get(symbol);
        }

        if (dfa1.acceptingStates.contains(currentState)) {
            System.out.println("Accepted! Final state: " + currentState);
        } else {
            System.out.println("Rejected. Final state: " + currentState + " is not accepting.");
        }

    }
}
