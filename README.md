# Recursive Descent Parser with Factored Grammar

## Overview

This project is a syntax analyzer built in Java using Spring Boot, based on a factored context-free grammar. The parser follows a **recursive descent** approach and generates a **parse tree** for the input string.

A simple web interface is provided so users can input commands and check if they are valid according to the defined grammar.

---

## Features

- Syntactic analysis of textual inputs  
- Generation and textual display of the parse tree  
- Detailed error messages for invalid input  
- Clean web interface for command input and validation  
- Embedded image showing the grammar used  
- Unit tests to validate the REST API  

---

## Technologies Used

- **Java 21**  
- **Spring Boot**  
- **Thymeleaf** (for HTML rendering)  
- **JUnit 5** (for unit testing)  
- **Maven** (for dependency management)  

---

## Grammar Used

The factored grammar for logical expressions is as follows:

```
E  → T E'
E' → AND T E' | OR T E' | ε
T  → NOT F | F
F  → ( E ) | id
```

Where:
- `id` represents an identifier  
- `AND`, `OR`, `NOT` are logical operators  
- `ε` represents the empty production  

---

## FIRST and FOLLOW Sets

The project includes an implementation of the **FIRST** and **FOLLOW** set computation algorithms for all non-terminals.

These sets are essential for:
- Grammar validation
- Building LL(1) parsing tables
- Deciding which production to use during parsing

#### Example with the current grammar:

```
FIRST(E)  = { id, (, NOT }
FOLLOW(E) = { $, ) }
```

---

## How to Run

1. Clone the repository  
2. Run using:  
   ```bash
   mvn spring-boot:run
   ```  
3. Open [http://localhost:8080](http://localhost:8080)  
4. Type a command and click "Validate"  
5. Check the result (valid/invalid) and view the parse tree if applicable  

---

## Example Commands

**Valid:**
- `id`
- `id AND id`
- `id OR ( NOT id )`
- `( id ) with spaces`

**Invalid:**
- `AND id`
- `id AND (id OR)`
- `(id) no spaces`

**Obs:**
- spaces are the delimiter for the tokens

---

## Tests

To run unit tests:
```bash
mvn test
```

The tests cover the `/verify` endpoint with valid and invalid inputs.

---

## Next Steps

The project is being extended to include an **LL(1) parser** using a **parsing table** and **analysis stack**, along with a stack execution simulator.

---

## License

This project is intended for academic use and does not yet have a defined license.

---

# Analisador Sintático com Gramática Fatorada - Recursive Descent Parser (PT-BR)

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
- `id OR ( NOT id )`
- `( id ) com espaços`

Comandos inválidos:
- `AND id`
- `id AND (id OR)`
- `(id) sem espaços`

**Obs:**
- espaços são os delimitadores dos tokens
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

