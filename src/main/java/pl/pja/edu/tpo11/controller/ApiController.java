package pl.pja.edu.tpo11.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pja.edu.tpo11.dto.CreateUrlDto;
import pl.pja.edu.tpo11.dto.UpdateUrlDto;
import pl.pja.edu.tpo11.dto.UrlResponseDto;
import pl.pja.edu.tpo11.service.UrlShortenerService;

@RestController
@RequestMapping("/api/links")
public class ApiController {
    private final UrlShortenerService service;

    public ApiController(UrlShortenerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UrlResponseDto> createShortUrl(@RequestBody CreateUrlDto dto, HttpServletRequest request) {
        UrlResponseDto response = service.createShortUrl(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", request.getRequestURL() + "/" + response.getId())
                .body(response);
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
