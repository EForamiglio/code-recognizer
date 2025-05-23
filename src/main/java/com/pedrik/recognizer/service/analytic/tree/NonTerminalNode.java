package com.pedrik.recognizer.service.analytic.tree;

import com.pedrik.recognizer.service.lexical.TokenType;
import com.pedrik.recognizer.service.semantic.SemanticInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class NonTerminalNode extends Node {

    private final String name;
    private final List<Node> children = new ArrayList<>();
    private SemanticInfo semanticInfo;

    public NonTerminalNode(String name) {
        this.name = name;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public String getName() {
        return name;
    }

    public List<Node> getChildren() {
        return children;
    }

    public SemanticInfo getSemanticInfo() {
        return semanticInfo;
    }

    public void setSemanticInfo(SemanticInfo semanticInfo) {
        this.semanticInfo = semanticInfo;
    }

    public TerminalNode getFirstTerminalWithType(TokenType type) {
        for (Node child : children) {
            if (child instanceof TerminalNode terminal) {
                if (terminal.getToken().getType() == type) {
                    return terminal;
                }
            } else if (child instanceof NonTerminalNode nonTerminal) {
                TerminalNode found = nonTerminal.getFirstTerminalWithType(type);
                if (found != null) return found;
            }
        }
        return null;
    }

    public List<TerminalNode> getAllChildrenOfType(TokenType type) {
        List<TerminalNode> result = new ArrayList<>();
        for (Node child : children) {
            if (child instanceof TerminalNode terminal && terminal.getToken().getType() == type) {
                result.add(terminal);
            } else if (child instanceof NonTerminalNode nonTerminal) {
                result.addAll(nonTerminal.getAllChildrenOfType(type));
            }
        }
        return result;
    }

    /**
     * Retorna todos os lexemas de identificadores usados neste nó (recursivamente).
     */
    public List<TerminalNode> getAllIdentifiers() {
        return getAllChildrenOfType(TokenType.IDENTIFIER);
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + name);
        for (Node child : children) {
            child.print(indent + "  ");
        }
    }

    /**
     * Retorna o filho na posição 'index' se for do tipo fornecido, ou null caso contrário.
     */
    public <T extends Node> T getChild(int index, Class<T> type) {
        if (index < 0 || index >= children.size()) return null;
        Node child = children.get(index);
        if (type.isInstance(child)) {
            return type.cast(child);
        }
        return null;
    }

    /**
     * Retorna o primeiro TerminalNode com tipo e lexema correspondentes, buscando recursivamente.
     */
    public TerminalNode getFirstTerminalWithType(TokenType type, String lexeme) {
        for (Node child : children) {
            if (child instanceof TerminalNode terminal) {
                if (terminal.getToken().getType() == type && terminal.getToken().getLexeme().equalsIgnoreCase(lexeme)) {
                    return terminal;
                }
            } else if (child instanceof NonTerminalNode nonTerminal) {
                TerminalNode found = nonTerminal.getFirstTerminalWithType(type, lexeme);
                if (found != null) return found;
            }
        }
        return null;
    }

}

