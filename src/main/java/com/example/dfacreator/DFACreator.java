package com.example.dfacreator;

import java.util.*;

public class DFACreator {
        private final List<String> stateList = new ArrayList<>();
        private final Set<String> acceptingStates = new HashSet<>();
        private String startState;
        private final List<String> alphabet = new ArrayList<>();
        private final Map<String, Map<String, String>> transitions = new HashMap<>();

        public void addState(String stateName){
            stateList.add(stateName);
        }

        public void setStartState(String stateName){
            startState = stateName;
        }

        public void addAcceptingState(String stateName){
            acceptingStates.add(stateName);
        }

        public void setAlphabet(String symbol){
            alphabet.add(symbol);
        }
        public void addTransition(String from, String symbol, String to) {
            if (!transitions.containsKey(from)) {
                transitions.put(from, new HashMap<>());
            }
            Map<String, String> symbolMap = transitions.get(from);
            symbolMap.put(symbol, to);
        }


        public List<String> getStates() { return stateList; }
        public Set<String> getAcceptingStates() { return acceptingStates; }
        public String getStartState() { return startState; }
        public Map<String, Map<String, String>> getTransitions() { return transitions; }
        public List<String> getAlphabet() { return alphabet; }


}
