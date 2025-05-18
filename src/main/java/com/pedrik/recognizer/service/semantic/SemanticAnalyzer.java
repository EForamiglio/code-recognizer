package com.pedrik.recognizer.service.semantic;

import com.pedrik.recognizer.service.analytic.tree.Node;
import com.pedrik.recognizer.service.analytic.tree.NonTerminalNode;
import com.pedrik.recognizer.service.analytic.tree.TerminalNode;
import com.pedrik.recognizer.service.lexical.Token;
import com.pedrik.recognizer.service.lexical.TokenType;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzer {

    private final SymbolTable symbolTable = new SymbolTable();
    private final List<String> errors = new ArrayList<>();

    public void analyze(Node root) {
        analyzeNode(root);
    }

    private void analyzeNode(Node node) {
        if (node instanceof NonTerminalNode nonTerminal) {
            // Tratamento para declarações
            if (nonTerminal.getName().equals("Declaration")) {
                // Exemplo: int x;
                TerminalNode typeNode = nonTerminal.getFirstTerminalWithType(TokenType.KEYWORD);
                TerminalNode idNode = nonTerminal.getFirstTerminalWithType(TokenType.IDENTIFIER);
                if (typeNode != null && idNode != null) {
                    String type = typeNode.getToken().getLexeme();
                    String name = idNode.getToken().getLexeme();
                    Token token = idNode.getToken();

                    boolean success = symbolTable.declare(name, type, token.getLine(), token.getColumn());
                    if (!success) {
                        reportError("Variável já declarada no escopo atual: " + name, token);
                    } else {
                        nonTerminal.setSemanticInfo(new SemanticInfo(type, true, false, false));
                    }
                }
            }

            // Tratamento para uso de identificadores
            List<TerminalNode> identifiers = nonTerminal.getAllChildrenOfType(TokenType.IDENTIFIER);
            for (TerminalNode idNode : identifiers) {
                Token token = idNode.getToken();
                String name = token.getLexeme();
                if (!symbolTable.isDeclared(name)) {
                    reportError("Uso de identificador não declarado: " + name, token);
                } else {
                    // Opcional: registrar uso
                    String type = symbolTable.getType(name);
                    SemanticInfo info = new SemanticInfo(type, false, true, false);
                    nonTerminal.setSemanticInfo(info);
                }
            }

            // Recursão nos filhos
            for (Node child : nonTerminal.getChildren()) {
                analyzeNode(child);
            }
        }
    }

    private void reportError(String message, Token token) {
        errors.add("Erro semântico na linha " + token.getLine() +
                ", coluna " + token.getColumn() + ": " + message);
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
