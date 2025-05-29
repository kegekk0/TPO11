package pl.pja.edu.tpo11.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pja.edu.tpo11.model.ShortUrl;
import java.util.List;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, String> {
    boolean existsByTargetUrl(String targetUrl);
    List<ShortUrl> findAll();
}