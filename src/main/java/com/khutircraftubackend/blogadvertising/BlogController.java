package com.khutircraftubackend.blogadvertising;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping(value = "/adv-posts")
    public BlogResponse blogPosts() {
        return blogService.sendRequest();
    }

    @GetMapping(value = "/blog-posts")
    public BlogResponse advPosts() {
        return blogService.sendRequest();
    }

    @PostMapping(value = "/blog-posts")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBlog(@Valid @RequestBody BlogRequest request) {
        blogService.createBlog(request);
    }
}
