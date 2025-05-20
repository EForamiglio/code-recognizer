package com.pedrik.recognizer.service;

import com.pedrik.recognizer.service.analytic.tree.NonTerminalNode;
import com.pedrik.recognizer.service.analytic.tree.TerminalNode;
import com.pedrik.recognizer.service.lexical.Token;
import com.pedrik.recognizer.service.lexical.TokenType;
import com.pedrik.recognizer.service.semantic.SemanticAnalyzer;
import com.pedrik.recognizer.service.semantic.SemanticInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SemanticAnalyzerTest {

    @Test
    void testValidDeclarationAndAssignment() {
        NonTerminalNode root = new NonTerminalNode("Program");

        NonTerminalNode decl = new NonTerminalNode("Declaration");
        decl.addChild(new TerminalNode(new Token(TokenType.KEYWORD, "boolean", 1, 1)));
        decl.addChild(new TerminalNode(new Token(TokenType.IDENTIFIER, "flag", 1, 9)));

        NonTerminalNode assign = new NonTerminalNode("Assignment");
        assign.addChild(new TerminalNode(new Token(TokenType.IDENTIFIER, "flag", 2, 1)));
        assign.addChild(new TerminalNode(new Token(TokenType.ASSIGN, "=", 2, 6)));

        NonTerminalNode expr = new NonTerminalNode("F");
        expr.addChild(new TerminalNode(new Token(TokenType.BOOLEAN_LITERAL, "true", 2, 8)));
        expr.setSemanticInfo(new SemanticInfo("boolean", false, true, false));
        assign.addChild(expr);

        root.addChild(decl);
        root.addChild(assign);

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(root);

        assertFalse(analyzer.hasErrors(), "Não deveria haver erros semânticos.");
    }

    @Test
    void testUndeclaredVariableUsage() {
        NonTerminalNode root = new NonTerminalNode("Program");

        NonTerminalNode expr = new NonTerminalNode("F");
        expr.addChild(new TerminalNode(new Token(TokenType.IDENTIFIER, "x", 1, 1)));

        root.addChild(expr);

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(root);

        assertTrue(analyzer.hasErrors());
        assertEquals(2, analyzer.getErrors().size());
        assertTrue(analyzer.getErrors().get(0).contains("Uso de identificador não declarado"));
    }

    @Test
    void testTypeMismatchInAssignment() {
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

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(root);

        assertTrue(analyzer.hasErrors(), "Deveria haver erro de tipo.");
        assertTrue(analyzer.getErrors().get(0).contains("Tipo incompatível"));
    }

    @Test
    void testDuplicateDeclaration() {
        NonTerminalNode root = new NonTerminalNode("Program");

        NonTerminalNode decl1 = new NonTerminalNode("Declaration");
        decl1.addChild(new TerminalNode(new Token(TokenType.KEYWORD, "boolean", 1, 1)));
        decl1.addChild(new TerminalNode(new Token(TokenType.IDENTIFIER, "x", 1, 9)));

        NonTerminalNode decl2 = new NonTerminalNode("Declaration");
        decl2.addChild(new TerminalNode(new Token(TokenType.KEYWORD, "boolean", 2, 1)));
        decl2.addChild(new TerminalNode(new Token(TokenType.IDENTIFIER, "x", 2, 9)));

        root.addChild(decl1);
        root.addChild(decl2);

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(root);

        assertTrue(analyzer.hasErrors());
        assertTrue(analyzer.getErrors().get(0).contains("já declarada"));
    }

    @Test
    void testNotOperatorWithNonBoolean() {
        NonTerminalNode root = new NonTerminalNode("Program");

        NonTerminalNode decl = new NonTerminalNode("Declaration");
        decl.addChild(new TerminalNode(new Token(TokenType.KEYWORD, "int", 1, 1)));
        decl.addChild(new TerminalNode(new Token(TokenType.IDENTIFIER, "x", 1, 5)));

        NonTerminalNode notExpr = new NonTerminalNode("F");
        notExpr.addChild(new TerminalNode(new Token(TokenType.KEYWORD, "NOT", 2, 1)));

        NonTerminalNode inner = new NonTerminalNode("F");
        inner.addChild(new TerminalNode(new Token(TokenType.IDENTIFIER, "x", 2, 5)));
        inner.setSemanticInfo(new SemanticInfo("int", false, true, false));

        notExpr.addChild(inner);

        root.addChild(decl);
        root.addChild(notExpr);

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(root);

        assertTrue(analyzer.hasErrors());
        assertTrue(analyzer.getErrors().get(0).contains("NOT espera expressão booleana"));
    }
}
