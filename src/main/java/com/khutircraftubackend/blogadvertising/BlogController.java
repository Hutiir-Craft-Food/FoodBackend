package com.khutircraftubackend.blogadvertising;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping(value = "/advPosts")
    @ResponseStatus(HttpStatus.OK)
    public BlogResponse blogPosts() {
        return blogService.sendRequest();
    }

    @GetMapping(value = "/blogPosts")
    @ResponseStatus(HttpStatus.OK)
    public BlogResponse advPosts() {
        return blogService.sendRequest();
    }

    @PostMapping(value = "/addposts")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBlog(@Valid @RequestBody BlogRequest request) {
        blogService.createBlog(request);
    }
}
