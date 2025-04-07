package com.pedrik.recognizer.controller;

import com.pedrik.recognizer.controller.dto.InstructionDto;
import com.pedrik.recognizer.controller.dto.ParseResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MainControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testVerifyValidInstruction() {
        InstructionDto dto = new InstructionDto();
        dto.setInstruction("id AND id");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<InstructionDto> request = new HttpEntity<>(dto, headers);

        ResponseEntity<ParseResponseDto> response = restTemplate.postForEntity(
                "/verify",
                request,
                ParseResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isValid());
    }

    @Test
    public void testVerifyInvalidInstruction() {
        InstructionDto dto = new InstructionDto();
        dto.setInstruction("id AND");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<InstructionDto> request = new HttpEntity<>(dto, headers);

        ResponseEntity<ParseResponseDto> response = restTemplate.postForEntity(
                "/verify",
                request,
                ParseResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isValid());
    }
}
