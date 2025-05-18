package com.pedrik.recognizer.service.lexical;

import java.util.regex.Pattern;

public enum TokenType {
    TYPE("\\b(int|string|boolean)\\b"),
    KEYWORD("\\b(if|else|while|return)\\b"),
    BOOLEAN_LITERAL("\\b(true|false)\\b"),
    LOGICAL_OPERATOR("\\b(AND|OR|NOT)\\b"),
    IDENTIFIER("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b"),
    NUMBER("\\b\\d+\\b"),
    STRING("\"[^\"]*\""),
    ASSIGN("="),
    OPERATOR("==|!=|<=|>=|<|>|\\+|-|\\*|/"),
    DELIMITER("[(){};,]"),
    COMMENT_LINE("//[^\\n]*"),
    COMMENT_BLOCK("/\\*(.|\\R)*?\\*/"),
    WHITESPACE("[ \t\r\f]+"),
    NEWLINE("\\n"),
    ERROR("."), // fallback para erro
    EOF(""); // adicionado manualmente

    public final Pattern pattern;

    TokenType(String regex) {
        this.pattern = Pattern.compile(regex, Pattern.DOTALL);
    }
}
