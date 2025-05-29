package pl.pja.edu.tpo11.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.pja.edu.tpo11.dto.UrlResponseDto;
import pl.pja.edu.tpo11.model.ShortUrl;
import pl.pja.edu.tpo11.repository.ShortUrlRepository;

import java.util.Random;

@Service
public class UrlShortenerService {
    private final ShortUrlRepository repository;
    private final Random random = new Random();

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlShortenerService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    public UrlResponseDto createShortUrl(CreateUrlDto dto) {
        String id = generateRandomId(10);
        String redirectUrl = baseUrl + "/red/" + id;

        ShortUrl shortUrl = new ShortUrl(
                id,
                dto.getName(),
                dto.getTargetUrl(),
                redirectUrl,
                0,
                dto.getPassword()
        );

        repository.save(shortUrl);

        return new UrlResponseDto(
                shortUrl.getId(),
                shortUrl.getName(),
                shortUrl.getTargetUrl(),
                shortUrl.getRedirectUrl(),
                shortUrl.getVisits()
        );
    }

    public UrlResponseDto getUrlInfo(String id) {
        ShortUrl shortUrl = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        return new UrlResponseDto(
                shortUrl.getId(),
                shortUrl.getName(),
                shortUrl.getTargetUrl(),
                shortUrl.getRedirectUrl(),
                shortUrl.getVisits()
        );
    }

    public String getTargetUrl(String id) {
        ShortUrl shortUrl = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        shortUrl.setVisits(shortUrl.getVisits() + 1);
        repository.save(shortUrl);

        return shortUrl.getTargetUrl();
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

    private String generateRandomId(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}
