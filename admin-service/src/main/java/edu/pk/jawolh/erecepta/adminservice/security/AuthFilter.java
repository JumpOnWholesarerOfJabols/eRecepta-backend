package edu.pk.jawolh.erecepta.adminservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class AuthFilter extends OncePerRequestFilter {
    private static final String ROLE = "ROLE_%s";
    private static final String ID_HEADER = "X-UserId";
    private static final String ROLE_HEADER = "X-UserRole";

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String userHeader = request.getHeader(ID_HEADER);
        String roleHeader = request.getHeader(ROLE_HEADER);

        if (userHeader == null || roleHeader == null || userHeader.isEmpty() || roleHeader.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(ROLE.formatted(roleHeader));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userHeader, null, List.of(grantedAuthority));

        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request, response);
    }
}
