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
                    "(?<KEYWORD>\\b(?:if|else|while|return|int|string|boolean|true|false)\\b)|" +
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

                int end = index;
                String content = input.substring(start + 1, end); // sem aspas
                tokens.add(new Token(TokenType.STRING, content, line, colStart));

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

                int end = index;
                String comment = input.substring(start + 2, end).trim(); // sem /* */
                tokens.add(new Token(TokenType.COMMENT_BLOCK, comment, line, colStart));

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

                if (matcher.group("COMMENTLINE") != null) {
                    String comment = lexeme.substring(2).trim();
                    token = new Token(TokenType.COMMENT_LINE, comment, line, column);
                } else if (matcher.group("COMMENTBLOCK") != null) {
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

}
