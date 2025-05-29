package pl.pja.edu.tpo11.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pja.edu.tpo11.dto.*;
import pl.pja.edu.tpo11.exception.DuplicateUrlException;
import pl.pja.edu.tpo11.exception.WrongPasswordException;
import pl.pja.edu.tpo11.model.ShortUrl;
import pl.pja.edu.tpo11.repository.ShortUrlRepository;
import java.util.*;

@Service
@Transactional
public class UrlShortenerService {
    private final ShortUrlRepository repository;
    private final Random random = new Random();
    private final MessageSource messageSource;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlShortenerService(ShortUrlRepository repository, MessageSource messageSource) {
        this.repository = repository;
        this.messageSource = messageSource;
    }

    public UrlResponseDto createShortUrl(CreateUrlDto dto, Locale locale) {
        try {
            if (repository.existsByTargetUrl(dto.getTargetUrl())) {
                throw new DuplicateUrlException(
                        messageSource.getMessage("error.url.duplicate", null, locale)
                );
            }

            String id = generateRandomId();
            String redirectUrl = baseUrl + "/red/" + id;

            ShortUrl shortUrl = new ShortUrl(
                    id,
                    dto.getName(),
                    dto.getTargetUrl(),
                    redirectUrl,
                    0,
                    dto.getPassword() != null && !dto.getPassword().trim().isEmpty() ? dto.getPassword() : null
            );

            System.out.println("Attempting to save ShortUrl with ID: " + id);
            ShortUrl saved = repository.saveAndFlush(shortUrl);
            System.out.println("Successfully saved and flushed ShortUrl with ID: " + saved.getId());

            return new UrlResponseDto(
                    saved.getId(),
                    saved.getName(),
                    saved.getTargetUrl(),
                    saved.getRedirectUrl(),
                    saved.getVisits(),
                    saved.getPassword()
            );
        } catch (Exception e) {
            System.err.println("Error creating short URL: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public UrlResponseDto getUrlInfo(String id) {
        ShortUrl shortUrl = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        return new UrlResponseDto(
                shortUrl.getId(),
                shortUrl.getName(),
                shortUrl.getTargetUrl(),
                shortUrl.getRedirectUrl(),
                shortUrl.getVisits(),
                shortUrl.getPassword()
        );
    }

    public String getTargetUrl(String id) {
        try {
            ShortUrl shortUrl = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("URL not found"));

            System.out.println("Found URL: " + shortUrl.getId() + ", visits: " + shortUrl.getVisits());

            shortUrl.setVisits(shortUrl.getVisits() + 1);
            System.out.println("Updating visits to: " + shortUrl.getVisits());

            ShortUrl saved = repository.saveAndFlush(shortUrl);
            System.out.println("Successfully saved URL with visits: " + saved.getVisits());

            return shortUrl.getTargetUrl();
        } catch (Exception e) {
            System.err.println("Error in getTargetUrl: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void updateUrl(String id, UpdateUrlDto dto) {
        ShortUrl shortUrl = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        if (shortUrl.getPassword() != null) {
            if (dto.getPassword() == null || !dto.getPassword().equals(shortUrl.getPassword())) {
                throw new WrongPasswordException();
            }
        }

        if (dto.getName() != null) {
            shortUrl.setName(dto.getName());
        }

        if (dto.getTargetUrl() != null) {
            shortUrl.setTargetUrl(dto.getTargetUrl());
        }

        repository.save(shortUrl);
    }

    public void deleteUrl(String id, String password) {
        ShortUrl shortUrl = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        if (shortUrl.getPassword() != null) {
            if (password == null || !password.equals(shortUrl.getPassword())) {
                throw new WrongPasswordException();
            }
        }

        repository.delete(shortUrl);
    }

    @Transactional(readOnly = true)
    public List<UrlResponseDto> getAllUrls() {
        List<ShortUrl> shortUrls = repository.findAll();
        List<UrlResponseDto> urlResponseDtos = new ArrayList<>();

        for (ShortUrl shortUrl : shortUrls) {
            UrlResponseDto dto = new UrlResponseDto(
                    shortUrl.getId(),
                    shortUrl.getName(),
                    shortUrl.getTargetUrl(),
                    shortUrl.getRedirectUrl(),
                    shortUrl.getVisits(),
                    shortUrl.getPassword()
            );
            urlResponseDtos.add(dto);
        }

        return urlResponseDtos;
    }

    private String generateRandomId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 7; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    public void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return;
        }

        List<String> errors = new ArrayList<>();

        if (!password.matches(".*[a-z].*")) {
            errors.add("at least one lowercase letter");
        }

        long upperCaseCount = password.chars().filter(Character::isUpperCase).count();
        if (upperCaseCount < 2) {
            errors.add("at least two uppercase letters (missing " + (2 - upperCaseCount) + ")");
        }

        long digitCount = password.chars().filter(Character::isDigit).count();
        if (digitCount < 3) {
            errors.add("at least three digits (missing " + (3 - digitCount) + ")");
        }

        long specialCharCount = password.replaceAll("[a-zA-Z0-9]", "").length();
        if (specialCharCount < 4) {
            errors.add("at least four special characters (missing " + (4 - specialCharCount) + ")");
        }

        if (password.length() < 10) {
            errors.add("minimum length of 10 characters (missing " + (10 - password.length()) + ")");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Invalid password. Must contain: " + String.join(", ", errors));
        }
    }

}