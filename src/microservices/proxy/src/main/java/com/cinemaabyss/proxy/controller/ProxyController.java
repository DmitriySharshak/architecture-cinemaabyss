package com.cinemaabyss.proxy.controller;

import com.cinemaabyss.proxy.service.FeatureFlagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Main controller for the API Gateway.
 * Handles all incoming requests and routes them to appropriate backend services
 * based on feature flags and path patterns.
 */
@RestController
public class ProxyController {

    private static final Logger logger = LoggerFactory.getLogger(ProxyController.class);
    
    private final FeatureFlagService featureFlagService;
    private final RestTemplate restTemplate;

    public ProxyController(FeatureFlagService featureFlagService, RestTemplate restTemplate) {
        this.featureFlagService = featureFlagService;
        this.restTemplate = restTemplate;
    }

    /**
     * Handles all requests to /api/* paths and routes them to appropriate services.
     */
    @RequestMapping("/api/**")
    public ResponseEntity<String> proxyRequest(HttpServletRequest request) {
        try {
            String path = request.getRequestURI();
            String targetService = featureFlagService.getTargetService(path);
            
            logger.info("Proxying request: {} -> {}", path, targetService);
            
            // Build target URL
            String targetUrl = targetService + path;
            if (request.getQueryString() != null) {
                targetUrl += "?" + request.getQueryString();
            }
            
            // Create headers
            HttpHeaders headers = createHeaders(request);
            
            // Create request entity
            HttpEntity<String> entity = new HttpEntity<>(getBody(request), headers);
            
            // Make the request
            ResponseEntity<String> response = restTemplate.exchange(
                targetUrl,
                HttpMethod.valueOf(request.getMethod()),
                entity,
                String.class
            );
            
            logger.info("Request completed: {} -> {} (status: {})", 
                       path, targetService, response.getStatusCode());
            
            return ResponseEntity
                .status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
                
        } catch (Exception e) {
            logger.error("Error proxying request: {}", request.getRequestURI(), e);
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Health check endpoint for the proxy service.
     */
    @RequestMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Strangler Fig Proxy is healthy");
    }

    /**
     * Creates HTTP headers from the incoming request.
     */
    private HttpHeaders createHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            
            // Skip hop-by-hop headers
            if (!isHopByHopHeader(headerName)) {
                headers.set(headerName, headerValue);
            }
        }
        
        return headers;
    }

    /**
     * Gets the request body from HttpServletRequest.
     */
    private String getBody(HttpServletRequest request) {
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            java.io.BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            logger.debug("No request body or error reading body", e);
            return "";
        }
    }

    /**
     * Checks if a header is a hop-by-hop header that shouldn't be forwarded.
     */
    private boolean isHopByHopHeader(String headerName) {
        switch (headerName.toLowerCase()) {
            case "connection", "keep-alive", "proxy-authenticate", "proxy-authorization", "te", "trailers", "transfer-encoding", "upgrade":
                return true;
            default:
                return false;
        }
    }
}
