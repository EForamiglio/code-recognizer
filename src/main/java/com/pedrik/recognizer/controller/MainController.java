package com.pedrik.recognizer.controller;

import com.pedrik.recognizer.controller.dto.InstructionDto;
import com.pedrik.recognizer.controller.dto.ParseResponseDto;
import com.pedrik.recognizer.service.analytic.RecognizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class MainController {

    @Autowired
    private RecognizerService service;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/verify")
    public ResponseEntity<ParseResponseDto> verifyInstruction(@RequestBody InstructionDto request) {
        return ResponseEntity.ok(service.verify(request.getInstruction()));
    }
}
