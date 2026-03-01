package com.example.employeepair.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request, Model model) {
        String errorId = generateErrorId();

        // Detailed logging
        logger.error("=== 404 ERROR DETAILS [ID: {}] ===", errorId);
        logger.error("Timestamp: {}", LocalDateTime.now().format(formatter));
        logger.error("Request URL: {}", request.getRequestURL());
        logger.error("Request Method: {}", request.getMethod());
        logger.error("Query String: {}", request.getQueryString());
        logger.error("Remote Address: {}", request.getRemoteAddr());
        logger.error("User Agent: {}", request.getHeader("User-Agent"));
        logger.error("Referer: {}", request.getHeader("Referer"));
        logger.error("HTTP Method: {}", ex.getHttpMethod());
        logger.error("Request URL (from exception): {}", ex.getRequestURL());

        // Log all headers
        logger.error("Request Headers:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logger.error("  {}: {}", headerName, request.getHeader(headerName));
        }

        if (request.getCookies() != null) {
            logger.error("Cookies:");
            Arrays.stream(request.getCookies()).forEach(cookie ->
                    logger.error("  {}: {}", cookie.getName(), cookie.getValue())
            );
        }

        model.addAttribute("errorId", errorId);
        model.addAttribute("timestamp", LocalDateTime.now().format(formatter));
        model.addAttribute("errorTitle", "404 - Page Not Found");
        model.addAttribute("errorMessage", "The endpoint you're looking for doesn't exist.");
        model.addAttribute("errorDetails",
                "We couldn't find the page you requested. " +
                        "This could be due to a typo in the URL or because the page has been moved.");
        model.addAttribute("errorType", "NoHandlerFound");
        model.addAttribute("requestUrl", request.getRequestURL().toString());
        model.addAttribute("requestMethod", request.getMethod());
        model.addAttribute("queryString", request.getQueryString());
        model.addAttribute("remoteAddr", request.getRemoteAddr());
        model.addAttribute("userAgent", request.getHeader("User-Agent"));
        model.addAttribute("referer", request.getHeader("Referer"));
        model.addAttribute("suggestions", new String[]{
                "Check the URL for typos and try again",
                "Return to the <a href='/' class='alert-link'>home page</a>",
                "Use the navigation menu to find what you're looking for",
                "Upload a CSV file to analyze employee pairs"
        });

        return "error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request, Model model) {
        String errorId = generateErrorId();
        String resourcePath = ex.getResourcePath();

        // Detailed logging
        logger.error("=== 404 RESOURCE NOT FOUND [ID: {}] ===", errorId);
        logger.error("Timestamp: {}", LocalDateTime.now().format(formatter));
        logger.error("Resource Path: {}", resourcePath);
        logger.error("HTTP Method: {}", ex.getHttpMethod());
        logger.error("Request URL: {}", request.getRequestURL());
        logger.error("Remote Address: {}", request.getRemoteAddr());
        logger.error("User Agent: {}", request.getHeader("User-Agent"));
        logger.error("Referer: {}", request.getHeader("Referer"));

        // Determine resource type
        String resourceType = "Unknown";
        if (resourcePath.endsWith(".css")) resourceType = "CSS Stylesheet";
        else if (resourcePath.endsWith(".js")) resourceType = "JavaScript File";
        else if (resourcePath.endsWith(".png")) resourceType = "PNG Image";
        else if (resourcePath.endsWith(".jpg") || resourcePath.endsWith(".jpeg")) resourceType = "JPEG Image";
        else if (resourcePath.endsWith(".gif")) resourceType = "GIF Image";
        else if (resourcePath.endsWith(".ico")) resourceType = "Favicon";
        else if (resourcePath.endsWith(".html")) resourceType = "HTML Page";
        else if (resourcePath.endsWith(".csv")) resourceType = "CSV File";
        else if (resourcePath.endsWith(".json")) resourceType = "JSON File";
        else if (resourcePath.endsWith(".xml")) resourceType = "XML File";

        if (resourcePath.equals("favicon.ico")) {
            logger.warn("Favicon.ico not found - this is normal if no favicon is configured");
            // Return a specific response for favicon
            model.addAttribute("faviconMissing", true);
            model.addAttribute("suggestions", new String[]{
                    "This is just a favicon request - it's normal and can be ignored",
                    "To fix this, add a favicon.ico file to your static resources",
                    "Continue to the <a href='/' class='alert-link'>home page</a>"
            });
        } else {
            logger.error("Resource type: {}", resourceType);
            logger.error("Full resource path: {}", request.getRequestURL());
        }

        if (!resourcePath.equals("favicon.ico")) {
            logger.info("Checking for similar resources...");
        }

        model.addAttribute("errorId", errorId);
        model.addAttribute("timestamp", LocalDateTime.now().format(formatter));
        model.addAttribute("errorTitle", "404 - Resource Not Found");
        model.addAttribute("errorMessage", "The requested resource could not be found.");
        model.addAttribute("errorDetails",
                "Resource: " + resourcePath + " (Type: " + resourceType + ")");
        model.addAttribute("errorType", "NoResourceFound");
        model.addAttribute("requestUrl", request.getRequestURL().toString());
        model.addAttribute("resourcePath", resourcePath);
        model.addAttribute("resourceType", resourceType);
        model.addAttribute("remoteAddr", request.getRemoteAddr());
        model.addAttribute("userAgent", request.getHeader("User-Agent"));
        model.addAttribute("referer", request.getHeader("Referer"));

        if (!resourcePath.equals("favicon.ico")) {
            model.addAttribute("suggestions", new String[]{
                    "Check if the file exists in the correct location",
                    "Clear your browser cache and refresh",
                    "Verify the path is correct: " + resourcePath,
                    "Return to the <a href='/' class='alert-link'>home page</a>"
            });
        }

        return "error";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, HttpServletRequest request,
                                              Model model) {
        String errorId = generateErrorId();

        logger.error("=== FILE UPLOAD ERROR [ID: {}] ===", errorId);
        logger.error("Error: {}", ex.getMessage());
        logger.error("Max allowed size: {}MB", ex.getMaxUploadSize() / (1024 * 1024));
        logger.error("Request URL: {}", request.getRequestURL());
        logger.error("Content Length: {}", request.getContentLengthLong());

        model.addAttribute("errorId", errorId);
        model.addAttribute("timestamp", LocalDateTime.now().format(formatter));
        model.addAttribute("errorTitle", "File Too Large");
        model.addAttribute("errorMessage",
                "The uploaded file exceeds the maximum allowed size.");
        model.addAttribute("errorDetails",
                String.format("Maximum file size is %dMB. Your file was too large.",
                ex.getMaxUploadSize() / (1024 * 1024)));
        model.addAttribute("maxSize", ex.getMaxUploadSize() / (1024 * 1024) + "MB");
        model.addAttribute("suggestions", new String[]{
                "Compress your CSV file to reduce its size",
                "Split your data into multiple smaller files",
                "Remove unnecessary columns or rows",
                "Return to the <a href='/' class='alert-link'>upload page</a>"
        });

        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request, Model model) {
        String errorId = generateErrorId();

        logger.error("=== INVALID ARGUMENT [ID: {}] ===", errorId);
        logger.error("Error: {}", ex.getMessage());
        logger.error("Request URL: {}", request.getRequestURL());
        logger.error("Query String: {}", request.getQueryString());

        model.addAttribute("errorId", errorId);
        model.addAttribute("timestamp", LocalDateTime.now().format(formatter));
        model.addAttribute("errorTitle", "Invalid Request");
        model.addAttribute("errorMessage", "The request contains invalid parameters.");
        model.addAttribute("errorDetails", ex.getMessage());
        model.addAttribute("suggestions", new String[]{
                "Check your input data for errors",
                "Verify the CSV format matches the requirements",
                "Ensure all dates are in a supported format",
                "Return to the <a href='/' class='alert-link'>upload page</a>"
        });

        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, HttpServletRequest request, Model model) {
        String errorId = generateErrorId();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stackTrace = sw.toString();

        logger.error("=== INTERNAL SERVER ERROR [ID: {}] ===", errorId);
        logger.error("Timestamp: {}", LocalDateTime.now().format(formatter));
        logger.error("Exception Type: {}", ex.getClass().getName());
        logger.error("Exception Message: {}", ex.getMessage());
        logger.error("Request URL: {}", request.getRequestURL());
        logger.error("Request Method: {}", request.getMethod());
        logger.error("Query String: {}", request.getQueryString());
        logger.error("Remote Address: {}", request.getRemoteAddr());
        logger.error("User Agent: {}", request.getHeader("User-Agent"));
        logger.error("Referer: {}", request.getHeader("Referer"));
        logger.error("Stack Trace:");
        logger.error(stackTrace);

        Map<String, String[]> paramMap = request.getParameterMap();
        if (!paramMap.isEmpty()) {
            logger.error("Request Parameters:");
            paramMap.forEach((key, value) ->
                    logger.error("  {}: {}", key, Arrays.toString(value))
            );
        }

        if (request.getSession(false) != null) {
            logger.error("Session ID: {}", request.getSession().getId());
        }

        model.addAttribute("errorId", errorId);
        model.addAttribute("timestamp", LocalDateTime.now().format(formatter));
        model.addAttribute("errorTitle", "500 - Internal Server Error");
        model.addAttribute("errorMessage",
                "An unexpected error occurred while processing your request.");
        model.addAttribute("errorDetails",
                "Our technical team has been notified of this issue.");
        model.addAttribute("exceptionType", ex.getClass().getSimpleName());
        model.addAttribute("exceptionMessage", ex.getMessage());
        model.addAttribute("stackTrace",
                stackTrace.length() > 500 ? stackTrace.substring(0, 500) + "..." : stackTrace);
        model.addAttribute("requestUrl", request.getRequestURL().toString());
        model.addAttribute("requestMethod", request.getMethod());
        model.addAttribute("suggestions", new String[]{
                "Try again later",
                "Upload a different CSV file",
                "Clear your browser cache and try again",
                "Return to the <a href='/' class='alert-link'>home page</a>",
                "If the problem persists, contact support with Error ID: " + errorId
        });

        return "error";
    }

    private String generateErrorId() {
        return "ERR-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
}