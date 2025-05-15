package com.pedrik.recognizer.service.lexical;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private static final String TOKEN_REGEX =
            "(?<WHITESPACE>[ \t\r\f]+)|" +
                    "(?<NEWLINE>\n)|" +
                    "(?<COMMENTLINE>//[^\n]*)|" +
                    "(?<COMMENTBLOCK>/\\*(.|\\R)*?\\*/)|" +
                    "(?<STRING>\"([^\"\\\\]|\\\\.)*\")|" +
                    "(?<KEYWORD>\\b(?:if|else|while|return)\\b)|" +
                    "(?<IDENTIFIER>\\b[a-zA-Z_][a-zA-Z_0-9]*\\b)|" +
                    "(?<NUMBER>\\b\\d+\\b)|" +
                    "(?<OPERATOR>==|!=|<=|>=|\\+|-|\\*|/|=|<|>)|" +
                    "(?<DELIMITER>[{}()\\[\\];,])";

    private final String input;
    private final List<Token> tokens = new ArrayList<>();
    private final Pattern tokenPatterns = Pattern.compile(TOKEN_REGEX);
    private int index = 0;
    private int line = 1;
    private int column = 1;

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        while (index < input.length()) {
            char currentChar = input.charAt(index);

            // Unterminated string check
            if (currentChar == '"') {
                int start = index;
                int colStart = column;

                index++;
                column++;
                boolean closed = false;

                while (index < input.length()) {
                    char ch = input.charAt(index);
                    if (ch == '"') {
                        closed = true;
                        break;
                    }
                    if (ch == '\n') {
                        line++;
                        column = 1;
                    } else {
                        column++;
                    }
                    index++;
                }

                if (!closed) {
                    String lexeme = input.substring(start);
                    tokens.add(new Token(TokenType.ERROR, "Unterminated string: " + lexeme, line, colStart));
                    break;
                }

                // consume closing quote
                index++;
                column++;
                continue;
            }

            // Unterminated block comment check
            if (currentChar == '/' && index + 1 < input.length() && input.charAt(index + 1) == '*') {
                int start = index;
                int colStart = column;

                index += 2;
                column += 2;
                boolean closed = false;

                while (index + 1 < input.length()) {
                    if (input.charAt(index) == '*' && input.charAt(index + 1) == '/') {
                        closed = true;
                        break;
                    }
                    if (input.charAt(index) == '\n') {
                        line++;
                        column = 1;
                    } else {
                        column++;
                    }
                    index++;
                }

                if (!closed) {
                    String lexeme = input.substring(start);
                    tokens.add(new Token(TokenType.ERROR, "Unterminated block comment: " + lexeme, line, colStart));
                    break;
                }

                // consume closing */
                index += 2;
                column += 2;
                continue;
            }

            Matcher matcher = tokenPatterns.matcher(input);
            matcher.region(index, input.length());

            if (matcher.lookingAt()) {
                String lexeme = matcher.group();
                Token token = null;

                if (matcher.group("WHITESPACE") != null || matcher.group("NEWLINE") != null) {
                    if ("\n".equals(lexeme)) {
                        line++;
                        column = 1;
                    } else {
                        column += lexeme.length();
                    }
                    index += lexeme.length();
                    continue;
                }

                if (matcher.group("LINE_COMMENT") != null) {
                    String comment = lexeme.substring(2).trim();
                    token = new Token(TokenType.COMMENT_LINE, comment, line, column);
                } else if (matcher.group("BLOCK_COMMENT") != null) {
                    String comment = lexeme.substring(2, lexeme.length() - 2).trim();
                    token = new Token(TokenType.COMMENT_BLOCK, comment, line, column);
                } else if (matcher.group("STRING") != null) {
                    String clean = lexeme.substring(1, lexeme.length() - 1);
                    token = new Token(TokenType.STRING, clean, line, column);
                } else if (matcher.group("KEYWORD") != null) {
                    token = new Token(TokenType.KEYWORD, lexeme, line, column);
                } else if (matcher.group("IDENTIFIER") != null) {
                    token = new Token(TokenType.IDENTIFIER, lexeme, line, column);
                } else if (matcher.group("NUMBER") != null) {
                    token = new Token(TokenType.NUMBER, lexeme, line, column);
                } else if (matcher.group("OPERATOR") != null) {
                    token = new Token(TokenType.OPERATOR, lexeme, line, column);
                } else if (matcher.group("DELIMITER") != null) {
                    token = new Token(TokenType.DELIMITER, lexeme, line, column);
                }

                if (token != null) {
                    tokens.add(token);
                }

                index += lexeme.length();
                column += lexeme.length();
            } else {
                tokens.add(new Token(TokenType.ERROR, "Invalid character: " + currentChar, line, column));
                index++;
                column++;
            }
        }

        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

    private Token currentToken;

    public Token nextToken() {
        if (position >= input.length()) {
            return new Token(TokenType.EOF, "", position);
        }

        Matcher matcher = TOKEN_PATTERN.matcher(input);
        matcher.region(position, input.length());

        if (matcher.lookingAt()) {
            for (TokenType type : TokenType.values()) {
                String value = matcher.group(type.name());
                if (value != null) {
                    position = matcher.end();
                    String lexeme = value;

                    if (type == TokenType.STRING && !lexeme.endsWith("\"")) {
                        return new Token(TokenType.ERROR, "Unterminated string: " + lexeme, matcher.start());
                    }

                    if (type == TokenType.COMMENT_BLOCK && !lexeme.endsWith("*/")) {
                        return new Token(TokenType.ERROR, "Unterminated block comment: " + lexeme, matcher.start());
                    }

                    // Remover aspas de strings
                    if (type == TokenType.STRING) {
                        lexeme = lexeme.substring(1, lexeme.length() - 1);
                    }

                    // Remover prefixo de comentários
                    if (type == TokenType.COMMENT_LINE) {
                        lexeme = lexeme.substring(2).trim();
                    }
                    if (type == TokenType.COMMENT_BLOCK) {
                        lexeme = lexeme.substring(2, lexeme.length() - 2).trim();
                    }

                    // Ignorar tokens irrelevantes
                    if (type == TokenType.WHITESPACE || type == TokenType.NEWLINE) {
                        return nextToken();
                    }

                    return new Token(type, lexeme, matcher.start());
                }
            }
        }

        // Se não casou nenhum token válido
        String invalidChar = String.valueOf(input.charAt(position));
        position++;
        return new Token(TokenType.ERROR, "Invalid character: " + invalidChar, position - 1);
    }

    public List<Token> tokenizeAll() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        do {
            token = nextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);
        return tokens;
    }

}
