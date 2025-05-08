package com.pedrik.recognizer.service.analytic;

import java.util.*;

public class LL1TableBuilder {

    private final Map<String, List<List<String>>> grammar;
    private final Map<String, Set<String>> first;
    private final Map<String, Set<String>> follow;
    private final Map<String, Map<String, List<String>>> table = new HashMap<>();

    public LL1TableBuilder(
            Map<String, List<List<String>>> grammar,
            Map<String, Set<String>> first,
            Map<String, Set<String>> follow
    ) {
        this.grammar = grammar;
        this.first = first;
        this.follow = follow;
        buildTable();
    }

    public Map<String, Map<String, List<String>>> getTable() {
        return table;
    }

    private void buildTable() {
        for (String nonTerminal : grammar.keySet()) {
            table.putIfAbsent(nonTerminal, new HashMap<>());

            for (List<String> production : grammar.get(nonTerminal)) {
                Set<String> firstSet = computeFirstOfSequence(production);

                for (String terminal : firstSet) {
                    if (!terminal.equals("ε")) {
                        table.get(nonTerminal).put(terminal, production);
                    }
                }

                if (firstSet.contains("ε")) {
                    for (String f : follow.get(nonTerminal)) {
                        table.get(nonTerminal).put(f, production);
                    }
                }
            }
        }
    }

    // FIRST(α) onde α é uma sequência de símbolos (ex: ["T", "E'"])
    private Set<String> computeFirstOfSequence(List<String> symbols) {
        Set<String> result = new HashSet<>();

        for (String symbol : symbols) {
            Set<String> currentFirst = first.get(symbol);
            if (currentFirst == null) {
                result.add(symbol); // terminal
                break;
            }

            result.addAll(currentFirst);
            if (!currentFirst.contains("ε")) {
                result.remove("ε");
                break;
            }
        }

        if (symbols.isEmpty() || result.contains("ε")) {
            result.add("ε");
        }

        return result;
    }

    public void printTable() {
        System.out.println("LL(1) Parsing Table:");
        for (String nonTerminal : table.keySet()) {
            for (String terminal : table.get(nonTerminal).keySet()) {
                List<String> production = table.get(nonTerminal).get(terminal);
                System.out.printf("M[%s, %s] = %s -> %s\n", nonTerminal, terminal, nonTerminal, String.join(" ", production));
            }
        }
    }
}
