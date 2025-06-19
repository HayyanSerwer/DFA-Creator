package com.example.dfacreator;

import java.util.*;

public class DFAModel {
    private List<Character> states;
    private Character startState;
    private List<Character> acceptingStates;
    private List<String> alphabet;
    private Map<Character, Map<String, Character>> transitions;

    public DFAModel() {
        this.states = new ArrayList<>();
        this.acceptingStates = new ArrayList<>();
        this.alphabet = new ArrayList<>();
        this.transitions = new HashMap<>();
    }

    // Getters and Setters
    public List<Character> getStates() { return states; }
    public void setStates(List<Character> states) { this.states = states; }

    public Character getStartState() { return startState; }
    public void setStartState(Character startState) { this.startState = startState; }

    public List<Character> getAcceptingStates() { return acceptingStates; }
    public void setAcceptingStates(List<Character> acceptingStates) { this.acceptingStates = acceptingStates; }

    public List<String> getAlphabet() { return alphabet; }
    public void setAlphabet(List<String> alphabet) { this.alphabet = alphabet; }

    public Map<Character, Map<String, Character>> getTransitions() { return transitions; }
    public void setTransitions(Map<Character, Map<String, Character>> transitions) { this.transitions = transitions; }

    public String testString(String input) {
        StringBuilder result = new StringBuilder();
        Character currentState = startState;

        result.append("Trace: ").append(currentState);

        for (int i = 0; i < input.length(); i++) {
            String symbol = String.valueOf(input.charAt(i));

            // Check if symbol is in alphabet
            if (!alphabet.contains(symbol)) {
                return "REJECTED: Invalid symbol '" + symbol + "' at position " + i + " (not in alphabet)";
            }

            // Check if transition exists
            Map<String, Character> currentTransitions = transitions.get(currentState);
            if (currentTransitions == null || !currentTransitions.containsKey(symbol)) {
                return "REJECTED: No transition from state " + currentState + " on symbol '" + symbol + "'";
            }

            // Make transition
            currentState = currentTransitions.get(symbol);
            result.append(" --").append(symbol).append("--> ").append(currentState);
        }

        // Check if final state is accepting
        if (acceptingStates.contains(currentState)) {
            result.append("\nRESULT: ACCEPTED (Final state ").append(currentState).append(" is accepting)");
        } else {
            result.append("\nRESULT: REJECTED (Final state ").append(currentState).append(" is not accepting)");
        }

        return result.toString();
    }

    public String minimize() {
        StringBuilder result = new StringBuilder();

        // Step 1: Find reachable states
        Set<Character> reachableStates = findReachableStates();
        result.append("Reachable states: ").append(reachableStates).append("\n");

        // Step 2: Initial partitioning
        Set<Set<Character>> partitions = new HashSet<>();
        Set<Character> acceptingSet = new HashSet<>();
        Set<Character> nonAcceptingSet = new HashSet<>();

        for (Character state : reachableStates) {
            if (acceptingStates.contains(state)) {
                acceptingSet.add(state);
            } else {
                nonAcceptingSet.add(state);
            }
        }

        if (!acceptingSet.isEmpty()) partitions.add(acceptingSet);
        if (!nonAcceptingSet.isEmpty()) partitions.add(nonAcceptingSet);

        result.append("Initial partitions: ").append(partitions).append("\n");

        // Step 3: Refine partitions
        boolean changed = true;
        int iteration = 0;

        while (changed) {
            changed = false;
            iteration++;
            Set<Set<Character>> newPartitions = new HashSet<>();

            for (Set<Character> partition : partitions) {
                Map<String, Set<Character>> refinedGroups = new HashMap<>();

                for (Character state : partition) {
                    StringBuilder signature = new StringBuilder();

                    for (String symbol : alphabet) {
                        Character targetState = transitions.get(state).get(symbol);
                        int targetPartition = findPartitionIndex(partitions, targetState);
                        signature.append(targetPartition).append(",");
                    }

                    String key = signature.toString();
                    refinedGroups.computeIfAbsent(key, k -> new HashSet<>()).add(state);
                }

                if (refinedGroups.size() > 1) {
                    changed = true;
                    result.append("Partition ").append(partition).append(" split into: ").append(refinedGroups.values()).append("\n");
                }

                newPartitions.addAll(refinedGroups.values());
            }

            partitions = newPartitions;

            if (changed) {
                result.append("After iteration ").append(iteration).append(": ").append(partitions).append("\n");
            }
        }

        result.append("\nFinal minimized partitions:\n");
        int partitionNum = 1;
        for (Set<Character> partition : partitions) {
            result.append("Partition ").append(partitionNum++).append(": ").append(partition).append("\n");
        }

        // Analyze results
        if (partitions.size() < states.size()) {
            result.append("\nDFA can be minimized from ").append(states.size()).append(" states to ").append(partitions.size()).append(" states.");
        } else {
            result.append("\nDFA is already minimal.");
        }

        return result.toString();
    }

    private Set<Character> findReachableStates() {
        Set<Character> reachable = new HashSet<>();
        Queue<Character> queue = new LinkedList<>();

        queue.add(startState);
        reachable.add(startState);

        while (!queue.isEmpty()) {
            Character current = queue.poll();
            Map<String, Character> currentTransitions = transitions.get(current);

            if (currentTransitions != null) {
                for (String symbol : alphabet) {
                    Character next = currentTransitions.get(symbol);
                    if (next != null && !reachable.contains(next)) {
                        reachable.add(next);
                        queue.add(next);
                    }
                }
            }
        }

        return reachable;
    }

    private int findPartitionIndex(Set<Set<Character>> partitions, Character state) {
        int index = 0;
        for (Set<Character> partition : partitions) {
            if (partition.contains(state)) {
                return index;
            }
            index++;
        }
        return -1;
    }
}