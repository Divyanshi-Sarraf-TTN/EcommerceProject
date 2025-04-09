package com.example.EcommerceProject.EcommerceProject.JWT;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;


import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext applicationContext;

    // Method to lazily fetch the UserService bean from the ApplicationContext
    // This is done to avoid Circular Dependency issues
    private SecurityUserService getUserService() {
        return applicationContext.getBean(SecurityUserService.class);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extracting token from the request header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;

// TODO : add check for blacklist tokens --> exception
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extracting the token from the Authorization header
            token = authHeader.substring(7);
            // Extracting username from the token
            userName = jwtService.extractUserName(token);
        }
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // If username is extracted and there is no authentication in the current SecurityContext
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Loading UserDetails by username extracted from the token
            UserDetails userDetails = getUserService().loadUserByUsername(userName);

            // Validating the token with loaded UserDetails
            //System.out.println("before validation");
            if (jwtService.validateToken(token, userDetails)) {
                //System.out.println("after validation");
                // Creating an authentication token using UserDetails
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // Setting authentication details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Setting the authentication token in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Proceeding with the filter chain
        filterChain.doFilter(request, response);
    }

}
