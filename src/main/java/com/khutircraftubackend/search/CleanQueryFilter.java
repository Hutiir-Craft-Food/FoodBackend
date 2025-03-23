package com.khutircraftubackend.search;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@WebFilter("/*")
@Slf4j
public class CleanQueryFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }
    
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
    
        String query = req.getParameter("query");
    
        if (query != null) {
            
            log.debug("Original query: {}", query);
            
            query = query.replaceAll("[^\\p{L}\\d\\s_-]", " ").trim();
    
            log.debug("Cleaned query: {}", query);
            
            String finalQuery = query;
            
            request = new HttpServletRequestWrapper(req) {
                
                @Override
                public String getParameter(String name) {
                    
                    return "query".equals(name) ? finalQuery : super.getParameter(name);
                }
            };
        }
        chain.doFilter(request, response);
    }
    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
