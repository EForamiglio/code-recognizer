package com.pedrik.recognizer.service.semantic;

import com.pedrik.recognizer.service.lexical.Token;

import java.util.*;

public class SymbolTable {

    private final Deque<Map<String, String>> scopes = new ArrayDeque<>();

    public SymbolTable() {
        enterScope(); // Cria escopo global
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        scopes.pop();
    }

    public boolean declare(String name, String type, int line, int column) {
        Map<String, String> currentScope = scopes.peek();
        if (currentScope.containsKey(name)) {
            return false; // Já declarado neste escopo
        }
        currentScope.put(name, type);
        return true;
    }

    public boolean isDeclared(String name) {
        for (Map<String, String> scope : scopes) {
            if (scope.containsKey(name)) return true;
        }
        return false;
    }

    public String getType(String name) {
        for (Map<String, String> scope : scopes) {
            if (scope.containsKey(name)) return scope.get(name);
        }
        return null;
    }
}
