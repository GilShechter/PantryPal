package com.pantrypal.pantrypal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pantrypal.pantrypal.model.IdentifierResponse;
import com.pantrypal.pantrypal.model.IngredientList;
import com.pantrypal.pantrypal.util.AWSService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/identifier")
public class IdentifierController {

    @Value("${identifier.api.target.url}")
    private String targetUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AWSService awsService;

    @Autowired
    private ObjectMapper om;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> identify(@RequestParam String imageUrl) {
        try{
            HttpEntity<String> request = BuildRequest(imageUrl);
            ResponseEntity<IdentifierResponse> response = restTemplate.postForEntity(targetUrl, request, IdentifierResponse.class);
            String content = response.getBody().getMessage().getContent();
            IngredientList ingredients = new IngredientList(content);
            return ResponseEntity.ok(ingredients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @RequestMapping(value = "imageFile", method = RequestMethod.POST)
    public ResponseEntity<?> uploadImage(@RequestParam("imageFile") MultipartFile imageFile) {
        String bucketPath = "apps/gils/" + imageFile.getOriginalFilename();
        awsService.putInBucket(imageFile, bucketPath);
        String link = awsService.generateLink(bucketPath);
        return ResponseEntity.ok(link);
    }

    @NotNull
    private HttpEntity<String> BuildRequest(String imageUrl) throws JsonProcessingException {
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("image_url", imageUrl);
        String jsonString = om.writeValueAsString(jsonMap);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(jsonString, headers);
    }
}
