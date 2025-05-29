package pl.pja.edu.tpo11.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pja.edu.tpo11.service.UrlShortenerService;

@RestController
@RequestMapping("/red")
public class RedirectController {
    private final UrlShortenerService service;

    public RedirectController(UrlShortenerService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Void> redirect(@PathVariable String id) {
        String targetUrl = service.getTargetUrl(id);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", targetUrl)
                .build();
    }
}