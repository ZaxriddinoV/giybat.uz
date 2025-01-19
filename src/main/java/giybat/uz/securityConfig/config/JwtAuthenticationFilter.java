package giybat.uz.securityConfig.config;


import giybat.uz.profile.dto.JwtDTO;
import giybat.uz.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7).trim();

        try {
            JwtDTO dto = JwtUtil.decode(token);

            String username = dto.getUsername();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException e) {
            String refreshToken = request.getHeader("Refresh-Token");

            if (refreshToken != null) {
                try {
                    JwtDTO refreshDto = JwtUtil.decode(refreshToken);

                    if ("refresh".equals(refreshDto.getType())) {
                        String newAccessToken = JwtUtil.encode(refreshDto.getUsername(), refreshDto.getRole());

                        response.setHeader("New-Access-Token", newAccessToken);

                        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshDto.getUsername());
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (JwtException ex) {
                    // Agar refresh token yaroqsiz bo'lsa, foydalanuvchini autentifikatsiyadan chiqarish
                    SecurityContextHolder.clearContext();
                }
            } else {
                // Refresh token mavjud emas bo'lsa
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

}
