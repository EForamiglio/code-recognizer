package com.pedrik.recognizer.service;

import java.util.*;

public class RecursiveDescentParser {

    private List<String> tokens;
    private int current;
    private StringBuilder logger = null;

    public RecursiveDescentParser(String input) {
        this.tokens = tokenize(input);
        this.current = 0;
    }

    public void setLogger(StringBuilder logger) {
        this.logger = logger;
    }

    private void log(String text, int depth) {
        if (logger != null) {
            logger.append("  ".repeat(depth)).append(text).append("\n");
        }
    }

    private List<String> tokenize(String input) {
        return new ArrayList<>(List.of(input.split("\\s+")));
    }

    private String peek() {
        return current < tokens.size() ? tokens.get(current) : "$";
    }

    private TerminalNode consume(String expected, int depth) {
        String token = peek();
        if (token.equals(expected)) {
            current++;
            log(token, depth + 1);
            return new TerminalNode(token);
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado '" + expected + "', mas encontrou '" + token + "'");
        }
    }

    public void parse() {
        Node root = parseE(0);
        if (!peek().equals("$")) {
            throw new RuntimeException("Erro: entrada não totalmente consumida. Último token: " + peek());
        }
        System.out.println("Entrada válida. Árvore sintática:");
        root.print("");
    }

    // E → T E'
    private Node parseE(int depth) {
        log("E", depth);
        NonTerminalNode node = new NonTerminalNode("E");
        node.addChild(parseT(depth + 1));
        node.addChild(parseEPrime(depth + 1));
        return node;
    }

    // E' → AND T E' | OR T E' | ε
    private Node parseEPrime(int depth) {
        log("E'", depth);
        NonTerminalNode node = new NonTerminalNode("E'");
        String token = peek();
        if (token.equals("AND") || token.equals("OR")) {
            node.addChild(consume(token, depth));  // AND ou OR
            node.addChild(parseT(depth + 1));
            node.addChild(parseEPrime(depth + 1));
        } else {
            // ε-produção (vazio)
            node.addChild(new TerminalNode("ε"));
            log("ε", depth + 1);
        }
        return node;
    }

    // T → NOT F | F
    private Node parseT(int depth) {
        log("T", depth);
        NonTerminalNode node = new NonTerminalNode("T");
        String token = peek();
        if (token.equals("NOT")) {
            node.addChild(consume("NOT", depth));
            node.addChild(parseF(depth + 1));
        } else {
            node.addChild(parseF(depth + 1));
        }
        return node;
    }

    // F → ( E ) | id
    private Node parseF(int depth) {
        log("F", depth);
        NonTerminalNode node = new NonTerminalNode("F");
        String token = peek();
        if (token.equals("(")) {
            node.addChild(consume("(", depth));
            node.addChild(parseE(depth + 1));
            node.addChild(consume(")", depth));
        } else if (token.equals("id")) {
            node.addChild(consume("id", depth));
        } else {
            throw new RuntimeException("Erro: esperado 'id' ou '(', mas encontrou '" + token + "'");
        }
        return node;
    }

    public static void main(String[] args) {
        String input = "id AND id";  // entrada de teste
        RecursiveDescentParser parser = new RecursiveDescentParser(input);
        parser.parse();
    }
}

abstract class Node {
    public abstract void print(String indent);
}

class NonTerminalNode extends Node {
    String name;
    List<Node> children = new ArrayList<>();

    public NonTerminalNode(String name) {
        this.name = name;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + name);
        for (Node child : children) {
            child.print(indent + "  ");
        }
    }
}

class TerminalNode extends Node {
    String token;

    public TerminalNode(String token) {
        this.token = token;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + token);
    }
}

