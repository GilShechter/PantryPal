package com.pantrypal.pantrypal.controller;

import com.pantrypal.pantrypal.model.IdentifierResponse;
import com.pantrypal.pantrypal.model.IngredientList;
import com.pantrypal.pantrypal.util.AWSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/identifier")
public class IdentifierController {

    @Value("${identifier.api.target.url}")
    private String targetUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AWSService awsService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> identify(@RequestParam String imageUrl) {
        try{
            String url = targetUrl + imageUrl;
            ResponseEntity<IdentifierResponse> response = restTemplate.postForEntity(url, null, IdentifierResponse.class);
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
}
