package com.pharmacy.ThaiDuongPharmacyAPI.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Account;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.AccountRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String phone;

        try {
            phone = jwtUtils.extractPhone(token);
        } catch (Exception e) {
            sendErrorResponse(response, "Mã xác thực không hợp lệ hoặc đã hết hạn, vui lòng đăng nhập lại!");
            return;
        }

        if (phone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtils.validateToken(token)) {
                // Fetch the user from the database to get their role
                Optional<Account> accountOpt = accountRepository.findByPhone(phone);
                
                if (accountOpt.isPresent()) {
                    Account account = accountOpt.get();
                    
                    // Create authorities based on the role
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    // Make sure the role has the "ROLE_" prefix which Spring Security expects for hasRole()
                    String role = account.getRole().startsWith("ROLE_") ? account.getRole() : "ROLE_" + account.getRole();
                    authorities.add(new SimpleGrantedAuthority(role));

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            phone, null, authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    sendErrorResponse(response, "Tài khoản không tồn tại!");
                    return;
                }
            } else {
                sendErrorResponse(response, "Từ chối truy cập!");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", 401);
        errorDetails.put("message", message);
        errorDetails.put("data", null);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}
