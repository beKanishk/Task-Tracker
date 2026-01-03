//package com.task.tracker.authentication.config;
//
//import com.task.tracker.authentication.service.AuthService;
//import com.task.tracker.authentication.service.JwtService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    private final AuthService authService;
//    private final UserDetailsService userDetailsService;
//    private final JwtService jwtService;
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = authHeader.substring(7);
//        String username = jwtService.extractUsername(token);
//
//        if (username != null &&
//                SecurityContextHolder.getContext().getAuthentication() == null) {
//
//            UserDetails userDetails =
//                    userDetailsService.loadUserByUsername(username);
//
//            if (authService.validateToken(token, userDetails)) {
//
//                UsernamePasswordAuthenticationToken auth =
//                        new UsernamePasswordAuthenticationToken(
//                                userDetails,
//                                null,
//                                userDetails.getAuthorities()
//                        );
//
//                auth.setDetails(
//                        new WebAuthenticationDetailsSource().buildDetails(request)
//                );
//
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
//


package com.task.tracker.authentication.config;

import com.task.tracker.authentication.service.AuthService;
import com.task.tracker.authentication.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {

            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);

            String username = jwtService.extractUsername(token);

            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                if (authService.validateToken(token, userDetails)) {

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    auth.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);
                }
            }

            filterChain.doFilter(request, response);
        }
        catch (Exception ex) {

            // 🔴 Log full exception stack trace
            log.error("JWT authentication failed: {}", ex.getMessage(), ex);

            // 🔴 Return safe structured error response
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            response.getWriter().write("""
            {
              "error": "JWT_AUTHENTICATION_FAILED",
              "message": "%s",
              "path": "%s"
            }
            """.formatted(
                    ex.getMessage(),
                    request.getRequestURI()
            ));
        }
    }
}
