package com.pedrik.recognizer.service.analytic.tree;

import com.pedrik.recognizer.service.lexical.Token;
import lombok.Getter;

@Getter
public class TerminalNode extends Node {
    private final Token token;

    public TerminalNode(Token token) {
        this.token = token;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + token.getLexeme() + " <" + token.getType() + ">");
    }
}
