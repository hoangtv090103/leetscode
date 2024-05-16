package com.example.leetscode.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import com.example.leetscode.model.Language;
import com.example.leetscode.service.LanguageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/languages")
public class LanguageController {
    @Value("${judge0-url-language}")
    private URI judge0UrlLanguage;

    @Value("${X-RapidAPI-Key}")
    private String X_RapidAPI_Key;

    @Value("${X-RapidAPI-Host}")
    private String X_RapidAPI_Host;

    public List<Language> getLanguagesFromJudge0() throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(judge0UrlLanguage)
                .header("X-RapidAPI-Key", X_RapidAPI_Key)
                .header("X-RapidAPI-Host", X_RapidAPI_Host)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonString = response.body();

        ObjectMapper objectMapper = new ObjectMapper();
        Language[] languages = objectMapper.readValue(jsonString, Language[].class);

        return Arrays.asList(languages);
    }

    @Autowired
    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }


    @GetMapping("/")
    public ResponseEntity<List<Language>> getAllLanguages() {
        List<Language> languages = languageService.getAllLanguages();
        return ResponseEntity.ok(languages);
    }

    // Get by id
    @GetMapping("/{id}")
    public ResponseEntity<Language> getLanguageById(@PathVariable Long id) {
        try {
            Language language = languageService.getLanguageById(id);
            if (language == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(language);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> addLanguage(@RequestBody Language language) {
        if (language.getName() == null) {
            try {
                List<Language> languages = getLanguagesFromJudge0();

                languageService.addAllLanguages(languages);
                
                return ResponseEntity.ok("Languages added successfully");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            }

        }
        languageService.addLanguage(language);
        return ResponseEntity.ok("Language added successfully");
    }

    @PutMapping("/{id}")
    public void updateLanguage(@PathVariable Long id, @RequestBody Language language) {
        languageService.updateLanguage(id, language);
    }

    @DeleteMapping("/{id}")
    public void deleteLanguage(@PathVariable Long id) {
        languageService.deleteLanguage(id);
    }
}