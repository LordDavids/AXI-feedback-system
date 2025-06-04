package me.abdul.axi_api.middleware;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.abdul.axi_api.entities.Role;
import me.abdul.axi_api.entities.User;
import me.abdul.axi_api.repos.UserRepository;
import me.abdul.axi_api.services.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {


    private final JwtService jwtService;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {


        String authToken = extractAuthTokenFromCookies(request);

        if (authToken != null) {
            handleAuthentication(authToken, request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        filterChain.doFilter(request, response);

    }

    private String extractAuthTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("auth-token")) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private void handleAuthentication(String authToken, HttpServletRequest request, HttpServletResponse response) {
        if (!jwtService.isTokenValid(authToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            String userId = jwtService.extractClaim(authToken,Claims::getSubject);

            User user = userRepository.findById(Integer.parseInt(userId)).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            List<GrantedAuthority> authorities = new ArrayList<>();
            for (Role role : user.getRoles()) {
                String roleName = ("role_" + role.getRoleName()).toUpperCase();
                authorities.add(() -> roleName);
            }

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, authorities);

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }
}