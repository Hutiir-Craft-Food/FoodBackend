package com.khutircraftubackend.blogadvertising;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final BlogMapper blogMapper;

    public BlogResponse sendRequest() {
        List<BlogEntity> allBlogs = blogRepository.findAll();
        return blogMapper.toBlogResponse(allBlogs);
    }

    public void createBlog(BlogRequest request) {
        BlogEntity blogEntity = BlogEntity
                .builder()
                .image(request.image())
                .name(request.name())
                .build();

        blogRepository.save(blogEntity);
    }
}
