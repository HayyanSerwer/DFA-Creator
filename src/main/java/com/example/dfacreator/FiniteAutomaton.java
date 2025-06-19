package com.example.dfacreator;

import java.util.*;

public abstract class FiniteAutomaton {
    protected final List<String> stateList = new ArrayList<>();
    protected final Set<String> acceptingStates = new HashSet<>();
    protected String startState;
    protected final List<String> alphabet = new ArrayList<>();

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

    public List<String> getStates() { return stateList; }
    public Set<String> getAcceptingStates() { return acceptingStates; }
    public String getStartState() { return startState; }
    public List<String> getAlphabet() { return alphabet; }

    // Subclasses should define how transitions work
    public abstract void addTransition(String from, String symbol, String to);
}
