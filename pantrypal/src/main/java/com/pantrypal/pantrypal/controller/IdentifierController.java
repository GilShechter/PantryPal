package com.pantrypal.pantrypal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/identifier")
public class IdentifierController {

    @Value("${api.target.url}")
    private String targetUrl;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> identify(@RequestParam String imageUrl) throws UnsupportedEncodingException {
        try{
            String url = targetUrl + imageUrl;
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            // Handle exceptions, possibly returning an appropriate HTTP status
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
