package pl.pja.edu.tpo11.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ShortUrl {
    @Id
    private String id;
    private String name;
    private String targetUrl;
    private String redirectUrl;
    private int visits;
    private String password;

    // Constructors
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

    // Getters and Setters
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
