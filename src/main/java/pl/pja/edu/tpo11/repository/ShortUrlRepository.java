package pl.pja.edu.tpo11.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pja.edu.tpo11.model.ShortUrl;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, String> {
}
