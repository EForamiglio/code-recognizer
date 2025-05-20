package com.pedrik.recognizer.service.semantic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa informações semânticas associadas a um NonTerminalNode.
 * Pode ser expandido com mais campos conforme a linguagem suportar mais recursos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemanticInfo {
    private String type; // Ex: "int", "boolean", "string"
    private boolean isDeclaration; // Ex: true para 'int x;'
    private boolean isUsed; // Ex: usado em alguma expressão
    private boolean isAssigned; // Ex: recebeu valor
}
