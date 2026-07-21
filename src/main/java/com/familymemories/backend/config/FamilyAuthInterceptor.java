package com.familymemories.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class FamilyAuthInterceptor implements HandlerInterceptor {

    @Value("${app.security.family-email}")
    private String familyEmail;

    @Value("${app.security.family-password}")
    private String familyPassword;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();

        
        String email;
        String password;
        if (path.startsWith("/uploads/")) {
            email = request.getParameter("email");
            password = request.getParameter("password");
        } else {
            email = request.getHeader("X-Family-Email");
            password = request.getHeader("X-Family-Password");
        }

        boolean emailOk = familyEmail.equalsIgnoreCase(email == null ? "" : email.trim());
        boolean passwordOk = familyPassword.equals(password);

        if (emailOk && passwordOk) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"Wrong or missing login\"}");
        return false;
    }
}