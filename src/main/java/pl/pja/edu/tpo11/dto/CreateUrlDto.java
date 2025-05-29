package pl.pja.edu.tpo11.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateUrlDto {
    @NotBlank
    private String name;

    @NotBlank
    private String targetUrl;

    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
