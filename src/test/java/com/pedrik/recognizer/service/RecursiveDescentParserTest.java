package com.pedrik.recognizer.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RecursiveDescentParserTest {

    @Test
    void testValidSimpleExpression() {
        assertDoesNotThrow(() -> {
            RecursiveDescentParser parser = new RecursiveDescentParser("id");
            parser.parse();
        });
    }

    @Test
    void testValidExpressionWithAndOr() {
        assertDoesNotThrow(() -> {
            RecursiveDescentParser parser = new RecursiveDescentParser("id AND id OR id");
            parser.parse();
        });
    }

    @Test
    void testValidExpressionWithNotAndParentheses() {
        assertDoesNotThrow(() -> {
            RecursiveDescentParser parser = new RecursiveDescentParser("id AND ( NOT id )");
            parser.parse();
        });
    }

    @Test
    void testInvalidMissingClosingParenthesis() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            RecursiveDescentParser parser = new RecursiveDescentParser("id AND ( id");
            parser.parse();
        });

        String message = exception.getMessage();
        assertTrue(message.contains("esperado"));
    }

    @Test
    void testInvalidUnexpectedToken() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            RecursiveDescentParser parser = new RecursiveDescentParser("id AND 123");
            parser.parse();
        });

        String message = exception.getMessage();
        assertTrue(message.contains("esperado"));
    }

    @Test
    void testInvalidExtraInput() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            RecursiveDescentParser parser = new RecursiveDescentParser("id id");
            parser.parse();
        });

        assertTrue(exception.getMessage().contains("entrada não totalmente consumida"));
    }
}
