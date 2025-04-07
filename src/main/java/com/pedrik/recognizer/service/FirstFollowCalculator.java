package com.pedrik.recognizer.service;

import java.util.*;

public class FirstFollowCalculator {

    private static final String EPSILON = "ε";
    private final Map<String, List<List<String>>> grammar;
    private final Set<String> nonTerminals;
    private final Map<String, Set<String>> first;
    private final Map<String, Set<String>> follow;
    private final String startSymbol;

    public FirstFollowCalculator(String startSymbol, Map<String, List<List<String>>> grammar) {
        this.grammar = grammar;
        this.nonTerminals = grammar.keySet();
        this.first = new HashMap<>();
        this.follow = new HashMap<>();
        this.startSymbol = startSymbol;
        init();
    }


    private void init() {
        for (String nt : nonTerminals) {
            first.put(nt, new HashSet<>());
            follow.put(nt, new HashSet<>());
        }
        follow.get(startSymbol).add("$"); // marcador de fim da entrada
    }

    public void computeFirst() {
        boolean changed;
        do {
            changed = false;
            for (String nt : nonTerminals) {
                for (List<String> production : grammar.get(nt)) {
                    Set<String> currentFirst = first.get(nt);
                    int beforeSize = currentFirst.size();

                    boolean allNullable = true;
                    for (String symbol : production) {
                        Set<String> symbolFirst;

                        if (nonTerminals.contains(symbol)) {
                            symbolFirst = first.get(symbol);
                        } else if (symbol.equals(EPSILON)) {
                            symbolFirst = Set.of(EPSILON);
                        } else {
                            symbolFirst = Set.of(symbol); // terminal
                        }

                        // Adiciona todos os símbolos, menos ε
                        for (String sym : symbolFirst) {
                            if (!sym.equals(EPSILON)) {
                                currentFirst.add(sym);
                            }
                        }

                        if (!symbolFirst.contains(EPSILON)) {
                            allNullable = false;
                            break;
                        }
                    }

                    if (allNullable) {
                        currentFirst.add(EPSILON);
                    }

                    if (currentFirst.size() > beforeSize) {
                        changed = true;
                    }
                }
            }
        } while (changed);
    }


    public void computeFollow() {
        boolean changed;
        do {
            changed = false;
            for (String lhs : nonTerminals) {
                for (List<String> production : grammar.get(lhs)) {
                    for (int i = 0; i < production.size(); i++) {
                        String B = production.get(i);
                        if (!nonTerminals.contains(B)) continue;

                        Set<String> followB = follow.get(B);
                        int beforeSize = followB.size();

                        boolean epsilonInAll = true;
                        for (int j = i + 1; j < production.size(); j++) {
                            String beta = production.get(j);

                            Set<String> firstBeta;
                            if (nonTerminals.contains(beta)) {
                                firstBeta = first.get(beta);
                            } else if (beta.equals(EPSILON)) {
                                firstBeta = Set.of(EPSILON);
                            } else {
                                firstBeta = Set.of(beta);
                            }

                            followB.addAll(firstBeta);
                            followB.remove(EPSILON);

                            if (!firstBeta.contains(EPSILON)) {
                                epsilonInAll = false;
                                break;
                            }
                        }

                        if (i == production.size() - 1 || epsilonInAll) {
                            followB.addAll(follow.get(lhs));
                        }

                        if (followB.size() > beforeSize) {
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);
    }

    public void printFirst() {
        System.out.println("First sets:");
        for (String nt : nonTerminals) {
            System.out.println("FIRST(" + nt + ") = " + first.get(nt));
        }
    }

    public void printFollow() {
        System.out.println("\nFollow sets:");
        for (String nt : nonTerminals) {
            System.out.println("FOLLOW(" + nt + ") = " + follow.get(nt));
        }
    }

    // Main para teste
    public static void main(String[] args) {
        Map<String, List<List<String>>> grammar = Map.of(
                "E", List.of(List.of("T", "E'")),
                "E'", List.of(List.of("AND", "T", "E'"), List.of("OR", "T", "E'"), List.of("ε")),
                "T", List.of(List.of("NOT", "F"), List.of("F")),
                "F", List.of(List.of("(", "E", ")"), List.of("id"))
        );

        FirstFollowCalculator calc = new FirstFollowCalculator("E", grammar);
        calc.computeFirst();
        calc.computeFollow();
        calc.printFirst();
        calc.printFollow();
    }
}
