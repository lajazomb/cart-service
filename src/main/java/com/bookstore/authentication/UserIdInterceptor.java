package com.bookstore.authentication;

import com.bookstore.cartservice.port.cart.exception.NotAuthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class UserIdInterceptor implements HandlerInterceptor {

    private Environment environment;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // all requests need USER role
        if (!JwtUtil.allowRequest(request, environment.getProperty("jwt.secret"))) {
            throw new NotAuthorizedException();
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}