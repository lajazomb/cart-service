package com.bookstore.authentication;

import com.bookstore.cartservice.port.cart.exception.NotAuthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
public class JwtRoleInterceptor implements HandlerInterceptor {

    private Environment environment;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*
        HttpServletRequestWrapper requestCopy = new HttpServletRequestWrapper(request);
        String requestBody = IOUtils.toString(requestCopy.getInputStream(), StandardCharsets.UTF_8);

        System.out.println(requestBody);

        request.setAttribute("requestBody", IOUtils.toInputStream(requestBody, StandardCharsets.UTF_8));
         */

        // all requests need USER role
        if (!JwtUtil.allowRequest(request, environment.getProperty("jwt.secret"))) {
            throw new NotAuthorizedException();
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}