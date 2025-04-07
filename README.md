# Analisador Sintático com Gramática Fatorada - Recursive Descent Parser

## Visão Geral

Este projeto consiste em um analisador sintático desenvolvido em Java com Spring Boot, baseado em uma gramática livre de contexto previamente fatorada. O parser foi implementado no estilo **descendente recursivo** e está preparado para gerar a **árvore sintática** da entrada analisada.

O sistema também inclui uma interface web para que o usuário possa digitar comandos e verificar se são válidos com base na gramática definida.

---

## Funcionalidades

- Análise sintática de entradas textuais
- Geração e exibição textual da árvore sintática
- Detalhamento de mensagens de erro em caso de entrada inválida
- Interface web simples para digitação e validação de comandos
- Exibição de uma imagem com a gramática utilizada
- Testes unitários para validação da API REST
- Algoritmo que calcula os conjuntos First e Follow da gramática requisitada (utilização futura)

---

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot**
- **Thymeleaf** (para renderização do HTML)
- **JUnit 5** (para testes unitários)
- **Maven** (gerenciamento de dependências)

---

## Gramática Utilizada

A gramática fatorada para expressões lógicas é a seguinte:

```
E  → T E'
E' → AND T E' | OR T E' | ε
T  → NOT F | F
F  → ( E ) | id
```

Onde:
- `id` representa um identificador
- `AND`, `OR`, `NOT` são operadores lógicos
- `ε` representa a produção vazia

---

## Como Executar

1. Clone o repositório
2. Execute com `mvn spring-boot:run`
3. Acesse [http://localhost:8080](http://localhost:8080)
4. Digite um comando na interface e clique em "Validar"
5. Veja o resultado (válido/inválido) e a árvore sintática (se aplicável)

---

## Exemplos de Comandos

Comandos válidos:
- `id`
- `id AND id`
- `id OR (NOT id)`

Comandos inválidos:
- `AND id`
- `id AND (id OR)`

---

## Testes

Para rodar os testes:
```bash
mvn test
```

Os testes validam o endpoint `/verify` com entradas válidas e inválidas.

---

## Continuidade

O projeto está sendo expandido para incluir uma versão com **tabela LL(1)** e **pilha de análise**, além da simulação da execução da pilha.

---

## Licença

Este projeto é de uso acadêmico e não possui licença específica definida.

