package com.example.dfacreator;

import java.util.Map;

public class DFASimulation {
    private DFACreator dfa;

    public DFASimulation(DFACreator dfa){
        this.dfa = dfa;
    }

    public void TestString(String test) {
        String result = testString(test);
        System.out.println(result);
    }

    public String testString(String test) {
        String currentState = dfa.getStartState();

        for (int i = 0; i < test.length(); i++) {
            String symbol = String.valueOf(test.charAt(i));

            if (!dfa.getAlphabet().contains(symbol)) {
                return "Invalid symbol '" + symbol + "' at position " + i;
            }

            Map<String, String> transitionFromCurrent = dfa.getTransitions().get(currentState);
            if (transitionFromCurrent == null || !transitionFromCurrent.containsKey(symbol)) {
                return "No transition from " + currentState + " on '" + symbol + "'";
            }

            currentState = transitionFromCurrent.get(symbol);
        }

        if (dfa.getAcceptingStates().contains(currentState)) {
            return "Accepted! Final state: " + currentState;
        } else {
            return "Rejected. Final state: " + currentState + " is not accepting.";
        }
    }
}