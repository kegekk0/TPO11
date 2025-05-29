package pl.pja.edu.tpo11.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pja.edu.tpo11.dto.CreateUrlDto;
import pl.pja.edu.tpo11.dto.UpdateUrlDto;
import pl.pja.edu.tpo11.dto.UrlResponseDto;
import pl.pja.edu.tpo11.exception.DuplicateUrlException;
import pl.pja.edu.tpo11.service.UrlShortenerService;

import java.net.URI;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/links")
public class ApiController {
    private final UrlShortenerService service;

    public ApiController(UrlShortenerService service) {
        this.service = service;
    }

    public ResponseEntity<?> createShortUrl(@RequestBody CreateUrlDto createUrlDto,
                                            @RequestHeader("Accept-Language") String acceptLanguage) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        try {
            service.validatePassword(createUrlDto.getPassword());
            UrlResponseDto response = service.createShortUrl(createUrlDto, locale);
            return ResponseEntity.created(URI.create("/api/links/" + response.getId()))
                    .body(response);
        } catch (DuplicateUrlException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("urlError", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("passwordError", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UrlResponseDto> getUrlInfo(@PathVariable String id) {
        UrlResponseDto response = service.getUrlInfo(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUrl(@PathVariable String id, @RequestBody UpdateUrlDto dto) {
        service.updateUrl(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUrl(@PathVariable String id, @RequestParam(required = false) String pass) {
        service.deleteUrl(id, pass);
    }
}
