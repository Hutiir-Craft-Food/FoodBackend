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

    @GetMapping("/blog-posts")
    @ResponseStatus(HttpStatus.OK)
    public BlogResponse getBlogPosts() {
        
        return blogService.getBlogResponse();
    }

    @GetMapping("/adv-posts")
    @ResponseStatus(HttpStatus.OK)
    public BlogResponse getAdvertisedBlogPosts() {
        
        return blogService.getBlogResponse();
    }

    @PostMapping("/blog-posts")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBlogPost(@Valid @RequestBody BlogRequest request) {
        
        blogService.createBlog(request);
    }
}
