package com.pedrik.recognizer.service.lexical;

import java.util.regex.Pattern;

public enum TokenType {
    KEYWORD("(?i)\\b(if|else|while|for|return|true|false|int|float|string|boolean|void)\\b"),
    IDENTIFIER("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b"),
    NUMBER("\\b\\d+(\\.\\d+)?\\b"),
    STRING("\"([^\"\\\\]|\\\\.)*\""),
    OPERATOR("==|!=|<=|>=|&&|\\|\\||[+\\-*/=<>!]"),
    DELIMITER("[(){};,\\[\\]]"),
    COMMENT_LINE("//[^\\n]*"),
    COMMENT_BLOCK("/\\*.*?\\*/"),
    WHITESPACE("[ \t\r\n]+"),
    ERROR("."), // fallback para erro
    EOF(""); // sem regex, é adicionado manualmente


    public final Pattern pattern;

    TokenType(String regex) {
        this.pattern = Pattern.compile(regex, Pattern.DOTALL);
    }
}
