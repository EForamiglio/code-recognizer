package com.pedrik.recognizer.service;

import com.pedrik.recognizer.service.analytic.RecursiveDescentParser;
import com.pedrik.recognizer.service.lexical.Lexer;
import com.pedrik.recognizer.service.lexical.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecursiveDescentParserTest {

    private List<Token> tokenize(String input) {
        return new Lexer(input).tokenize();
    }

    @Test
    void testValidSimpleExpression() {
        List<Token> tokens = tokenize("id");
        assertDoesNotThrow(() -> new RecursiveDescentParser(tokens).parse());
    }

    @Test
    void testValidExpressionWithAndOr() {
        List<Token> tokens = tokenize("id AND id OR id");
        assertDoesNotThrow(() -> new RecursiveDescentParser(tokens).parse());
    }

    @Test
    void testValidExpressionWithNotAndParentheses() {
        List<Token> tokens = tokenize("id AND ( NOT id )");
        assertDoesNotThrow(() -> new RecursiveDescentParser(tokens).parse());
    }

    @Test
    void testInvalidMissingClosingParenthesis() {
        List<Token> tokens = tokenize("id AND ( id");
        Exception exception = assertThrows(RuntimeException.class, () -> new RecursiveDescentParser(tokens).parse());
        assertTrue(exception.getMessage().contains("esperado"));
    }

    @Test
    void testInvalidUnexpectedToken() {
        List<Token> tokens = tokenize("id AND 123");
        Exception exception = assertThrows(RuntimeException.class, () -> new RecursiveDescentParser(tokens).parse());
        assertTrue(exception.getMessage().contains("esperado"));
    }

    @Test
    void testInvalidExtraInput() {
        List<Token> tokens = tokenize("id id");
        Exception exception = assertThrows(RuntimeException.class, () -> new RecursiveDescentParser(tokens).parse());
        assertTrue(exception.getMessage().contains("entrada não totalmente consumida"));
    }

    @Test
    void testEmptyInputShouldFail() {
        List<Token> tokens = tokenize("");
        Exception exception = assertThrows(RuntimeException.class, () -> new RecursiveDescentParser(tokens).parse());
        assertTrue(exception.getMessage().toLowerCase().contains("esperado"));
    }

    @Test
    void testUnterminatedStringShouldNotPassLexicalPhase() {
        List<Token> tokens = tokenize("\"unterminated string");
        Token error = tokens.stream().filter(t -> t.getType().name().equals("ERROR")).findFirst().orElse(null);
        assertNotNull(error, "Deveria detectar erro léxico em string malformada.");
    }
}
