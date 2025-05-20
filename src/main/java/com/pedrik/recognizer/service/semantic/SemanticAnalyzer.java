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
            String name = nonTerminal.getName();

            // Tratamento de declarações
            if (name.equals("Declaration")) {
                TerminalNode typeNode = nonTerminal.getFirstTerminalWithType(TokenType.KEYWORD);
                TerminalNode idNode = nonTerminal.getFirstTerminalWithType(TokenType.IDENTIFIER);
                if (typeNode != null && idNode != null) {
                    String type = typeNode.getToken().getLexeme();
                    String nameId = idNode.getToken().getLexeme();
                    Token token = idNode.getToken();

                    boolean success = symbolTable.declare(nameId, type, token.getLine(), token.getColumn());
                    if (!success) {
                        reportError("Variável já declarada no escopo atual: " + nameId, token);
                    } else {
                        nonTerminal.setSemanticInfo(new SemanticInfo(type, true, false, false));
                    }
                }
            }

            // Tratamento de atribuições
            if (name.equals("Assignment")) {
                TerminalNode idNode = nonTerminal.getFirstTerminalWithType(TokenType.IDENTIFIER);
                if (idNode != null) {
                    Token token = idNode.getToken();
                    String varName = token.getLexeme();

                    if (!symbolTable.isDeclared(varName)) {
                        reportError("Variável não declarada na atribuição: " + varName, token);
                    } else {
                        String declaredType = symbolTable.getType(varName);
                        NonTerminalNode exprNode = nonTerminal.getChild(2, NonTerminalNode.class); // expressão depois de '='
                        if (exprNode != null && exprNode.getSemanticInfo() != null) {
                            String exprType = exprNode.getSemanticInfo().getType();

                            if (!declaredType.equals(exprType)) {
                                reportError("Tipo incompatível na atribuição. Esperado: " + declaredType + ", encontrado: " + exprType, token);
                            } else {
                                nonTerminal.setSemanticInfo(new SemanticInfo(declaredType, false, false, true));
                            }
                        }
                    }
                }
            }

            // Tratamento do uso de identificadores e literais booleanos
            if (name.equals("F")) {
                TerminalNode idNode = nonTerminal.getFirstTerminalWithType(TokenType.IDENTIFIER);
                if (idNode != null) {
                    Token token = idNode.getToken();
                    String varName = token.getLexeme();
                    if (!symbolTable.isDeclared(varName)) {
                        reportError("Uso de identificador não declarado: " + varName, token);
                    } else {
                        String type = symbolTable.getType(varName);
                        nonTerminal.setSemanticInfo(new SemanticInfo(type, false, true, false));
                    }
                } else {
                    // Literal true/false
                    TerminalNode literalNode = nonTerminal.getFirstTerminalWithType(TokenType.BOOLEAN_LITERAL);
                    if (literalNode != null) {
                        nonTerminal.setSemanticInfo(new SemanticInfo("boolean", false, false, false));
                    }
                }
            }

            // Análise dos filhos primeiro (bottom-up)
            for (Node child : nonTerminal.getChildren()) {
                analyzeNode(child);
            }

            // Verificações semânticas específicas
            switch (name) {
                case "E", "T", "F" -> analyzeBooleanExpression(nonTerminal);
            }
        }
    }

    private void analyzeBooleanExpression(NonTerminalNode node) {
        String name = node.getName();

        switch (name) {
            case "E", "T" -> {
                NonTerminalNode left = node.getChild(0, NonTerminalNode.class);
                if (left == null || left.getSemanticInfo() == null) return;

                SemanticInfo leftInfo = left.getSemanticInfo();
                if (!"boolean".equals(leftInfo.getType())) {
                    TerminalNode opNode = node.getFirstTerminalWithType(TokenType.KEYWORD);
                    if (opNode != null) {
                        reportError("Operação lógica espera operandos booleanos", opNode.getToken());
                    }
                    return;
                }

                node.setSemanticInfo(new SemanticInfo("boolean", false, false, false));
            }

            case "F" -> {
                TerminalNode notOp = node.getFirstTerminalWithType(TokenType.KEYWORD, "NOT");
                if (notOp != null) {
                    NonTerminalNode subExpr = node.getChild(1, NonTerminalNode.class);
                    if (subExpr != null && subExpr.getSemanticInfo() != null) {
                        if (!"boolean".equals(subExpr.getSemanticInfo().getType())) {
                            reportError("Operador NOT espera expressão booleana", notOp.getToken());
                        } else {
                            node.setSemanticInfo(new SemanticInfo("boolean", false, false, false));
                        }
                    }
                } else {
                    if (node.getSemanticInfo() == null) {
                        TerminalNode fallback = node.getFirstTerminalWithType(TokenType.IDENTIFIER);
                        if (fallback == null) {
                            fallback = node.getFirstTerminalWithType(TokenType.KEYWORD, "false");
                            if (fallback == null) {
                                fallback = node.getFirstTerminalWithType(TokenType.KEYWORD, "true");
                            }
                        }

                        if (fallback != null) {
                            reportError("Expressão F inválida ou não reconhecida", fallback.getToken());
                        }
                    }
                }
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

    // Método de teste
    public static void main(String[] args) {
        // Simulação da seguinte entrada:
        // int x;
        // boolean y;
        // x = y;

        // Construção da árvore sintática manualmente
        NonTerminalNode root = new NonTerminalNode("Program");

        NonTerminalNode decl1 = new NonTerminalNode("Declaration");
        decl1.addChild(new TerminalNode(new Token(TokenType.KEYWORD, "int", 1, 1)));
        decl1.addChild(new TerminalNode(new Token(TokenType.IDENTIFIER, "x", 1, 5)));

        NonTerminalNode decl2 = new NonTerminalNode("Declaration");
        decl2.addChild(new TerminalNode(new Token(TokenType.KEYWORD, "boolean", 2, 1)));
        decl2.addChild(new TerminalNode(new Token(TokenType.IDENTIFIER, "y", 2, 9)));

        NonTerminalNode assign = new NonTerminalNode("Assignment");
        assign.addChild(new TerminalNode(new Token(TokenType.IDENTIFIER, "x", 3, 1)));
        assign.addChild(new TerminalNode(new Token(TokenType.ASSIGN, "=", 3, 3)));

        NonTerminalNode expr = new NonTerminalNode("F");
        expr.addChild(new TerminalNode(new Token(TokenType.IDENTIFIER, "y", 3, 5)));
        expr.setSemanticInfo(new SemanticInfo("boolean", false, true, false));

        assign.addChild(expr);

        root.addChild(decl1);
        root.addChild(decl2);
        root.addChild(assign);

        // Executar análise semântica
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(root);

        if (analyzer.hasErrors()) {
            System.out.println("Erros encontrados:");
            analyzer.getErrors().forEach(System.out::println);
        } else {
            System.out.println("Análise semântica concluída sem erros.");
        }
    }
}
