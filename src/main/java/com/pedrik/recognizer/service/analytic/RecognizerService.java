package com.pedrik.recognizer.service.analytic;

import com.pedrik.recognizer.controller.dto.ParseResponseDto;
import com.pedrik.recognizer.service.analytic.tree.Node;
import com.pedrik.recognizer.service.lexical.Lexer;
import com.pedrik.recognizer.service.lexical.Token;
import com.pedrik.recognizer.service.semantic.SemanticAnalyzer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RecognizerService {

    public ParseResponseDto verify(final String input) {
        try {
            Lexer lexer = new Lexer(input);
            List<Token> tokenList = lexer.tokenize();

            RecursiveDescentParser parser = new RecursiveDescentParser(tokenList);
            StringBuilder treeLog = new StringBuilder();
            parser.setLogger(treeLog);
            Node root = parser.parse();

            // Suponha que os identificadores válidos estejam definidos aqui:
            Set<String> declaredIdentifiers = Set.of("velocidade", "caindo", "subindo", "ativo"); // exemplo

            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            analyzer.analyze(root);

            return new ParseResponseDto(true, treeLog.toString(), null);
        } catch (Exception e) {
            return new ParseResponseDto(false, "", e.getMessage());
        }
    }

}

