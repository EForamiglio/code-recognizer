package com.pedrik.recognizer.service.analytic;

import com.pedrik.recognizer.service.analytic.tree.Node;
import com.pedrik.recognizer.service.analytic.tree.NonTerminalNode;
import com.pedrik.recognizer.service.analytic.tree.TerminalNode;
import com.pedrik.recognizer.service.lexical.Lexer;
import com.pedrik.recognizer.service.lexical.Token;
import com.pedrik.recognizer.service.lexical.TokenType;

import java.util.List;

public class RecursiveDescentParser {

    private final List<Token> tokens;
    private int current;
    private StringBuilder logger = null;

    public RecursiveDescentParser(List<Token> tokens) {
        this.tokens = tokens;
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

    private Token peek() {
        return current < tokens.size() ? tokens.get(current) : new Token(TokenType.EOF, "$", -1, -1);
    }

    private Token consume(TokenType expectedType, String expectedLexeme, int depth) {
        Token token = peek();
        if (token.getType() == expectedType && (expectedLexeme == null || token.getLexeme().equals(expectedLexeme))) {
            current++;
            log(token.getLexeme(), depth + 1);
            return token;
        } else {
            throw new RuntimeException("Erro de sintaxe: esperado '" +
                    (expectedLexeme != null ? expectedLexeme : expectedType.name()) +
                    "', mas encontrou '" + token.getLexeme() + "'");
        }
    }

    public Node parse() {
        Node root = parseE(0);
        if (peek().getType() != TokenType.EOF) {
            throw new RuntimeException("Erro: entrada não totalmente consumida. Último token: " + peek().getLexeme());
        }
        System.out.println("Entrada válida. Árvore sintática:");
        root.print("");
        return root; // <- retornar a raiz
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
        Token token = peek();
        if (token.getType() == TokenType.KEYWORD &&
                (token.getLexeme().equals("AND") || token.getLexeme().equals("OR"))) {
            node.addChild(new TerminalNode(consume(TokenType.KEYWORD, token.getLexeme(), depth)));
            node.addChild(parseT(depth + 1));
            node.addChild(parseEPrime(depth + 1));
        } else {
            node.addChild(new TerminalNode(new Token(TokenType.EOF, "ε", -1, -1)));
            log("ε", depth + 1);
        }
        return node;
    }

    // T → NOT F | F
    private Node parseT(int depth) {
        log("T", depth);
        NonTerminalNode node = new NonTerminalNode("T");
        Token token = peek();
        if (token.getType() == TokenType.KEYWORD && token.getLexeme().equals("NOT")) {
            node.addChild(new TerminalNode(consume(TokenType.KEYWORD, "NOT", depth)));
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
        Token token = peek();
        if (token.getType() == TokenType.DELIMITER && token.getLexeme().equals("(")) {
            node.addChild(new TerminalNode(consume(TokenType.DELIMITER, "(", depth)));
            node.addChild(parseE(depth + 1));
            node.addChild(new TerminalNode(consume(TokenType.DELIMITER, ")", depth)));
        } else if (token.getType() == TokenType.IDENTIFIER) {
            node.addChild(new TerminalNode(consume(TokenType.IDENTIFIER, null, depth)));
        } else {
            throw new RuntimeException("Erro: esperado identificador ou '(', mas encontrou '" + token.getLexeme() + "'");
        }
        return node;
    }

    // Teste direto
    public static void main(String[] args) {
        String input = "velocidade AND NOT (caindo OR subindo)";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();

        for (Token token : tokens) {
            System.out.println(token.getType() + " => '" + token.getLexeme() + "'");
        }

        RecursiveDescentParser parser = new RecursiveDescentParser(tokens);
        parser.parse();
    }
}



