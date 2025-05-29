package pl.pja.edu.tpo11.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

@Entity
public class ShortUrl {
    @Id
    private String id;

    @Size(min = 5, max = 20, message = "{validation.name.size}")
    private String name;

    @URL(message = "{validation.url.invalid}")
    @Pattern(regexp = "^https://.*", message = "{validation.url.https}")
    @Column(unique = true)
    private String targetUrl;

    private String redirectUrl;
    private int visits;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=(.*[A-Z]){2})(?=(.*\\d){3})(?=(.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]){4}).{10,}$",
            message = "{validation.password.pattern}"
    )
    private String password;

    public ShortUrl() {
    }

    public ShortUrl(String id, String name, String targetUrl, String redirectUrl, int visits, String password) {
        this.id = id;
        this.name = name;
        this.targetUrl = targetUrl;
        this.redirectUrl = redirectUrl;
        this.visits = visits;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
