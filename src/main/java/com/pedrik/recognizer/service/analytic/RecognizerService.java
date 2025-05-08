package com.pedrik.recognizer.service.analytic;

import com.pedrik.recognizer.controller.dto.ParseResponseDto;
import org.springframework.stereotype.Service;

@Service
public class RecognizerService {

    public ParseResponseDto verify(final String input) {
        try {
            RecursiveDescentParser parser = new RecursiveDescentParser(input);
            StringBuilder treeLog = new StringBuilder();
            parser.setLogger(treeLog); // <-- adicionamos esse recurso
            parser.parse();
            return new ParseResponseDto(true, treeLog.toString(), null);
        } catch (Exception e) {
            return new ParseResponseDto(false, "", e.getMessage());
        }
    }
}

