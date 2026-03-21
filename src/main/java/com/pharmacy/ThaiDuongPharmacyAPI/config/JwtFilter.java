package com.pharmacy.ThaiDuongPharmacyAPI.config;

import com.pharmacy.ThaiDuongPharmacyAPI.entity.Account;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ApiException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.AccountRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final AccountRepository accountRepository;
    private final HandlerExceptionResolver exceptionResolver;

    @Autowired
    public JwtFilter(JwtUtils jwtUtils, AccountRepository accountRepository, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.jwtUtils = jwtUtils;
        this.accountRepository = accountRepository;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
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
            exceptionResolver.resolveException(request, response, null, ApiException.unauthorized("Mã xác thực không hợp lệ hoặc đã hết hạn, vui lòng đăng nhập lại!"));
            return;
        }

        if (phone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtils.validateToken(token)) {
                Optional<Account> accountOpt = accountRepository.findByPhone(phone);
                
                if (accountOpt.isPresent()) {
                    Account account = accountOpt.get();

                    List<GrantedAuthority> authorities = new ArrayList<>();
                    String role = account.getRole().startsWith("ROLE_") ? account.getRole() : "ROLE_" + account.getRole();
                    authorities.add(new SimpleGrantedAuthority(role));

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            phone, null, authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    exceptionResolver.resolveException(request, response, null, ApiException.unauthorized("Tài khoản không tồn tại!"));
                    return;
                }
            } else {
                exceptionResolver.resolveException(request, response, null, ApiException.unauthorized("Từ chối truy cập!"));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
