package com.khutircraftubackend.blogadvertising;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlogMapper {
    
    BlogResponse.BlogItem toBlogItem(BlogEntity blogEntity);
    
    default BlogResponse toBlogResponse(List<BlogEntity> entities) {
        
        List<BlogResponse.BlogItem> items = entities.stream()
                .map(this::toBlogItem)
                .toList();
        
        return new BlogResponse(items);
    }
}
