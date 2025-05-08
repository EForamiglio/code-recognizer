package com.pedrik.recognizer.service.lexical;

import java.util.*;

public class Lexer {
    private enum State { DEFAULT, STRING, COMMENT }

    private final String input;
    private final List<Token> tokens;
    private int position;
    private int line;
    private int column;
    private State state;

    private static final List<String> KEYWORDS = Arrays.asList(
            "if", "else", "while", "for", "return", "true", "false",
            "int", "float", "string", "boolean", "void"
    );

    public Lexer(String input) {
        this.input = input;
        this.tokens = new ArrayList<>();
        this.position = 0;
        this.line = 1;
        this.column = 1;
        this.state = State.DEFAULT;
    }

    public List<Token> tokenizeAll() {
        while (!isAtEnd()) {
            skipWhitespace();
            if (isAtEnd()) break;
            char current = peek();

            switch (state) {
                case DEFAULT:
                    if (Character.isLetter(current) || current == '_') {
                        tokenizeIdentifierOrKeyword();
                    } else if (Character.isDigit(current)) {
                        tokenizeNumber();
                    } else if (current == '"') {
                        state = State.STRING;
                        tokenizeString();
                    } else if (current == '/' && matchNext('/')) {
                        state = State.COMMENT;
                        tokenizeLineComment();
                    } else if (current == '/' && matchNext('*')) {
                        state = State.COMMENT;
                        tokenizeBlockComment();
                    } else if (isOperatorStart(current)) {
                        tokenizeOperator();
                    } else if (isSeparator(current)) {
                        tokenizeSeparator();
                    } else {
                        advance();
                    }
                    break;
                case STRING:
                    tokenizeString();
                    break;
                case COMMENT:
                    // já estamos no método tokenizeLineComment ou tokenizeBlockComment
                    break;
            }

        }
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

    private void skipWhitespace() {
        while (!isAtEnd()) {
            char c = peek();
            if (c == ' ' || c == '\t' || c == '\r') {
                advance();
            } else if (c == '\n') {
                advance();
                line++;
                column = 1;
            } else {
                break;
            }
        }
    }

    private void tokenizeIdentifierOrKeyword() {
        int start = position;
        int startColumn = column;
        while (!isAtEnd() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            advance();
        }
        String text = input.substring(start, position);
        TokenType type = KEYWORDS.contains(text) ? TokenType.KEYWORD : TokenType.IDENTIFIER;
        tokens.add(new Token(type, text, line, startColumn));
    }

    private void tokenizeNumber() {
        int start = position;
        int startColumn = column;
        boolean hasDot = false;

        while (!isAtEnd() && (Character.isDigit(peek()) || (!hasDot && peek() == '.'))) {
            if (peek() == '.') hasDot = true;
            advance();
        }

        String number = input.substring(start, position);
        tokens.add(new Token(TokenType.NUMBER, number, line, startColumn));
    }

    private void tokenizeString() {
        int startColumn = column;
        advance(); // Consumir a primeira aspa "

        int start = position;
        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n') {
                tokens.add(new Token(TokenType.ERROR, "Unterminated string", line, column));
                state = State.DEFAULT;
                return;
            }
            advance();
        }

        if (isAtEnd()) {
            tokens.add(new Token(TokenType.ERROR, "Unterminated string", line, column));
            state = State.DEFAULT;
            return;
        }

        String str = input.substring(start, position);
        advance(); // Consumir a aspa final
        tokens.add(new Token(TokenType.STRING, str, line, startColumn));
        state = State.DEFAULT;
    }

    private boolean isOperatorStart(char c) {
        return "+-*/=<>!&|".indexOf(c) != -1;
    }

    private void tokenizeOperator() {
        int startColumn = column;
        char first = peek();
        advance();
        if (!isAtEnd()) {
            char second = peek();
            String combined = "" + first + second;
            if (combined.equals("==") || combined.equals("!=") ||
                    combined.equals("<=") || combined.equals(">=") ||
                    combined.equals("&&") || combined.equals("||")) {
                advance();
                tokens.add(new Token(TokenType.OPERATOR, combined, line, startColumn));
                return;
            }
        }
        tokens.add(new Token(TokenType.OPERATOR, String.valueOf(first), line, startColumn));
    }

    private boolean isSeparator(char c) {
        return "();{},[]".indexOf(c) != -1;
    }

    private void tokenizeSeparator() {
        int startColumn = column;
        char c = peek();
        advance();
        tokens.add(new Token(TokenType.SEPARATOR, String.valueOf(c), line, startColumn));
    }

    private void tokenizeLineComment() {
        advance(); // consumir o primeiro '/'
        advance(); // consumir o segundo '/'

        int start = position;
        int startColumn = column;

        while (!isAtEnd() && peek() != '\n') {
            advance();
        }

        String comment = input.substring(start, position);
        tokens.add(new Token(TokenType.COMMENT, comment, line, startColumn));
        state = State.DEFAULT;
    }

    private void tokenizeBlockComment() {
        advance(); // consumir o primeiro '/'
        advance(); // consumir o '*'

        int start = position;
        int startColumn = column;

        while (!isAtEnd()) {
            if (peek() == '*' && matchNext('/')) {
                advance(); // consumir '*'
                advance(); // consumir '/'
                break;
            }
            if (peek() == '\n') {
                line++;
                column = 1;
            }
            advance();
        }

        if (isAtEnd()) {
            tokens.add(new Token(TokenType.ERROR, "Unterminated block comment", line, startColumn));
        } else {
            String comment = input.substring(start, position - 2); // sem o */
            tokens.add(new Token(TokenType.COMMENT, comment, line, startColumn));
        }
        state = State.DEFAULT;
    }

    private boolean matchNext(char expected) {
        if (position + 1 >= input.length()) return false;
        return input.charAt(position + 1) == expected;
    }

    private boolean isAtEnd() {
        return position >= input.length();
    }

    private char peek() {
        return input.charAt(position);
    }

    private void advance() {
        position++;
        column++;
    }

    public static void main(String[] args) {
        String code = """
        int a = 5;
        /* Comentário não fechado
        string b = "Erro";
        """;


        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenizeAll();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}

