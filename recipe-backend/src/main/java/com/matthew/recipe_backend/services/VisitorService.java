package com.matthew.recipe_backend.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class VisitorService {

    private static final String VISITOR_COOKIE_NAME = "visitor_id";
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 365; // 1 year

    public String getOrCreateVisitorId() {
        HttpServletRequest request = getRequest();
        HttpServletResponse response = getResponse();

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (VISITOR_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        String visitorId = UUID.randomUUID().toString();

        Cookie cookie = new Cookie(VISITOR_COOKIE_NAME, visitorId);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setSecure(false);

        response.addCookie(cookie);

        return visitorId;
    }

    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
    }

    public HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getResponse();
    }
}
