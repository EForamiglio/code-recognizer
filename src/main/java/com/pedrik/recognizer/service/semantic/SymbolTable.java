package com.pedrik.recognizer.service.semantic;

import com.pedrik.recognizer.service.lexical.Token;

import java.util.*;

public class SymbolTable {

    private final Deque<Map<String, SemanticInfo>> scopes = new ArrayDeque<>();

    public SymbolTable() {
        enterScope(); // escopo global
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    public boolean declare(String name, String type, int line, int column) {
        Map<String, SemanticInfo> currentScope = scopes.peek();
        if (currentScope.containsKey(name)) {
            return false; // já declarado neste escopo
        }
        currentScope.put(name, new SemanticInfo(type, true, false, false));
        return true;
    }

    public boolean isDeclared(String name) {
        return scopes.stream().anyMatch(scope -> scope.containsKey(name));
    }

    public String getType(String name) {
        for (Map<String, SemanticInfo> scope : scopes) {
            if (scope.containsKey(name)) {
                return scope.get(name).getType();
            }
        }
        return null;
    }
}
