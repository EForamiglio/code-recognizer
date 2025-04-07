package com.pedrik.recognizer.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParseResponseDto {
    private boolean valid;
    private String tree;
    private String errorMessage;
}
