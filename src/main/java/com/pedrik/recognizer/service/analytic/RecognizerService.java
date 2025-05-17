package com.pedrik.recognizer.service.analytic;

import com.pedrik.recognizer.controller.dto.ParseResponseDto;
import com.pedrik.recognizer.service.lexical.Lexer;
import com.pedrik.recognizer.service.lexical.Token;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecognizerService {

    public ParseResponseDto verify(final String input) {
        try {
            Lexer lexer = new Lexer(input);
            List<Token> tokenList = lexer.tokenize();

            RecursiveDescentParser parser = new RecursiveDescentParser(tokenList);
            StringBuilder treeLog = new StringBuilder();
            parser.setLogger(treeLog);
            parser.parse();
            return new ParseResponseDto(true, treeLog.toString(), null);
        } catch (Exception e) {
            return new ParseResponseDto(false, "", e.getMessage());
        }
    }
}

