package pl.pja.edu.tpo11.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import pl.pja.edu.tpo11.dto.*;
import pl.pja.edu.tpo11.exception.*;
import pl.pja.edu.tpo11.service.UrlShortenerService;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/web")
public class WebController {
    private final UrlShortenerService service;
    private final MessageSource messageSource;

    public WebController(UrlShortenerService service, MessageSource messageSource) {
        this.service = service;
        this.messageSource = messageSource;
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createUrlDto", new CreateUrlDto());
        return "create";
    }

    @PostMapping("/create")
    public String createShortUrl(@Valid @ModelAttribute CreateUrlDto createUrlDto,
                                 BindingResult result,
                                 Model model,
                                 Locale locale) {
        try {
            service.validatePassword(createUrlDto.getPassword());

            if (result.hasErrors()) {
                return "create";
            }

            UrlResponseDto response = service.createShortUrl(createUrlDto, locale);
            return "redirect:/web/info/" + response.getId();
        } catch (DuplicateUrlException e) {
            model.addAttribute("urlError", e.getMessage());
            return "create";
        } catch (IllegalArgumentException e) {
            model.addAttribute("passwordError", e.getMessage());
            return "create";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "create";
        }
    }

    @GetMapping("/info/{id}")
    public String showUrlInfo(@PathVariable String id, Model model) {
        try {
            UrlResponseDto urlInfo = service.getUrlInfo(id);
            model.addAttribute("shortUrl", urlInfo);
            return "info";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        try {
            UrlResponseDto urlInfo = service.getUrlInfo(id);
            UpdateUrlDto updateUrlDto = new UpdateUrlDto();
            updateUrlDto.setName(urlInfo.getName());
            updateUrlDto.setTargetUrl(urlInfo.getTargetUrl());
            model.addAttribute("updateUrlDto", updateUrlDto);
            model.addAttribute("id", id);
            return "edit";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateUrl(@PathVariable String id,
                            @Valid @ModelAttribute UpdateUrlDto updateUrlDto,
                            BindingResult result,
                            Model model,
                            Locale locale) {
        if (result.hasErrors()) {
            model.addAttribute("id", id);
            return "edit";
        }

        try {
            service.updateUrl(id, updateUrlDto);
            return "redirect:/web/info/" + id;
        } catch (WrongPasswordException e) {
            model.addAttribute("error", messageSource.getMessage("error.wrong.password", null, locale));
            model.addAttribute("id", id);
            return "edit";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("id", id);
            return "edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteUrl(@PathVariable String id,
                            @RequestParam(required = false) String password,
                            RedirectAttributes redirectAttributes,
                            Locale locale) {
        try {
            service.deleteUrl(id, password);
            redirectAttributes.addFlashAttribute("success",
                    messageSource.getMessage("url.deleted", null, locale));
        } catch (WrongPasswordException e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("error.wrong.password", null, locale));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/all";
    }

    @GetMapping("/change-language")
    public String changeLanguage(@RequestParam String lang,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver != null) {
            localeResolver.setLocale(request, response, Locale.forLanguageTag(lang));
        }

        String referer = request.getHeader("Referer");
        if (referer == null) {
            referer = "/web/create";
        }

        String baseUrl = referer.split("\\?")[0];
        return "redirect:" + baseUrl;
    }

    @GetMapping("/all")
    public String showAllUrls(Model model) {
        List<UrlResponseDto> urls = service.getAllUrls();
        model.addAttribute("urls", urls);
        return "all-urls";
    }

}