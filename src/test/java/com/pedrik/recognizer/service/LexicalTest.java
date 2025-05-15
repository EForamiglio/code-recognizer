package com.pedrik.recognizer.service;

import com.pedrik.recognizer.service.lexical.Lexer;
import com.pedrik.recognizer.service.lexical.Token;
import com.pedrik.recognizer.service.lexical.TokenType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    private List<Token> tokenizeAll(Lexer lexer) {
        List<Token> tokens = new ArrayList<>();
        Token token;
        do {
            token = lexer.nextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);
        return tokens;
    }

    private Token assertToken(List<Token> tokens, int index, TokenType expectedType, String expectedLexeme) {
        Token token = tokens.get(index);
        assertEquals(expectedType, token.getType(), "Token type mismatch at index " + index);
        assertEquals(expectedLexeme, token.getLexeme(), "Lexeme mismatch at index " + index);
        return token;
    }

    @Test
    void testKeywordsIdentifiersAndOperators() {
        String input = "int idade = 30;\nif (idade >= 18) { return true; }";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = tokenizeAll(lexer);

        assertToken(tokens, 0, TokenType.KEYWORD, "int");
        assertToken(tokens, 1, TokenType.IDENTIFIER, "idade");
        assertToken(tokens, 2, TokenType.OPERATOR, "=");
        assertToken(tokens, 3, TokenType.NUMBER, "30");
        assertToken(tokens, 4, TokenType.DELIMITER, ";");
        assertToken(tokens, 5, TokenType.KEYWORD, "if");
        assertToken(tokens, 6, TokenType.DELIMITER, "(");
        assertToken(tokens, 7, TokenType.IDENTIFIER, "idade");
        assertToken(tokens, 8, TokenType.OPERATOR, ">=");
        assertToken(tokens, 9, TokenType.NUMBER, "18");
        assertToken(tokens, 10, TokenType.DELIMITER, ")");
        assertToken(tokens, 11, TokenType.DELIMITER, "{");
        assertToken(tokens, 12, TokenType.KEYWORD, "return");
        assertToken(tokens, 13, TokenType.KEYWORD, "true");
        assertToken(tokens, 14, TokenType.DELIMITER, ";");
        assertToken(tokens, 15, TokenType.DELIMITER, "}");
        assertToken(tokens, 16, TokenType.EOF, "");
    }

    @Test
    void testStringLiterals() {
        String input = "string nome = \"João\";";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = tokenizeAll(lexer);

        assertToken(tokens, 0, TokenType.KEYWORD, "string");
        assertToken(tokens, 1, TokenType.IDENTIFIER, "nome");
        assertToken(tokens, 2, TokenType.OPERATOR, "=");
        assertToken(tokens, 3, TokenType.STRING, "João");
        assertToken(tokens, 4, TokenType.DELIMITER, ";");
        assertToken(tokens, 5, TokenType.EOF, "");
    }

    @Test
    void testLineComment() {
        String input = "// isso é um comentário";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = tokenizeAll(lexer);

        assertToken(tokens, 0, TokenType.COMMENT_LINE, "isso é um comentário");
        assertToken(tokens, 1, TokenType.EOF, "");
    }

    @Test
    void testBlockComment() {
        String input = "/* bloco \n comentário */";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = tokenizeAll(lexer);

        assertToken(tokens, 0, TokenType.COMMENT_BLOCK, "bloco \n comentário");
        assertToken(tokens, 1, TokenType.EOF, "");
    }

    @Test
    void testUnterminatedString() {
        String input = "string erro = \"incompleto;";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = tokenizeAll(lexer);

        Token errorToken = tokens.stream().filter(t -> t.getType() == TokenType.ERROR).findFirst().orElse(null);
        assertNotNull(errorToken, "Expected an ERROR token");
        assertTrue(errorToken.getLexeme().contains("Unterminated string"));
    }

    @Test
    void testUnterminatedBlockComment() {
        String input = "/* sem fim";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = tokenizeAll(lexer);

        Token errorToken = tokens.stream().filter(t -> t.getType() == TokenType.ERROR).findFirst().orElse(null);
        assertNotNull(errorToken, "Expected an ERROR token");
        assertTrue(errorToken.getLexeme().contains("Unterminated block comment"));
    }

    @Test
    void testInvalidCharacter() {
        String input = "int x = 10 @;";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = tokenizeAll(lexer);

        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.ERROR), "Expected at least one ERROR token");
    }
}
