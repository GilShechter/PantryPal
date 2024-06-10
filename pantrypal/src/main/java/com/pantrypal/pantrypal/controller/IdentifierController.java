package com.pantrypal.pantrypal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pantrypal.pantrypal.model.IdentifierRequest;
import com.pantrypal.pantrypal.model.IdentifierResponse;
import com.pantrypal.pantrypal.model.ImageUploadRequest;
import com.pantrypal.pantrypal.model.IngredientList;
import com.pantrypal.pantrypal.util.AWSService;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
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

    @PostMapping(value = "")
    public ResponseEntity<?> identify(@RequestBody IdentifierRequest request) {
        try{
            String imageUrl = request.getImageUrl();
            System.out.println("Identifying image: " + imageUrl);
            HttpEntity<String> requestBody = BuildRequest(imageUrl);
            ResponseEntity<IdentifierResponse> response = restTemplate.postForEntity(targetUrl, requestBody, IdentifierResponse.class);
            String content = response.getBody().getMessage().getContent();
            IngredientList ingredients = new IngredientList(content);
            return ResponseEntity.ok(ingredients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping(value = "imageFile")
    public ResponseEntity<?> uploadImage(@RequestBody ImageUploadRequest uploadRequest) throws IOException {
        byte[] imageBytes = Base64.decodeBase64(uploadRequest.getImageFile());
        InputStream inputStream = new ByteArrayInputStream(imageBytes);
        MultipartFile imageFile = new MockMultipartFile("imageFile", inputStream);

        String bucketPath = "apps/gils/" + imageFile.getOriginalFilename();
        awsService.putInBucket(imageFile, bucketPath);
        String link = awsService.generateLink(bucketPath);
        return ResponseEntity.ok(Collections.singletonMap("imageUrl", link));
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
